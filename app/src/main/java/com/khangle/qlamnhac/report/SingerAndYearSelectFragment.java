package com.khangle.qlamnhac.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.khangle.qlamnhac.R;

import com.khangle.qlamnhac.model.Singer;
import com.khangle.qlamnhac.singer.SingerActivity;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;
import java.util.Date;

public class SingerAndYearSelectFragment extends DialogFragment {

    Button selectSingerBtn;
    Button selectYearBtn;
    Button okBtn;
    Button cancelBtn;
    TextView singerNameTextview;
    TextView yearTextview;
    int selectedSingerId = -1;
    String  selectedSingerName = "";
    Long from;
    Long to;
    public SingerAndYearSelectFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_singer_and_year_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setControl(view);
        setEvent();
    }

    private void setControl(View view) {
        selectSingerBtn = view.findViewById(R.id.selectSinger);
        selectYearBtn = view.findViewById(R.id.selectYear2);
        okBtn = view.findViewById(R.id.okBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        singerNameTextview = view.findViewById(R.id.singerNameTextview);
        yearTextview = view.findViewById(R.id.yearTextview2);
    }

    private void setEvent() {
        cancelBtn.setOnClickListener(v -> {
                dismiss();
        });
        selectSingerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SingerActivity.class);
            intent.putExtra("selectSinger",true);
            startActivityForResult(intent,99);
        });
        selectYearBtn.setOnClickListener(v -> {
            showYearPicker();
        });
        okBtn.setOnClickListener(v -> {
            if (from == null) {
                Toast.makeText(requireContext(), "Chưa chọn năm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSingerId == -1) {
                Toast.makeText(requireContext(), "Chưa chọn ca sĩ", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(requireActivity(), ReportSingerPerformancePerMonthActivity.class);
            intent.putExtra("singerId",selectedSingerId);
            intent.putExtra("singerName",selectedSingerName);
            intent.putExtra("from",from);
            intent.putExtra("to",to);
            startActivity(intent);
            dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            selectedSingerName = data.getStringExtra("singerName");
           singerNameTextview.setText(selectedSingerName);
           selectedSingerId = data.getIntExtra("singerId",-1);
        }
    }

    private void showYearPicker() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(requireActivity(),
                (selectedMonth, selectedYear) -> {
                    from = yearIntToDate(selectedYear-1).getTime();
                    to = yearIntToDate(selectedYear+1).getTime();
                    yearTextview.setText(String.valueOf(selectedYear));
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
}