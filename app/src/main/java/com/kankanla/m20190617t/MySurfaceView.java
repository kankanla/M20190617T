package com.kankanla.m20190617t;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class MySurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private final String T = "### MySurfaceView";
    private SurfaceHolder surfaceHolderM;
    private Canvas canvasM;
    private Thread threadM;
    private boolean isWork;
    private long sleeptime = 50;

    private Bitmap bitmapM1;
    private ArrayList<Point> arrayList;


    public MySurfaceView(Context context) {
        super(context);
        surfaceHolderM = this.getHolder();
        surfaceHolderM.addCallback(this);
        init();
    }

    public void init() {
        bitmapM1 = BitmapFactory.decodeResource(getResources(), R.mipmap.a2);
        arrayList = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(T, "### onTouchEvent");
        Point point = new Point();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(T, "ACTION_MOVE");
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                arrayList.add(point);
                System.out.println(arrayList.size());

                break;
            case MotionEvent.ACTION_DOWN:
                Log.i(T, "ACTION_MOVE");
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                arrayList.add(point);
                System.out.println(arrayList.size());
                break;
            default:
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isWork = true;
        threadM = new Thread(this);
        threadM.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isWork = false;
    }

    //    private Matrix matrixM;
    private Paint paint;

    {
        paint = new Paint();
//        matrixM = new Matrix();
    }

    private void Log() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                for (int i = 0; i < arrayList.size(); i++) {
                    int x = arrayList.get(i).x;
                    int y = arrayList.get(i).y;
                    if (y < 50) {
                        arrayList.remove(i);
                    }
                }
            }
        }) {
        }.start();
        System.out.println(Thread.currentThread().getName());
    }

    private void MyDraw1(Canvas canvas) {
        canvas.save();
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < arrayList.size(); i++) {
            int x = arrayList.get(i).x;
            int y = arrayList.get(i).y;
            Matrix matrixM = new Matrix();
            matrixM.setTranslate(x, y = y - 50);
            canvas.drawBitmap(bitmapM1, matrixM, paint);
            arrayList.set(i, new Point(x, y));
        }
        canvas.restore();
    }

    private void MyDrawWork() {
        canvasM = surfaceHolderM.lockCanvas();
        if (isWork && canvasM != null) {
            try {
                Log();
                MyDraw1(canvasM);
            } catch (Exception e) {
            } finally {
                surfaceHolderM.unlockCanvasAndPost(canvasM);
            }
        }
    }


    @Override
    public void run() {
        while (isWork) {
            long s = System.currentTimeMillis();
            MyDrawWork();
            long e = System.currentTimeMillis();
            if (e - s < sleeptime) {
                try {
                    Thread.sleep(sleeptime - (e - s));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
