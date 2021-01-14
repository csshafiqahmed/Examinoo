package com.codestown.examinoo.model;

public class User {

    public String matchingId, langId, authID, userID, isAvail, status, email, name, image, user_id, opponentName, opponentProfile, resut;


    public User() {
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getOpponentProfile() {
        return opponentProfile;
    }

    public void setOpponentProfile(String opponentProfile) {
        this.opponentProfile = opponentProfile;
    }

    public String getResut() {
        return resut;
    }

    public void setResut(String resut) {
        this.resut = resut;
    }

    public User(String first_name, String email, String user_id) {
        this.name = first_name;
        this.email = email;
        this.user_id = user_id;
    }

    public User(String userID, String name, String image, String isAvail, String langId) {
        this.userID = userID;
        this.name = name;
        this.image = image;
        this.isAvail = isAvail;
        this.langId = langId;

    }

    public String getMatchingId() {
        return matchingId;
    }

    public String getAuthID() {
        return authID;
    }

    public String getUserID() {
        return userID;
    }

    public String getIsAvail() {
        return isAvail;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUser_id() {
        return user_id;
    }
}
