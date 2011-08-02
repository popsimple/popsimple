package com.project.website.shared.client.widgets.authentication.registration;

import com.project.website.shared.data.Invitation;

public class RegistrationRequestData {
    private final String email;
    private final String password;
    private final String name;
    private final Invitation invitation;

    public RegistrationRequestData(String name, String email, String password, Invitation invitation)
    {
        this.email = email;
        this.name = name;
        this.password = password;
        this.invitation = invitation;
    }

    public Invitation getInvitation()
    {
        return invitation;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }
    public String getPassword()
    {
        return password;
    }
}