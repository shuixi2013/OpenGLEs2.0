package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SeekBarProgressChange;
import org.androidannotations.annotations.ViewById;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.WavingFlag;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-25.
 * WavingFlagActivity
 */

@EActivity(R.layout.activity_wavingflag)
public class WavingFlagActivity extends Activity {

    private final static String TAG = WavingFlagActivity.class.getSimpleName();

    @ViewById(R.id.gLSurfaceView)
    GLSurfaceView glSurfaceView;

    @ViewById(R.id.radioGroup)
    RadioGroup radioGroup;

    private WavingFlagRenderer wavingFlagRenderer;

    @AfterViews
    void afterViews() {
        glSurfaceView.setEGLContextClientVersion(2);
        wavingFlagRenderer = new WavingFlagRenderer();
        glSurfaceView.setRenderer(wavingFlagRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio1) {
                    wavingFlagRenderer.getWavingFlag().setType(0);
                } else if (checkedId == R.id.radio2) {
                    wavingFlagRenderer.getWavingFlag().setType(1);
                } else if (checkedId == R.id.radio3) {
                    wavingFlagRenderer.getWavingFlag().setType(2);
                }
            }
        });
    }

    @SeekBarProgressChange({R.id.spanSeekBar})
    void onSeekBarChange(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.spanSeekBar) {
            wavingFlagRenderer.getWavingFlag().setWidthSpan((float) progress / 10);
        }
    }


    class WavingFlagRenderer implements GLSurfaceView.Renderer {

        private WavingFlag wavingFlag;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            wavingFlag = new WavingFlag(WavingFlagActivity.this, R.mipmap.android_flag);
        }

        public WavingFlag getWavingFlag() {
            return wavingFlag;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.setInitStack();

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            MatrixState.translate(-wavingFlag.getHorizontalUnit() / 2, -wavingFlag.getVerticalUnit() / 2, 0);
            wavingFlag.drawSelf();
            MatrixState.popMatrix();
        }
    }
}
