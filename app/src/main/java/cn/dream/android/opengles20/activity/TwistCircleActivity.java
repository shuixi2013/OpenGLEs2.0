package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.TwistCircle;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-28.
 * TwistCircleActivity
 */

@EActivity
public class TwistCircleActivity extends Activity {

    private final static String TAG = TwistCircleActivity.class.getSimpleName();

    private GLSurfaceView glSurfaceView;

    private float ctLength = 3;
    private float cx = 0;
    private float cy = 0;
    private float cz = ctLength;

    private float tx = 0;
    private float ty = 0;
    private float tz = 0;

    private int direction = -1;
    private float radians;


    private int[] mPrograms;
    private int[] texturesId;

    private TwistCircle twistCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new TwistCircleRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(glSurfaceView);
    }

    @AfterViews
    void afterViews() {
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float curX = event.getX();
                float curY = event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (curX < v.getWidth() / 2 && curY < v.getHeight() / 2) {          // left top
                        direction = 0;
                    } else if (curX >= v.getWidth() / 2 && curY < v.getHeight() / 2) {  // right top
                        direction = 1;
                    } else if (curX < v.getWidth() / 2 && curY >= v.getHeight() / 2) {  // left bottom
                        direction = 2;
                    } else {                                                        // right bottom
                        direction = 3;
                    }
                    rotateOrTranslate();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    direction = -1;
                }
                return true;
            }
        });
    }

    @UiThread(delay = 50)
    void rotateOrTranslate() {
        if (direction == -1)
            return;
        switch (direction) {
            case 0:     // 前进
                cx += 0.2 * Math.sin(radians);
                cz -= 0.2 * Math.cos(radians);
                break;
            case 1:     // 后退
                cx -= 0.2 * Math.sin(radians);
                cz += 0.2 * Math.cos(radians);
                break;
            case 2:     // 左旋
                radians -= Math.PI / 50;
                tx = (float) (cx + ctLength * Math.sin(radians));
                tz = (float) (cz - ctLength * Math.cos(radians));
                break;
            case 3:     // 右旋
                radians += Math.PI / 50;
                tx = (float) (cx + ctLength * Math.sin(radians));
                tz = (float) (cz - ctLength * Math.cos(radians));
                break;
            default:
                direction = -1;
                break;
        }
        MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
        rotateOrTranslate();
    }

    class TwistCircleRenderer implements GLSurfaceView.Renderer {


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);

            mPrograms = new int[1];
            mPrograms[0] = ShaderUtil.createProgram(TwistCircle.VERTEX_CODE, TwistCircle.FRAGMENT_CODE);

            texturesId = new int[1];
            GLES20.glGenTextures(1, texturesId, 0);
            ShaderUtil.bindTextureId(TwistCircleActivity.this, texturesId, new int[]{R.mipmap.lgq});

            twistCircle = new TwistCircle(mPrograms[0]);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            Log.d(TAG, "onSurfaceChanged() width=" + width + "  height=" + height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
            MatrixState.setInitStack();


            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            twistCircle.drawSelf(texturesId[0]);
            MatrixState.popMatrix();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (twistCircle != null)
            twistCircle.onDestroy();
    }
}
