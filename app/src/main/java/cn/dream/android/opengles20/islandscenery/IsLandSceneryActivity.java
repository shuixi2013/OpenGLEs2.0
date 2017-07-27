package cn.dream.android.opengles20.islandscenery;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

/**
 * Created by lgb on 17-7-26.
 * IsLandSceneryActivity
 */

@EActivity
public class IsLandSceneryActivity extends Activity {

    private int direction = -1;
    private IsLandSceneryGLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new IsLandSceneryGLView(this);
        setContentView(glView);
    }

    class IsLandSceneryGLView extends GLSurfaceView {

        private IsLandSceneryRenderer renderer;

        public IsLandSceneryGLView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            renderer = new IsLandSceneryRenderer(context);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        public IsLandSceneryRenderer getRenderer() {
            return renderer;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float curX = event.getX();
            float curY = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (curX < getWidth() / 2 && curY < getHeight() / 2) {          // left top
                    direction = 0;
                } else if (curX >= getWidth() / 2 && curY < getHeight() / 2) {  // right top
                    direction = 1;
                } else if (curX < getWidth() / 2 && curY >= getHeight() / 2) {  // left bottom
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
    }

    @UiThread(delay = 50)
    void rotateOrTranslate() {
        if (direction == -1)
            return;
        switch (direction) {
            case 0:
                glView.getRenderer().addTranslateValue(true);
                break;
            case 1:
                glView.getRenderer().addTranslateValue(false);
                break;
            case 2:
                glView.getRenderer().addRotateValue(-2);
                break;
            case 3:
                glView.getRenderer().addRotateValue(2);
                break;
            default:
                direction = -1;
                break;
        }
        rotateOrTranslate();
    }
}
