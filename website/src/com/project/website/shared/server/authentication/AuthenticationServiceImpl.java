package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.project.shared.utils.StringUtils;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.data.User;

public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService
{
    private static final long serialVersionUID = 1L;

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
    public void register(String email, String password, String name)
    {
        if (false == canRegisterUsers()) {
            this.onAuthenticationFailed();
            return;
        }

        if (null != AuthenticationUtils.loadUser(email)) {
            // todo do this normally
            throw new RuntimeException("User already exists.");
        }

        AuthenticationUtils.createUser(email, password, name);
    }


    @Override
    public boolean canRegisterUsers()
    {
        // TODO implement a permissions system
        User registeringUser = HttpAuthentication.getAuthenticatedUser(this.getThreadLocalRequest(), this.getThreadLocalResponse());
        boolean canRegisterUsers = registeringUser.username.equals(ADMIN_USERNAME);
        return canRegisterUsers;
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

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("mail@popsimple.com", "PopSimple.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            msg.setSubject("Welcome to PopSimple!");
            msg.setText("Visit us at http://www.PopSimple.com");
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
}
