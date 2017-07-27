package cn.dream.android.opengles20.islandscenery;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.shape.Mountain;
import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.Constant;
import cn.dream.android.opengles20.utils.MatrixState;

import static cn.dream.android.opengles20.utils.Constant.yArray;

/**
 * Created by lgb on 17-7-27.
 * Reptile
 */

public class Reptile {

    private final static String TAG = Reptile.class.getSimpleName();

    private float[] vertex = new float[]{
            0.5f, 0.5f, 0,
            0.5f, -0.5f, 0,
            -0.5f, 0.5f, 0,
            -0.5f, -0.5f, 0
    };

    private float[] vertexOrigin = new float[]{
            0.5f, 0.5f, 0,
            0.5f, -0.5f, 0,
            -0.5f, 0.5f, 0,
            -0.5f, -0.5f, 0
    };

    private float[] texture = new float[]{
            1f, 0,
            1f, 1,
            0, 0,
            0, 1
    };

    private float unit = 1;

    private int mProgram;
    private int uMVPMatrixHandle;
    private int vertexHandle;
    private int textureHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int startIndex;
    private int curIndex;
    private int endIndex;
    private boolean isRun;


    /**
     * @param mProgram      程序索引
     * @param unit          缩放比例
     * @param startIndex    爬行动物开始爬行的位置
     * @param endIndex      爬行动物结束爬行的位置
     */
    public Reptile(int mProgram, int unit, int startIndex, int endIndex) {
        this.mProgram = mProgram;
        this.unit = unit;
        this.startIndex = startIndex;
        this.endIndex = endIndex - 1;
        
        this.unit = unit;
        for (int i = 0; i < vertexOrigin.length; i++) {
            vertexOrigin[i] = vertexOrigin[i] * this.unit;
            vertex[i] = vertex[i] * this.unit;
        }
        
        vertexBuffer = BufferUtil.toFloatBuffer(vertexOrigin);
        textureBuffer = BufferUtil.toFloatBuffer(texture);
        
        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        startRun();
    }

    private void startRun() {
        isRun = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                curIndex = Reptile.this.startIndex;
                isRun = true;
                int posJ = Constant.yArray[0].length / 2 + 3;
                while (isRun) {
                    float tX = -Mountain.UNIT_SIZE * (Constant.yArray.length - 1) / 2 + curIndex * Mountain.UNIT_SIZE;
                    float tY;// = yArray[posJ][curIndex];
                    float tZ = -Mountain.UNIT_SIZE * (Constant.yArray[0].length - 1) / 2 + posJ * Mountain.UNIT_SIZE;

                    if (vertex[0] + 0.05f < vertexOrigin[0] + tX + Mountain.UNIT_SIZE) {
                        //Log.d(TAG, "running " + (vertex[0] + 0.05f));
                        for (int i = 0; i < vertex.length; i = i + 3) {
                            vertex[i] = vertex[i] + 0.05f;
                            vertex[i + 1] = vertex[i + 1];
                            vertex[i + 2] = vertex[i + 2];
                        }
                    } else {
                        curIndex++;
                        if (curIndex >  Reptile.this.endIndex)
                            curIndex = 0;
                        tX = -Mountain.UNIT_SIZE * (Constant.yArray.length - 1) / 2 + curIndex * Mountain.UNIT_SIZE;
                        tY = yArray[posJ][curIndex];
                        tZ = -Mountain.UNIT_SIZE * (Constant.yArray[0].length - 1) / 2 + posJ * Mountain.UNIT_SIZE;

                        for (int i = 0; i < vertex.length; i = i + 3) {
                            vertex[i] = vertexOrigin[i] + tX + 0.05f;
                            vertex[i + 1] = vertexOrigin[i + 1] + tY + 0.02f;
                            vertex[i + 2] = vertexOrigin[i + 2] + tZ;
                        }
                    }

                    vertexBuffer = BufferUtil.toFloatBuffer(vertex);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void onDestroy() {
        isRun = false;
    }
}
