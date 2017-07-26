package cn.dream.android.opengles20.islandscenery;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;


/**
 * Created by lgb on 17-7-25.
 * WavingFlag
 */

public class WavingWater {

    private final static String TAG = WavingWater.class.getSimpleName();

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec2 aTexture;\n" +                                  // 顶点纹理
            "varying  vec2 vTexture;\n" +
            "uniform float uWidthSpan;\n"+
            "uniform float uStartAngle;\n" +
            "uniform float uType;\n" +                                      // 起伏方向类型
            "void main() {\n" +
            "   float angleSpanH = 4.0 * 3.14159265;\n" +                   // 横向角度总跨度
            "   float startX = -uWidthSpan / 2.0;\n" +                      // 起始X坐标
            "   float currAngle = uStartAngle + ((aPosition.x - startX) / uWidthSpan) * angleSpanH;\n" +
            "   if(uType == 0.0) {\n" +
            "       float ty = sin(currAngle) * 0.05;\n" +
            "       gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y + ty, aPosition.z, 1);\n" +         // 根据总变换矩阵计算此次绘制顶点的位置
            "   } else if(uType == 1.0) {\n" +
            "       float angleSpanY = 4.0 * 3.14159265;\n" +
            "       float uHeightSpan = 0.75 * uWidthSpan;\n" +
            "       float startZ = -uHeightSpan / 2.0;\n" +
            "       float currAngleY =((aPosition.z - startZ) / uHeightSpan) * angleSpanY;\n" +
            "       float tyH = sin(currAngle - currAngleY) * 0.05;\n" +
            "       gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y + tyH, aPosition.z, 1);\n" +
            "   } else {\n" +
            "       float tyH = sin(currAngle) * 0.1;\n" +
            "       float angleSpanY = 4.0 * 3.14159265;\n" +//纵向角度总跨度
            "       float uHeightSpan = 0.75 * uWidthSpan;\n" +//纵向长度总跨度
            "       float startZ = -uHeightSpan / 2.0;\n" +//起始Y坐标
            "       float currAngleY = uStartAngle + 3.14159265 / 3.0 + ((aPosition.z - startZ) / uHeightSpan) * angleSpanY;\n" +
            "       float tyY = sin(currAngleY) * 0.05;\n" +
            "       gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y + tyH + tyY, aPosition.z, 1);\n"+
            "   }\n" +
            "   vTexture = aTexture;\n" +
            "}";


    public final static String FRAGMENT2_CODE = "precision mediump float;\n" +
            "uniform  sampler2D sTexture;\n" +                              // 纹理采样器，代表一幅纹理
            "varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(sTexture, vTexture);\n" +          // 进行纹理采样
            "}";

    private float[] vertex;
    private float[] texture;
    private int vertexCount;

    private float horizontalUnit = 1;
    private float verticalUnit = 1;
    private float type = 2;

    private int mProgram;
    private int uMVPMatrixHandle;
    private int vertexHandle;
    private int uTypeHandle;
    private int uWidthSpanHandle;
    private int uStartAngleHandle;
    private int textureHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private float currStartAngle;
    private float widthSpan = 3.3f;
    private float yOffSet;

    public WavingWater(int mProgram, float width, float height, float yOffSet) {
        this.mProgram = mProgram;
        this.yOffSet = yOffSet;
        initData(width, height);
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uTypeHandle = GLES20.glGetUniformLocation(mProgram, "uType");
        uWidthSpanHandle = GLES20.glGetUniformLocation(mProgram, "uWidthSpan");
        uStartAngleHandle = GLES20.glGetUniformLocation(mProgram, "uStartAngle");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void initData(float width, float height) {
        int wCount = (int) (width + 0.5f);
        int hCount = (int) (height + 0.5f);

        vertexCount = wCount * hCount * 6;
        vertex = new float[3 * vertexCount];
        texture = new float[2 * vertexCount];

        int k = 0;
        for (int j = hCount; j > 0; j--) {
            float z1 = (float) (j - hCount / 2);
            float z2 = (float) (j - 1 - hCount / 2);

            for (int i = 0; i < wCount; i++) {
                float x1 = (float) (i - wCount / 2);
                float x2 = (float) (i + 1 - wCount / 2);

                vertex[k++] = x1;
                vertex[k++] = yOffSet;
                vertex[k++] = z1;

                vertex[k++] = x2;
                vertex[k++] = yOffSet;
                vertex[k++] = z1;

                vertex[k++] = x1;
                vertex[k++] = yOffSet;
                vertex[k++] = z2;

                vertex[k++] = x2;
                vertex[k++] = yOffSet;
                vertex[k++] = z1;

                vertex[k++] = x2;
                vertex[k++] = yOffSet;
                vertex[k++] = z2;

                vertex[k++] = x1;
                vertex[k++] = yOffSet;
                vertex[k++] = z2;
            }
        }
        Log.d(TAG, "initData() vertex vertexCount*3=" + vertexCount * 3 + " k=" + k);

        k = 0;
        for (int j = 0; j < hCount; j++) {
            float z1 = (float) j / hCount;
            float z2 = (float) (j + 1) / hCount;

            for (int i = 0; i < wCount; i++) {
                float x1 = (float) i / wCount;
                float x2 = (float) (i + 1) / wCount;
                texture[k++] = x1;
                texture[k++] = z1;

                texture[k++] = x2;
                texture[k++] = z1;

                texture[k++] = x1;
                texture[k++] = z2;

                texture[k++] = x2;
                texture[k++] = z1;

                texture[k++] = x2;
                texture[k++] = z2;

                texture[k++] = x1;
                texture[k++] = z2;
            }
        }
        Log.d(TAG, "initData() texture vertexCount*2=" + vertexCount * 2 + " k=" + k);
    }

    public void setWidthSpan(float widthSpan) {
        this.widthSpan = widthSpan;
    }

    public float getHorizontalUnit() {
        return horizontalUnit;
    }

    public float getVerticalUnit() {
        return verticalUnit;
    }

    public void setType(float type) {
        this.type = type;
    }

    public void drawSelf(int textureId) {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(uTypeHandle, type);
        GLES20.glUniform1f(uWidthSpanHandle, widthSpan);
        GLES20.glUniform1f(uStartAngleHandle, currStartAngle);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);                 // 设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);      // 绑定纹理id

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        currStartAngle += (float) (Math.PI / 50);
    }
}
