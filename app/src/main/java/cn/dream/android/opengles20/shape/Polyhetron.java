package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-14.
 * Polyhetron:多面体
 */

public class Polyhetron {

    private final static String TAG = Polyhetron.class.getSimpleName();

    private float[] vertex = new float[] {
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0, 0.8f, 0,
            0, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
    };

    private float[] color = new float[] {
            0.9f, 0.2f, 0.3f, 0,
            0.1f, 0.6f, 0.3f, 0,
            0.0f, 0.6f, 0.3f, 0,
            0.3f, 0.2f, 0.9f, 0,
            0.9f, 0.2f, 0.3f, 0,
            0.1f, 0.6f, 0.3f, 0
    };

    private float[] ambient = new float[] {
            0.6f, 0.6f, 0.6f, 1
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int mProgram;
    private int vertexHandle;
    private int colorHandle;
    private int ambientHandle;
    private int uMVPMatrixHandle;

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec4 aColor;\n" +                                    // 顶点颜色
            //"attribute vec4 aAmbient"+
            "varying  vec4 vColor;\n" +                                     // 用于传递给片元着色器的易变变量
            //"varying vec4 vAmbient;\n"+
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "   vColor = aColor;\n" +                                       // 将接收的顶点颜色传递给片元着色器
            //"   vAmbient = vec4(0.5,0.5,0.5,1.0);\n"+
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying  vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            //"varying vec4 vAmbient;\n" +
            "uniform vec4 uAmbient;\n" +
            "void main() {\n" +
            "   gl_FragColor = vColor * uAmbient;\n" +                                 // 给片源附上颜色值
            "}";

    public Polyhetron() {
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        colorBuffer = BufferUtil.toFloatBuffer(color);

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        ambientHandle = GLES20.glGetUniformLocation(mProgram, "uAmbient");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform4fv(ambientHandle, 1, ambient, 0);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 6);
    }
}
