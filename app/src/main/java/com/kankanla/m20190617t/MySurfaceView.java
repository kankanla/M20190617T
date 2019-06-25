package com.kankanla.m20190617t;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    private Context context;
    private SurfaceHolder surfaceHolderM;
    protected Canvas canvasM;
    protected Thread threadM;
    private boolean isWork;
    protected long sleeptime = 10;

    private Bitmap bitmapM1;
    private ArrayList<Point> arrayList;
    private ArrayList<Integer> arrayListRandom;

    protected HandlerThread handlerThread;
    private Handler handler;

    protected SensorManager sensorManagerM;
    protected Sensor sensorM;

    public MySurfaceView(Context context) {
        super(context);
        this.context = context;
        surfaceHolderM = this.getHolder();
        surfaceHolderM.addCallback(this);
        init();
    }

    public void init() {
        /*http://icooon-mono.com/*/
        bitmapM1 = BitmapFactory.decodeResource(getResources(), R.mipmap.a2);
        arrayList = new ArrayList<>();
        arrayListRandom = new ArrayList<>();

        handlerThread = new HandlerThread("LogThread");
        handlerThread.start();
        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log();
                return false;
            }
        };
        handler = new Handler(handlerThread.getLooper(), callback);
    }

    private void Sensor_init() {
        sensorManagerM = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorM = sensorManagerM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManagerM.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        break;
                    case Sensor.TYPE_ALL:
                        break;
                    default:
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensorM, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                arrayList.add(point);
                arrayListRandom.add((Integer) (int) (Math.random() * (5 - 8) + 8));
                break;
            default:
        }
        performClick();
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
        paint.setColor(Color.RED);
        matrixM = new Matrix();
    }

    private void Log() {
        for (int i = 0; i < arrayList.size(); i++) {
            int x = arrayList.get(i).x;
            int y = arrayList.get(i).y;
            if (y < 200) {
                arrayList.remove(i);
                arrayListRandom.remove(i);
            }
        }
    }

    private void MyDraw1(Canvas canvas) {
        for (int i = 0; i < arrayList.size(); i++) {
            int x = arrayList.get(i).x;
            int y = arrayList.get(i).y;
            matrixM.setTranslate(x, y = y - arrayListRandom.get(i));
            canvas.drawBitmap(bitmapM1, matrixM, paint);
            arrayList.set(i, new Point(x, y));
        }
    }

    private void MyDraw2(Canvas canvas) {
        canvas.drawLine(0, 200, getWidth(), 200, paint);
    }

    private void MyDrawWork() {
        canvasM = surfaceHolderM.lockCanvas();
        if (isWork && canvasM != null) {
            try {
                handler.sendEmptyMessage(12);  //Thread 执行，画面更新快
//                Log();    // 直接执行  画面更新慢
                canvasM.save();
                canvasM.drawColor(Color.WHITE);
                MyDraw1(canvasM);
                MyDraw2(canvasM);
                canvasM.restore();
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
