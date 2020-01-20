package com.example.helikoptery;

import android.content.Context;

class Helicopter2 extends Helicopter {

    int width;
    int height;
    int size;
    public Helicopter2(Context context, int size, int width, int height) {
        super(context, size, width, height, false);
        this.width = width;
        this.height = height;
        this.size = size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width*size , 2*height);
    }
}
