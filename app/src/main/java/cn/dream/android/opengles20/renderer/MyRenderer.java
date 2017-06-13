package cn.dream.android.opengles20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.Triangle;

/**
 * Created by lgb on 17-6-13.
 * MyRenderer
 */

public class MyRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = MyRenderer.class.getSimpleName();

    public static float[] mProMatrix = new float[16];     // 投影矩阵
    public static float[] mVMatrix = new float[16];       // 摄像机位置朝向的参数矩阵

    private Triangle triangle;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");

        triangle = new Triangle();

        GLES20.glClearColor(0, 0, 0, 1);        //　设置屏幕背景色
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);  //　开启深度测试
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);     //　设置视窗大小及位置

        float ratio = (float) width / height;       // 计算GLSurfaceView的宽高比
        Matrix.frustumM(mProMatrix, 0, -ratio, ratio, -1, 1, 1, 10);   // 设置透视投影
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);     // 设置摄像机
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i(TAG, "onDrawFrame()");
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        triangle.drawSelf();
    }

    public void setmAngle(float angle) {
        triangle.setmAngle(angle);
    }
}
