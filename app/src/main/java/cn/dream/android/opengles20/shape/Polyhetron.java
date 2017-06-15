package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-14.
 * Polyhetron:多面体
 */

public class Polyhetron {

    private final static String TAG = Polyhetron.class.getSimpleName();

    private float[] vertex = new float[] {      // 顶点坐标
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0, 0.8f, 0,

            0.5f, -0.5f, 0.5f,
            0, -0.5f, -0.5f,
            0, 0.8f, 0,

            0, 0.8f, 0,
            0, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,

            0, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f
    };

    private float[] normal = new float[] {      // 对应平面每个顶点的法向量
            0, 1, 13f/5f,
            0, 1, 13f/5f,
            0, 1, 13f/5f,

            18f/5f, 1, -1,
            18f/5f, 1, -1,
            18f/5f, 1, -1,

            -2, 5f/13f, -1,
            -2, 5f/13f, -1,
            -2, 5f/13f, -1,

            0, -1, 0,
            0, -1, 0,
            0, -1, 0
    };

    private float[] color = new float[] {       // 顶点颜色
            0.9f, 0.1f, 0.1f, 0,
            0.1f, 0.9f, 0.1f, 0,
            0.1f, 0.1f, 0.9f, 0,

            0.1f, 0.9f, 0.1f, 0,
            0.9f, 0.9f, 0.1f, 0,
            0.1f, 0.1f, 0.9f, 0,

            0.1f, 0.1f, 0.9f, 0,
            0.9f, 0.9f, 0.1f, 0,
            0.9f, 0.1f, 0.1f, 0,

            0.9f, 0.9f, 0.1f, 0,
            0.1f, 0.9f, 0.1f, 0,
            0.9f, 0.1f, 0.1f, 0
    };

    private float[] ambient = new float[] {     // 环境光
            0.6f, 0.6f, 0.6f, 1
    };


    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;

    private int mProgram;
    private int vertexHandle;
    private int colorHandle;
    private int ambientHandle;
    private int lightPositionHandle;
    private int normalHandle;
    private int mMatrixHandle;
    private int uMVPMatrixHandle;

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "uniform mat4 uMMatrix;\n" +                                    // 变换矩阵，如平移，缩放，旋转
            "uniform vec3 uLightPosition;\n" +                              // 光源位置
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec4 aColor;\n" +                                    // 顶点颜色
            "attribute vec3 aNormal;\n" +                                   // 顶点法向量
            "varying vec3 vPosition;\n" +                                    // 用于传递给片元着色器的易变变亮
            "varying vec4 vColor;\n" +                                      // 用于传递给片元着色器的易变变量
            "varying vec4 vDiffuse;\n" +                                       // 散射光易变变量
            "void lightDiffuse(in vec3 normal, inout vec4 diffuse, " +      // 输入法向量，输出计算后的散射光
            "   in vec3 lightPosition, in vec4 lightDiffuse) {\n"+         // 输入光源位置，输入漫射光强度
            "   vec3 tempNormal = aPosition + normal;\n" +                  // 计算变换后的法向量
            "   vec3 newNormal = (uMMatrix * vec4(tempNormal, 1)).xyz - (uMMatrix * vec4(aPosition, 1)).xyz;\n" +
            "   newNormal = normalize(newNormal);\n" +                      // 对法向量规格化
            "   vec3 vp = normalize(lightPosition - (uMMatrix * vec4(aPosition, 1)).xyz);\n" +    // 计算表面点到光源位置的向量vp
            "   float mDotViewPosition = max(0.0, dot(newNormal, vp));\n" + // 求法向量和vp向量的点积　与0的最大值
            "   diffuse = lightDiffuse * mDotViewPosition;\n" +             // 计算散射光的最终结果
            "}\n" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "   vPosition = aPosition;\n" +                                 // 将接收的顶点位置传递给片元着色器
            "   vColor = aColor;\n" +                                       // 将接收的顶点颜色传递给片元着色器
            "   vec4 targetDiffuse = vec4(0, 0, 0, 0);\n" +
            "   lightDiffuse(normalize(aNormal), targetDiffuse, uLightPosition, vec4(1, 1, 1, 1));\n" +
            "   vDiffuse = targetDiffuse;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "varying vec4 vDiffuse;\n" +
            "uniform vec4 uAmbient;\n" +
            "void main() {\n" +
            "   gl_FragColor = vColor * uAmbient * vDiffuse;\n" +          // 给片源附上颜色值
            "}";

    public Polyhetron() {
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        colorBuffer = BufferUtil.toFloatBuffer(color);
        normalBuffer = BufferUtil.toFloatBuffer(normal);

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");

        ambientHandle = GLES20.glGetUniformLocation(mProgram, "uAmbient");
        lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "uLightPosition");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES20.glUniform4fv(ambientHandle, 1, ambient, 0);
        GLES20.glUniform3fv(lightPositionHandle, 1, MatrixState.lightBuffer);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12);
    }
}
