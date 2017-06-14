package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);

        setContentView(glSurfaceView);
    }

    @AfterViews
    void afterViews() {
        glSurfaceView.setRenderer(new PolyhetronRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
