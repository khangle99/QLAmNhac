package com.khangle.qlamnhac.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ComposerWithSongs {
    @Embedded
    public Composer composer;
    @Relation(
            parentColumn = "id",
            entityColumn = "composerId"
    )
    public List<Song> songs;
}
