package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * @author lgb 2017-6-14
 *         PointsOrLines:颜色点或线
 */

public class PointsOrLines {
    private int mProgram;               // 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;      // 总变换矩阵引用
    private int maPositionHandle;       // 顶点位置属性引用
    private int maColorHandle;          // 顶点颜色属性引用
    private String mVertexShader;       // 顶点着色器代码脚本
    private String mFragmentShader;     // 片元着色器代码脚本

    private FloatBuffer mVertexBuffer;  // 顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;   // 顶点着色数据缓冲
    private int vCount = 0;
    private int mode = 0;               // 模式

    public PointsOrLines() {
        initVertexData();
        initShader();
    }

    private void initVertexData() {
        vCount = 5;

        float vertices[] = new float[]{
                0, 0, 0, 1f, 1f, 0,
                -1f, 1f, 0,
                -1f, -1f, 0,
                1f, -1f, 0,};

        mVertexBuffer = BufferUtil.toFloatBuffer(vertices);

        // 顶点颜色值数组，每个顶点4个色彩值RGBA
        float colors[] = new float[]{
                1, 1, 0, 0,// 黄
                1, 1, 1, 0,// 白
                0, 1, 0, 0,// 绿
                1, 1, 1, 0,// 白
                1, 1, 0, 0,// 黄
        };
        mColorBuffer = BufferUtil.toFloatBuffer(colors);
    }

    private void initShader() {
        mVertexShader = ShaderUtil.VERTEX_CODE;         // 加载顶点着色器的脚本内容
        mFragmentShader = ShaderUtil.FRAGMENT_CODE;     // 加载片元着色器的脚本内容
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);    // 基于顶点着色器与片元着色器创建程序
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");   // 获取程序中顶点位置属性引用id
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");         // 获取程序中顶点颜色属性引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");// 获取程序中总变换矩阵引用id
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);  // 制定使用某套shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);            // 将最终变换矩阵传入shader程序
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);    // 为画笔指定顶点位置数据
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);        // 为画笔指定顶点着色数据

        GLES20.glEnableVertexAttribArray(maPositionHandle); // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maColorHandle);    // 允许顶点颜色数据数组

        GLES20.glLineWidth(10); // 设置线的宽度

        switch (mode) {
            case 0:
                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);       // GL_POINTS方式
                break;
            case 1:
                GLES20.glDrawArrays(GLES20.GL_LINES, 0, vCount);        // GL_LINES方式
                break;
            case 2:
                GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vCount);   // GL_LINE_STRIP方式
                break;
            case 3:
                GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, vCount);    // GL_LINE_LOOP方式
                break;
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (this.mode > 3 || this.mode < 0)
            this.mode = 0;
    }

    public void addMode() {
        this.mode++;
        if (this.mode > 3)
            this.mode = 0;
    }
}
