package cn.dream.android.opengles20.utils;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author lgb 2017-6-14
 * MatrixState:存储系统矩阵状态的类
 */

public class MatrixState {
    private static float[] mProMatrix = new float[16];  // 4x4矩阵 投影用
    private static float[] mVMatrix = new float[16];    // 摄像机位置朝向9参数矩阵
    private static float[] currMatrix;                  // 当前变换矩阵

    private static float[] lightPosition = new float[]{0, 0, 0};    // 点光源位置
    public static FloatBuffer lightBuffer;

    private static float[] directLightPosition = new float[] {0, 0, 0};// 平行光源位置
    public static FloatBuffer directLightBuffer;

    static float[] mMVPMatrix = new float[16];          // 获取具体物体的总变换矩阵
    static float[][] mStack = new float[10][16];        // 保护变换矩阵的栈
    static int stackTop = -1;

    static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);  // 设置摄像机
    public static float[] cameraLocation = new float[3];               // 摄像机位置
    public static FloatBuffer cameraBuffer;

    public static void setInitStack() {                 // 获取不变换初始矩阵
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    public static void pushMatrix() {                   // 保护变换矩阵
        stackTop++;
        for (int i = 0; i < 16; i++) {
            mStack[stackTop][i] = currMatrix[i];
        }
    }

    public static void popMatrix() {                    // 恢复变换矩阵
        for (int i = 0; i < 16; i++) {
            currMatrix[i] = mStack[stackTop][i];
        }
        stackTop--;
    }

    /**
     * 点光源位置
     * @param x x
     * @param y y
     * @param z z
     */
    public static void setLightPosition(float x, float y, float z) {
        lightPosition[0] = x;
        lightPosition[1] = y;
        lightPosition[2] = z;

        if (lightBuffer != null)
            lightBuffer.clear();
        lightBuffer = BufferUtil.toFloatBuffer(lightPosition);
    }

    /**
     * 平行光源位置，当与点光源共存时就体现两个方法的重用性
     * @param x
     * @param y
     * @param z
     */
    public static void setSunLightPosition(float x, float y, float z) {
        directLightPosition[0] = x;
        directLightPosition[1] = y;
        directLightPosition[2] = z;
        if (directLightBuffer != null)
            directLightBuffer.clear();
        directLightBuffer = BufferUtil.toFloatBuffer(directLightPosition);
    }

    /**
     * 设置沿xyz轴移动
     * @param x 坐标
     * @param y 坐标
     * @param z 坐标
     */
    public static void translate(float x, float y, float z) {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    /**
     * 设置沿xyz轴旋转
     * @param angle 旋转角度
     * @param x 坐标
     * @param y 坐标
     * @param z 坐标
     */
    public static void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    /**
     * 设置缩放
     * @param x 坐标
     * @param y 坐标
     * @param z 坐标
     */
    public static void scale(float x, float y, float z) {
        Matrix.scaleM(currMatrix, 0, x, y, z);
    }

    /**
     * @param cx    摄像机位置x
     * @param cy    摄像机位置y
     * @param cz    摄像机位置z
     * @param tx    摄像机目标点x
     * @param ty    摄像机目标点y
     * @param tz    摄像机目标点z
     * @param upx   摄像机UP向量X分量
     * @param upy   摄像机UP向量Y分量
     * @param upz   摄像机UP向量Z分量
     */
    public static void setCamera(float cx, float cy, float cz, float tx,  float ty, float tz,
                    float upx, float upy, float upz) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;
        if (cameraBuffer != null)
            cameraBuffer.clear();
        cameraBuffer = BufferUtil.toFloatBuffer(cameraLocation);
    }

    /**
     * 设置透视投影参数
     * @param left      near面的left
     * @param right     near面的right
     * @param bottom    near面的bottom
     * @param top       near面的top
     * @param near      near面距离
     * @param far       far面距离
     */
    public static void setProjectFrustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mProMatrix, 0, left, right, bottom, top, near, far);
    }

    /**
     * 设置正交投影参数
     * @param left      near面的left
     * @param right     near面的right
     * @param bottom    near面的bottom
     * @param top       near面的top
     * @param near      near面距离
     * @param far       far面距离
     */
    public static void setProjectOrtho(float left, float right, float bottom, float top, float near, float far) {
        Matrix.orthoM(mProMatrix, 0, left, right, bottom, top, near, far);
    }



    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    /**
     * 获取具体物体的变换矩阵
     * @return
     */
    public static float[] getMMatrix() {
        return currMatrix;
    }
}
