package com.johnyang.opengldemo;

import android.opengl.GLU;
import android.util.Log;

import com.johnyang.opengldemo.util.BufferUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by szu on 2017/3/20.
 */

public class MultiArrow extends AbstractMyRender {
    private int Number;
    private int tempNumber;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //清屏色
        gl10.glClearColor(0, 0, 0, 0f);
        //启用顶点缓冲区数组
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //启用深度测试
        gl10.glEnable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        //设置视口
        gl10.glViewport(0, 0, i, i1);
        //投影矩阵
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        //加载单位矩阵
        gl10.glLoadIdentity();
        //设置平截头体
        gl10.glFrustumf(-1, 1, -2, 2, 3f, 50f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl10.glColor4f(0.8f, 0.1f, 0.1f, 1f);
        //模型视图矩阵
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glLoadIdentity();
        GLU.gluLookAt(gl10, 0, 0, 5, 0, 0, 0, 0, 1, 0);
        gl10.glRotatef(xrotate, 1, 0, 0);
        gl10.glRotatef(yrotate, 0, 1, 0);
        gl10.glRotatef(zrotate, 0, 0, 1);

        Number = getDistance.getDrawNum();
        tempNumber = Number;
        int i = tempNumber - 10;
        Log.d("LogDebug", "templeNumber的值是" + String.valueOf(Number));
        if (i >= 0) {
            new drawnFront(gl10, i).Draw();
            new drawnRight(gl10, 10).Draw();
        } else if (i < 0) {
            new drawnRight(gl10, 10 - Math.abs(i)).Draw();
        }
    }

    /*
     * 向前画多少个
     * */
    public static class drawnFront {
        public int times;
        private GL10 gl10;

        public drawnFront(GL10 gl10, int times) {
            this.gl10 = gl10;
            this.times = times;
        }

        public void Draw() {
            for (int i = 0; i < times; i++) {
                BufferUtil.DrawCircle(gl10, 0.2f, 40);
                gl10.glPushMatrix();
                gl10.glTranslatef(0f, -1f, 0f);
            }
        }
    }

    /*
     * 向左画多少个
     * */
    public static class drawnLeft {
        public int times;
        private GL10 gl10;

        public drawnLeft(GL10 gl10, int times) {
            this.gl10 = gl10;
            this.times = times;
        }

        public void Draw() {
            for (int i = 0; i < times; i++) {
                BufferUtil.DrawCircle(gl10, 0.2f, 40);
                gl10.glPushMatrix();
                gl10.glTranslatef(1f, 0f, 0f);
            }
        }
    }

    /*
     * 向右画多少个
     * */
    public static class drawnRight {
        public int times;
        private GL10 gl10;

        public drawnRight(GL10 gl10, int times) {
            this.gl10 = gl10;
            this.times = times;
        }

        public void Draw() {
            for (int i = 0; i < times; i++) {
                BufferUtil.DrawCircle(gl10, 0.2f, 40);
                gl10.glPushMatrix();
                gl10.glTranslatef(-1f, 0f, 0f);
            }
        }
    }

    public static class getDistance {
        public static int Distance;
        public static int DrawNum;

        public static int getDrawNum() {
            return DrawNum;
        }

        public void setDrawNum(int drawNum) {
            DrawNum = drawNum;
        }

        public static int getDistance() {
            return Distance;
        }

        public void setDistance(int distance) {
            Distance = distance;
        }
    }
}

