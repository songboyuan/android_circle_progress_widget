package com.dynasty.song.progresswidgetexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private HoloCircularProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (HoloCircularProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress((float) 0 / 100.0f);
        progressBar.setSecondProgress((float) 0 / 100.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setProgress((float) 80 / 100.0f);
        progressBar.setSecondProgress((float) 20 / 100.0f);

    }
}
