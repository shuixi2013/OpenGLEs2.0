package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.shape.CurveGeometry;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-27.
 * CurveGeometryActivity
 */

@EActivity
public class CurveGeometryActivity extends Activity {

    private float angleX;
    private float angleY;
    private float selfAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new CurveGeometryView(this));
    }

    class CurveGeometryView extends GLSurfaceView {

        private float mPreviousY;
        private float mPreviousX;

        public CurveGeometryView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            setRenderer(new CurveGeometryRenderer());
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float y = event.getY();
            float x = event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dy = y - mPreviousY;  //计算触控笔Y位移
                    float dx = x - mPreviousX;  //计算触控笔X位移
                    angleX += dy * 0.56f;
                    angleY += dx * 0.56f;
            }
            mPreviousY = y;                     //记录触控笔位置
            mPreviousX = x;                     //记录触控笔位置
            return true;
        }
    }

    class CurveGeometryRenderer implements GLSurfaceView.Renderer {

        private CurveGeometry curveGeometry;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            curveGeometry = new CurveGeometry();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
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
            MatrixState.rotate(angleX, 1, 0, 0);
            MatrixState.rotate(angleY, 0, 1, 0);
            MatrixState.rotate(selfAngle, 0, 1, 0);
            curveGeometry.drawSelf();
            MatrixState.popMatrix();
            selfAngle += 0.3f;
        }
    }
}
