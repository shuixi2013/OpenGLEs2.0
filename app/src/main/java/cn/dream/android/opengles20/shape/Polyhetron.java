package cn.dream.android.opengles20.shape;

import android.content.Context;
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

    private float[] vertex = new float[] {      // 顶点坐标
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0, 0.8f, 0,

            0.5f, -0.5f, 0.5f,
            0, -0.5f, -0.5f,
            0, 0.8f, 0,

            0, 0.8f, 0,
            0, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,

            0, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f
    };

    private float[] normal = new float[] {      // 对应平面每个顶点的法向量
            0, 1, 13f/5f,
            0, 1, 13f/5f,
            0, 1, 13f/5f,

            18f/5f, 1, -1,
            18f/5f, 1, -1,
            18f/5f, 1, -1,

            -2, 5f/13f, -1,
            -2, 5f/13f, -1,
            -2, 5f/13f, -1,

            0, -1, 0,
            0, -1, 0,
            0, -1, 0
    };

    private float[] color = new float[] {       // 顶点颜色
            0.9f, 0.1f, 0.1f, 0,
            0.1f, 0.9f, 0.1f, 0,
            0.1f, 0.1f, 0.9f, 0,

            0.1f, 0.9f, 0.1f, 0,
            0.9f, 0.9f, 0.1f, 0,
            0.1f, 0.1f, 0.9f, 0,

            0.1f, 0.1f, 0.9f, 0,
            0.9f, 0.9f, 0.1f, 0,
            0.9f, 0.1f, 0.1f, 0,

            0.9f, 0.9f, 0.1f, 0,
            0.1f, 0.9f, 0.1f, 0,
            0.9f, 0.1f, 0.1f, 0
    };

    private float[] ambient = new float[] {     // 环境光
            0.35f, 0.35f, 0.35f, 1
    };

    private float[] diffuse = new float[] {     // 漫射光
            0.8f, 0.8f, 0.8f, 1
    };

    private float[] specular = new float[] {     // 反射光
            0.9f, 0.9f, 0.9f, 1
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer ambientBuffer;
    private FloatBuffer diffuseBuffer;
    private FloatBuffer specularBuffer;

    private int mProgram;
    private int vertexHandle;
    private int colorHandle;
    private int ambientHandle;
    private int diffuseHandle;
    private int specularHandle;
    private int lightPositionHandle;
    private int cameraHandle;
    private int normalHandle;
    private int mMatrixHandle;
    private int uMVPMatrixHandle;


    public Polyhetron(Context context) {
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        colorBuffer = BufferUtil.toFloatBuffer(color);
        normalBuffer = BufferUtil.toFloatBuffer(normal);
        ambientBuffer = BufferUtil.toFloatBuffer(ambient);
        diffuseBuffer = BufferUtil.toFloatBuffer(diffuse);
        specularBuffer = BufferUtil.toFloatBuffer(specular);

        mProgram = ShaderUtil.createProgram(ShaderUtil.loadFromAssetsFile("opengles/code/vertex.sh", context.getResources()),
                ShaderUtil.loadFromAssetsFile("opengles/code/fragment.sh", context.getResources()));

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");

        ambientHandle = GLES20.glGetUniformLocation(mProgram, "uAmbient");
        diffuseHandle = GLES20.glGetUniformLocation(mProgram, "uDiffuse");
        specularHandle = GLES20.glGetUniformLocation(mProgram, "uSpecular");

        lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "uLightPosition");
        cameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);

        GLES20.glUniform3fv(lightPositionHandle, 1, MatrixState.lightBuffer);
        GLES20.glUniform3fv(cameraHandle, 1, MatrixState.cameraBuffer);

        GLES20.glUniform4fv(ambientHandle, 1, ambientBuffer);
        GLES20.glUniform4fv(diffuseHandle, 1, diffuseBuffer);
        GLES20.glUniform4fv(specularHandle, 1, specularBuffer);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer);

        GLES20.glEnableVertexAttribArray(ambientHandle);
        GLES20.glEnableVertexAttribArray(diffuseHandle);
        GLES20.glEnableVertexAttribArray(specularHandle);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12);
    }
}
