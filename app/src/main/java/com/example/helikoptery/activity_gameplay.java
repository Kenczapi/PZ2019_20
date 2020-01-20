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
import android.view.ViewGroup;
import android.view.WindowManager;

public class activity_gameplay extends Data {

    public Cell [][] cells = player2;
    private Point displaySize = new Point();
    Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gameplay);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        ViewGroup myLayout = findViewById(R.id.myLayout2);
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(displaySize);
        int xStartPoint = (int) (displaySize.x / 18.5528756957);
        int yStartPoint = (int) (displaySize.y / 20.4675557261);
        int cellWidth = (int) (displaySize.x / 11.12);
        int cellHeight = (int) (displaySize.y / 19.9528198946);
        context = this;
        Intent intent = new Intent(this, activity_gameplay2.class);

        GameField2 myGameField = new GameField2(context, xStartPoint, yStartPoint, cellWidth, cellHeight, myLayout, cells, intent, this, 1, false);
        myGameField.print();
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
