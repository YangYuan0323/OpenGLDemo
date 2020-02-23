package com.johnyang.opengldemo;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class AbstractMyRender implements GLSurfaceView.Renderer {

    public float ratio;
    //围绕X轴旋转的角度
    public float xrotate = 0f;
    //围绕Y轴旋转的角度
    public float yrotate = 0f;
    //围绕Z轴旋转的角度
    public float zrotate = 0f;


    /**
     * 第一步
     *
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //清屏色
        gl10.glClearColor(0f, 0f, 0f, 1f);
        //启用顶点缓冲区数组
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    /**
     * 第二步
     *
     * @param gl10
     * @param i
     * @param i1
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        //设置视口
        gl10.glViewport(0, 0, i, i1);
        ratio = (float) i / (float) i1;
        //投影矩阵
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        //加载单位矩阵
        gl10.glLoadIdentity();
        //设置平截头体
        gl10.glFrustumf(-ratio, ratio, -1, 1, 3f, 7f);
    }

//    /**
//     * 这是第三步
//     * @param gl10
//     */
//    public abstract void onDrawFrame(GL10 gl10);

}
