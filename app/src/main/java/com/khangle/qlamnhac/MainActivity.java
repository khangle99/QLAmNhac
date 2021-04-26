package com.khangle.qlamnhac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import com.khangle.qlamnhac.singer.SingerActivity;
import com.khangle.qlamnhac.composer.ComposerActivity;

public class MainActivity extends AppCompatActivity {
    CardView singerCard;
    CardView composerCard;
    CardView reportCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();
    }

    private void setControl() {
        singerCard = findViewById(R.id.caSiScreen);
        composerCard = findViewById(R.id.nhacSiScreen);
        reportCard = findViewById(R.id.baoCaoScreen);
    }

    private void setEvent() {
        singerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, SingerActivity.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        composerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ComposerActivity.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        reportCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }


}