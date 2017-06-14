package cn.dream.android.opengles20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.Square;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-14.
 * PlaneRenderer
 */

public class PlaneRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = PlaneRenderer.class.getSimpleName();

    private Square square;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");

        square = new Square();
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        float radio = (float) width / height;
        MatrixState.setProjectOrtho(-radio, radio, -1, 1, 2, 10);
        MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        square.drawSelf();
    }
}