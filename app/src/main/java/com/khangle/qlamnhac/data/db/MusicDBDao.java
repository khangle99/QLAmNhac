package com.khangle.qlamnhac.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.ComposerWithSongs;
import com.khangle.qlamnhac.model.PerformanceInfo;
import com.khangle.qlamnhac.model.PerformanceSongTuple;
import com.khangle.qlamnhac.model.ReportTopSingerTuple;
import com.khangle.qlamnhac.model.Singer;
import com.khangle.qlamnhac.model.SingerWithPerformanceInfo;
import com.khangle.qlamnhac.model.Song;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MusicDBDao {
    // composer
    @Query("SELECT * FROM composer")
    LiveData<List<Composer>> getAllComposer();

    @Query("SELECT * FROM composer WHERE name LIKE :composeName")
    LiveData<List<Composer>> searchComposerByName(String composeName);

    @Query("SELECT * FROM composer WHERE name = :composeName")
    LiveData<Composer> getComposerByName(String composeName);

    @Transaction
    @Query("SELECT * FROM composer WHERE id = :composerId")
    LiveData<ComposerWithSongs> getComposerDetailById(int composerId);

    @Insert
    Completable insertComposer(Composer composer);

    @Update
    Completable updateComposer(Composer composer);

    @Delete
    Completable deleteComposer(Composer composer);

    // song
    @Query("SELECT * FROM song")
    LiveData<List<Song>> getAllSong();

    @Query("SELECT * FROM song WHERE songName LIKE :songName")
    LiveData<List<Song>> searchSongByName(String songName);

    @Transaction
    @Query("SELECT * FROM song WHERE songId = :songId")
    LiveData<Song> getSongById(int songId);

    @Insert
    Completable insertSong(Song song);

    @Update
    Completable updateSong(Song song);

    @Query("DELETE FROM song where songId =:songId")
    Completable deleteSongById(int songId);

    // singer
    @Query("SELECT * FROM singer")
    LiveData<List<Singer>> getAllSinger();

    @Query("SELECT * FROM singer WHERE name = :singerName")
    LiveData<Singer> getSingerByName(String singerName);

    @Query("SELECT * FROM singer WHERE name LIKE :composeName")
    LiveData<List<Singer>> searchSingerByName(String composeName);

    @Query("SELECT * FROM singer WHERE id = :composerId")
    LiveData<Singer> getSingerDetailById(int composerId);

    @Insert
    Completable insertSinger(Singer singer);

    @Update
    Completable updateSinger(Singer singer);

    @Delete
    Completable deleteSinger(Singer singer);

    // performance
    @Query("SELECT id, date, location, songName " +
            "FROM performanceinfo INNER JOIN song ON performanceinfo.songID = song.songId " +
            "WHERE performanceinfo.singerId = :singerId")
    LiveData<List<PerformanceSongTuple>> getPerformSongTupleBySingerId(int singerId);

    @Query("SELECT * FROM performanceinfo WHERE id = :performId")
    LiveData<PerformanceInfo> getPerformanceInfoById(int performId);
    @Insert
    Completable insertPerformance(PerformanceInfo info);
    @Update
    Completable updatePerformance(PerformanceInfo info);
    @Delete
    Completable deletePerformance(PerformanceInfo info);

    //statistics

    // truy van top ca si hat nhieu trong nam dc chon

    @Query("SELECT singerId, name, uriString, COUNT(singer.id) AS  time " +
            "FROM performanceinfo INNER JOIN singer ON performanceinfo.singerId = singer.id " +
            "WHERE date BETWEEN :from AND :to " +
            "GROUP BY singerId " +
            "ORDER BY time " +
            "LIMIT 5")
    LiveData<List<ReportTopSingerTuple>> getMostPerformanceSinger(Long from, Long to);

    // truy van bieu dien theo thang cua 1 ca si
    @Query("SELECT id, date, location, songName " +
            "FROM performanceinfo INNER JOIN song ON performanceinfo.songID = song.songId " +
            "WHERE performanceinfo.singerId = :singerId AND date BETWEEN :from AND :to ")
    LiveData<List<PerformanceSongTuple>> getPerformSongTupleBySingerIdAndYear(int singerId, Long from, Long to);

}
