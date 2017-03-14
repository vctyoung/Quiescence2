package com.example.victor.quiescence;

/**
 * Created by victor on 2017/3/4.
 */

public class Room {

    private String title;
    private int level;
    //  public Image image;

    public Room(String title, int level)
    {
        this.level=level;
        this.title=title;
    }

    public String getName(){ return title;}
    public int getLevel() {return level;}


}
