package com.example.helikoptery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

class Cell extends View {

    private boolean isBoardField;
    private int value;
    private boolean isShooted;

    private Bitmap bitmap;
    private Rect rect;
    private int width, height;

    public Cell(Context context, int width, int height) {
        super(context);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cell);
        rect = new Rect(0,0,width,height);
        isBoardField = false;
        this.isShooted = false;
        this.width = width;
        this.height = height;
        this.value = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    public void setIsBoardField(boolean boardField){
        this.isBoardField = boardField;
    }

    public boolean getIsBoardField(){
        return this.isBoardField;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public boolean isShooted() {
        return isShooted;
    }

    public void setShooted(boolean shooted) {
        isShooted = shooted;
    }

}
