package com.m2dl.helloandroid.rollingstone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class BallView extends View {

    private float mX;
    private float mY;
    //private final int mR;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap icon;

    //construct new ball object
    public BallView(Context context, float x, float y) {
        super(context);
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.stone);
        //color hex is [transparency][red][green][blue]
        //mPaint.setColor(0xFF000000); // transparent.
        this.mX = x;
        this.mY = y;
        //this.mR = r; //radius
    }

    //called by invalidate()	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(icon, mX - icon.getWidth() / 2, mY - icon.getHeight() / 2, mPaint);
        //canvas.drawCircle(mX, mY, mR, mPaint);
    }

    public float getmX() {
        return mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }



}
