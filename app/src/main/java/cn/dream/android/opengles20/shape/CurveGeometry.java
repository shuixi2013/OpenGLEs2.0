package cn.dream.android.opengles20.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-27.
 * CurveGeometry 曲线几何体，利用一条曲线围绕Ｚ轴旋转得到一个几何体
 */

public class CurveGeometry {

    private final static String TAG = CurveGeometry.class.getSimpleName();

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec2 aTexture;\n" +                                  // 顶点纹理
            "varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "   vTexture = aTexture;\n" +
            "   gl_PointSize = 2.0;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "uniform sampler2D uTexture;\n" +
            "varying vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(uTexture, vTexture);\n" +                                 // 给片源附上颜色值
            "}";


    private PointF point0 = new PointF(0.1f, 0.9f);
    private PointF point1 = new PointF(0.8f, 0.8f);
    private PointF point2 = new PointF(0.2f, -0.2f);
    private PointF point3 = new PointF(0.8f, -0.35f);

    private float[] vertex;
    private float[] texture;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int vertexCount;
    private int[] textureId = new int[1];

    private int mProgram;
    private int vertexHandle;
    private int textureHandle;
    private int uMVPMatrixHandle;

    public CurveGeometry(Context context) {
        vertexCount = (int) (1f / 0.05f * 360 / 10) * 6;
        Log.d(TAG, "vertexCount=" + vertexCount);
        vertex = new float[vertexCount * 3];
        texture = new float[vertexCount * 2];
        int k = 0;
        for (int i = 0; i < 360; i = i +10) {
            float t;
            int count = (int) (1 / 0.05f);
            for (int j = 0; j < count; j++) {
                t = (float) j / count;
                float x1 = (float) (bezierCurveX(t) * Math.cos(Math.toRadians(i)));
                float y1 = bezierCurveY(t);
                float z1 = (float) (bezierCurveX(t) * Math.sin(Math.toRadians(i)));

                float x2 = (float) (bezierCurveX(t + 0.05f) * Math.cos(Math.toRadians(i)));
                float y2 = bezierCurveY(t + 0.05f);
                float z2 = (float) (bezierCurveX(t + 0.05f) * Math.sin(Math.toRadians(i)));

                float x3 = (float) (bezierCurveX(t) * Math.cos(Math.toRadians(i + 10)));
                float y3 = bezierCurveY(t);
                float z3 = (float) (bezierCurveX(t) * Math.sin(Math.toRadians(i + 10)));

                float x4 = (float) (bezierCurveX(t + 0.05f) * Math.cos(Math.toRadians(i + 10)));
                float y4 = bezierCurveY(t + 0.05f);
                float z4 = (float) (bezierCurveX(t + 0.05f) * Math.sin(Math.toRadians(i + 10)));

                // triangle 1
                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                vertex[k++] = x4;
                vertex[k++] = y4;
                vertex[k++] = z4;

                // triangle 2
                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;

                vertex[k++] = x4;
                vertex[k++] = y4;
                vertex[k++] = z4;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

            }
        }

        k = 0;
        for (int i = 0; i < 360; i = i +10) {
            for (float j = 0f; j < 1f; j = j + 0.05f) {
                float x1 = (float) i / 360;
                float y1 = j;

                float x2 = (float) i /360;
                float y2 = j + 0.05f;

                float x3 = (float) (i + 10) / 360;
                float y3 = j;

                float x4 = (float) (i + 10) / 360;
                float y4 = j + 0.05f;

                // triangle 1
                texture[k++] = x1;
                texture[k++] = y1;

                texture[k++] = x2;
                texture[k++] = y2;

                texture[k++] = x4;
                texture[k++] = y4;

                // triangle 2
                texture[k++] = x1;
                texture[k++] = y1;

                texture[k++] = x4;
                texture[k++] = y4;

                texture[k++] = x3;
                texture[k++] = y3;
            }
        }

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);      // 设置MIN时为最近采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);       // 设置MAG时为线性采样方式

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);           // 沿着S轴方向拉伸
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);           // 沿着T轴方向拉伸

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.tietu001);    // 图片的宽、高严格来讲是2的倍数
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0); // 实际加载纹理进显存，参数解释：纹理类型；纹理的层次，０表示基本图像，可以理解为直接贴图；；纹理边框尺寸　
        bitmap.recycle();
    }

    private float bezierCurveX(float t) {
        float temp = 1f - t;
        return temp * temp * temp * point0.x + 3 * t * temp * temp * point1.x + 3 * t * t * temp * point2.x + t * t * t * point3.x;
    }

    private float bezierCurveY(float t) {
        float temp = 1f - t;
        return temp * temp * temp * point0.y + 3 * t * temp * temp * point1.y + 3 * t * t * temp * point2.y + t * t * t * point3.y;
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);                 // 设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);   // 绑定纹理id
        //GLES20.glUniform1i(textureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
