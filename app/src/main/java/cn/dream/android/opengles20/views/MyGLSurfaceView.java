package cn.dream.android.opengles20.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.dream.android.opengles20.renderer.MyRenderer;

/**
 * Created by lgb on 17-6-13.
 * MyGLSurfaceView
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private final static String TAG = MyGLSurfaceView.class.getSimpleName();

    private MyRenderer myRenderer;

    private float downX, downY;

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

        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            myRenderer.setmAngle((event.getY() - downY) / 20);
            requestRender();
            downX = event.getY();
        }
        return true;
    }
}
