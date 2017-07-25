package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.Polyhetron;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-24.
 * ScissorActivity
 */

@EActivity
public class ScissorActivity extends Activity{

    GLSurfaceView glSurfaceView;
    private ScissorRenderer scissorRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        scissorRenderer = new ScissorRenderer(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(scissorRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
    }

    class ScissorRenderer implements GLSurfaceView.Renderer {

        private final String TAG = cn.dream.android.opengles20.renderer.PolyhetronRenderer.class.getSimpleName();

        private Context context;

        private Polyhetron polyhetron;

        private float angleX;
        private float angleY;
        private float angleSelf;

        private float radio;
        private int mWidth;
        private int mHeight;

        public ScissorRenderer(Context context){
            this.context = context;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i(TAG, "onSurfaceChanged()");

            polyhetron = new Polyhetron(context);

            GLES20.glClearColor(0.3f, 0.3f ,0.3f, 1);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG, "onSurfaceChanged() width=" + width + "  height=" + height);

            GLES20.glViewport(0, 0, width, height);

            radio = (float) width / height;
            mWidth = width;
            mHeight = height;
            MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 10);
            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.setLightPosition(0, 0, 8);
            MatrixState.setSunLightPosition(0, 0, 8);
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClearColor(0.3f, 0.3f ,0.3f, 1);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -1);
            MatrixState.rotate(angleSelf, 1, 0, 0);
            MatrixState.rotate(angleSelf, 0, 1, 0);
            polyhetron.drawSelf();
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(10, 50, 500, 500);         // 以左下角为原点，向右为正ｘ轴，向上为正ｙ轴
            GLES20.glClearColor(0.7f, 0.7f, 0.7f ,0.7f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setProjectFrustum(-radio, radio, -1f, 1f, 2, 10);
            MatrixState.setCamera(-radio, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.translate(-radio, 0, 0);
            MatrixState.translate(0, 0, -1);
            MatrixState.rotate(angleSelf, 1, 0, 0);
            MatrixState.rotate(angleSelf, 0, 1, 0);
            polyhetron.drawSelf();
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

            MatrixState.popMatrix();
            angleSelf += 0.3f;
        }

        public void addAngle(float angleX, float angleY) {
            this.angleX += angleX;
            this.angleY += angleY;
        }

        public void setLightPosition(float x, float y, float z) {
            MatrixState.setLightPosition(x, y, z);
            MatrixState.setSunLightPosition(x, y, z);
        }

        public void setLightStyle(boolean isPointLight) {
            polyhetron.setPointLight(isPointLight);
        }

        public boolean getLightStyle() {
            return polyhetron.isPointLight();
        }
    }
}
