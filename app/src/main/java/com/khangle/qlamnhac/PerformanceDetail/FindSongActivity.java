package com.khangle.qlamnhac.PerformanceDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.adapter.ComposerAdapter;
import com.khangle.qlamnhac.composer.ComposerActivity;
import com.khangle.qlamnhac.composer.ComposerDetailActivity;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.Song;
import com.khangle.qlamnhac.songDetail.SongListAdapter;

import java.util.List;

public class FindSongActivity extends AppCompatActivity {
    MusicDBDao musicDBDao;
    SongListAdapter adapter;
    RecyclerView songReycle;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_song);
        setControl();
        setEvent();
        setupRecycleview();
        setupDatabase();
        observeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeData();
    }

    private void observeData() {
        LiveData<List<Song>> allSong = musicDBDao.getAllSong();
        allSong.observe(this, songs -> {
            adapter.submitList(songs);
        });
    }

    private void setupRecycleview() {
        adapter = new SongListAdapter(song -> {
           // tra ve bai hat result
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("song",song);
            intent.putExtras(bundle);
            setResult(RESULT_OK,intent);
            finish();
        });
        songReycle.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        songReycle.setAdapter(adapter);
    }

    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }

    private static final String TAG = "SongFindActivity";
    private void setControl() {
        songReycle = findViewById(R.id.songList);
        searchView = findViewById(R.id.songSearchView);
    }
    private void setEvent() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    musicDBDao.getAllSong().observe(FindSongActivity.this, songs -> {
                        adapter.submitList(songs);
                    });
                    return true;
                }
                musicDBDao.searchSongByName("%"+newText+"%").observe(FindSongActivity.this, songs -> {
                    adapter.submitList(songs);
                });
                return true;
            }
        });
    }
}