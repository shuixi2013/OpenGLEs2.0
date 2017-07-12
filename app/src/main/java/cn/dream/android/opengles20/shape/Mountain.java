package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

import static cn.dream.android.opengles20.utils.Constant.yArray;

/**
 * Created by lgb on 17-7-12.
 * Mountain: use the gray scale image
 */

public class Mountain {

    private final static String TAG = Mountain.class.getSimpleName();

    private final static float UNIT_SIZE = 1.0f;

    private final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" +    //总变换矩阵
            "attribute vec3 aPosition;\n" +                                     //顶点位置
            "attribute vec2 aTexCoor;\n" +                                      //顶点纹理坐标
            "varying vec2 vTextureCoord;\n" +                                   //用于传递给片元着色器的变量
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1); \n" +             //根据总变换矩阵计算此次绘制此顶点位置
            "   vTextureCoord = aTexCoor;\n" +                                  //将接收的纹理坐标传递给片元着色器
            "}";

    private final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +                               //接收从顶点着色器过来的参数
            "uniform sampler2D sTexture;\n" +                               //纹理内容数据
            "void main() {\n" +
            "   gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +     //给此片元从纹理中采样出颜色值
            "}";

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int mProgram;
    private int vertexHandle;
    private int textureHandle;
    private int textureGrassHandle;
    private int uMVPMatrixHandle;

    private int vertexCount;

    public Mountain(float[][] data) {
        initVertex(data);
        initTexture(data[0].length - 1, data.length - 1);
        initShader();
    }

    private void initVertex(float[][] data) {
        int rows = data.length - 1;
        int cols = data[0].length - 1;

        vertexCount = rows * cols * 2 * 3;              // 灰度图中每个像素进行构建一个矩形
        float[] vertex = new float[vertexCount * 3];
        int k = 0;
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                //计算当前格子左上侧点坐标 
                float zsx = -UNIT_SIZE * cols / 2 + i * UNIT_SIZE;
                float zsz = -UNIT_SIZE * rows / 2 + j * UNIT_SIZE;

                vertex[k++] = zsx;
                vertex[k++] = yArray[j][i];
                vertex[k++] = zsz;

                vertex[k++] = zsx;
                vertex[k++] = yArray[j + 1][i];
                vertex[k++] = zsz + UNIT_SIZE;

                vertex[k++] = zsx + UNIT_SIZE;
                vertex[k++] = yArray[j][i + 1];
                vertex[k++] = zsz;

                vertex[k++] = zsx + UNIT_SIZE;
                vertex[k++] = yArray[j][i + 1];
                vertex[k++] = zsz;

                vertex[k++] = zsx;
                vertex[k++] = yArray[j + 1][i];
                vertex[k++] = zsz + UNIT_SIZE;

                vertex[k++] = zsx + UNIT_SIZE;
                vertex[k++] = yArray[j + 1][i + 1];
                vertex[k++] = zsz + UNIT_SIZE;
            }
        }

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
    }

    //自动切分纹理产生纹理数组的方法
    private void initTexture(int bw, int bh) {
        float[] texture = new float[bw * bh * 6 * 2];
        float sizew = 16.0f / bw;                       // 列数
        float sizeh = 16.0f / bh;                       // 行数
        int c = 0;
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s = j * sizew;
                float t = i * sizeh;

                texture[c++] = s;
                texture[c++] = t;

                texture[c++] = s;
                texture[c++] = t + sizeh;

                texture[c++] = s + sizew;
                texture[c++] = t;

                texture[c++] = s + sizew;
                texture[c++] = t;

                texture[c++] = s;
                texture[c++] = t + sizeh;

                texture[c++] = s + sizew;
                texture[c++] = t + sizeh;
            }
        }
        textureBuffer = BufferUtil.toFloatBuffer(texture);
    }

    private void initShader() {

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        textureGrassHandle = GLES20.glGetUniformLocation(mProgram, "vTextureCoord");
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
        GLES20.glUniform1i(textureGrassHandle, 0);       // 使用0号纹理

        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
