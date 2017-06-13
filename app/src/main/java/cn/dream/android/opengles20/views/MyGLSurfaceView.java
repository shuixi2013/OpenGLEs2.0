package cn.dream.android.opengles20.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import cn.dream.android.opengles20.renderer.MyRenderer;

/**
 * Created by lgb on 17-6-13.
 * MyGLSurfaceView
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private final static String TAG = MyGLSurfaceView.class.getSimpleName();

    private MyRenderer myRenderer;

    public MyGLSurfaceView(Context context) {
        this(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        myRenderer = new MyRenderer();
        setEGLContextClientVersion(2);
        setRenderer(myRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
