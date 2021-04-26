package com.khangle.qlamnhac.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity
public class Composer implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;

    public Composer(String name) {
        this.name = name;
    }

    protected Composer(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Composer> CREATOR = new Creator<Composer>() {
        @Override
        public Composer createFromParcel(Parcel in) {
            return new Composer(in);
        }

        @Override
        public Composer[] newArray(int size) {
            return new Composer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}


