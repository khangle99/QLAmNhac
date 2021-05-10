package com.khangle.qlamnhac.report;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.PerformanceSongTuple;
import com.khangle.qlamnhac.singer.PerformanceListAdapter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReportSingerPerformancePerMonthActivity extends AppCompatActivity {
    List<BarEntry> entries = new ArrayList<>();
    MusicDBDao musicDBDao;
    BarChart chart;
    TextView singerNameTextView;
    List<PerformanceSongTuple> performanceSongTupleList;
    RecyclerView performList;
    PerformanceListAdapter adapter;
    ArrayList<String> monthLable = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_singer_performance_per_month);
        setUpDatabase();
        setControl();
        setUpRecycleView();
        getPerformanceData();

    }


    private void setUpRecycleView() {
        adapter = new PerformanceListAdapter(info -> {

        });
        performList.setAdapter(adapter);
        performList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }


    private void getPerformanceData() {
        final long from = getIntent().getLongExtra("from", -1);
        final long to = getIntent().getLongExtra("to", -1);
        String singerName = getIntent().getStringExtra("singerName");
        int singerId = getIntent().getIntExtra("singerId", -1);
        LiveData<List<PerformanceSongTuple>> performSongTupleBySingerIdAndYear = musicDBDao.getPerformSongTupleBySingerIdAndYear(singerId, from, to);
        performSongTupleBySingerIdAndYear.observe(this, performanceSongTuples -> {
            performanceSongTupleList = performanceSongTuples;
            adapter.submitList(performanceSongTuples);
            setUpChart();
        });
        singerNameTextView.setText(singerName);
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
    private void setUpChart() {
        List<Integer> times = countTimePerMonth(performanceSongTupleList);
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, times.get(i)));
            monthLable.add("Tháng " + i);
        }
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return monthLable.get((int) value);
            }
        };
        Description description = chart.getDescription();
        description.setEnabled(false);
        BarDataSet set = new BarDataSet(entries, "Số lần biểu diễn trong năm");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        YAxis axisLeft = chart.getAxisLeft();
        YAxis axisRight = chart.getAxisRight();
        axisLeft.setGranularity(1);
        axisRight.setGranularity(1);
        chart.invalidate(); // refresh
    }

    private void setControl() {
        chart = findViewById(R.id.chartReport);
        singerNameTextView = findViewById(R.id.singerNameReport);
        performList = findViewById(R.id.performList);
    }

    private void setUpDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }
    private List<Integer> countTimePerMonth(List<PerformanceSongTuple> list) {
        ArrayList<Integer> times = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            times.add(0);
        }
        performanceSongTupleList.forEach(performanceSongTuple -> {
            LocalDate localDate = performanceSongTuple.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int month = localDate.getMonthValue();
            Integer time = times.get(month - 1);
            times.set(month - 1, time + 1);
        });

        return times;
    }
}