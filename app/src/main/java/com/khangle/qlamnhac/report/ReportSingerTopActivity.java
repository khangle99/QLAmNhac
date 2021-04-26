package com.khangle.qlamnhac.report;

import androidx.appcompat.app.AppCompatActivity;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.model.ReportTopSingerTuple;

import java.util.ArrayList;
import java.util.List;

public class ReportSingerTopActivity extends AppCompatActivity {
    List<BarEntry> entries = new ArrayList<>();
    ArrayList<ReportTopSingerTuple> array;
    BarChart chart;
    TextView header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_singer_top);
        setControl();
        setupChart();
    }
    ArrayList<String> singerNames = new ArrayList<>();
    private void setupChart() {
        array = getIntent().getExtras().getParcelableArrayList("chart");

        for (int i = 0; i < array.size(); i++) {
            entries.add(new BarEntry(i, array.get(i).time));
            singerNames.add(array.get(i).name);
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return singerNames.get((int) value);
            }
        };
        Description description = chart.getDescription();
        description.setEnabled(false);
        BarDataSet set = new BarDataSet(entries, "Số lần biểu diễn trong năm");
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
        chart = findViewById(R.id.chart);
        header = findViewById(R.id.reportTitle);
        int year = getIntent().getExtras().getInt("year");
        header.setText("Danh sách top 5 ca sĩ biểu diễn nhiều nhất trong năm: " + year);
    }
}