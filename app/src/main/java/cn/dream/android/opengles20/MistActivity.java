package cn.dream.android.opengles20;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import org.androidannotations.annotations.EActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.activity.CurveGeometryActivity;
import cn.dream.android.opengles20.shape.CurveGeometry;
import cn.dream.android.opengles20.shape.TextureBall;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-3.
 * MistActivity
 */

@EActivity
public class MistActivity extends Activity{

    private float angleX;
    private float angleY;
    private float selfAngle;

    private float tempZ = 5;

    //关于摄像机的变量
    float cx=0;//摄像机x位置
    float cy=150;//摄像机y位置
    float cz=400;//摄像机z位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new MistActivity.MistGLSurfaceView(this));
        //setContentView(new BezierCurve3View(this));
    }

    class MistGLSurfaceView extends GLSurfaceView {

        private float mPreviousY;
        private float mPreviousX;


        private final float TOUCH_SCALE_FACTOR = 180.0f/200;//角度缩放比例

        public MistGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            setRenderer(new MistActivity.MistRenderer());
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = x - mPreviousX;//计算触控笔X位移
                    cx += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
                    //将cx限制在一定范围内
                    cx = Math.max(cx, -400);
                    cx = Math.min(cx, 400);
                    MatrixState.setCamera(cx, cy, cz, 0, 0, 0, 0, 1, 0);
                    break;
            }
            mPreviousX = x;//记录触控笔位置
            return true;
        }
    }

    class MistRenderer implements GLSurfaceView.Renderer {

        private int[] texturesId;
        private TextureBall textureBall;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            textureBall = new TextureBall(MistActivity.this, 150f);
            textureBall.initProgram(MistActivity.this, "opengles/code/vertex_mist.sh", "opengles/code/fragment_mist.sh");
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float radio = (float) width / height;
            //MatrixState.setProjectFrustum(-radio, radio, -1, 1, 2, 100);
            float a = 1f;
            MatrixState.setProjectFrustum(-radio*a, radio*a, -1*a, 1*a, 2, 1000);
            MatrixState.setLightPosition(200, 200, 200);
            MatrixState.setCamera(cx, cy, cz, 0, 0, 0, 0, 1, 0);
            //MatrixState.setLightPosition(50, 50, 50);
            //MatrixState.setCamera(tempZ, 1, 5, 0, 0, 0, 0, 1, 0);
            MatrixState.setInitStack();

            texturesId = new int[1];
            GLES20.glGenTextures(1, texturesId, 0);
            ShaderUtil.bindTextureId(MistActivity.this, texturesId, new int[]{R.mipmap.earth});
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            MatrixState.rotate(angleX, 1, 0, 0);
            MatrixState.rotate(angleY, 0, 1, 0);
            MatrixState.rotate(selfAngle, 0, 1, 0);
            textureBall.drawSelf(texturesId[0]);
            MatrixState.popMatrix();
            selfAngle += 0.3f;
        }
    }
}
