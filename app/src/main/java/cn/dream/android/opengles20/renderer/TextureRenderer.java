package cn.dream.android.opengles20.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.TextureSquare;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-20.
 * TextureRenderer
 */

public class TextureRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = TextureRenderer.class.getSimpleName();


    private Context context;
    private TextureSquare textureSquare;

    private float angleX, angleY;

    public TextureRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");

        textureSquare = new TextureSquare(context);

        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);
        float radio = (float) width / height;
        MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 10);
        MatrixState.setCamera(0, 0, 5, 0, 0, 0, 0, 3, 0);
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleX, 1, 0, 0);
        MatrixState.rotate(angleY, 0, 1, 0);
        textureSquare.drawSelf();
        MatrixState.popMatrix();
    }

    public void addAngle(float angleX, float angleY) {
        this.angleX += angleX;
        this.angleY += angleY;
    }
}
