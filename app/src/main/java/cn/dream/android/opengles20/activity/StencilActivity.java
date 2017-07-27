package cn.dream.android.opengles20.activity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.TextureSquare;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-25.
 * StencilActivity
 */

@EActivity
public class StencilActivity extends Activity {

    private final static String TAG = StencilActivity.class.getSimpleName();

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new StencilRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(glSurfaceView);
    }

    class StencilRenderer implements GLSurfaceView.Renderer {

        private TextureSquare textureBall;
        private TextureSquare textureSquare1;
        private TextureSquare textureSquare2;
        private float ratio;
        private float vStep = 0;
        private boolean vFlag = true;


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            textureBall = new TextureSquare(StencilActivity.this, R.mipmap.ball);
            textureSquare1 = new TextureSquare(StencilActivity.this, R.mipmap.mirror);
            textureSquare1.setUnit(3);
            textureSquare2 = new TextureSquare(StencilActivity.this, R.mipmap.mirror_real);
            textureSquare2.setUnit(3);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            MatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
            MatrixState.setInitStack();

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);     // 记得关闭此深度测试，以开启模板测试
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT);
            GLES20.glEnable(GLES20.GL_STENCIL_TEST);
            GLES20.glStencilFunc(GLES20.GL_ALWAYS, 1, 1);
            GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_REPLACE);
            MatrixState.translate(0, -1, 0);
            textureSquare2.drawSelf();      //绘制不透明的镜面

            GLES20.glStencilFunc(GLES20.GL_EQUAL, 1, 1);
            GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_KEEP);
            MatrixState.pushMatrix();
            MatrixState.translate(0, -vStep, 0);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
            textureBall.drawSelf();         //绘制镜像体
            GLES20.glDisable(GLES20.GL_BLEND);
            MatrixState.popMatrix();

            GLES20.glDisable(GLES20.GL_STENCIL_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            textureSquare1.drawSelf();      //绘制透明的镜面
            GLES20.glDisable(GLES20.GL_BLEND);

            MatrixState.translate(0, 1, 0);
            MatrixState.pushMatrix();
            MatrixState.translate(0, vStep + 0.5f, 0);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
            textureBall.drawSelf();         //绘制目标体
            GLES20.glDisable(GLES20.GL_BLEND);
            MatrixState.popMatrix();

            MatrixState.popMatrix();

            if (vFlag)
                vStep += 0.01f;
            else vStep -= 0.01f;

            if (vStep > 1)
                vFlag = false;
            else if (vStep < 0)
                vFlag = true;
        }
    }
}
