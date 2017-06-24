package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-24.
 * Torus: 圆环体
 */

public class Torus {

    private final static String TAG = Torus.class.getSimpleName();

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying  vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "void main() {\n" +
            "   gl_FragColor = vec4(0.2, 0.3, 0.9, 1);\n" +                                 // 给片源附上颜色值
            "}";


    private float[] vertex;
    private int pointCount;
    private FloatBuffer vertexBuffer;

    private int mProgram;
    private int vertexHandle;
    private int uMVPMatrixHandle;

    private float outerRadius;  // 圆环体中心到实心圆圆心的距离
    private float innerRadius;  // 圆环体实心圆半径

    public Torus(float outerRadius, float innerRadius) {
        long time = System.currentTimeMillis();
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        int startAngle =  0, endAngle = 360;
        int k = 0;
        pointCount = 6 * endAngle / 10 * endAngle / 10;
        vertex = new float[pointCount * 3];
        for (int i = startAngle; i < endAngle; i = i + 10) {        // XOZ平面切图的逆时针角度递增
            for (int j = startAngle; j < endAngle; j = j + 10) {    // X'OY平面切图的逆时针角度递增
                float x1 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j))) * Math.sin(Math.toRadians(i)));
                float x2 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j +10))) * Math.sin(Math.toRadians(i)));
                float x3 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j))) * Math.sin(Math.toRadians(i + 10)));
                float x4 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j + 10))) * Math.sin(Math.toRadians(i + 10)));

                float y1 = (float) (innerRadius * Math.sin(Math.toRadians(j)));
                float y2 = (float) (innerRadius * Math.sin(Math.toRadians(j + 10)));
                float y3 = y1;
                float y4 = y2;

                float z1 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j))) * Math.cos(Math.toRadians(i)));
                float z2 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j +10))) * Math.cos(Math.toRadians(i)));
                float z3 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j))) * Math.cos(Math.toRadians(i + 10)));
                float z4 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j + 10))) * Math.cos(Math.toRadians(i + 10)));

                // 第一个三角形
                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                // 第二个三角形
                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

                vertex[k++] = x4;
                vertex[k++] = y4;
                vertex[k++] = z4;
                if (i == 0 && j == 0)
                    Log.e(TAG, "(" + x1 + " " + y1 + " " + z1 + ") (" + x2 + " " + y2 + " " + z2 + ") (" + x3 + " " + y3 + " " + z3 + ") (" + x4 + " " + y4 + " " + z4 + ")" );
            }
        }

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);
        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        Log.e(TAG, "Torus() end time=" + (System.currentTimeMillis() - time));
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointCount);
    }
}
