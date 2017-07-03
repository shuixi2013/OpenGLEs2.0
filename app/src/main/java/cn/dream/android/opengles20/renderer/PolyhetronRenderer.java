package cn.dream.android.opengles20.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.Polyhetron;
import cn.dream.android.opengles20.shape.TextureSquare;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-14.
 * PolyhetronRenderer
 */

public class PolyhetronRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = PolyhetronRenderer.class.getSimpleName();

    private Context context;

    private Polyhetron polyhetron;
    private TextureSquare textureSquare2;

    private float angleX;
    private float angleY;
    private float angleSelf;

    public PolyhetronRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceChanged()");

        polyhetron = new Polyhetron(context);
        textureSquare2 = new TextureSquare(context, R.mipmap.lgq);

        GLES20.glClearColor(0.3f, 0.3f ,0.3f, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        float radio = (float) width / height;
        MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 10);
        MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        MatrixState.setLightPosition(0, 0, 8);
        MatrixState.setSunLightPosition(0, 0, 8);
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, -1);
        MatrixState.rotate(angleSelf, 1, 0, 0);
        MatrixState.rotate(angleSelf, 0, 1, 0);
        polyhetron.drawSelf();
        MatrixState.popMatrix();

        GLES20.glEnable(GLES20.GL_BLEND);                                           // 开启混合
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_ONE_MINUS_SRC_COLOR);     // 设置混合因子，第一个为源
        MatrixState.pushMatrix();
        MatrixState.translate(angleY/500, -angleX/500, 0);
        textureSquare2.drawSelf();
        MatrixState.popMatrix();
        GLES20.glDisable(GLES20.GL_BLEND);

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
