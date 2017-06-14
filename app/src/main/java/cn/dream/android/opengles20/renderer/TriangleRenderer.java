package cn.dream.android.opengles20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.PointsOrLines;
import cn.dream.android.opengles20.shape.Triangle;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-13.
 * MyRenderer
 */

public class TriangleRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = Triangle.class.getSimpleName();

    public static float[] mProMatrix = new float[16];     // 投影矩阵
    public static float[] mVMatrix = new float[16];       // 摄像机位置朝向的参数矩阵

    private Triangle triangle;
    private PointsOrLines pointsOrLines;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");

        triangle = new Triangle();
        pointsOrLines = new PointsOrLines();

        GLES20.glClearColor(0, 0, 0, 1);        // 设置屏幕背景色
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);  // 开启深度检测
        //GLES20.glEnable(GLES20.GL_CULL_FACE);   // 打开背面裁剪
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);     //　设置视窗大小及位置

        // 场景体中的物体会投影到近平面上，然后再映射到显示屏幕上
        float ratio = (float) width / height;       // 计算GLSurfaceView的宽高比
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 10);       // 设置透视投影
        //MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);         // 设置正交投影
        MatrixState.setCamera(0, 0, 2, 0, 0, 0, 0, 1, 0);                  // 设置摄像机
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i(TAG, "onDrawFrame()");
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.translate(-1, 0, 0);
        pointsOrLines.drawSelf();
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.translate(1, 0, -0.8f);
        MatrixState.rotate(triangle.getmAngle(), 1, 0, 0);
        triangle.drawSelf();
        MatrixState.popMatrix();
    }

    public void setmAngle(float angle) {
        if (triangle != null)
          triangle.setmAngle(angle);
    }

    public void addMode() {
        if (pointsOrLines != null) {
            pointsOrLines.addMode();
        }
    }
}
