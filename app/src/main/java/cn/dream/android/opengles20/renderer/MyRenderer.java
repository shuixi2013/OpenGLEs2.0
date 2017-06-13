package cn.dream.android.opengles20.renderer;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lgb on 17-6-13.
 * MyRenderer
 */

public class MyRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = MyRenderer.class.getSimpleName();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
