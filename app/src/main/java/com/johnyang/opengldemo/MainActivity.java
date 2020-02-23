package com.johnyang.opengldemo;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AbstractMyRender render;
    private GLSurfaceView glSurfaceView;
    SensorManager sensorManager;
    private SurfaceView cameraPreview;
    private Camera camera;
    private TextView DistanceTv;
    public Handler handler;
    MultiArrow.getDistance getDistance = new MultiArrow.getDistance();
    private static final String TAG = "Loginfo";


    private SurfaceHolder.Callback cameraPreviewcallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            startPreview();
            Log.i(TAG, "surfaceCreated" + Thread.currentThread().getName());
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.i(TAG, "surfaceChanged");
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.i(TAG, "surfaceDestoryed");
            if (camera != null) {
                camera.stopPreview();
                camera.release();//释放相机资源
                camera = null;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHandler();
        //距离提示线程开启
        distanceThread.start();

        //设置渲染模块 GlsurfaceView
        glSurfaceView = new GLSurfaceView(this);
        //设置渲染器
        render = new MultiArrow();
        glSurfaceView.setZOrderOnTop(true);
        //这一步和下面的setFormat都是为了能够使surfaceView能够显示出来
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setZOrderMediaOverlay(true);
        glSurfaceView.setRenderer(render);
        //GLSurfaceView.RENDERMODE_CONTINUOUSLY:持续渲染(默认)
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //GLSurfaceView.RENDERMODE_WHEN_DIRTY:脏渲染,命令渲染
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);//设置glview为透明的


        //开启传感器服务
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //设置相机预览SurfaceView
        cameraPreview = new SurfaceView(this);
        cameraPreview.getHolder().addCallback(cameraPreviewcallback);
        cameraPreview.getHolder().setFormat(PixelFormat.TRANSPARENT);


        //设置距离提示文字栏
        DistanceTv = new TextView(this);
        DistanceTv.setText("导航开始");
        DistanceTv.setGravity(Gravity.CENTER);
        DistanceTv.setTextSize(25f);
        DistanceTv.setTextColor(Color.parseColor("#ff0000"));


        FrameLayout layout = new FrameLayout(this);

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //添加渲染
        layout.addView(glSurfaceView, params1);
        //添加相机预览
        layout.addView(cameraPreview, params1);
        //添加文字
        layout.addView(DistanceTv, params2);
        setContentView(layout, params1);
    }

    /**
     * Handler接受子线程更改UI模块
     */
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                if (what == Constant.distancemsg) {
                    Bundle bundle = msg.getData();
                    int distance = bundle.getInt("离终点距离");
                    int DrawNum = distance / 2;
                    if (distance != 0) {
                        Log.i(TAG, "distance的值为：" + distance + "DrawNum的值为：" + DrawNum);
                        getDistance.setDistance(distance);
                        getDistance.setDrawNum(DrawNum);
                        DistanceTv.setText("距离终点还有" + String.valueOf(distance) + "米");
                    } else {
                        DistanceTv.setText("已到达目的地");
                    }
                }
            }
        };
    }


    /**
     * 传感器服务注册模块
     */
    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    /**
     * 根据方向传感器的参数变化，重新渲染
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ORIENTATION:
//                获取绕三轴的角度
                float degreeZ = sensorEvent.values[0];
                float degreeX = sensorEvent.values[1];
                float degreeY = sensorEvent.values[2];
                render.xrotate = degreeX;
                render.zrotate = degreeZ;
//                render.yrotate = degreeY;
                glSurfaceView.requestRender();    //请求渲染，和脏渲染配合使用
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * 相机预览功能函数
     */
    private void startPreview() {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(cameraPreview.getHolder());
            camera.setDisplayOrientation(90);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 距离计算线程
     */
    Thread distanceThread = new Thread() {
        int distance = 40;
        int tipTimes = 40;

        @Override
        public void run() {
            super.run();
            int averSpeed = distance / tipTimes;
            for (int i = 0; i < tipTimes; i++) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = Constant.distancemsg;
                Bundle bundle = new Bundle();
                bundle.putInt("离终点距离", distance - ((i + 1) * averSpeed));
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    };
}
