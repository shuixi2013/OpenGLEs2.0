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

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int mProgram;
    private int vertexHandle;
    private int colorHandle;
    private int uMVPMatrixHandle;

    public Polyhetron() {
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        colorBuffer = BufferUtil.toFloatBuffer(color);

        mProgram = ShaderUtil.createProgram(ShaderUtil.VERTEX_CODE, ShaderUtil.FRAGMENT_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 6);
    }
}
