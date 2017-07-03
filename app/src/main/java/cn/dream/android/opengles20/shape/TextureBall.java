package cn.dream.android.opengles20.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-20.
 * TextureSquare
 */

public class TextureBall {

    private final static String TAG = TextureBall.class.getSimpleName();

    private float[] vertex;
    private float[] texture;
    private int vertexCount;

    private float[] ambient = new float[]{     // 环境光
            0.5f, 0.5f, 0.5f, 1
    };

    private float[] diffuse = new float[]{     // 漫射光
            0.8f, 0.8f, 0.8f, 1
    };

    private float[] specular = new float[]{     // 反射光
            0.1f, 0.1f, 0.1f, 1
    };

    private int mProgram;
    private int vertexHandle;
    private int normalHandle;               // 法向量
    private int textureHandle;

    private int ambientHandle;
    private int diffuseHandle;
    private int specularHandle;
    private int lightPositionHandle;        // 点光源位置
    private int sTextureDayHandle;          // 球纹理引用

    private int cameraHandle;
    private int mMatrixHandle;
    private int uMVPMatrixHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private FloatBuffer ambientBuffer;
    private FloatBuffer diffuseBuffer;
    private FloatBuffer specularBuffer;

    public TextureBall(Context context, float radios) {
        long time = System.currentTimeMillis();
        initVertex(radios);
        Log.e(TAG, "TextureBall() initVertex take time =" + (System.currentTimeMillis() - time));

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        ambientBuffer = BufferUtil.toFloatBuffer(ambient);
        diffuseBuffer = BufferUtil.toFloatBuffer(diffuse);
        specularBuffer = BufferUtil.toFloatBuffer(specular);

        initProgram(context, "opengles/code/vertex_moon.sh", "opengles/code/fragment_moon_mix.sh");
        Log.e(TAG, "TextureBall() end");
    }

    public void initProgram(Context context, String assetPathVertex, String assetPathFrag) {
        mProgram = ShaderUtil.createProgram(ShaderUtil.loadFromAssetsFile(assetPathVertex, context.getResources()),
                ShaderUtil.loadFromAssetsFile(assetPathFrag, context.getResources()));

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        ambientHandle = GLES20.glGetUniformLocation(mProgram, "uAmbient");
        diffuseHandle = GLES20.glGetUniformLocation(mProgram, "uDiffuse");
        specularHandle = GLES20.glGetUniformLocation(mProgram, "uSpecular");
        sTextureDayHandle = GLES20.glGetUniformLocation(mProgram, "aTextureDay");

        lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "uLightPosition");
        cameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void initVertex(float radios) {
        float radius = 1f * radios;
        int unitAngle = 10;
        int startVAngle = 0, endVAngle = 180;
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

    /**
     * @param ads the abbreviation of the ambient, diffuse and the specular in a order
     */
    public void setADS(float[] ads){
        if (ads.length == 12) {
            ambient[0] = ads[0];
            ambient[1] = ads[1];
            ambient[2] = ads[2];
            ambient[3] = ads[3];

            diffuse[0] = ads[4];
            diffuse[1] = ads[5];
            diffuse[2] = ads[6];
            diffuse[3] = ads[7];

            specular[0] = ads[8];
            specular[1] = ads[9];
            specular[2] = ads[10];
            specular[3] = ads[11];

            ambientBuffer = BufferUtil.toFloatBuffer(ambient);
            diffuseBuffer = BufferUtil.toFloatBuffer(diffuse);
            specularBuffer = BufferUtil.toFloatBuffer(specular);
        } else throw new IllegalArgumentException("the ads's length must be bigger than 12!");
    }

    public void drawSelf(int textureId) {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);

        GLES20.glUniform3fv(lightPositionHandle, 1, MatrixState.lightBuffer);
        GLES20.glUniform3fv(cameraHandle, 1, MatrixState.cameraBuffer);

        GLES20.glUniform4fv(ambientHandle, 1, ambientBuffer);
        GLES20.glUniform4fv(diffuseHandle, 1, diffuseBuffer);
        GLES20.glUniform4fv(specularHandle, 1, specularBuffer);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);                 // 设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);      // 绑定纹理id
        GLES20.glUniform1i(sTextureDayHandle, 0);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
