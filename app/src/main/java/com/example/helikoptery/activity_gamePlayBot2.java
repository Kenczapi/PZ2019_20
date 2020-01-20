package com.example.helikoptery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class activity_gamePlayBot2 extends Data {

    public Cell [][] cells = player1;
    private Point displaySize = new Point();
    Context context;
    GameField2 myGameField;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_play_bot2);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ViewGroup myLayout = findViewById(R.id.myLayout2);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(displaySize);
        int xStartPoint = (int) (displaySize.x / 18.5528756957);
        int yStartPoint = (int) (displaySize.y / 20.4675557261);
        int cellWidth = (int) (displaySize.x / 11.12);
        int cellHeight = (int) (displaySize.y / 19.9528198946);
        context = this;
        Intent intent = new Intent(this, activity_gamePlayBot.class);
        myGameField = new GameField2(context, xStartPoint, yStartPoint, cellWidth, cellHeight, myLayout, cells, intent, this, 2, true);
        myGameField.print();
        startBot();
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

    public void startBot(View view) {
        myGameField.startBot();
    }

    public void startBot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myGameField.startBot();
                    }
                });

            }
        }).start();

    }
}