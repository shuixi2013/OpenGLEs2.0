package cn.dream.android.opengles20.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.dream.android.opengles20.renderer.TriangleRenderer;

/**
 * Created by lgb on 17-6-13.
 * MyGLSurfaceView
 */

public class TriangleGLSurfaceView extends GLSurfaceView {

    private final static String TAG = TriangleGLSurfaceView.class.getSimpleName();

    private TriangleRenderer renderer;

    private float downX, downY;

    public TriangleGLSurfaceView(Context context) {
        this(context, null);
    }

    public TriangleGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        renderer = new TriangleRenderer();
        setEGLContextClientVersion(2);
        setRenderer(renderer);

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
            renderer.setmAngle((event.getY() - downY) / 20);
            requestRender();
            downX = event.getY();
        }
        return true;
    }
}
