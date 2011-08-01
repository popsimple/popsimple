package com.project.website.shared.data;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    public UserProfile(String email, String publicName, boolean canInvite)
    {
        this();
        this.email = email;
        this.publicName = publicName;
        this.canInvite = canInvite;
    }

    public UserProfile() {}

    public String email = "";
    public String publicName = "";
    public boolean canInvite = false;

}
