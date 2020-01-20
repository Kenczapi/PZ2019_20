package com.example.helikoptery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

class Helicopter extends View {

    private boolean isOnBoard;
    private int size;
    private boolean orientation; // true = pionowo
    private Point positionOnBoard;

    private Bitmap bitmap;
    private Rect rect;
    private int width, height;
    Drawable drawable;
    Resources res = getResources();

    public Helicopter(Context context, int size, int width, int height, boolean orientation) {
        super(context);

        this.width = width;
        this.height = height;
        this.size = size;
        this.orientation = orientation;
        this.positionOnBoard = new Point(-1,-1);
        this.isOnBoard = false;
        if (orientation)
            rect = new Rect(0,0,2*width, size*height);
        else
            rect = new Rect(0, 0, size*width, 2*height);

        switch(size){
            case 1:
                if (orientation)bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.verhelicopteryellow);
                else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.horhelicopteryellow);
                break;
            case 2:
                if (orientation)bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.verhelicopterred);
                else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.horhelicopterred);
                break;
            case 3:
                if (orientation)bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.verhelicopterblue);
                else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.horhelicopterblue);
                break;
            case 4:
                if (orientation)bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.verhelicoptergreen);
                else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.horhelicoptergreen);
                break;


        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, rect, null);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(2*width , height*size);
    }

    public void setOnBoard(boolean onBoard) {
        isOnBoard = onBoard;
    }

    public boolean getOnBoard() {
        return isOnBoard;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean getOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public void refresh(){
        invalidate();
    }

    public Point getPositionOnBoard() {
        return positionOnBoard;
    }

    public void setPositionOnBoard(Point positionOnBoard) {
        this.positionOnBoard = positionOnBoard;
    }
}
