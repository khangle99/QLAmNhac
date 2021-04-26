package com.khangle.qlamnhac.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Singer implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String uriString;
    public Singer(String name) {
        this.name = name;
    }

    protected Singer(Parcel in) {
        id = in.readInt();
        name = in.readString();
        uriString = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(uriString);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Singer> CREATOR = new Creator<Singer>() {
        @Override
        public Singer createFromParcel(Parcel in) {
            return new Singer(in);
        }

        @Override
        public Singer[] newArray(int size) {
            return new Singer[size];
        }
    };
}

