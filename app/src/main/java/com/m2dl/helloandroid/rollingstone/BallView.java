package com.m2dl.helloandroid.rollingstone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    public float mX;
    public float mY;
    //private final int mR;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap icon;

    //construct new ball object
    public BallView(Context context, float x, float y) {
        super(context);
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.stone_copy);
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



}
