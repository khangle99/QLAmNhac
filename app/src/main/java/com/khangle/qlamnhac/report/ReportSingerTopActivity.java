package com.khangle.qlamnhac.report;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.khangle.qlamnhac.model.ReportTopSingerTuple;
import com.khangle.qlamnhac.model.Singer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.N)
public class ReportSingerTopActivity extends AppCompatActivity {
    List<BarEntry> entries = new ArrayList<>();
    ArrayList<ReportTopSingerTuple> tupleArrayList;
    BarChart chart;
    TextView header;
    SingerWithTimesListAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_singer_top);
        setControl();
        setupChart();
        setUpRecycleview();
    }


    private void setUpRecycleview() {
        adapter = new SingerWithTimesListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        Collections.sort(tupleArrayList, (h1, h2) -> h2.time - h1.time);
        adapter.submitList(tupleArrayList);
    }

    ArrayList<String> singerNames = new ArrayList<>();
    private void setupChart() {
        tupleArrayList = getIntent().getExtras().getParcelableArrayList("chart");

        for (int i = 0; i < tupleArrayList.size(); i++) {
            entries.add(new BarEntry(i, tupleArrayList.get(i).time));
            singerNames.add(tupleArrayList.get(i).name);
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return singerNames.get((int) value);
            }
        };
        Description description = chart.getDescription();
        description.setEnabled(false);
        BarDataSet set = new BarDataSet(entries, "S??? l???n bi???u di???n trong n??m");
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
        chart = findViewById(R.id.chart);
        header = findViewById(R.id.reportTitle);
        int year = getIntent().getExtras().getInt("year");
        header.setText("Danh s??ch top 5 ca s?? bi???u di???n nhi???u nh???t trong n??m: " + year);
        recyclerView = findViewById(R.id.singerList1);

    }
}