package com.khangle.qlamnhac.singer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.khangle.qlamnhac.PerformanceDetail.PerformanceDetailActivity;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.ComposerWithSongs;
import com.khangle.qlamnhac.model.PerformanceInfo;
import com.khangle.qlamnhac.model.PerformanceSongTuple;
import com.khangle.qlamnhac.model.Singer;
import com.khangle.qlamnhac.model.SingerWithPerformanceInfo;
import com.khangle.qlamnhac.util.UseState;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SingerDetailActivity extends AppCompatActivity {
    MusicDBDao musicDBDao;
    EditText nameEditText;
    Chip addPerformanceBtn;
    UseState state;
    Singer singer;
    List<PerformanceSongTuple> performanceSongTuples;
    PerformanceListAdapter adapter;
    RecyclerView performanceList;
    ImageView singerAvartar;
    private String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_detail);
        setupDatabase();
        setControl();
        setEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        invalidateMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private static final String TAG = "SingerDetailActivity";


    private void refresh() {

    }



    private void setControl() {
        nameEditText = findViewById(R.id.singerDetailName);
        addPerformanceBtn = findViewById(R.id.addSingerPerformance);
        singerAvartar = findViewById(R.id.singerDetailAvartar);
        performanceList = findViewById(R.id.singerPerformanceList);
        final String flag = getIntent().getExtras().getString("detail_flag");
        UseState state = UseState.VIEW;
        switch (flag) {
            case "view":
                state = UseState.VIEW;
                break;
            case "edit":
                state = UseState.EDIT;
                break;
            case "add":
                initEmptyComposer();
                state = UseState.ADD;
                break;
        }
        changeState(state);

        setupSongListRecycle();

        if (imageUri.equals("")) {
            singerAvartar.setImageResource(R.drawable.ic_baseline_image_search_24);
        }

    }

    private void setEvent() {
        addPerformanceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerformanceDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("detail_flag", "add");
            singer.name = nameEditText.getText().toString(); // lay tamp khi chua save xuong db
            bundle.putParcelable("singer", singer);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        singerAvartar.setOnClickListener(v -> {
            // kiem tra uri rong sau
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "select a picture"), IMAGE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                FileOutputStream fos = null;
                InputStream iStream = null;
                try {
                    iStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] inputData = getBytes(iStream);
                    fos = getBaseContext().openFileOutput(""+ singer.id, Context.MODE_PRIVATE);
                    fos.write(inputData);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                        iStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                File file = new File(getBaseContext().getFilesDir(), ""+ singer.id);
                Uri uri = Uri.fromFile(file);
                imageUri = uri.toString();
                singerAvartar.setImageDrawable(null);
                singerAvartar.setImageURI(uri);
            }

        }
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static final int IMAGE_REQUEST_CODE = 22;


    private void initEmptyComposer() {
        musicDBDao.insertSinger(new Singer("default-name")) // su dung default name
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
            musicDBDao.getSingerByName("default-name").observe(this, singer -> {
                this.singer = singer; // get lai de lay id
            });
        });
    }

    private void setupSongListRecycle() {
        adapter = new PerformanceListAdapter(performanceSongTuple -> {
            Intent intent = new Intent(this, PerformanceDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("detail_flag", "view");
            bundle.putParcelable("singer", singer);
            bundle.putString("songName", performanceSongTuple.songName);
            bundle.putInt("performId", performanceSongTuple.id);
            intent.putExtras(bundle);
            startActivity(intent);

        });
        performanceList.setAdapter(adapter);
        performanceList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeData();
        adapter.notifyDataSetChanged();
        if (!imageUri.equals("")) {
            singerAvartar.setImageDrawable(null);
            singerAvartar.setImageURI(Uri.parse(imageUri));
        }
    }

    private void invalidateView() {
        switch (state) {
            case ADD:
                nameEditText.setEnabled(true);
                singerAvartar.setEnabled(true);
                break;
            case EDIT:
                nameEditText.setEnabled(true);
                singerAvartar.setEnabled(true);
                break;
            case VIEW:
                observeData();
                nameEditText.setEnabled(false);
                singerAvartar.setEnabled(false);
                break;
        }
    }

    public void observeData() {
        int singerId = getIntent().getExtras().getInt("singerId");
        musicDBDao.getSingerDetailById(singerId).observe(this, singer -> {
            if (singer != null) {
                this.singer = singer;
                nameEditText.setText(singer.name);
                if (singer.uriString != null && !singer.uriString.equals("")) {
                    singerAvartar.setImageURI(Uri.parse(singer.uriString));
                }

            }
        });
        musicDBDao.getPerformSongTupleBySingerId(singerId).observe(this, performanceSongTuples -> {
            if (singer != null) {
                this.performanceSongTuples = performanceSongTuples;
                adapter.submitList(Collections.emptyList());
                adapter.submitList(performanceSongTuples);
            }
        });
    }


    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }

    private void changeState(UseState state) {
        this.state = state;
        invalidateView();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (state == UseState.ADD) {
            musicDBDao.deleteSinger(singer).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(this::finish);
        }
    }


    private Boolean validateInput() { // false khi fail
        boolean isSetName = true;
        if (nameEditText.getText().toString().trim().length() == 0) {
            isSetName = false;
            nameEditText.setError("Không được để trống tên");
        }
        return isSetName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (validateInput()) {
                    String name = nameEditText.getText().toString();
                    if (state == UseState.ADD) {
                        singer.name = name;
                        singer.uriString = imageUri;
                        musicDBDao.updateSinger(singer).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Toast.makeText(getBaseContext(), "Insert thanh cong", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else { // state update
                        singer.name = name;
                        singer.uriString = imageUri;
                        musicDBDao.updateSinger(singer).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Toast.makeText(getBaseContext(), "Update thanh cong", Toast.LENGTH_SHORT).show();
                            observeData();
                            changeState(UseState.VIEW);
                        });
                    }
                }
                return true;
            case R.id.reset:
                // reset view
                refresh();
                nameEditText.setText(singer.name);

                return true;
            case R.id.edit:
                changeState(UseState.EDIT);
                return true;
            case R.id.cancel:
                if (state == UseState.ADD) {
                    musicDBDao.deleteSinger(singer).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(this::finish);
                } else if (state == UseState.EDIT) {
                    changeState(UseState.VIEW);
                }
                return true;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Singer")
                        .setMessage("Are you sure you want to remove this singer?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicDBDao.deleteSinger(singer).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                                    Toast.makeText(getBaseContext(), "Delete thanh cong", Toast.LENGTH_SHORT).show();
                                    finish();
                                });

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
}