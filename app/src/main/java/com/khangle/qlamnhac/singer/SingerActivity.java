package com.khangle.qlamnhac.singer;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Singer;

import java.util.List;

public class SingerActivity extends AppCompatActivity {
    FloatingActionButton fab;
    MusicDBDao musicDBDao;
    SingerListAdapter adapter;
    RecyclerView singerRecycle;
    SearchView searchView;
    Boolean isSelectSinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_singer);
        setControl();
        setEvent();
        setupDatabase();
        setupRecycleview();
        observeData();
        isSelectSinger = getIntent().getBooleanExtra("selectSinger", false);
        if (isSelectSinger) {
            setResult(RESULT_CANCELED); // init state
            // hide add new singer
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeData();
        adapter.notifyDataSetChanged();
    }

    private void observeData() {
        LiveData<List<Singer>> allSinger = musicDBDao.getAllSinger();
        allSinger.observe(this, singers -> {
            adapter.submitList(singers);
        });
    }

    private void setupRecycleview() {
        adapter = new SingerListAdapter(singer -> {
            if (isSelectSinger) { // set
                Intent data = new Intent();
                data.putExtra("singerName",singer.name);
                data.putExtra("singerId",singer.id);
                setResult(RESULT_OK,data);
                finish();
            } else {
                Intent intent = new Intent(this, SingerDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("detail_flag", "view");
                bundle.putInt("singerId", singer.id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        singerRecycle.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        singerRecycle.setAdapter(adapter);
    }

    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }

    private static final String TAG = "SingerActivity";

    private void setControl() {
        fab = findViewById(R.id.addSingerFab);
        singerRecycle = findViewById(R.id.singerList);
        searchView = findViewById(R.id.singerSearchView);
    }

    private void setEvent() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, SingerDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("detail_flag", "add");
            intent.putExtras(bundle);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    musicDBDao.getAllSinger().observe(SingerActivity.this, singers -> {
                        adapter.submitList(singers);
                    });
                    return true;
                }
                musicDBDao.searchSingerByName("%" + newText + "%").observe(SingerActivity.this, singers -> {
                    adapter.submitList(singers);
                });
                return true;
            }
        });
    }
}