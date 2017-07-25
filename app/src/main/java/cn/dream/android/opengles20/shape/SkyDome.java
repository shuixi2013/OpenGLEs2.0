package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.Constant;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-18.
 * SkyDome
 */

public class SkyDome {

    private final static String TAG = SkyDome.class.getSimpleName();

    private float[] vertex;
    private float[] texture;
    private int vertexCount;


    private int mProgram;
    private int vertexHandle;
    private int textureHandle;
    private int sTextureHandle;          // 球纹理引用

    private int uMVPMatrixHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;


    public SkyDome(float radios) {
        long time = System.currentTimeMillis();
        initVertex(radios);
        Log.e(TAG, "TextureBall() initVertex take time =" + (System.currentTimeMillis() - time));

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        initProgram(ShaderUtil.VERTEX_CODE, ShaderUtil.FRAGMENT2_CODE);
        Log.e(TAG, "TextureBall() end");
    }

    public void initProgram(String vertexCode, String fragCode) {
        mProgram = ShaderUtil.createProgram(vertexCode, fragCode);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        sTextureHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void initVertex(float radios) {
        float radius = 1f * radios;
        int unitAngle = 10;
        int startVAngle = 0, endVAngle = 90;
        int startHAngle = 0, endHAngle = 360;
        vertexCount = endHAngle / unitAngle * endVAngle / unitAngle * 6;
        vertex = new float[3 * vertexCount];
        texture = new float[2 * vertexCount];

        int k = 0;
        // 初始化球体顶点数组
        for (int i = startVAngle; i < endVAngle; i = i + unitAngle) {
            float y1 = (float) (radius * Math.cos(Math.toRadians(i)));
            float y2 = y1;
            float y3 = (float) (radius * Math.cos(Math.toRadians(i + unitAngle)));
            float y4 = y3;
            float radius1 = (float) (radius * Math.sin(Math.toRadians(i)));
            float radius2 = (float) (radius * Math.sin(Math.toRadians(i + unitAngle)));

            for (int j = startHAngle; j < endHAngle; j = j + unitAngle) {
                double jRadians = Math.toRadians(j);
                double jRadians2 = Math.toRadians(j + unitAngle);

                float x1 = (float) (radius1 * Math.cos(jRadians));
                float x2 = (float) (radius1 * Math.cos(jRadians2));
                float x3 = (float) (radius2 * Math.cos(jRadians));
                float x4 = (float) (radius2 * Math.cos(jRadians2));

                float z1 = (float) (radius1 * Math.sin(jRadians));
                float z2 = (float) (radius1 * Math.sin(jRadians2));
                float z3 = (float) (radius2 * Math.sin(jRadians));
                float z4 = (float) (radius2 * Math.sin(jRadians2));

                // 第一个三角形
                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

                // 第二个三角形
                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                vertex[k++] = x4;
                vertex[k++] = y4;
                vertex[k++] = z4;
            }
        }
        // 初始化纹理顶点数据
        int hCount = endHAngle / unitAngle;
        int vCount = endVAngle / unitAngle;
        k = 0;
        for (int i = 0; i < vCount; i++) {
            float t1 = (float) i / vCount;          // 注意不是(float) (i / vCount)
            float t2 = (float) (i + 1)/ vCount;
            for (int j = hCount; j > 0; j--) {
                float s1 = (float) j / hCount;
                float s2 = (float) (j - 1)/ hCount;
                // 第一个三角形对应的纹理顶点
                texture[k++] = s1;
                texture[k++] = t1;

                texture[k++] = s2;
                texture[k++] = t1;

                texture[k++] = s1;
                texture[k++] = t2;

                // 第二个三角形对应的纹理顶点
                texture[k++] = s1;
                texture[k++] = t2;

                texture[k++] = s2;
                texture[k++] = t1;

                texture[k++] = s2;
                texture[k++] = t2;
            }
        }
    }

    public void drawSelf(int textureId) {
        GLES20.glUseProgram(mProgram);
        MatrixState.pushMatrix();
        MatrixState.translate(MatrixState.cameraLocation[0], Constant.LAND_HIGH_ADJUST, MatrixState.cameraLocation[2] - 12);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);                 // 设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);      // 绑定纹理id
        GLES20.glUniform1i(sTextureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        MatrixState.popMatrix();
    }
}
