package com.project.website.shared.server.authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.project.shared.utils.StringUtils;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.data.User;

public class AuthenticationServiceImpl extends RemoteServiceServlet implements AuthenticationService
{
    private static final long serialVersionUID = 1L;

    private static final String STUB_USERNAME = "!StubUser!@StubEmail.com";

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
    public void register(String email, String password)
    {
        User user = HttpAuthentication.loadUser(email);
        if (null != user) {
            // todo do this normally
            throw new RuntimeException("User already exists.");
        }

        user = new User();
        user.username = email;
        user.password = password;
        user.isEnabled = true;
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        datastore.store(user);

        if (null == HttpAuthentication.loadUser(email)) {
            throw new RuntimeException("Failed to save user");
        }

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
            // ...
        } catch (MessagingException e) {
            // ...
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
    }

    @Override
    public void login(String username, String password) throws IOException
    {
        this.logout();

        if (StringUtils.isEmptyOrNull(username))
        {
            this.onLoginFailed();
            return;
        }

        //TODO: Remove!
        this.validateStubUserExists();

        User user = HttpAuthentication.loadUser(username);
        if (null == user)
        {
            this.onLoginFailed();
            return;
        }
        if ((false == user.isEnabled) || (false == user.password.equals(password)))
        {
            this.onLoginFailed();
            return;
        }

        HttpAuthentication.setAuthCookies(user, this.getThreadLocalRequest(), this.getThreadLocalResponse());
    }


    private void validateStubUserExists()
    {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        User user = datastore.load(User.class, STUB_USERNAME);
        if (null != user)
        {
            return;
        }
        //Create a stub user for the datastore to know the object type in order to be able to manually add
        //users in the appengine admin.
        user = new User();
        user.username = STUB_USERNAME;
        user.password = UUID.randomUUID().toString();
        user.isEnabled = false;
        datastore.store(user);
    }

    private void onLoginFailed() throws IOException
    {
        // There's a small GWT bug when using the thread local response:
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=3298
        // it causes an exception (at least in Jetty) because it tries to write into the response after it has closed
        // In any case it will get sent, so leaving it here.
        this.getThreadLocalResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    @Override
    public void logout() {
        HttpServletRequest request = this.getThreadLocalRequest();
        if (null == request)
        {
            return;
        }

    }
}
