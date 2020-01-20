package com.example.helikoptery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Vector;

class GameField {
    private Helicopter tmpHelicopterHorizontal1;
    private Helicopter tmpHelicopterHorizontal2;
    private Helicopter tmpHelicopterHorizontal3;
    private Helicopter tmpHelicopterHorizontal4;
    private Helicopter tmpHelicopterVertical1;
    private Helicopter tmpHelicopterVertical2;
    private Helicopter tmpHelicopterVertical3;
    private Helicopter tmpHelicopterVertical4;
    private Cell [][] cells = new Cell[12][10];
    private Cell [][] cellsBot = new Cell[12][10];
    private int xStartPoint, yStartPoint;
    private int cellWidth, cellHeight;
    public Context context;
    private final int ROTATE_DURATION = 800;
    private final int MOVE_DURATION = 800;
    private MyThread myThreadMove;
    private MyThread myThreadRotate;
    private boolean isAnimated = false;
    private ViewGroup v;
    private int helicopterCounter = 0;
    private Vector<Helicopter> allHelicopters = new Vector<>();
    private Random random = new Random();
    private Helicopter buffer;
    private Activity activity;

    GameField(Context context, ViewGroup v, int xStartPoint, int yStartPoint, int cellWidth, int cellHeight, Activity activity) {
        this.context = context;
        this.xStartPoint = xStartPoint;
        this.yStartPoint = yStartPoint;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.activity = activity;
        this.v = v;
        tmpHelicopterHorizontal1 = new Helicopter2(context, 1, cellWidth, cellHeight); tmpHelicopterHorizontal1.setX(-2000); v.addView(tmpHelicopterHorizontal1);
        tmpHelicopterHorizontal2 = new Helicopter2(context, 2, cellWidth, cellHeight); tmpHelicopterHorizontal2.setX(-2000); v.addView(tmpHelicopterHorizontal2);
        tmpHelicopterHorizontal3 = new Helicopter2(context, 3, cellWidth, cellHeight); tmpHelicopterHorizontal3.setX(-2000); v.addView(tmpHelicopterHorizontal3);
        tmpHelicopterHorizontal4 = new Helicopter2(context, 4, cellWidth, cellHeight); tmpHelicopterHorizontal4.setX(-2000); v.addView(tmpHelicopterHorizontal4);
        tmpHelicopterVertical1 = new Helicopter(context, 1, cellWidth, cellHeight, true); tmpHelicopterVertical1.setX(-2000); v.addView(tmpHelicopterVertical1);
        tmpHelicopterVertical2 = new Helicopter(context, 2, cellWidth, cellHeight, true); tmpHelicopterVertical2.setX(-2000); v.addView(tmpHelicopterVertical2);
        tmpHelicopterVertical3 = new Helicopter(context, 3, cellWidth, cellHeight, true); tmpHelicopterVertical3.setX(-2000); v.addView(tmpHelicopterVertical3);
        tmpHelicopterVertical4 = new Helicopter(context, 4, cellWidth, cellHeight, true); tmpHelicopterVertical4.setX(-2000); v.addView(tmpHelicopterVertical4);
        myThreadMove = new MyThread(MOVE_DURATION);
        myThreadRotate = new MyThread(ROTATE_DURATION);
    }

    Cell[][] getCells(){
        Cell [][] cellsReturn = new Cell[12][10];
        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 10; j++){
                cellsReturn[i][j] = new Cell(context, cellWidth, cellHeight);
                cellsReturn[i][j].setShooted(cells[i][j].isShooted());
                cellsReturn[i][j].setValue(cells[i][j].getValue());
                cellsReturn[i][j].setIsBoardField(cells[i][j].getIsBoardField());
            }
        }
        return cellsReturn;
    }
    Cell[][] getCellsBot(){
        Cell [][] cellsReturn = new Cell[12][10];
        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 10; j++){
                cellsReturn[i][j] = new Cell(context, cellWidth, cellHeight);
                cellsReturn[i][j].setShooted(cellsBot[i][j].isShooted());
                cellsReturn[i][j].setValue(cellsBot[i][j].getValue());
                cellsReturn[i][j].setIsBoardField(cellsBot[i][j].getIsBoardField());
            }
        }
        return cellsReturn;
    }

    void drawMap(){
        for (int x = 0; x < 12; x++){
            for (int y = 0; y < 10; y++){
                cells[x][y] = new Cell(context, cellWidth, cellHeight);
                if(!(   ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==0)   ||  ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==11)  ||  (y==0 && x == 1)  || (y==9 && x==1)  || (y == 0 && x == 10)  || (y==9 && x==10))){
                    cells[x][y].setX(xStartPoint+y*cellWidth);
                    cells[x][y].setY(yStartPoint+x*cellHeight);
                    cells[x][y].setIsBoardField(true);
                    cells[x][y].setOnDragListener(dragListener);
                    v.addView(cells[x][y]);
                }
            }
        }
        createHelicopter();
    }

    void randomBot(){
        for (int x = 0; x < 12; x++){
            for (int y = 0; y < 10; y++){
                cellsBot[x][y] = new Cell(context, cellWidth, cellHeight);
                if(!(   ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==0)   ||  ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==11)  ||  (y==0 && x == 1)  || (y==9 && x==1)  || (y == 0 && x == 10)  || (y==9 && x==10))){
                    cellsBot[x][y].setX(xStartPoint+y*cellWidth);
                    cellsBot[x][y].setY(yStartPoint+x*cellHeight);
                    cellsBot[x][y].setIsBoardField(true);
                }
            }
        }

        do {
            int x = random.nextInt(12);
            int y = random.nextInt(10);
            boolean or = random.nextBoolean();

            if(areaIsBusyBot1(x, y, 4, or, cellsBot)){
                setCallCounterBot(x, y, 4, or, cellsBot);
                break;
            }
        }while (true);

        for(int i = 0; i < 2; i++){
            do {
                int x = random.nextInt(12);
                int y = random.nextInt(10);
                boolean or = random.nextBoolean();

                if(areaIsBusyBot1(x, y, 3, or, cellsBot)){
                    setCallCounterBot(x, y, 3, or, cellsBot);
                    break;
                }
            }while (true);
        }

        for(int i = 0; i < 3; i++){
            do {
                int x = random.nextInt(12);
                int y = random.nextInt(10);
                boolean or = random.nextBoolean();

                if(areaIsBusyBot1(x, y, 2, or, cellsBot)){
                    setCallCounterBot(x, y, 2, or, cellsBot);
                    break;
                }
            }while (true);
        }

        for(int i = 0; i < 4; i++){
            do {
                int x = random.nextInt(12);
                int y = random.nextInt(10);
                boolean or = random.nextBoolean();

                if(areaIsBusyBot1(x, y, 1, or, cellsBot)){
                    setCallCounterBot(x, y, 1, or, cellsBot);
                    break;
                }
            }while (true);
        }


    }

    void random() {

        if (isAnimated)
            return;
        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 10; j++){
                cells[i][j].setValue(0);
            }
        }

        class myPoint{
            private int x;

            private int y;

            private boolean rot;

            private myPoint(int x, int y, boolean rot){
                this.x = x;
                this.y = y;
                this.rot = rot;
            }

        }

        Vector<myPoint> myPoints = new Vector<>();

        cellHighlightOffAll();

        do {
            int x = random.nextInt(12);
            int y = random.nextInt(10);
            boolean or = random.nextBoolean();

            if(areaIsBusyBot1(x, y, 4, or, cells)){
                setCallCounterBot(x, y, 4, or, cells);
                myPoints.add(new myPoint(x, y, or));

                break;
            }
        }while (true);

        for(int i = 0; i < 2; i++){
            do {
                int x = random.nextInt(12);
                int y = random.nextInt(10);
                boolean or = random.nextBoolean();

                if(areaIsBusyBot1(x, y, 3, or, cells)){
                    setCallCounterBot(x, y, 3, or, cells);
                    myPoints.add(new myPoint(x, y, or));

                    break;
                }
            }while (true);
        }

        for(int i = 0; i < 3; i++){
            do {
                int x = random.nextInt(12);
                int y = random.nextInt(10);
                boolean or = random.nextBoolean();

                if(areaIsBusyBot1(x, y, 2, or, cells)){
                    setCallCounterBot(x, y, 2, or, cells);
                    myPoints.add(new myPoint(x, y, or));

                    break;
                }
            }while (true);
        }

        for(int i = 0; i < 4; i++){
            do {
                int x = random.nextInt(12);
                int y = random.nextInt(10);
                boolean or = random.nextBoolean();

                if(areaIsBusyBot1(x, y, 1, or, cells)){
                    setCallCounterBot(x, y, 1, or, cells);
                    myPoints.add(new myPoint(x, y, or));

                    break;
                }
            }while (true);
        }

        while (helicopterCounter < 10)
            createHelicopter();


        for(int i = 0; i < 10; i++){
            Helicopter h = allHelicopters.elementAt(i);
            myPoint p = myPoints.remove(0);
            int rotation;
            if(p.rot) rotation = 0;
            else  rotation = 90;
            h.setPositionOnBoard(new Point(p.x, p.y));
            h.setOnBoard(true);
            h.setOrientation(p.rot);

            if(p.rot){
                if(h.getSize() == 4){
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth))
                            .y((int)(cells[p.x][p.y].getY()-2*cellHeight))
                            .rotation(rotation)
                            .start();
                }
                else if(h.getSize() == 3){
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth))
                            .y(cells[p.x][p.y].getY()-cellHeight)
                            .rotation(rotation)
                            .start();
                }
                else if(h.getSize() == 2){
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth))
                            .y(cells[p.x][p.y].getY()-cellHeight)
                            .rotation(rotation)
                            .start();
                }
                else {
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth))
                            .y((int)(cells[p.x][p.y].getY()))
                            .rotation(rotation)
                            .start();
                }
            }
            else {
                if(h.getSize() == 4){
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x(cells[p.x][p.y].getX())
                            .y((int)(cells[p.x][p.y].getY()-1.5*cellHeight))
                            .rotation(rotation)
                            .start();
                }
                else if(h.getSize() == 3){
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth))
                            .y((int)(cells[p.x][p.y].getY()-cellHeight))
                            .rotation(rotation)
                            .start();
                }
                else if(h.getSize() == 2){
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth+0.5*cellWidth))
                            .y((int)(cells[p.x][p.y].getY()-0.5*cellHeight))
                            .rotation(rotation)
                            .start();
                }
                else {
                    h.animate()
                            .setDuration(MOVE_DURATION)
                            .x((int)(cells[p.x][p.y].getX()-0.5*cellWidth))
                            .y((int)(cells[p.x][p.y].getY()))
                            .rotation(rotation)
                            .start();
                }

            }

        }


        new Thread(myThreadMove).start();
    }

    public void rotateHelicopter() {
        if(isAnimated)
            return;
        final Helicopter buffor2 = buffer;
        if(buffor2.getOrientation()){
            if (buffor2.getSize()%2 == 1){
                if(buffor2.getOnBoard()){
                    animateObject2(buffor2, ROTATE_DURATION, 0, 0, 90);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(ROTATE_DURATION-200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOnRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                }
                            });
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ColorDrawable colorCell = (ColorDrawable) cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y].getBackground();
                            if(colorCell.getColor() == Color.RED) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateObject(buffor2, ROTATE_DURATION, 0, 0, 0);
                                    }
                                });
                            }
                            else {
                                isAnimated = false;
                                setCallCounterRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                buffor2.setOrientation(!buffor2.getOrientation());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOffAll();
                                }
                            });
                        }
                    }).start();
                }
                else {
                    animateObject(buffor2, ROTATE_DURATION, 0, 0, 90);
                    buffor2.setOrientation(!buffor2.getOrientation());
                }
            }
            else {
                if(buffor2.getOnBoard()){
                    animateObject2(buffor2, ROTATE_DURATION, (int) (buffor2.getX()+ 0.5*cellWidth), (int) (buffor2.getY()+ 0.5*cellHeight), 90);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(ROTATE_DURATION-200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOnRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                }
                            });
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ColorDrawable colorCell = (ColorDrawable) cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y].getBackground();
                            if(colorCell.getColor() == Color.RED) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateObject(buffor2, ROTATE_DURATION, (int) (buffor2.getX() - 0.5 * cellWidth), (int) (buffor2.getY() - 0.5 * cellHeight), 0);
                                    }
                                });
                            }
                            else {
                                isAnimated = false;
                                setCallCounterRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                buffor2.setOrientation(!buffor2.getOrientation());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOffAll();
                                }
                            });
                        }
                    }).start();
                }
                else {
                    animateObject(buffor2, ROTATE_DURATION, 0, 0, 90);
                    buffor2.setOrientation(!buffor2.getOrientation());
                }
            }


        }
        else {
            if (buffor2.getSize()%2 == 1) {
                if(buffor2.getOnBoard()){
                    animateObject2(buffor2, ROTATE_DURATION, 0, 0, 0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(ROTATE_DURATION-200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOnRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                }
                            });
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ColorDrawable colorCell = (ColorDrawable) cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y].getBackground();
                            if(colorCell.getColor() == Color.RED) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateObject(buffor2, ROTATE_DURATION, 0,0, 90);
                                    }
                                });
                            }
                            else {
                                isAnimated = false;
                                setCallCounterRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                buffor2.setOrientation(!buffor2.getOrientation());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOffAll();
                                }
                            });
                        }
                    }).start();
                }
                else {
                    animateObject(buffor2, ROTATE_DURATION, 0, 0, 0);
                    buffor2.setOrientation(!buffor2.getOrientation());
                }
            }
            else {
                if(buffor2.getOnBoard()){
                    animateObject2(buffor2, ROTATE_DURATION, (int) (buffor2.getX()- 0.5*cellWidth), (int) (buffor2.getY()- 0.5*cellHeight), 0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(ROTATE_DURATION-200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOnRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                }
                            });
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ColorDrawable colorCell = (ColorDrawable) cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y].getBackground();
                            if(colorCell.getColor() == Color.RED) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateObject(buffor2, ROTATE_DURATION, (int) (buffor2.getX() + 0.5 * cellWidth), (int) (buffor2.getY() + 0.5 * cellHeight), 90);
                                    }
                                });
                            }
                            else {
                                isAnimated = false;
                                setCallCounterRotate(cells[buffor2.getPositionOnBoard().x][buffor2.getPositionOnBoard().y], buffor2);
                                buffor2.setOrientation(!buffor2.getOrientation());
                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cellHighlightOffAll();
                                }
                            });
                        }
                    }).start();
                }
                else {
                    animateObject(buffor2, ROTATE_DURATION, 0, 0, 0);
                    buffor2.setOrientation(!buffor2.getOrientation());
                }
            }
        }



    }

    private final class ChoiceTouchListener implements  View.OnTouchListener{

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            buffer = (Helicopter)v;
            Helicopter tmp = null;
            if(buffer.getOrientation()){
                if(buffer.getSize() == 1) tmp = tmpHelicopterVertical1;
                else if(buffer.getSize() == 2) tmp = tmpHelicopterVertical2;
                else if(buffer.getSize() == 3) tmp = tmpHelicopterVertical3;
                else if(buffer.getSize() == 4) tmp = tmpHelicopterVertical4;
            }
            else {
                if(buffer.getSize() == 1) tmp = tmpHelicopterHorizontal1;
                else if(buffer.getSize() == 2) tmp = tmpHelicopterHorizontal2;
                else if(buffer.getSize() == 3) tmp = tmpHelicopterHorizontal3;
                else if(buffer.getSize() == 4) tmp = tmpHelicopterHorizontal4;
            }

            Bitmap bitmap;

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(tmp);

            v.startDrag(data, myShadowBuilder, v, 0);
            return false;
        }

    }

    private void cellHighlightOffAll(){

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j].setBackgroundColor(Color.TRANSPARENT);
            }
        }

    }

    private void cellHighlightOn(Cell cell, Helicopter h) {

        if(h == null) return;
        int size = h.getSize();
        boolean orientation = h.getOrientation();
        int x = 0, y = 0;

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                if (cells[i][j] == cell) {
                    x = i;
                    y = j;
                }
            }
        }

        if (orientation) {
            switch (size) {
                case 1:
                    if(!areaIsBusy(x, y, h)){
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 2:
                    if((x-1 >=0 && cells[x-1][y].getIsBoardField()) && !(areaIsBusy(x, y, h))){
                        cells[x-1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (x-1 >=0)
                            cells[x-1][y].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 3:
                    if((x+1 < 12 && cells[x+1][y].getIsBoardField())&&(x-1 >=0 && cells[x-1][y].getIsBoardField()) && !(areaIsBusy(x, y, h))){
                        cells[x-1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x+1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (x-1 >=0)
                            cells[x-1][y].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (x+1<12)
                            cells[x+1][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 4:
                    if((x+1 < 12 && cells[x+1][y].getIsBoardField())&&(x-2 >=0 && cells[x-2][y].getIsBoardField()) && !(areaIsBusy(x, y, h))){
                        cells[x-2][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x-1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x+1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (x-2 >=0)
                            cells[x-2][y].setBackgroundColor(Color.RED);
                        if (x-1 >=0)
                            cells[x-1][y].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (x+1<12)
                            cells[x+1][y].setBackgroundColor(Color.RED);
                    }
                    break;
            }
        } else {
            switch (size) {
                case 1:
                    if(!areaIsBusy(x, y, h)){
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 2:
                    if((y+1 < 10 && cells[x][y+1].getIsBoardField()) && !(areaIsBusy(x, y, h))){
                        cells[x][y+1].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (y+1 < 10)
                            cells[x][y+1].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 3:
                    if((y-1 >=0 && cells[x][y-1].getIsBoardField())&&(y+1 < 10 && cells[x][y+1].getIsBoardField()) && !(areaIsBusy(x, y, h))){
                        cells[x][y+1].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y-1].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (y+1 < 10)
                            cells[x][y+1].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (y-1 >= 0)
                            cells[x][y-1].setBackgroundColor(Color.RED);
                    }
                    break;
                case 4:
                    if((y-1 >=0 && cells[x][y-1].getIsBoardField())&&(y+2 < 10 && cells[x][y+2].getIsBoardField()) && !(areaIsBusy(x, y, h))){
                        cells[x][y+2].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y+1].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y-1].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (y+2 < 10)
                            cells[x][y+2].setBackgroundColor(Color.RED);
                        if (y+1 < 10)
                            cells[x][y+1].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (y-1 >= 0)
                            cells[x][y-1].setBackgroundColor(Color.RED);
                    }
                    break;
            }

        }
    }

    private void cellHighlightOff(Cell cell, Helicopter h, boolean orientation){

        if(h == null) return;
        int size = h.getSize();
        int x = 0, y = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                if (cells[i][j] == cell) {
                    x = i;
                    y = j;
                }
            }
        }


        if (orientation) {
            switch (size) {
                case 1:
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case 2:
                    if (x-1 >=0)
                        cells[x-1][y].setBackgroundColor(Color.TRANSPARENT);
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case 3:
                    if (x-1 >=0)
                        cells[x-1][y].setBackgroundColor(Color.TRANSPARENT);
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    if (x+1<12)
                        cells[x+1][y].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case 4:
                    if (x-2 >=0)
                        cells[x-2][y].setBackgroundColor(Color.TRANSPARENT);
                    if (x-1 >=0)
                        cells[x-1][y].setBackgroundColor(Color.TRANSPARENT);
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    if (x+1<12)
                        cells[x+1][y].setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
        } else {
            switch (size) {
                case 1:
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case 2:
                    if (y+1 < 10)
                        cells[x][y+1].setBackgroundColor(Color.TRANSPARENT);
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case 3:
                    if (y+1 < 10)
                        cells[x][y+1].setBackgroundColor(Color.TRANSPARENT);
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    if (y-1 >= 0)
                        cells[x][y-1].setBackgroundColor(Color.TRANSPARENT);
                    break;
                case 4:
                    if (y+2 < 10)
                        cells[x][y+2].setBackgroundColor(Color.TRANSPARENT);
                    if (y+1 < 10)
                        cells[x][y+1].setBackgroundColor(Color.TRANSPARENT);
                    cells[x][y].setBackgroundColor(Color.TRANSPARENT);
                    if (y-1 >= 0)
                        cells[x][y-1].setBackgroundColor(Color.TRANSPARENT);
                    break;
            }

        }
    }

    private void setCallCounter(Cell cell, Helicopter h){

        if(h == null) return;
        int size = h.getSize();
        boolean orientation = h.getOrientation();
        Point point = h.getPositionOnBoard();
        int x = 0, y = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                if (cells[i][j] == cell) {
                    x = i;
                    y = j;
                }
            }
        }


        if (orientation) {
            switch (size) {
                case 1:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 2:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x-1][point.y].setValue(0);
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 3:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x-1][point.y].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x+1][point.y].setValue(0);
                    }
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x+1][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 4:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x-2][point.y].setValue(0);
                        cells[point.x-1][point.y].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x+1][point.y].setValue(0);
                    }
                    cells[x-2][y].setValue(size);
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x+1][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
            }
        } else {
            switch (size) {
                case 1:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 2:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y+1].setValue(0);
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 3:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y+1].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x][point.y-1].setValue(0);
                    }
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x][y-1].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 4:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y+2].setValue(0);
                        cells[point.x][point.y+1].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x][point.y-1].setValue(0);
                    }
                    cells[x][y+2].setValue(size);
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x][y-1].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
            }

        }
    }

    private boolean areaIsBusy(int x, int y, Helicopter h) {

        if(h == null) return false;
        int size = h.getSize();
        boolean orientation = h.getOrientation();

        if (orientation) {
            switch (size) {

                case 1:
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }

                    return false;

                case 2:
                    for(int i = -2; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] ||  cells[x+i][y+j] == cells[h.getPositionOnBoard().x-1][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }

                    return false;

                case 3:
                    for(int i = -2; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] ||  cells[x+i][y+j] == cells[h.getPositionOnBoard().x-1][h.getPositionOnBoard().y] || cells[x+i][y+j] == cells[h.getPositionOnBoard().x+1][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 4:
                    for(int i = -3; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] ||  cells[x+i][y+j] == cells[h.getPositionOnBoard().x-1][h.getPositionOnBoard().y] || cells[x+i][y+j] == cells[h.getPositionOnBoard().x+1][h.getPositionOnBoard().y] || cells[x+i][y+j] == cells[h.getPositionOnBoard().x-2][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
            }
        }
        else {
            switch (size) {
                case 1:
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 2:
                    for(int i = -1; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 3:

                    for(int i = -2; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y-1]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 4:

                    for(int i = -2; i <= 3; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y-1] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+2]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
            }

        }
        return false;
    }

    private void cellHighlightOnRotate(Cell cell, Helicopter h) {

        if(h == null) return;
        boolean orientation = !h.getOrientation();
        int size = h.getSize();
        int x = 0, y = 0;

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                if (cells[i][j] == cell) {
                    x = i;
                    y = j;
                }
            }
        }

        if (orientation) {
            switch (size) {
                case 1:
                    if(!areaIsBusyRotate(x, y, h)){
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 2:
                    if((x-1 >=0 && cells[x-1][y].getIsBoardField()) && !(areaIsBusyRotate(x, y, h))){
                        cells[x-1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (x-1 >=0)
                            cells[x-1][y].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 3:
                    if((x+1 < 12 && cells[x+1][y].getIsBoardField())&&(x-1 >=0 && cells[x-1][y].getIsBoardField()) && !(areaIsBusyRotate(x, y, h))){
                        cells[x-1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x+1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (x-1 >=0)
                            cells[x-1][y].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (x+1<12)
                            cells[x+1][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 4:
                    if((x+1 < 12 && cells[x+1][y].getIsBoardField())&&(x-2 >=0 && cells[x-2][y].getIsBoardField()) && !(areaIsBusyRotate(x, y, h))){
                        cells[x-2][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x-1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x+1][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (x-2 >=0)
                            cells[x-2][y].setBackgroundColor(Color.RED);
                        if (x-1 >=0)
                            cells[x-1][y].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (x+1<12)
                            cells[x+1][y].setBackgroundColor(Color.RED);
                    }
                    break;
            }
        } else {
            switch (size) {
                case 1:
                    if(!areaIsBusyRotate(x, y, h)){
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 2:
                    if((y+1 < 10 && cells[x][y+1].getIsBoardField()) && !(areaIsBusyRotate(x, y, h))){
                        cells[x][y+1].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (y+1 < 10)
                            cells[x][y+1].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                    }
                    break;
                case 3:
                    if((y-1 >=0 && cells[x][y-1].getIsBoardField())&&(y+1 < 10 && cells[x][y+1].getIsBoardField()) && !(areaIsBusyRotate(x, y, h))){
                        cells[x][y+1].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y-1].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (y+1 < 10)
                            cells[x][y+1].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (y-1 >= 0)
                            cells[x][y-1].setBackgroundColor(Color.RED);
                    }
                    break;
                case 4:
                    if((y-1 >=0 && cells[x][y-1].getIsBoardField())&&(y+2 < 10 && cells[x][y+2].getIsBoardField()) && !(areaIsBusyRotate(x, y, h))){
                        cells[x][y+2].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y+1].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y].setBackgroundColor(Color.parseColor("#99ff33"));
                        cells[x][y-1].setBackgroundColor(Color.parseColor("#99ff33"));
                    }
                    else {
                        if (y+2 < 10)
                            cells[x][y+2].setBackgroundColor(Color.RED);
                        if (y+1 < 10)
                            cells[x][y+1].setBackgroundColor(Color.RED);
                        cells[x][y].setBackgroundColor(Color.RED);
                        if (y-1 >= 0)
                            cells[x][y-1].setBackgroundColor(Color.RED);
                    }
                    break;
            }

        }
    }

    private void setCallCounterRotate(Cell cell, Helicopter h){
        if(h == null) return;
        int size = h.getSize();
        boolean orientation = h.getOrientation();
        Point point = h.getPositionOnBoard();
        int x = 0, y = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                if (cells[i][j] == cell) {
                    x = i;
                    y = j;
                }
            }
        }


        if (orientation) {
            switch (size) {
                case 1:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 2:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x-1][point.y].setValue(0);
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);


                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 3:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x-1][point.y].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x+1][point.y].setValue(0);
                    }
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x][y-1].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 4:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x-2][point.y].setValue(0);
                        cells[point.x-1][point.y].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x+1][point.y].setValue(0);
                    }
                    cells[x][y+2].setValue(size);
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x][y-1].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
            }
        } else {
            switch (size) {
                case 1:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 2:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y+1].setValue(0);
                        cells[point.x][point.y].setValue(0);
                    }
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 3:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y+1].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x][point.y-1].setValue(0);
                    }
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x+1][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
                case 4:
                    if (point.x > -1 && point.y > -1){
                        cells[point.x][point.y+2].setValue(0);
                        cells[point.x][point.y+1].setValue(0);
                        cells[point.x][point.y].setValue(0);
                        cells[point.x][point.y-1].setValue(0);
                    }
                    cells[x-2][y].setValue(size);
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x+1][y].setValue(size);
                    h.setPositionOnBoard(new Point(x, y));
                    break;
            }

        }
    }

    private boolean areaIsBusyRotate(int x, int y, Helicopter h) {

        if(h == null) return false;
        int size = h.getSize();
        boolean orientation = !h.getOrientation();

        if (orientation) {
            switch (size) {

                case 1:
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }

                    return false;

                case 2:
                    for(int i = -2; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] ||  cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }

                    return false;

                case 3:
                    for(int i = -2; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] ||  cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1] || cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y-1]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 4:
                    for(int i = -3; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] ||  cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1] || cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y-1] || cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y+1]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
            }
        }
        else {
            switch (size) {
                case 1:
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+i][y+j] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 2:
                    for(int i = -1; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x-1][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 3:

                    for(int i = -2; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x-1][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x+1][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;

                case 4:

                    for(int i = -2; i <= 3; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                if(h.getOnBoard()){
                                    if(!(cells[x+j][y+i] == cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x-1][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x+1][h.getPositionOnBoard().y] || cells[x+j][y+i] == cells[h.getPositionOnBoard().x-2][h.getPositionOnBoard().y]))
                                        return true;
                                }else {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
            }

        }
        return false;
    }

    private boolean areaIsBusyBot1(int x, int y, int size, boolean orientation, Cell[][] cells) {

        if(   ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==0)   ||  ((y==0||y==1||y==2||y==7||y==8||y==9)&&x==11)  ||  (y==0 && x == 1)  || (y==9 && x==1)  || (y == 0 && x == 10)  || (y==9 && x==10))
            return false;


        if (orientation) {
            switch (size) {
                case 1:
                    if(!areaIsBusyBot2(x, y, size, orientation, cells)){
                        return true;
                    }
                    else {
                        return false;
                    }
                case 2:
                    if((x-1 >=0 && cells[x-1][y].getIsBoardField()) && !(areaIsBusyBot2(x, y, size, orientation, cells))){
                        return true;
                    }
                    else {
                        return false;
                    }
                case 3:
                    if((x+1 < 12 && cells[x+1][y].getIsBoardField())&&(x-1 >=0 && cells[x-1][y].getIsBoardField()) && !(areaIsBusyBot2(x, y, size, orientation, cells))){
                        return true;
                    }
                    else {
                        return false;
                    }
                case 4:
                    if((x+1 < 12 && cells[x+1][y].getIsBoardField())&&(x-2 >=0 && cells[x-2][y].getIsBoardField()) && !(areaIsBusyBot2(x, y, size, orientation, cells))){
                        return true;
                    }
                    else {
                        return false;
                    }
            }
        } else {
            switch (size) {
                case 1:
                    if(!areaIsBusyBot2(x, y, size, orientation, cells)){
                        return true;
                    }
                    else {
                        return false;
                    }
                case 2:
                    if((y+1 < 10 && cells[x][y+1].getIsBoardField()) && !(areaIsBusyBot2(x, y, size, orientation, cells))){
                        return true;
                    }
                    else {
                        return false;
                    }
                case 3:
                    if((y-1 >=0 && cells[x][y-1].getIsBoardField())&&(y+1 < 10 && cells[x][y+1].getIsBoardField()) && !(areaIsBusyBot2(x, y, size, orientation, cells))){
                        return true;
                    }
                    else {
                        return false;
                    }
                case 4:
                    if((y-1 >=0 && cells[x][y-1].getIsBoardField())&&(y+2 < 10 && cells[x][y+2].getIsBoardField()) && !(areaIsBusyBot2(x, y, size, orientation, cells))){
                        return true;
                    }
                    else {
                        return false;
                    }
            }

        }
        return false;
    }

    private boolean areaIsBusyBot2(int x, int y, int size, boolean orientation, Cell[][] cells) {

        if (orientation) {
            switch (size) {

                case 1:
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                return true;
                            }
                        }
                    }

                    return false;

                case 2:
                    for(int i = -2; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                return true;
                            }
                        }
                    }

                    return false;

                case 3:
                    for(int i = -2; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                return true;
                            }
                        }
                    }
                    return false;

                case 4:
                    for(int i = -3; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                return true;
                            }
                        }
                    }
                    return false;
            }
        }
        else {
            switch (size) {
                case 1:
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+i >= 0 && x+i < 12 && y+j >= 0 && y+j < 10 && cells[x+i][y+j].getValue()>0){
                                return true;
                            }
                        }
                    }
                    return false;

                case 2:
                    for(int i = -1; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                return true;
                            }
                        }
                    }
                    return false;

                case 3:

                    for(int i = -2; i <= 2; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                return true;
                            }
                        }
                    }
                    return false;

                case 4:

                    for(int i = -2; i <= 3; i++){
                        for (int j = -1; j <= 1; j++){
                            if(x+j >= 0 && x+j < 12 && y+i >= 0 && y+i < 10 && cells[x+j][y+i].getValue()>0){
                                return true;
                            }
                        }
                    }
                    return false;
            }

        }
        return false;
    }

    private void setCallCounterBot(int x, int y, int size, boolean orientation, Cell[][] cells){

        if (orientation) {
            switch (size) {
                case 1:
                    cells[x][y].setValue(size);
                    break;
                case 2:
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    break;
                case 3:
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x+1][y].setValue(size);
                    break;
                case 4:
                    cells[x-2][y].setValue(size);
                    cells[x-1][y].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x+1][y].setValue(size);
                    break;
            }
        } else {
            switch (size) {
                case 1:
                    cells[x][y].setValue(size);
                    break;
                case 2:
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    break;
                case 3:
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x][y-1].setValue(size);
                    break;
                case 4:
                    cells[x][y+2].setValue(size);
                    cells[x][y+1].setValue(size);
                    cells[x][y].setValue(size);
                    cells[x][y-1].setValue(size);
                    break;
            }

        }
    }

    private void animateObject(View view, int duration, int x, int y, int rotation){
        if(x == 0 && y == 0){
            view.animate()
                    .setDuration(duration)
                    .rotation(rotation)
                    .start();
        }
        else if(x == 0){
            view.animate()
                    .setDuration(duration)
                    .y(y)
                    .rotation(rotation)
                    .start();
        }
        else if(y == 0){
            view.animate()
                    .setDuration(duration)
                    .x(x)
                    .rotation(rotation)
                    .start();
        }
        else {
            view.animate()
                    .setDuration(duration)
                    .x(x)
                    .y(y)
                    .rotation(rotation)
                    .start();
        }
        if(duration == MOVE_DURATION)
            new Thread(myThreadMove).start();
        else
            new Thread(myThreadRotate).start();

    }

    private void animateObject2(View view, int duration, int x, int y, int rotation){
        if(x == 0 && y == 0){
            view.animate()
                    .setDuration(duration)
                    .rotation(rotation)
                    .start();
        }
        else if(x == 0){
            view.animate()
                    .setDuration(duration)
                    .y(y)
                    .rotation(rotation)
                    .start();
        }
        else if(y == 0){
            view.animate()
                    .setDuration(duration)
                    .x(x)
                    .rotation(rotation)
                    .start();
        }
        else {
            view.animate()
                    .setDuration(duration)
                    .x(x)
                    .y(y)
                    .rotation(rotation)
                    .start();
        }
        isAnimated = true;

    }

    class MyThread implements Runnable{

        int speed;

        MyThread(int speed) {
            this.speed = speed;
        }

        @Override
        public void run() {
            isAnimated = true;
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isAnimated = false;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void createHelicopter(){
        if (helicopterCounter >= 10)
            return;
        int size;
        if (helicopterCounter == 0)
            size = 4;
        else if(helicopterCounter < 3)
            size = 3;
        else if(helicopterCounter < 6)
            size = 2;
        else size = 1;

        Helicopter helicopter = new Helicopter(context, size, cellWidth, cellHeight, true);
        helicopter.setX(xStartPoint+(float)(0.5*cellWidth)-(float)(0.5*helicopter.getWidth()));
        helicopter.setY(yStartPoint+(float)(16*cellHeight-size*(cellHeight/2)));
        helicopter.setOnTouchListener(new ChoiceTouchListener());
        v.addView(helicopter);
        helicopterCounter++;
        buffer = helicopter;
        allHelicopters.add(helicopter);
    }

    private View.OnDragListener dragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, @NotNull DragEvent event) {
            int dragEvent = event.getAction();
            final View view = (View) event.getLocalState();
            if(view == null) return false;
            Cell c = (Cell)v;
            Helicopter h = (Helicopter)view;
            switch (dragEvent){
                case DragEvent.ACTION_DRAG_ENTERED:
                    cellHighlightOn(c, h);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    cellHighlightOffAll();
                    break;
                case DragEvent.ACTION_DROP:
                    if(isAnimated){
                        cellHighlightOffAll();
                        return false;
                    }

                    if(h.getOnBoard() && cells[h.getPositionOnBoard().x][h.getPositionOnBoard().y] == v){
                        cellHighlightOff(c, h, h.getOrientation());
                        return false;
                    }

                    ColorDrawable colorCell = (ColorDrawable) v.getBackground();

                    if(h.getSize()%2 == 1 && colorCell.getColor() == Color.parseColor("#99ff33")){
                        setCallCounter(c, h);
                        if(h.getOrientation()){
                            animateObject(h, MOVE_DURATION, (int) (v.getX()+(0.5*v.getWidth())-(0.5*view.getWidth())), (int) (v.getY()+(0.5*v.getHeight())-(0.5*view.getHeight())), 0);
                        }
                        else {
                            animateObject(h, MOVE_DURATION, (int) (v.getX()+(0.5*v.getWidth())-(0.5*view.getWidth())), (int) (v.getY()+(0.5*v.getHeight())-(0.5*view.getHeight())), 90);
                        }

                        if (!h.getOnBoard())
                            createHelicopter();
                        h.setOnBoard(true);
                    }
                    else if(h.getSize()%2 == 0 && colorCell.getColor() == Color.parseColor("#99ff33")){
                        setCallCounter(c, h);
                        if(h.getOrientation()){
                            animateObject(h, MOVE_DURATION, (int) (v.getX()+(0.5*v.getWidth())-(0.5*view.getWidth())), (int) (v.getY()-(0.5*view.getHeight())), 0);
                        }
                        else {
                            animateObject(h, MOVE_DURATION, (int) (v.getX()+(v.getWidth())-(0.5*view.getWidth())), (int) (v.getY()+(0.5*v.getHeight())-(0.5*view.getHeight())), 90);

                        }
                        if (!h.getOnBoard())
                            createHelicopter();
                        h.setOnBoard(true);
                    }
                    new Thread(myThreadMove).start();
                    cellHighlightOff(c, h, h.getOrientation());


                    break;
            }
            return true;
        }
    };

}
