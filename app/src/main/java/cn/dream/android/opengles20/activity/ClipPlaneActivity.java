package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.ClipPolyhetron;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-25.
 * ClipPlaneActivity
 */

@EActivity
public class ClipPlaneActivity extends Activity {

    private final static String TAG = ClipPlaneActivity.class.getSimpleName();

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new ClipPlaneRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
    }


    class ClipPlaneRenderer implements GLSurfaceView.Renderer {

        private ClipPolyhetron polyhetron;
        private float angleSelf;
        private float ratio;

        float countE = 0;
        float spanE = 0.01f;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            polyhetron = new ClipPolyhetron(ClipPlaneActivity.this);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.setLightPosition(10, 3, 0);
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            if (countE >= 2) {
                spanE = -0.01f;
            } else if (countE <= 0) {
                spanE = 0.01f;
            }
            countE = countE + spanE;
            float e[] = { 1, countE - 1, -countE + 1, 0 };//定义裁剪平面

            MatrixState.pushMatrix();
            MatrixState.rotate(angleSelf, 0, 1, 0);
            polyhetron.drawSelf(e);
            MatrixState.popMatrix();

            angleSelf += 0.3f;
        }
    }
}
