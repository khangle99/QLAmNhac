package com.khangle.qlamnhac.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.PerformanceInfo;
import com.khangle.qlamnhac.model.Singer;
import com.khangle.qlamnhac.model.Song;

@Database(entities = {Composer.class, PerformanceInfo.class, Singer.class, Song.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MusicManagerDatabase extends RoomDatabase {
    public abstract MusicDBDao musicDBDao();
}