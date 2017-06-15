package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import cn.dream.android.opengles20.renderer.PlaneRenderer;
import cn.dream.android.opengles20.renderer.PolyhetronRenderer;

/**
 * Created by lgb on 17-6-14.
 * PolyhetronActivity
 */

@EActivity
public class PolyhetronActivity extends Activity {

    private static final String TAG = PolyhetronActivity.class.getSimpleName();

    private GLSurfaceView glSurfaceView;
    private PolyhetronRenderer polyhetronRenderer;
    private float mPreviousY;               //上次的触控位置Y坐标
    private float mPreviousX;               //上次的触控位置X坐标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);

        setContentView(glSurfaceView);
    }

    @AfterViews
    void afterViews() {
        polyhetronRenderer = new PolyhetronRenderer();
        glSurfaceView.setRenderer(polyhetronRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();
                float x = event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float dy = y - mPreviousY;  //计算触控笔Y位移
                        float dx = x - mPreviousX;  //计算触控笔X位移
                        polyhetronRenderer.addAngle(dy * 0.56f, dx * 0.56f);
                }
                mPreviousY = y;                     //记录触控笔位置
                mPreviousX = x;                     //记录触控笔位置
                return true;
            }
        });
    }
}
