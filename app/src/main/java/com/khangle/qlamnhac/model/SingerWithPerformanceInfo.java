package com.khangle.qlamnhac.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SingerWithPerformanceInfo {
    @Embedded
    public Singer singer;
    @Relation(
            parentColumn = "id",
            entityColumn = "singerId"
    )
    public List<PerformanceInfo> performanceInfoList;
}
