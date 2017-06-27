package cn.dream.android.opengles20.shape;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

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
            //"attribute vec4 aColor;\n" +                                    // 顶点颜色
            //"attribute vec2 aTexture;\n" +                                  // 顶点纹理
            //"varying  vec4 vColor;\n" +                                     // 用于传递给片元着色器的易变变量
            //"varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            //"   vColor = aColor;\n" +                                       // 将接收的顶点颜色传递给片元着色器
            //"   vTexture = aTexture;\n" +
            "   gl_PointSize = 2.0;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            //"varying  vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "void main() {\n" +
            "   gl_FragColor = vec4(1, 1, 1, 1);\n" +                                 // 给片源附上颜色值
            "}";


    private PointF point0 = new PointF(0.2f, 1);
    private PointF point1 = new PointF(3f, 0.8f);
    private PointF point2 = new PointF(-1f, -0.2f);
    private PointF point3 = new PointF(3.8f, -0.8f);

    private float[] vertex;
    private int vertexCount;
    private FloatBuffer vertexBuffer;

    private int mProgram;
    private int vertexHandle;
    private int uMVPMatrixHandle;

    public CurveGeometry() {
        vertexCount = (int) (1f / 0.05f * 360 / 10);
        Log.d(TAG, "vertexCount=" + vertexCount);
        vertex = new float[vertexCount * 3];
        int k = 0;
        for (int i = 0; i < 360; i = i +10) {
            for (float j = 0f; j < 1f; j = j + 0.05f) {
                float x1 = (float) (bezierCurveX(j) * Math.cos(Math.toRadians(i)));
                float y1 = bezierCurveY(j);
                float z1 = (float) (bezierCurveX(j) * Math.sin(Math.toRadians(i)));

                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;
            }
        }

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private float bezierCurveX(float t) {
        float temp = (1 - t);
        return temp * temp * temp * point0.x + 3 * temp * temp * point1.x + 3 * t * t * temp * point2.x + t * t * t * point3.x;
    }

    private float bezierCurveY(float t) {
        float temp = (1 - t);
        return temp * temp * temp * point0.y + 3 * temp * temp * point1.y + 3 * t * t * temp * point2.y + t * t * t * point3.y;
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);
    }
}
