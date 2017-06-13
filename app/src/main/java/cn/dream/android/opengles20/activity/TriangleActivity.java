package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

import cn.dream.android.opengles20.views.TriangleGLSurfaceView;

/**
 * Created by lgb on 17-6-13.
 * TriangleActivity
 */

@EActivity
public class TriangleActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TriangleGLSurfaceView(this));
    }
}
