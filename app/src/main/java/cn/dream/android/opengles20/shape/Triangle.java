package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.renderer.TriangleRenderer;
import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-13.
 * Triangle
 */

public class Triangle {

    private final static String TAG = Triangle.class.getSimpleName();

    private int mProgram;                           // 自定义渲染管线着色器程序id

    private String mVertexShader;                   // 顶点着色器代码脚本
    private String mFragmentShader;                 // 片源着色器代码脚本

    private int mVertexHandle;                      // 顶点位置属性引用
    private int mColorHandle;                       // 顶点颜色属性引用
    private int mMVPMatrixHandle;                   // 总变换矩阵属性引用

    private FloatBuffer mVertexBuffer;              // 顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;               // 顶点颜色数据缓冲

    private float[] mVertex = new float[] {         // 顶点坐标数据
            0f, 0.8f, 0f,                           // x,y,z
            -0.8f, 0f, 0f,
            0.8f, 0f, 0f
    };

    private float[] mColor = new float[] {          // 顶点颜色数据
            0.8f, 0f, 0f, 0f,                       // r,g,b,a
            0f, 0.8f, 0f, 0f,
            0f, 0f, 0.8f, 0f
    };

    private float mAngle;                           // 旋转角度

    public Triangle() {
        initVertex();
        initShader();
    }

    private void initVertex() {
        mVertexBuffer = BufferUtil.toFloatBuffer(mVertex);
        mColorBuffer = BufferUtil.toFloatBuffer(mColor);
    }

    private void initShader() {     // 创建、初始化着色器
        mVertexShader = ShaderUtil.VERTEX_CODE;
        mFragmentShader = ShaderUtil.FRAGMENT_CODE;
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        mVertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

    }


    public void drawSelf() {
        GLES20.glUseProgram(mProgram);                      // 制定使用某套shader程序

        GLES20.glVertexAttribPointer(mVertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);   // 将顶点位置数据传送进渲染管线
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);     // 将顶点颜色数据传送进渲染管线

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glEnableVertexAttribArray(mVertexHandle);    // 启用顶点位置数据
        GLES20.glEnableVertexAttribArray(mColorHandle);     // 启用顶点着色数据

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public void setmAngle(float mAngle) {
        this.mAngle += mAngle;
    }

    public float getmAngle() {
        return mAngle;
    }
}
