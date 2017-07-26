package cn.dream.android.opengles20.islandscenery;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-26.
 * CoconutLeaf
 */

public class CoconutLeaf implements Comparable<CoconutLeaf>{

    private final static String TAG = CoconutTree.class.getSimpleName();


    private float[] vertex;
    private float[] texture;

    private int vertexCount;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int mProgram;
    private int vertexHandle;
    private int textureHandle;
    private int sTextureHandle;
    private int uMVPMatrixHandle;

    public CoconutLeaf(int mProgram, float posX, float posY, float posZ, int xzAngle) {
        initData(posX, posY, posZ, xzAngle);

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        this.mProgram = mProgram;
        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        sTextureHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void initData(float posX, float posY, float posZ, int xzAngle) {
        vertexCount = 4;
        float unit = 1f;
        vertex = new float[]{   // 标准尺寸下在ｘｏｙ平面的贴图坐标
                0f * unit, (0f - 0.335f) * unit, 0f * unit,
                -1f * unit, (0f - 0.335f) * unit, 0f * unit,
                0f * unit, (0.5f - 0.335f) * unit, 0f * unit,
                -1f * unit, (0.5f - 0.335f) * unit, 0f * unit
        };

        for (int i = 0; i < vertexCount * 3; i = i + 3) {   // 树叶自身旋转
            vertex[i] = (float) (vertex[i] * Math.cos(Math.toRadians(xzAngle)));
            //vertex[i + 1] = ;
            vertex[i + 2] = (float) (vertex[i] * Math.sin(Math.toRadians(xzAngle)));
        }

        for (int i = 0; i < vertexCount * 3; i = i + 3) {   // 树叶移至制定位置
            vertex[i] += posX;
            vertex[i + 1] += posY;
            vertex[i + 2] += posZ;
        }
        texture = new float[]{
                1, 1,
                0, 1,
                1, 0,
                0, 0
        };
    }

    public void drawSelf(int textureId) {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1f(sTextureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
    }

    @Override
    public int compareTo(CoconutLeaf o) {
        return 0;
    }
}
