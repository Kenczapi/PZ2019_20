package com.example.helikoptery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;


public class activity_gameField extends Data {

    private Point displaySize = new Point();
    public int counterr = 45;
    public Context context = this;

    private GameField myGameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_field);

        activity_gameField.scoreP1 = 0;
        activity_gameField.scoreP2 = 0;
        ViewGroup myLayout = findViewById(R.id.myLayout);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(displaySize);

        int xStartPoint = (int) (displaySize.x / 18.5528756957);
        int yStartPoint = (int) (displaySize.y / 20.4675557261);
        int cellWidth = (int) (displaySize.x / 11.12);
        int cellHeight = (int) (displaySize.y / 19.9528198946);

        myGameField = new GameField(context, myLayout, xStartPoint, yStartPoint, cellWidth, cellHeight, this);
        myGameField.drawMap();

        final TextView timer = findViewById(R.id.timer);

        new CountDownTimer(46000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.format(Locale.getDefault(), "00:%02d", counterr));
                counterr--;
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    public void goToGamePlay(View view) {
        activity_gameField.player1 = myGameField.getCells();
        Intent intent = new Intent(context, activity_gameField2.class);
        startActivity(intent);
        finish();
    }

    public void random(View view) {

        myGameField.random();
    }

    public void rotateHelicopter(View view) {
        myGameField.rotateHelicopter();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK)
            return super.onKeyDown(keyCode, event);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(context, activity_menu.class);
                        startActivity(intent);
                        finish();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.messageQuit)
                .setNegativeButton(R.string.sNo, dialogClickListener)
                .setPositiveButton(R.string.sYes, dialogClickListener)
                .show();
        return super.onKeyDown(keyCode, event);
    }
}
