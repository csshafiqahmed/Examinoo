package com.codestown.examinoo.model;


import java.util.ArrayList;

public class LeaderBoard {
  private String rank,name,score,userId,profile;
public ArrayList<LeaderBoard> topList;

    public LeaderBoard(ArrayList<LeaderBoard> topList) {
        this.topList = topList;
    }

    public ArrayList<LeaderBoard> getTopList() {
        return topList;
    }

    public LeaderBoard() {
    }

    public LeaderBoard(String rank, String name, String score, String userId, String profile) {
        this.rank = rank;
        this.name = name;
        this.score = score;
        this.userId = userId;
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    public String getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getUserId() {
        return userId;
    }


}
