package com.khangle.qlamnhac.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity()
public class PerformanceInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public Date date;
    public String location;
    public int singerId;
    public int songID;
    public PerformanceInfo(){

    }
    public PerformanceInfo(PerformanceInfo info){
        this.id = info.id;
        this.date = info.date;
        this.location = info.location;
        this.singerId = info.singerId;
        this.songID = info.songID;
    }
    public PerformanceInfo(String location, int singerId, int songID, Date date) {
        this.date = date;
        this.location = location;
        this.singerId = singerId;
        this.songID = songID;
    }
}
