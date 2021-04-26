package com.khangle.qlamnhac.composer;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.ComposerWithSongs;
import com.khangle.qlamnhac.model.Song;
import com.khangle.qlamnhac.songDetail.SongFragment;
import com.khangle.qlamnhac.songDetail.SongListAdapter;
import com.khangle.qlamnhac.util.UseState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ComposerDetailActivity extends AppCompatActivity {
    MusicDBDao musicDBDao;
    EditText nameEditText;
    Button addSongBtn;
    UseState state;
    ComposerWithSongs composerWithSongs;
    SongListAdapter adapter;
    RecyclerView songList;
    Composer addComposer; // danh cho add

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer_detail);
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

    private static final String TAG = "ComposerDetailActivity";


    private void refresh() {

    }

    private void setControl() {
        nameEditText = findViewById(R.id.composerDetailName);

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
        songList = findViewById(R.id.composerSongList);
        setupSongListRecycle();
        addSongBtn = findViewById(R.id.addComposerSong);

    }

    private void initEmptyComposer() {
        composerWithSongs = new ComposerWithSongs(); // khoi tao rong
        musicDBDao.insertComposer(new Composer("default-name")) // su dung default name
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
            musicDBDao.getComposerByName("default-name").observe(this, composer -> {
                composerWithSongs.composer = composer; // get lai de lay id
            });
        });
    }


    private void setupSongListRecycle() {
        adapter = new SongListAdapter(song -> {
            SongFragment songFragment = new SongFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("song", song);
            songFragment.setArguments(bundle);
            songFragment.show(getSupportFragmentManager(), "");
        });
        songList.setAdapter(adapter);
        songList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void invalidateView() {
        switch (state) {
            case ADD:
                nameEditText.setEnabled(true);
                break;
            case EDIT:
                nameEditText.setEnabled(true);
                break;
            case VIEW:
                observeData();
                nameEditText.setEnabled(false);
                break;
        }
    }

    public void observeData() {
        int composerId = getIntent().getExtras().getInt("composerId");
        musicDBDao.getComposerDetailById(composerId).observe(this, composerWithSongs1 -> {
            if (composerWithSongs1 != null) {
                this.composerWithSongs = composerWithSongs1;
                nameEditText.setText(composerWithSongs.composer.name);
                List<Song> list = cloneSongList(composerWithSongs.songs);
                adapter.submitList(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private List<Song> cloneSongList(List<Song> source) {
        ArrayList<Song> des = new ArrayList<>();
        for (Song s : source) {
            des.add(new Song(s));
        }
        return des;
    }


    private void setEvent() {
        addSongBtn.setOnClickListener(v -> {
            SongFragment songFragment = new SongFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("composerId", composerWithSongs.composer.id);
            songFragment.setArguments(bundle);
            songFragment.show(getSupportFragmentManager(), "");
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
            musicDBDao.deleteComposer(composerWithSongs.composer).subscribeOn(Schedulers.io())
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
                        composerWithSongs.composer.name = name;
                        musicDBDao.updateComposer(composerWithSongs.composer).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Toast.makeText(getBaseContext(), "Insert thanh cong", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else { // state update
                        composerWithSongs.composer.name = name;
                        musicDBDao.updateComposer(composerWithSongs.composer).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            Toast.makeText(getBaseContext(), "Update thanh cong", Toast.LENGTH_SHORT).show();

                            changeState(UseState.VIEW);
                        });

                    }
                }

                return true;
            case R.id.reset:
                // reset view
                refresh();
                nameEditText.setText(composerWithSongs.composer.name);


                return true;
            case R.id.edit:
                changeState(UseState.EDIT);
                return true;
            case R.id.cancel:
                if (state == UseState.ADD) {
                    // xoa default
                    musicDBDao.deleteComposer(composerWithSongs.composer).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(this::finish);

                } else if (state == UseState.EDIT) {
                    changeState(UseState.VIEW);
                }

                return true;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Composer")
                        .setMessage("Are you sure you want to remove this composer?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicDBDao.deleteComposer(composerWithSongs.composer).subscribeOn(Schedulers.io())
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

