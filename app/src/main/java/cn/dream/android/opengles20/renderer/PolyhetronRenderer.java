package cn.dream.android.opengles20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.Polyhetron;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-14.
 * PolyhetronRenderer
 */

public class PolyhetronRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = PolyhetronRenderer.class.getSimpleName();

    private Polyhetron polyhetron;
    private float angleX;
    private float angleY;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceChanged()");

        polyhetron = new Polyhetron();

        GLES20.glClearColor(0, 0 ,0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        float radio = (float) width / height;
        MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 10);
        MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        MatrixState.setLightPosition(0, 0, 8);
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleX, 1, 0, 0);
        MatrixState.rotate(angleY, 0, 1, 0);
        polyhetron.drawSelf();
        MatrixState.popMatrix();
    }

    public void addAngle(float angleX, float angleY) {
        this.angleX += angleX;
        this.angleY += angleY;
    }
}
