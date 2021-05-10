package com.khangle.qlamnhac.songDetail;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.composer.ComposerDetailActivity;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Song;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SongFragment extends BottomSheetDialogFragment {
    Button saveBtn;
    Button cancelBtn;
    ImageButton deleteBtn;
    DatePicker datePicker;
    EditText nameEditText;
    TextView releaseDateTextview;
    MusicDBDao musicDBDao;
    Song song;
    int composerId;

    public SongFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        composerId = getArguments().getInt("composerId"); // co khi add mode
        song = getArguments().getParcelable("song"); // lay song neu co (khi select item)
        setControl();
        setEvent();
        setupDatabase();
        if (song != null) {
            loadDataIntoView(); // chi can load 1 lan k realtime
        }

    }

    private void loadDataIntoView() {
        nameEditText.setText(song.songName);
        Calendar cal = Calendar.getInstance();
        cal.setTime(song.releaseDate);
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
    }

    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(requireActivity().getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }


    private Boolean validateInput() { // false khi fail
        boolean isSetName = true;
        if (nameEditText.getText().toString().trim().length() == 0) {
            isSetName = false;
            nameEditText.setError("Không được để trống tên");
        }
        return isSetName;
    }


    private void setEvent() {
        saveBtn.setOnClickListener(v -> {
            if (validateInput()) {
                Date date = getDateFromDatePicker(datePicker);
                String name = nameEditText.getText().toString();
                if (song != null) { // update khi song khac null
                    song.songName = name;
                    song.releaseDate = date;
                    musicDBDao.updateSong(song).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                        Toast.makeText(requireContext(), "Update thanh cong", Toast.LENGTH_SHORT).show();
                        ((ComposerDetailActivity) requireActivity()).observeData();
                        dismiss();
                    });
                } else {
                    musicDBDao.insertSong(new Song(name, date, composerId))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                        Toast.makeText(requireContext(), "Insert thanh cong", Toast.LENGTH_SHORT).show();
                        ((ComposerDetailActivity) requireActivity()).observeData();
                        dismiss();
                    });
                }
            }

        });
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });
        deleteBtn.setOnClickListener(v -> {
            Boolean isAddmode = getArguments().getBoolean("isAdd");
            if (!isAddmode) {
                // hien thong bao confirm
                new AlertDialog.Builder(requireActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Song")
                        .setMessage("Are you sure you want to remove this Song?")
                        .setPositiveButton("Yes", (dialog, which) -> musicDBDao.deleteSongById(song.songId).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                    Toast.makeText(requireContext(), "Delete thanh cong", Toast.LENGTH_SHORT).show();
                                    ((ComposerDetailActivity) requireActivity()).observeData();
                                    dismiss();
                                }))
                        .setNegativeButton("No", null)
                        .show();
            }

        });
        datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            releaseDateTextview.setText("Ngày " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        });

    }

    public static Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    private void setControl() {

        saveBtn = getView().findViewById(R.id.SaveSong);
        releaseDateTextview = getView().findViewById(R.id.dateTextviewSong);
        cancelBtn = getView().findViewById(R.id.CancelSong);
        deleteBtn = getView().findViewById(R.id.deleteSong);
        datePicker = getView().findViewById(R.id.releaseDate);
        nameEditText = getView().findViewById(R.id.songNameEditText);
        Boolean isAddmode = getArguments().getBoolean("isAdd");
        deleteBtn.setEnabled(!isAddmode);
    }
}