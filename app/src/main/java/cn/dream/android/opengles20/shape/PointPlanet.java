package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-23.
 * PointPlanet
 */

public class PointPlanet {

    private static final String TAG = PointPlanet.class.getSimpleName();

    private final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" +// 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "uniform float uPointSize;" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "   gl_PointSize = uPointSize;\n" +                             // 点的大小
            "}";

    private final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying  vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "void main() {\n" +
            "   gl_FragColor = vec4(1, 1, 1, 1);\n" +                       // 给片源附上颜色值
            "}";

    private final double UNIT_SIZE = 50;

    private float[] vertex;

    private FloatBuffer vertexBuffer;
    private int pointCount;

    private int mProgram;
    private int vertexHandle;
    private int uPointSizeHandle;
    private int uMVPMatrixHandle;

    public PointPlanet() {
        pointCount = 200;
        vertex = new float[pointCount * 3];
        for (int i = 0; i < pointCount; i++) {
            //随机产生每个星星的xyz坐标
            double angleTempJD = Math.PI * 2 * Math.random();
            double angleTempWD = Math.PI * (Math.random() - 0.5f);
            vertex[i * 3] = (float) (UNIT_SIZE * Math.cos(angleTempWD) * Math.sin(angleTempJD));
            vertex[i * 3 + 1] = (float) (UNIT_SIZE * Math.sin(angleTempWD));
            vertex[i * 3 + 2] = (float) (UNIT_SIZE * Math.cos(angleTempWD) * Math.cos(angleTempJD));
        }

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        uPointSizeHandle = GLES20.glGetUniformLocation(mProgram, "uPointSize");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(uPointSizeHandle, 3);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, pointCount);
    }
}
