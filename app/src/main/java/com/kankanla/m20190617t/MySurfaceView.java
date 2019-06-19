package com.kankanla.m20190617t;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
    private long sleeptime = 10;

    private Bitmap bitmapM1;
    private ArrayList<Point> arrayList;

    private HandlerThread handlerThread;
    private Handler handler;

    public MySurfaceView(Context context) {
        super(context);
        surfaceHolderM = this.getHolder();
        surfaceHolderM.addCallback(this);
        init();
    }

    public void init() {
        bitmapM1 = BitmapFactory.decodeResource(getResources(), R.mipmap.a2);
        arrayList = new ArrayList<>();

        handlerThread = new HandlerThread("LogThread");
        handlerThread.start();
        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
//                Log.i(T, "###  callback");
//                Log.i(T, Thread.currentThread().getName());
//                Log.i(T, Thread.activeCount() +"  cound");
                Log();
                return false;
            }
        };
        handler = new Handler(handlerThread.getLooper(), callback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:
//                point.x = (int) event.getX();
//                point.y = (int) event.getY();
//                arrayList.add(point);
                break;
            case MotionEvent.ACTION_DOWN:
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                arrayList.add(point);
                Log.i(T, Thread.currentThread().getName() + "          main");
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

    private Matrix matrixM;
    private Paint paint;

    {
        paint = new Paint();
        matrixM = new Matrix();
    }

    private void Log() {
        for (int i = 0; i < arrayList.size(); i++) {
            int x = arrayList.get(i).x;
            int y = arrayList.get(i).y;
            if (y < 500) {
                arrayList.remove(i);
            }
            System.out.println(arrayList.size() + "---size");

            //测试循环
//            for (int ii = 0; ii < 30000000; ii++) {
//                if (ii == 200000) {
//                    System.out.println("300000-------------");
//                }
//            }
        }
    }

    private void MyDraw1(Canvas canvas) {
        canvas.save();
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < arrayList.size(); i++) {
            int x = arrayList.get(i).x;
            int y = arrayList.get(i).y;
            matrixM.setTranslate(x, y = y - 10);
            canvas.drawBitmap(bitmapM1, matrixM, paint);
            arrayList.set(i, new Point(x, y));
        }
        canvas.restore();
    }

    private void MyDrawWork() {
        canvasM = surfaceHolderM.lockCanvas();
        if (isWork && canvasM != null) {
            try {
                handler.sendEmptyMessage(12);  //Thread 执行，画面更新快
//                Log();    // 直接执行  画面更新慢
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
//                    System.out.println(sleeptime - (e - s) + "-----");
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
