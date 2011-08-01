package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.shared.server.UrlEncodedQueryString;
import com.project.shared.utils.StringUtils;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.data.Invitation;
import com.project.website.shared.data.QueryParameters;
import com.project.website.shared.data.User;
import com.project.website.shared.data.UserProfile;

public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService
{
    private static final long serialVersionUID = 1L;

    private static final String SITE_BASE_ADDR = "http://www.PopSimple.com";
    private static final String INVITE_PATH = "/Login.html";

    private static final String ADMIN_USERNAME = "admin@popsimple.com";
    private static final String ADMIN_DEFAULT_PASSWORD = "admin";

    @Override
    protected void doUnexpectedFailure(Throwable e)
    {
        // see http://code.google.com/p/google-web-toolkit/issues/detail?id=3298
        if (e instanceof IOException) {
            // mask it.
            return;
        }
        super.doUnexpectedFailure(e);
    }


    @Override
    public void register(String email, String password, String name, Invitation invitation)
    {
        User authenticatedUser = getAuthenticatedUser();
        boolean needsInvitation = false == canRegisterUsers(authenticatedUser);
        boolean validInvitation = AuthenticationUtils.invitationIsValid(invitation);
        if (needsInvitation && (false == validInvitation)) {
            // User has no invitation and no privileges to register new users.
            this.onInvalidInvitation();
            return;
        }

        if (null != AuthenticationUtils.loadUser(email)) {
            // todo do this normally
            throw new RuntimeException("User already exists.");
        }

        AuthenticationUtils.createUser(email, password, name);
        if (needsInvitation || validInvitation) {
            AuthenticationUtils.invalidateInvitation(invitation);
        }
    }




    @Override
    public void login(String username, String password)
    {
        this.logout();

        if (StringUtils.isEmptyOrNull(username))
        {
            this.onAuthenticationFailed();
            return;
        }

        this.validateAdminUserExists();

        User user = AuthenticationUtils.loadUser(username);
        if (null == user)
        {
            this.onAuthenticationFailed();
            return;
        }
        if ((false == user.isEnabled) || (false == user.password.equals(AuthenticationUtils.hashPassword(password))))
        {
            this.onAuthenticationFailed();
            return;
        }

        HttpAuthentication.setAuthCookies(user, this.getThreadLocalRequest(), this.getThreadLocalResponse());
    }


    private void validateAdminUserExists()
    {
        if (null != AuthenticationUtils.loadUser(ADMIN_USERNAME))
        {
            return;
        }
        AuthenticationUtils.createUser(ADMIN_USERNAME, ADMIN_DEFAULT_PASSWORD, "Admin");
    }


    private void onInvalidInvitation()
    {
        // TODO respond in a way that the client understands that this was the problem...
        //onAuthenticationFailed();
        throw new RuntimeException("Invitation was already used or is invalid.");
    }

    private void onAuthenticationFailed()
    {
        // There's a small GWT bug when using the thread local response:
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=3298
        // it causes an exception (at least in Jetty) because it tries to write into the response after it has closed
        // In any case it will get sent, so leaving it here.
        try {
            // TODO: use SC_FORBIDDEN  instead?
            this.getThreadLocalResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Mask this.
        }
        return;
    }

    @Override
    public void logout() {
        HttpAuthentication.clearAuthCookies(getThreadLocalRequest(), getThreadLocalResponse());
    }


    @Override
    public void invite(String email, String message, String name)
    {
        // TODO add logic for validating the invited user
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        Invitation invitation = AuthenticationUtils.createInvitation();
        UrlEncodedQueryString query = UrlEncodedQueryString.create();
        query.set(QueryParameters.INVITE_ID, invitation.id);
        String inviteUrl;

        try {
            URI inviteURI = new URI(SITE_BASE_ADDR + INVITE_PATH);
            inviteUrl = query.apply(inviteURI).toURL().toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("no-reply@popsimple.com", "PopSimple.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            msg.setSubject("You're invited to PopSimple.com!");
            String messageWrapped = StringUtils.isWhitespaceOrNull(message) ? "" : ("Here's a message from the person who invited you:\r\n"
                                                                                    + "'" + message + "'\r\n");
            String text = "Your invitation is waiting at " + inviteUrl + "\r\n"
                        + messageWrapped + "\r\n"
                        + "Come and try it out!";
            msg.setText(text);
            Transport.send(msg);

        } catch (AddressException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public UserProfile getUserProfile()
    {
        User user = getAuthenticatedUser();
        boolean canInvite = this.canRegisterUsers(user);
        UserProfile userProfile = new UserProfile(user.username, user.publicName, canInvite);
        return userProfile;
    }

    private boolean canRegisterUsers(User user)
    {
        // TODO implement a permissions system
        if (null == user) {
            return false;
        }
        return user.username.equals(ADMIN_USERNAME);
    }

    private User getAuthenticatedUser()
    {
        User user = HttpAuthentication.getAuthenticatedUser(this.getThreadLocalRequest(), this.getThreadLocalResponse());
        return user;
    }
}
