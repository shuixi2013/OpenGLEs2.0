package cn.dream.android.opengles20.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.Square;
import cn.dream.android.opengles20.shape.Torus;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-14.
 * PlaneRenderer
 */

public class PlaneRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = PlaneRenderer.class.getSimpleName();

    private Context context;
    //private Square square;
    private Torus torus;
    private float angle;

    private float angleX;
    private float angleY;

    public PlaneRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");
        torus = new Torus(context, 1,0.4f);
        torus.setTorusAngle(0, 720, 0, 360);
        torus.setTorusHeight(2f);
        torus.setUnitAngle(10);
        torus.initTorusData();
        //square = new Square();
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        float radio = (float) width / height;
        MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 100);
        MatrixState.setCamera(0, 0, 5, 0, 0, 0, 0, 1, 0);
        MatrixState.setInitStack();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleX, 0, 0, 1);
        MatrixState.rotate(angleY, 1, 0, 0);
        //MatrixState.rotate(angle++, 1, 0, 0);
        //square.drawSelf();
        torus.drawSelf();
        MatrixState.popMatrix();
    }

    public void addAngle(float angleX, float angleY) {
        this.angleX += angleX;
        this.angleY += angleY;
    }
}
