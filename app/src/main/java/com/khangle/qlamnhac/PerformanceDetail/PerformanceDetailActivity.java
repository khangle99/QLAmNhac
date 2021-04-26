package com.khangle.qlamnhac.PerformanceDetail;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.PerformanceInfo;
import com.khangle.qlamnhac.model.Singer;
import com.khangle.qlamnhac.model.Song;
import com.khangle.qlamnhac.util.UseState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PerformanceDetailActivity extends AppCompatActivity {
    MusicDBDao musicDBDao;
    Button selectSongBtn;
    DatePicker startDatePicker;
    EditText locationEditText;
    TextView singerNameTextView;
    TextView songNameTextView;
    UseState state;
    PerformanceInfo info; // add,update,delete
    Singer singer; // constant
    Song song; // for add performance
    String songName; // query or pick

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_detail);
        setupDatabase();
        setControl();
        setEvent();


    }

    private void setControl() {
        selectSongBtn = findViewById(R.id.selectSong);
        startDatePicker = findViewById(R.id.startDatePicker);
        locationEditText = findViewById(R.id.locationTextview);
        songNameTextView = findViewById(R.id.songNameTextview);
        singerNameTextView = findViewById(R.id.singerPerformName);
        final String flag = getIntent().getExtras().getString("detail_flag");
        UseState state = UseState.VIEW;
        int id = 0;
        singer = getIntent().getExtras().getParcelable("singer");
        switch (flag) {
            case "view":
                state = UseState.VIEW;
                id = getIntent().getExtras().getInt("performId");
                songName = getIntent().getExtras().getString("songName");
                break;
            case "edit":
                state = UseState.EDIT;
                id = getIntent().getExtras().getInt("performId");
                songName = getIntent().getExtras().getString("songName");
                break;
            case "add":
                state = UseState.ADD;
                break;
        }
        if (id != 0) {
            // query performance info
            musicDBDao.getPerformanceInfoById(id).observe(this, info -> {
                if (info != null) {
                    this.info = info;
                    locationEditText.setText(info.location);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(info.date);
                    startDatePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                }
            });
        }
        changeState(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        invalidateMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }

    private void invalidateView() {
        singerNameTextView.setText(singer.name);
        switch (state) {
            case ADD:
                locationEditText.setEnabled(true);
                startDatePicker.setEnabled(true);
                selectSongBtn.setEnabled(true);
                break;
            case EDIT:
                locationEditText.setEnabled(true);
                startDatePicker.setEnabled(true);
                selectSongBtn.setEnabled(false);
                break;
            case VIEW:
                setDataToView();
                locationEditText.setEnabled(false);
                startDatePicker.setEnabled(false);
                selectSongBtn.setEnabled(false);
                break;
        }
    }

    public void setDataToView() {
        // chua update location, date

        songNameTextView.setText(this.songName);
    }

    private List<Song> cloneSongList(List<Song> source) {
        ArrayList<Song> des = new ArrayList<>();
        for (Song s : source) {
            des.add(new Song(s));
        }
        return des;
    }

    public static Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    private Boolean validateInput() { // false khi fail
        boolean isSetName = true;
        boolean isSetSong = true;
        if (locationEditText.getText().toString().trim().length() == 0) {
            isSetName = false;
            locationEditText.setError("Không được để trống tên");
        }

        if (songNameTextView.getText().toString().trim().equals("Song name")) {
            isSetSong = false;
            songNameTextView.setError("Chưa chọn bài hát");
        }
        return isSetName && isSetSong;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (validateInput()) {
                    String location = locationEditText.getText().toString();
                    Date date = getDateFromDatePicker(startDatePicker);
                    if (state == UseState.ADD) {
                        musicDBDao.insertPerformance(new PerformanceInfo(location, singer.id, song.songId, date))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Toast.makeText(getBaseContext(), "Insert thanh cong", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else { // state update
                        info.location = locationEditText.getText().toString();
                        info.date = date;
                        musicDBDao.updatePerformance(info).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Toast.makeText(getBaseContext(), "Update thanh cong", Toast.LENGTH_SHORT).show();

                            changeState(UseState.VIEW);
                        });

                    }
                }

                return true;
            case R.id.reset:
                // reset view

                locationEditText.setText(info.location);
                Calendar cal = Calendar.getInstance();
                cal.setTime(info.date);
                startDatePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                return true;
            case R.id.edit:
                changeState(UseState.EDIT);
                return true;
            case R.id.cancel:
                if (state == UseState.ADD) {
                    finish();
                } else if (state == UseState.EDIT) {
                    changeState(UseState.VIEW);
                }
                return true;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Performance")
                        .setMessage("Are you sure you want to remove this Performance?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicDBDao.deletePerformance(info).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                    Toast.makeText(getBaseContext(), "Delete thanh cong", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeState(UseState state) {
        this.state = state;
        invalidateView();
        invalidateOptionsMenu();
    }

    private void invalidateMenu(Menu menu) {
        switch (state) {
            case ADD:
                menu.findItem(R.id.save).setVisible(true);
                menu.findItem(R.id.edit).setVisible(false);
                menu.findItem(R.id.reset).setVisible(false);
                menu.findItem(R.id.cancel).setVisible(true);
                menu.findItem(R.id.delete).setVisible(false);
                break;
            case EDIT:
                menu.findItem(R.id.save).setVisible(true);
                menu.findItem(R.id.edit).setVisible(false);
                menu.findItem(R.id.reset).setVisible(true);
                menu.findItem(R.id.cancel).setVisible(true);
                menu.findItem(R.id.delete).setVisible(false);
                break;
            case VIEW:
                menu.findItem(R.id.save).setVisible(false);
                menu.findItem(R.id.edit).setVisible(true);
                menu.findItem(R.id.reset).setVisible(false);
                menu.findItem(R.id.cancel).setVisible(false);
                menu.findItem(R.id.delete).setVisible(true);
                break;
        }
    }

    private void setEvent() {
        selectSongBtn.setOnClickListener(v -> {
            // start activity for result
            Intent intent = new Intent(this, FindSongActivity.class);
            startActivityForResult(intent, 99);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == RESULT_OK) {
            song = data.getExtras().getParcelable("song");
            songNameTextView.setText(song.songName);
        }
    }

    private static final String TAG = "PerformanceDetailActivi";
}