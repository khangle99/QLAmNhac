package com.khangle.qlamnhac;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.ReportTopSingerTuple;
import com.khangle.qlamnhac.report.ReportSingerTopActivity;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {
    MusicDBDao musicDBDao;
    Button selectYearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setupDatabase();
        setControl();
        setEvent();
    }

    private void setControl() {
        selectYearBtn = findViewById(R.id.selectYear);

    }

    private void setEvent() {
        selectYearBtn.setOnClickListener(v -> {
            showYearPicker();
        });
    }

    private static final String TAG = "ReportActivity";
    private void showYearPicker() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(ReportActivity.this,
                (selectedMonth, selectedYear) -> {
                    musicDBDao.getMostPerformanceSinger(yearIntToDate(selectedYear-1).getTime(),yearIntToDate(selectedYear+1).getTime()).observe(this, reportTopSingerTuples -> {
                        Intent intent = new Intent(this, ReportSingerTopActivity.class);
                        Bundle bundle = new Bundle();
                        ArrayList<ReportTopSingerTuple> array = new ArrayList<>(reportTopSingerTuples);
                        bundle.putParcelableArrayList("chart", array);
                        bundle.putInt("year", selectedYear);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    });
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));

        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1990)
                .setActivatedYear(2021)
                .setMaxYear(2030)
                .setTitle("Select year")
                .setMonthRange(Calendar.JANUARY, Calendar.NOVEMBER)
                .showYearOnly()
                .build()
                .show();
    }

    private Date yearIntToDate(int yearInt) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearInt);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        Date d = cal.getTime();
        return d;
    }

    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }
}