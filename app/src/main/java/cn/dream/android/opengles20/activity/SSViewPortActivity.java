package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import org.androidannotations.annotations.EActivity;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-27.
 * SSViewPostActivity: ScreenSize ViewPort
 */

@EActivity
public class SSViewPortActivity extends Activity {

    private final static String TAG = AlphaActivity.class.getSimpleName();


    private GLSurfaceView glSurfaceView;

    private int[] texturesId;
    private boolean isTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new SSViewPortRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    isTouch = false;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouch = true;
                }
                return true;
            }
        });
    }


    class SSViewPortRenderer implements GLSurfaceView.Renderer {

        private ScreenButton screenButton;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            MatrixState.setProjectOrtho(0, width, 0, height, 2, 100);
            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.setInitStack();

            screenButton = new ScreenButton();

            texturesId = new int[2];
            GLES20.glGenTextures(2, texturesId, 0);
            ShaderUtil.bindTextureId(SSViewPortActivity.this, texturesId, new int[]{R.mipmap.ic_launcher, R.mipmap.wall});
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            /*if (isTouch)
                screenButton.drawSelf(texturesId[1]);
            else screenButton.drawSelf(texturesId[0]);*/
        }
    }

    class ScreenButton {

        private float[] vertex = new float[]{
                0, 0, 0,
                200, 0, 0,
                0, 100, 0,
                200, 100, 0
        };

        private float[] texture = new float[]{
                0,1,
                1, 1,
                0, 0,
                1, 0
        };

        private int mProgram;
        private int uMVPMatrixHandle;
        private int vertexHandle;
        private int textureHandle;

        private FloatBuffer vertexBuffer;
        private FloatBuffer textureBuffer;

        public ScreenButton() {
            mProgram = ShaderUtil.createProgram(ShaderUtil.VERTEX_CODE, ShaderUtil.FRAGMENT2_CODE);
            vertexBuffer = BufferUtil.toFloatBuffer(vertex);
            textureBuffer = BufferUtil.toFloatBuffer(texture);

            vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
            uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        }

        public void drawSelf(int textureId) {
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(textureHandle);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);                 // 设置使用的纹理编号
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);      // 绑定纹理id

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }
}
