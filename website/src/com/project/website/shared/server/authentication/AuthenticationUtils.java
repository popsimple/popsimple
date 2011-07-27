package com.project.website.shared.server.authentication;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.project.website.shared.data.Invitation;
import com.project.website.shared.data.User;

public class AuthenticationUtils
{

    public static User createUser(String userName, String password, String name)
    {
        User user = new User();
        user.username = userName.toLowerCase();
        user.password = AuthenticationUtils.hashPassword(password);
        user.publicName = name;
        user.isEnabled = true;
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        datastore.store(user);

        if (null == loadUser(userName)) {
            throw new RuntimeException("Failed to save user");
        }

        return user;
    }

    public static User loadUser(String userName)
    {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        return datastore.load(User.class, userName.toLowerCase());
    }

    public static boolean invitationIsValid(Invitation invitation)
    {
        if (null == invitation) {
            return false;
        }
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        Invitation foundInv = datastore.load(Invitation.class, invitation.id);
        return (null != foundInv);
    }


    public static MessageDigest newDigest()
    {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("SHA-512");
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Couldn't find algorithm");
        }
        return m;
    }

    public static String getDigestString(MessageDigest m)
    {
        return new BigInteger(1, m.digest()).toString(16);
    }

    public static String hashPassword(String password)
    {
        MessageDigest m = newDigest();
        // To make the calculation intentionally slower
        for (long i = 0; i < 10000; i++) {
            m.update(password.getBytes());
            m.update((byte) 1);
        }
        return getDigestString(m);
    }

    public static void invalidateInvitation(Invitation invitation)
    {
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        datastore.delete(invitation);
    }

    public static Invitation createInvitation()
    {
        Invitation res = new Invitation();
        res.id = (UUID.randomUUID()).toString();
        ObjectDatastore datastore = new AnnotationObjectDatastore();
        datastore.store(res);
        return res;
    }
}
