package cn.dream.android.opengles20.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.PointPlanet;
import cn.dream.android.opengles20.shape.TextureEarth;
import cn.dream.android.opengles20.shape.TextureSquare;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-20.
 * TextureRenderer
 */

public class TextureEarthRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = TextureEarthRenderer.class.getSimpleName();


    private Context context;
    private TextureEarth textureEarth;
    private TextureEarth textureMoon;
    private PointPlanet pointPlanet;

    private float angleX, angleY;
    private float angleSelf, angleEarth, angleMoon;

    public TextureEarthRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");

        textureEarth = new TextureEarth(context, 1, R.mipmap.earth);
        textureMoon = new TextureEarth(context, 0.4f, R.mipmap.moon);
        pointPlanet = new PointPlanet();

        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);
        float radio = (float) width / height;
        MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 100);
        MatrixState.setCamera(0, 0, 5, 0, 0, 0, 0, 3, 0);
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleX, 1, 0, 0);
        MatrixState.rotate(angleY, 0, 1, 0);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleSelf, 0, 1, 0);
        pointPlanet.drawSelf();
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.rotate(angleEarth, 0, 1, 0);
        textureEarth.drawSelf();
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.rotate(angleEarth, 0, 1, 0);
        MatrixState.translate(2, 0, 0);
        MatrixState.rotate(angleMoon, 0, 1, 0);
        textureMoon.drawSelf();
        MatrixState.popMatrix();

        MatrixState.popMatrix();

        angleSelf += 0.08f;
        angleEarth += 0.2f;
        angleMoon -= 0.8f;
    }

    public void addAngle(float angleX, float angleY) {
        this.angleX += angleX;
        this.angleY += angleY;
    }
}
