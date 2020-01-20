package com.example.helikoptery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

import static com.example.helikoptery.Bot.temp;

class GameField2 {
    private Cell [][] cells;
    private int xStartPoint, yStartPoint;
    private int cellWidth, cellHeight;
    private Context context;
    private ViewGroup v;
    private Intent intent;
    private Activity activity;
    private int numberPlayer;
    private boolean bot;
    private Random random = new Random();
    private boolean botMode;
    private ImageView hShot;
    private ImageView vShot;
    private Point tmp = new Point();


    GameField2(Context context, int xStartPoint, int yStartPoint, int cellWidth, int cellHeight, ViewGroup v, Cell[][] c, Intent intent, Activity activity, int numberPlayer, boolean bot) {
        this.context = context;
        this.xStartPoint = xStartPoint;
        this.yStartPoint = yStartPoint;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.v = v;
        this.activity = activity;
        this.numberPlayer = numberPlayer;
        this.cells = c;
        this.intent = intent;
        this.bot = bot;
        botMode = activity_gamePlayBot2.botMode;

        hShot = new ImageView(context);
        vShot = new ImageView(context);
    }

    private void printShot(int x, int y){
        hShot = new ImageView(context);
        vShot = new ImageView(context);

        if(x == 0 || x == 11){
            hShot.setLayoutParams(new LinearLayout.LayoutParams(4*cellWidth, cellHeight));
            hShot.setX(cells[x][3].getX());
        }
        else if(x == 1 || x == 10){
            hShot.setLayoutParams(new LinearLayout.LayoutParams(8*cellWidth, cellHeight));
            hShot.setX(cells[x][1].getX());
        }
        else{
            hShot.setLayoutParams(new LinearLayout.LayoutParams(10*cellWidth, cellHeight));
            hShot.setX(cells[x][0].getX());
        }

        hShot.setY(cells[x][y].getY());
        hShot.setBackgroundResource(R.drawable.hshot);

        if(y == 0 || y == 9){
            vShot.setLayoutParams(new LinearLayout.LayoutParams(cellWidth, 8 * cellHeight));
            vShot.setY(cells[2][y].getY());
        }
        else if(y == 1 || y == 2 || y == 7 || y == 8){
            vShot.setLayoutParams(new LinearLayout.LayoutParams(cellWidth, 10 * cellHeight));
            vShot.setY(cells[1][y].getY());
        }
        else{
            vShot.setLayoutParams(new LinearLayout.LayoutParams(cellWidth, 12 * cellHeight));
            vShot.setY(cells[0][y].getY());
        }

        vShot.setX(cells[x][y].getX());
        vShot.setBackgroundResource(R.drawable.vshot);

        v.addView(hShot);
        v.addView(vShot);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup)vShot.getParent()).removeView(vShot);
                        ((ViewGroup)hShot.getParent()).removeView(hShot);
                    }
                });


            }
        }).start();
    }

    @SuppressLint("ClickableViewAccessibility")
    void print(){
        for (int x = 0; x < 12; x++){
            for (int y = 0; y < 10; y++){
                if(!(   ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==0)   ||  ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==11)  ||  (y==0 && x == 1)  || (y==9 && x==1)  || (y == 0 && x == 10)  || (y==9 && x==10))){
                    cells[x][y].setX(xStartPoint+y*cellWidth);
                    cells[x][y].setY(yStartPoint+x*cellHeight);
                    if(!bot)
                        cells[x][y].setOnTouchListener(new ChoiceTouchListener());
                    if(cells[x][y].getParent() != null){
                        ((ViewGroup)cells[x][y].getParent()).removeView(cells[x][y]);
                    }
                    v.addView(cells[x][y]);
                }
            }
        }
        /*for (int x = 0; x < 12; x++){
            for (int y = 0; y < 10; y++){
                if (cells[x][y].getValue()>0)
                    cells[x][y].setBackgroundColor(Color.BLACK);
            }
        }*/
        for (int x = 0; x < 12; x++){
            for (int y = 0; y < 10; y++){
                if(cells[x][y].isShooted())
                    cells[x][y].setBackgroundResource(R.drawable.hit);

            }
        }
        for (int x = 0; x < 12; x++){
            for (int y = 0; y < 10; y++){
                if(cells[x][y].isShooted() && cells[x][y].getValue()>0)
                    cells[x][y].setBackgroundResource(R.drawable.kafelektrafiony);

            }
        }
    }

    void startBot(){
        Point p;
        if(!botMode)
            p = botMode1(); // Zwraca losowy punkt
        else
            p = botMode2(temp); // Tryb 2 - poprzedni strzał był trafiony

        if(cells[p.x][p.y].getValue()>0)
        {
            if(!botMode)
            {
                Bot.check = false;
                temp = p;
                Bot.position = 0;
            }

            if(botMode)
            {
                if(p.y != temp.y)
                    Bot.position = 2;
                else
                    Bot.position = 1;
            }

            Bot.last = p;

            printShot(p.x, p.y);
            botMode = true;
            activity_gameplay.scoreP2++;
            cells[p.x][p.y].setBackgroundResource(R.drawable.kafelektrafiony);
            cells[p.x][p.y].setShooted(true);
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            if(isDestroyed(p.x, p.y)){
                cells[p.x][p.y].setBackgroundResource(R.drawable.kafelektrafiony);
                printHit(p.x, p.y);
                botMode = false;
                Bot.check = false;
                Bot.position = 0;
                Bot.type = 0;
            }

            /*if (activity_gameplay2.scoreP2 == 20) {
                //AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //* AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));   //personalizacja okienka -> + w styles.xml
                //builder.setMessage("Wygral gracz 2!")
                //      .show();
                //-----------------------------------
                showAlertDialogButtonClicked();
            }*/

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startBot();
                        }
                    });

                }
            }).start();
        }
        else if(cells[p.x][p.y].getValue() == 0){

            if(!Bot.check && Bot.type != 0)
            {
                Bot.check = true;
                Bot.last = temp;
            }

            printShot(p.x, p.y);
            cells[p.x][p.y].setShooted(true);
            cells[p.x][p.y].setBackgroundResource(R.drawable.hit);
            activity_gamePlayBot2.botMode = botMode;
            activity_gamePlayBot2.player1 = cells;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activity.startActivity(intent);
                }
            }).start();
        }
    }

    private Point botMode1(){
        Point p = new Point();
        do{
            p.x = random.nextInt(12);
            p.y = random.nextInt(10);
        }while (!cells[p.x][p.y].getIsBoardField() || cells[p.x][p.y].isShooted());

        return p;
    }

    private Point botMode2(Point temp){
        Point p = new Point();

        if(Bot.position == 0)
        {
            do{
                int xxx = random.nextInt(4);
                switch (xxx){
                    case 0: if(temp.x + 1 > 11)break; p.x = temp.x + 1; p.y = temp.y;break;
                    case 1: if(temp.x - 1 < 0)break;  p.x = temp.x - 1; p.y = temp.y;break;
                    case 2: if(temp.y + 1 > 9)break;  p.x = temp.x; p.y = temp.y + 1;break;
                    case 3: if(temp.y - 1 < 0)break;  p.x = temp.x; p.y = temp.y - 1;break;
                }
            }while (!cells[p.x][p.y].getIsBoardField() || cells[p.x][p.y].isShooted());
        }
        else if(Bot.position == 1)
        {
            if(!Bot.check && (Bot.last.x > Bot.temp.x))
            {
                if((Bot.last.x+1 <= 11) && (cells[Bot.last.x + 1][Bot.last.y].getIsBoardField()) && (!cells[Bot.last.x+1][Bot.last.y].isShooted()))
                {
                    p.x = Bot.last.x + 1;
                    p.y = Bot.last.y;
                }
                else
                {
                    Bot.check = true;
                    Bot.last = Bot.temp;
                }

                Bot.type = 1;
            }
            else if(!Bot.check && (Bot.last.x < Bot.temp.x))
            {
                if((Bot.last.x-1 >= 0) && (cells[Bot.last.x - 1][Bot.last.y].getIsBoardField()) && (!cells[Bot.last.x-1][Bot.last.y].isShooted()))
                {
                    p.x = Bot.last.x - 1;
                    p.y = Bot.last.y;
                }
                else
                {
                    Bot.check = true;
                    Bot.last = Bot.temp;
                }

                Bot.type = 2;
            }
            else if(!Bot.check)
            {
                Bot.check = true;
                Bot.last = Bot.temp;
            }

            //-----------------------------------------------------------------------


            if(Bot.check && Bot.type == 2)
            {
                p.x = Bot.last.x + 1;
                p.y = Bot.last.y;
            }
            else if(Bot.check && Bot.type == 1)
            {
                p.x = Bot.last.x - 1;
                p.y = Bot.last.y;
            }
        }
        else if(Bot.position == 2)
        {
            if(!Bot.check && (Bot.last.y > Bot.temp.y))
            {
                if((Bot.last.y+1 <= 9) && (cells[Bot.last.x][Bot.last.y + 1].getIsBoardField()) && (!cells[Bot.last.x][Bot.last.y+1].isShooted()))
                {
                    p.x = Bot.last.x;
                    p.y = Bot.last.y + 1;
                }
                else
                {
                    Bot.check = true;
                    Bot.last = Bot.temp;
                }

                Bot.type = 1;
            }
            else if(!Bot.check && (Bot.last.y < Bot.temp.y))
            {
                if((Bot.last.y-1 >= 0) && (cells[Bot.last.x][Bot.last.y -1].getIsBoardField()) && (!cells[Bot.last.x][Bot.last.y-1].isShooted()))
                {
                    p.x = Bot.last.x;
                    p.y = Bot.last.y - 1;
                }
                else
                {
                    Bot.check = true;
                    Bot.last = Bot.temp;
                }

                Bot.type = 2;
            }

            //-----------------------------------------------------------------------

            if(Bot.check && Bot.type == 2)
            {
                p.x = Bot.last.x;
                p.y = Bot.last.y + 1;
            }
            else if(Bot.check && Bot.type == 1)
            {
                p.x = Bot.last.x;
                p.y = Bot.last.y - 1;
            }
        }

        return p;
    }

    public final class ChoiceTouchListener implements  View.OnTouchListener{

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = 0, y = 0;
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < 10; j++) {
                    if (cells[i][j] == v) {
                        x = i;
                        y = j;
                    }
                }
            }

            tmp.x = x;
            tmp.y = y;
            if(cells[x][y].getIsBoardField() && !cells[x][y].isShooted() && cells[x][y].getValue()>0) {
                printShot(x, y);
                if(numberPlayer == 1)
                    activity_gameplay.scoreP1++;
                else
                    activity_gameplay.scoreP2++;
                cells[x][y].setBackgroundResource(R.drawable.kafelektrafiony);
                cells[x][y].setShooted(true);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                if(isDestroyed(x, y)){
                    cells[x][y].setBackgroundResource(R.drawable.kafelektrafiony);
                    printHit(x, y);
                }
                if (activity_gameplay2.scoreP1 == 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Wygral gracz 1!")
                            .show();
                }
                else if (activity_gameplay2.scoreP2 == 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Wygral gracz 2!")
                            .show();
                }
            }
            else if(!cells[x][y].isShooted() && cells[x][y].getValue() == 0){
                printShot(x, y);
                cells[x][y].setShooted(true);
                cells[x][y].setBackgroundResource(R.drawable.hit);
                if(numberPlayer == 1)
                    activity_gameplay.player2 = cells;
                else
                    activity_gameplay2.player1 = cells;

                activity.startActivity(intent);

            }

            return false;
        }
    }

    private boolean isDestroyed(int x, int y){
        if(x-1 >= 0 && cells[x-1][y].getIsBoardField() && cells[x-1][y].getValue()>0 || x+1 < 12 && cells[x+1][y].getIsBoardField() && cells[x+1][y].getValue() > 0){
            int tmp = x-1;
            while (tmp >= 0 && cells[tmp][y].getIsBoardField() && cells[tmp][y].getValue()>0){
                if(!cells[tmp][y].isShooted())
                    return false;
                tmp--;
            }
            tmp = x+1;
            while (tmp < 12 && cells[tmp][y].getIsBoardField() && cells[tmp][y].getValue()>0){
                if(!cells[tmp][y].isShooted())
                    return false;
                tmp++;
            }
            return true;
        }
        else if(y-1 >= 0 && cells[x][y-1].getIsBoardField() && cells[x][y-1].getValue()>0 || y+1<10 && cells[x][y+1].getIsBoardField() && cells[x][y+1].getValue() > 0){
            int tmp = y-1;
            while (tmp >= 0 && cells[x][tmp].getIsBoardField() && cells[x][tmp].getValue()>0){
                if(!cells[x][tmp].isShooted())
                    return false;
                tmp--;
            }
            tmp = y+1;
            while (tmp < 10 && cells[x][tmp].getIsBoardField() && cells[x][tmp].getValue()>0){
                if(!cells[x][tmp].isShooted())
                    return false;
                tmp++;
            }

            return true;
        }
        return true;
    }

    private void printHit(int x, int y){
        if(x-1 >= 0 && cells[x-1][y].getIsBoardField() && cells[x-1][y].getValue()>0 || x+1 < 12 && cells[x+1][y].getIsBoardField() && cells[x+1][y].getValue() > 0){
            int tmp = x-1;
            if(x-1>=0){
                if(y-1>=0)
                    cells[x-1][y-1].setShooted(true);
                cells[x-1][y].setShooted(true);
                if(y+1<10)
                    cells[x-1][y+1].setShooted(true);
            }
            if(y-1>=0)
                cells[x][y-1].setShooted(true);
            if(y+1<10)
                cells[x][y+1].setShooted(true);
            if(x+1<12){
                if(y-1>=0)
                    cells[x+1][y-1].setShooted(true);
                cells[x+1][y].setShooted(true);
                if(y+1<10)
                    cells[x+1][y+1].setShooted(true);
            }
            while (tmp >= 0 && cells[tmp][y].getIsBoardField() && cells[tmp][y].getValue()>0){
                if(tmp-1>=0){
                    if(y-1>=0)
                        cells[tmp-1][y-1].setShooted(true);
                    cells[tmp-1][y].setShooted(true);
                    if(y+1<10)
                        cells[tmp-1][y+1].setShooted(true);
                }
                if(y-1>=0)
                    cells[tmp][y-1].setShooted(true);
                if(y+1<10)
                    cells[tmp][y+1].setShooted(true);
                if(tmp+1<12){
                    if(y-1>=0)
                        cells[tmp+1][y-1].setShooted(true);
                    cells[tmp+1][y].setShooted(true);
                    if(y+1<10)
                        cells[tmp+1][y+1].setShooted(true);
                }
                tmp--;
            }
            tmp = x+1;
            while (tmp < 12 && cells[tmp][y].getIsBoardField() && cells[tmp][y].getValue()>0){
                if(tmp-1>=0){
                    if(y-1>=0)
                        cells[tmp-1][y-1].setShooted(true);
                    cells[tmp-1][y].setShooted(true);
                    if(y+1<10)
                        cells[tmp-1][y+1].setShooted(true);
                }
                if(y-1>=0)
                    cells[tmp][y-1].setShooted(true);
                if(y+1<10)
                    cells[tmp][y+1].setShooted(true);
                if(tmp+1<12){
                    if(y-1>=0)
                        cells[tmp+1][y-1].setShooted(true);
                    cells[tmp+1][y].setShooted(true);
                    if(y+1<10)
                        cells[tmp+1][y+1].setShooted(true);
                }
                tmp++;
            }
        }
        else if(y-1 >= 0 && cells[x][y-1].getIsBoardField() && cells[x][y-1].getValue()>0 || y+1<10 && cells[x][y+1].getIsBoardField() && cells[x][y+1].getValue() > 0){
            int tmp = y-1;
            if(x-1>=0){
                if(y-1>=0)
                    cells[x-1][y-1].setShooted(true);
                cells[x-1][y].setShooted(true);
                if(y+1<10)
                    cells[x-1][y+1].setShooted(true);
            }
            if(y-1>=0)
                cells[x][y-1].setShooted(true);
            if(y+1<10)
                cells[x][y+1].setShooted(true);
            if(x+1<12){
                if(y-1>=0)
                    cells[x+1][y-1].setShooted(true);
                cells[x+1][y].setShooted(true);
                if(y+1<10)
                    cells[x+1][y+1].setShooted(true);
            }
            while (tmp >= 0 && cells[x][tmp].getIsBoardField() && cells[x][tmp].getValue()>0){
                if(x-1>=0){
                    if(tmp-1>=0)
                        cells[x-1][tmp-1].setShooted(true);
                    cells[x-1][tmp].setShooted(true);
                    if(tmp+1<10)
                        cells[x-1][tmp+1].setShooted(true);
                }
                if(tmp-1>=0)
                    cells[x][tmp-1].setShooted(true);
                if(tmp+1<10)
                    cells[x][tmp+1].setShooted(true);
                if(x+1<12){
                    if(tmp-1>=0)
                        cells[x+1][tmp-1].setShooted(true);
                    cells[x+1][tmp].setShooted(true);
                    if(tmp+1<10)
                        cells[x+1][tmp+1].setShooted(true);
                }
                tmp--;
            }
            tmp = y+1;
            while (tmp < 10 && cells[x][tmp].getIsBoardField() && cells[x][tmp].getValue()>0){
                if(x-1>=0){
                    if(tmp-1>=0)
                        cells[x-1][tmp-1].setShooted(true);
                    cells[x-1][tmp].setShooted(true);
                    if(tmp+1<10)
                        cells[x-1][tmp+1].setShooted(true);
                }
                if(tmp-1>=0)
                    cells[x][tmp-1].setShooted(true);
                if(tmp+1<10)
                    cells[x][tmp+1].setShooted(true);
                if(x+1<12){
                    if(tmp-1>=0)
                        cells[x+1][tmp-1].setShooted(true);
                    cells[x+1][tmp].setShooted(true);
                    if(tmp+1<10)
                        cells[x+1][tmp+1].setShooted(true);
                }
                tmp++;
            }
        }
        else {
            if(x-1>=0){
                if(y-1>=0)
                    cells[x-1][y-1].setShooted(true);
                cells[x-1][y].setShooted(true);
                if(y+1<10)
                    cells[x-1][y+1].setShooted(true);
            }
            if(y-1>=0)
                cells[x][y-1].setShooted(true);
            if(y+1<10)
                cells[x][y+1].setShooted(true);
            if(x+1<12){
                if(y-1>=0)
                    cells[x+1][y-1].setShooted(true);
                cells[x+1][y].setShooted(true);
                if(y+1<10)
                    cells[x+1][y+1].setShooted(true);
            }
        }
        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 10; j++){
                if(cells[i][j].isShooted())
                    cells[i][j].setBackgroundResource(R.drawable.hit);

            }
        }for (int i = 0; i < 12; i++){
            for (int j = 0; j < 10; j++){
                if(cells[i][j].isShooted() && cells[i][j].getValue()>0)
                    cells[i][j].setBackgroundResource(R.drawable.kafelektrafiony);

            }
        }

    }
}
