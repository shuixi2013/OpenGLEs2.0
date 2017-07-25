package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.AlphaSquare;
import cn.dream.android.opengles20.shape.TextureSquare;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-25.
 * AlphaActivity
 */

@EActivity
public class AlphaActivity extends Activity{

    private final static String TAG = AlphaActivity.class.getSimpleName();


    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new AlphaTestRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
    }


    class AlphaTestRenderer implements GLSurfaceView.Renderer {

        private TextureSquare textureSquare;
        private AlphaSquare alphaSquare;
        private float angleSelf;
        private float ratio;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);

            textureSquare = new TextureSquare(AlphaActivity.this, R.mipmap.wall);
            textureSquare.setUnit(2);
            alphaSquare = new AlphaSquare(AlphaActivity.this, R.mipmap.mask);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 10);
            MatrixState.setCamera(0, 0, 5, 0, 0, 0, 0, 3, 0);
            MatrixState.pushMatrix();
            MatrixState.rotate(angleSelf, 1, 1, 0);
            textureSquare.drawSelf();
            MatrixState.popMatrix();

            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
            MatrixState.pushMatrix();
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 2, 10);
            MatrixState.setCamera(0, 0, 5, 0, 0, 0, 0, 3, 0);
            alphaSquare.drawSelf();
            MatrixState.popMatrix();

            angleSelf += 0.3f;
        }
    }
}
