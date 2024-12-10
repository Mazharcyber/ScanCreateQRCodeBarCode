package com.example.barcodeapplictaion.utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class MySurfaceView extends SurfaceView {
    private Paint mPaint;
    private Path mPath;
    //private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private float mX, mY, newX, newY;

    public MySurfaceView(Context context) {
        super(context);
        initi(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void initi(Context context) {
        mSurfaceHolder = getHolder();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(12);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
/*
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                newX = event.getX();
                newY = event.getY();
                break;
            default:
                // Do nothing
        }
        drawRect();
        invalidate();*/
        return true;
    }

    private void drawRect(Canvas mCanvas) {
        mPath = new Path();
        mPath.moveTo(mX, mY);
        mCanvas = mSurfaceHolder.lockCanvas();
        mCanvas.save();
        mPath.addRect(mX, mY, newX, newY, Path.Direction.CCW);
        mCanvas.drawPath(mPath, mPaint);
        mCanvas.restore();
        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        mX = newX;
        mY = newY;
    }

}
