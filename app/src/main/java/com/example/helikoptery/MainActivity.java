package com.example.helikoptery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Timer timer;
    private ProgressBar progressBar;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);


        final long period = 100;
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (i < 100) {
                    progressBar.setProgress(i);
                    i+=3;
                } else {
                    timer.cancel();
                    Intent intent = new Intent(MainActivity.this, activity_menu.class);
                    startActivity(intent);
                    finish();
                }
            }
            }, 0, period);
        }

}
