package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.Mountain;
import cn.dream.android.opengles20.utils.Constant;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-12.
 * GrayScaleImageActivity
 */

@EActivity
public class GrayScaleImageActivity extends Activity {

    private GLSurfaceView glSurfaceView;

    private static float WIDTH;
    private static float HEIGHT;

    private float direction = 0;    // 视线方向
    private float cx = 0;           // 摄像机x坐标
    private float cz = 12;          // 摄像机z坐标
    private float tx = 0;           // 观察目标点x坐标
    private float tz = 0;           // 观察目标点z坐标

    private static final float DEGREE_SPAN = (float) (0.5f / 180.0f * Math.PI);  // 摄像机每次转动的角度

    private boolean isRun = false;
    private float x;
    private float y;
    private float Offset = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 获得系统的宽度以及高度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            WIDTH = dm.widthPixels;
            HEIGHT = dm.heightPixels;
        } else {
            WIDTH = dm.heightPixels;
            HEIGHT = dm.widthPixels;
        }
        // 设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new MountainsRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
    }

    @AfterViews
    void afterViews() {
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isRun = true;
                        moveCamera();
                        break;
                    case MotionEvent.ACTION_UP:
                        isRun = false;
                        break;
                }
                return true;
            }
        });
    }

    @Background(delay = 10)
    void moveCamera() {
        if (x > 0 && x < WIDTH / 2 && y > 0 && y < HEIGHT / 2) {                //向前
            cx = cx - (float) Math.sin(direction) * 0.1f;
            cz = cz - (float) Math.cos(direction) * 0.1f;
        } else if (x > WIDTH / 2 && x < WIDTH && y > 0 && y < HEIGHT / 2) {     //向后
            cx = cx + (float) Math.sin(direction) * 0.1f;
            cz = cz + (float) Math.cos(direction) * 0.1f;
        } else if (x > 0 && x < WIDTH / 2 && y > HEIGHT / 2 && y < HEIGHT) {
            direction = direction + DEGREE_SPAN;
        } else if (x > WIDTH / 2 && x < WIDTH && y > HEIGHT / 2 && y < HEIGHT) {
            direction = direction - DEGREE_SPAN;
        }
        //设置新的观察目标点XZ坐标
        tx = (float) (cx - Math.sin(direction) * Offset);       // 观察目标点x坐标
        tz = (float) (cz - Math.cos(direction) * Offset);       // 观察目标点z坐标
        MatrixState.setCamera(cx, 3, cz, tx, 1, tz, 0, 1, 0);   // 设置新的摄像机位置

        if (isRun)
            moveCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    class MountainsRenderer implements GLSurfaceView.Renderer {

        private final String TAG = MountainsRenderer.class.getSimpleName();

        private int[] textureIds;
        private Mountain mountain;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.d(TAG, "onSurfaceCreated()");

            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            Constant.yArray = Constant.loadLandforms(getResources(), R.mipmap.land);    // 将灰度图写入一个float数组中
            mountain = new Mountain(Constant.yArray);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.d(TAG, "onSurfaceChanged()");

            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 100);
            MatrixState.setCamera(cx, 3, cz, tx, 1, tz, 0, 1, 0);
            MatrixState.setInitStack();

            textureIds = new int[1];
            GLES20.glGenTextures(1, textureIds, 0);
            ShaderUtil.bindTextureId(GrayScaleImageActivity.this, textureIds, new int[]{R.mipmap.grass});
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            mountain.drawSelf(textureIds[0]);
            MatrixState.popMatrix();
        }
    }

}
