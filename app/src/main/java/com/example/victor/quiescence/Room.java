package com.example.victor.quiescence;

/**
 * Created by victor on 2017/3/4.
 */

public class Room {

    private String title;
    private String time;
    private int level;
    //  public Image image;

    public Room(String title, int level,String time)
    {
        this.level=level;
        this.title=title;
        this.time=time;
    }

    public String getName(){ return title;}
    public int getLevel() {return level;}
    public String getTime() {return time;}

}
