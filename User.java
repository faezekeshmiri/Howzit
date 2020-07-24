package com.example.faridam.howzit;

import android.widget.ImageView;

import java.util.UUID;

/**
 * Created by farida.M on 6/27/2020.
 */

public class User {
     private String name;
     private String uid;
     private byte[] profile;

    public byte[] getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile(byte[] profile) {
        this.profile = profile;
    }

    public void setUid(String uuid) {
        this.uid = uuid;
    }
}
