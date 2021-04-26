package com.khangle.qlamnhac.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Song implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int songId;
    public String songName;
    public Date releaseDate;
    public int composerId; // foreign
    public Song() {}
    public Song(Song song) {
        this.releaseDate = (Date) song.releaseDate.clone();
        this.songName = song.songName;
        this.composerId = song.composerId;
        this.songId = song.songId;
    }
    public Song(String name, Date releaseDate, int composerId) {
        this.songName = name;
        this.releaseDate = releaseDate;
        this.composerId = composerId;
    }

    protected Song(Parcel in) {
        songId = in.readInt();
        songName = in.readString();
        composerId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(songId);
        dest.writeString(songName);
        dest.writeInt(composerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
