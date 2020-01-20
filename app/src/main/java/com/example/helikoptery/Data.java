package com.example.helikoptery;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;

public abstract class Data extends AppCompatActivity {
    public static Cell[][] player1;
    public static Cell[][] player2;
    public static int scoreP1;
    public static int scoreP2;


    public static boolean botMode;

    class Bot{
        boolean stan;
        Point point;

    }
}
