package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-13.
 * Tree
 */

public class Tree {

    private final static String TAG = Tree.class.getSimpleName();

    private final static float UNIT_SIZE = 3.0f;

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

    public Tree(float posX, float posY, float posZ) {
        Log.d("Tree", "Tree() " + posX + " " + posY + " " + posZ);
        initVertex(posX, posY, posZ);
        initTexture();
        initShader();
    }

    private void initVertex(float posX, float posY, float posZ) {
        vertexCount = 4;
        float[] vertex = new float[] {
                0.4f * UNIT_SIZE + posX, 0f + posY, posZ,
                -0.4f * UNIT_SIZE + posX, 0f + posY, posZ,
                0.4f * UNIT_SIZE + posX, 1f * UNIT_SIZE + posY, posZ,
                -0.4f * UNIT_SIZE + posX, 1f * UNIT_SIZE + posY, posZ
        };
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
    }

    private void initTexture() {
        float[] texture = new float[] {
                1, 1,
                0, 1,
                1, 0,
                0, 0
        };
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
    }
}
