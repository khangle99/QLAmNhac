package com.khangle.qlamnhac.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportTopSingerTuple implements Parcelable {
    public int time;
    public int singerId;
    public String name;
    public String uriString;

    public ReportTopSingerTuple() {}

    protected ReportTopSingerTuple(Parcel in) {
        time = in.readInt();
        singerId = in.readInt();
        name = in.readString();
        uriString = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(time);
        dest.writeInt(singerId);
        dest.writeString(name);
        dest.writeString(uriString);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReportTopSingerTuple> CREATOR = new Creator<ReportTopSingerTuple>() {
        @Override
        public ReportTopSingerTuple createFromParcel(Parcel in) {
            return new ReportTopSingerTuple(in);
        }

        @Override
        public ReportTopSingerTuple[] newArray(int size) {
            return new ReportTopSingerTuple[size];
        }
    };
}
