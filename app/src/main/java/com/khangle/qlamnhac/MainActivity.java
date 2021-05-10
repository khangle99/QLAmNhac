package com.khangle.qlamnhac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.khangle.qlamnhac.report.ReportActivity;
import com.khangle.qlamnhac.singer.SingerActivity;
import com.khangle.qlamnhac.composer.ComposerActivity;
import com.rbddevs.splashy.Splashy;

public class MainActivity extends AppCompatActivity {
    CardView singerCard;
    CardView composerCard;
    CardView reportCard;
    Button infoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setSlashy();
        }
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();
    }

    private void setSlashy() {
        new Splashy(this)
                .setLogo(R.mipmap.logo1)
                .setBackgroundColor(R.color.white)
                .setTitle("")
                .setSubTitle("Team 25")
                .setSubTitleColor(R.color.dark_grey)
                .setSubTitleSize(28f)
                .setSubTitleItalic(true)
                .setProgressColor(R.color.white)
                .setAnimation(Splashy.Animation.SLIDE_IN_TOP_BOTTOM, 1000)
                .setFullScreen(true)
                .setDuration(2000)
                .show();
    }

    private void setControl() {
        singerCard = findViewById(R.id.caSiScreen);
        composerCard = findViewById(R.id.nhacSiScreen);
        reportCard = findViewById(R.id.baoCaoScreen);
        infoBtn = findViewById(R.id.infoBtn);
    }

    private void setEvent() {
        singerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, SingerActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        composerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ComposerActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        reportCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        infoBtn.setOnClickListener(v -> {
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.microIcon), "Ca sĩ","Quản lý ca sĩ")
                                    .cancelable(false)
                                    .dimColor(R.color.black)
                                    .outerCircleColor(R.color.purple_200)
                                    .textColor(android.R.color.white),
                            TapTarget.forView(findViewById(R.id.composerIcon), "Nhạc sĩ", "Quản lý nhạc sĩ")
                                    .cancelable(false)
                                    .dimColor(R.color.black)
                                    .outerCircleColor(R.color.purple_200)
                                    .textColor(android.R.color.white),
                            TapTarget.forView(findViewById(R.id.staticIcon), "Thống kê", "Thống kê quản lý")
                                    .cancelable(false)
                                    .dimColor(R.color.black)
                                    .outerCircleColor(R.color.purple_200)
                                    .textColor(android.R.color.white)).start();
        });
    }


}