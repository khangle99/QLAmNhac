package com.khangle.qlamnhac.composer;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.adapter.ComposerAdapter;
import com.khangle.qlamnhac.data.db.MusicDBDao;
import com.khangle.qlamnhac.data.db.MusicManagerDatabase;
import com.khangle.qlamnhac.model.Composer;

import java.util.List;

public class ComposerActivity extends AppCompatActivity {
    FloatingActionButton fab;
    MusicDBDao musicDBDao;
    ComposerAdapter adapter;
    RecyclerView composerReycle;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);
        setControl();
        setEvent();
        setupDatabase();
        setupRecycleview();
        observeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeData();
    }

    private void observeData() {
        LiveData<List<Composer>> allComposer = musicDBDao.getAllComposer();
        allComposer.observe(this, composers -> {
            adapter.submitList(composers);
        });
    }

    private void setupRecycleview() {
        adapter = new ComposerAdapter(composer -> {
            Intent intent = new Intent(this, ComposerDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("detail_flag", "view");
          //  bundle.putParcelable("composer",composer);
            bundle.putInt("composerId", composer.id);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        composerReycle.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        composerReycle.setAdapter(adapter);
    }


    private void setupDatabase() {
        musicDBDao = Room.databaseBuilder(getApplicationContext(),
                MusicManagerDatabase.class, "music_db").build().musicDBDao();
    }

    private static final String TAG = "ComposerActivity";
    private void setControl() {
        fab = findViewById(R.id.addComposerFab);
        composerReycle = findViewById(R.id.composerList);
        searchView = findViewById(R.id.composerSearchView);
    }
    private void setEvent() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, ComposerDetailActivity.class);
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
                    musicDBDao.getAllComposer().observe(ComposerActivity.this, composers -> {
                        adapter.submitList(composers);
                    });
                    return true;
                }
                musicDBDao.searchComposerByName("%"+newText+"%").observe(ComposerActivity.this, composers -> {
                    adapter.submitList(composers);
                });
                return true;
            }
        });
    }
}