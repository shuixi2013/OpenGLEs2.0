package cn.dream.android.opengles20.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-24.
 * Torus: 圆环体
 */

public class Torus {

    private final static String TAG = Torus.class.getSimpleName();

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec2 aTexture;\n" +
            "varying vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "   vTexture = aTexture;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "varying vec2 vTexture;\n" +
            "uniform sampler2D uTexture;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(uTexture, vTexture);\n" +                                 // 给片源附上颜色值
            "}";


    private float[] vertex;
    private float[] texture;
    private int pointCount;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int mProgram;
    private int vertexHandle;
    private int aTextureHandle;
    private int[] textureId = new int[1];
    private int uTextureHandle;
    private int uMVPMatrixHandle;

    private float outerRadius;      // 圆环体中心到实心圆圆心的距离
    private float innerRadius;      // 圆环体实心圆半径
    private int unitAngle = 10;     // 角度切分精度值
    private float torusHeight = 0;  // XOZ平面切图，管状体在Y轴上的渐变高度梯度

    private int startAngle =  0;    // X'OY平面切图，开始角度
    private int endAngle = 360;     // X'OY平面切图，结束角度
    private int hStartAngle = 0;    // XOZ平面切图，开始角度
    private int hEndAngle = 360;    // XOZ平面切图，结束角度

    public Torus(Context context, float outerRadius, float innerRadius) {
        long time = System.currentTimeMillis();
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;

        if (outerRadius < innerRadius) {
            throw new IllegalArgumentException("The outerRadius must be bigger than innerRadius");
        }
        
        initData();

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);
        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        aTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        
        GLES20.glGenTextures(1, textureId, 0);
        ShaderUtil.bindTextureId(context, textureId[0], R.mipmap.tietu001);

        Log.e(TAG, "Torus() end time=" + (System.currentTimeMillis() - time));
    }

    /**
     * @param hStartAngle XOZ平面切图，开始角度
     * @param hEndAngle   XOZ平面切图，结束角度
     * @param endAngle    X'OY平面切图，开始角度
     * @param startAngle  X'OY平面切图，结束角度
     */
    public void setTorusAngle(int hStartAngle, int hEndAngle, int startAngle, int endAngle) {
        if (hStartAngle < 0 || startAngle < 0)
            throw new IllegalArgumentException("The hStartAngle and the startAngle must be a natural number");
        if (hStartAngle > hEndAngle)
            throw new IllegalArgumentException("The hStartAngle must be smaller than hEndAngle");
        if (startAngle > endAngle)
            throw new IllegalArgumentException("The startAngle must be smaller than endAngle");
        this.hEndAngle = hEndAngle;
        this.hStartAngle = hStartAngle;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    /**
     * @param unitAngle 角度切分精度值，其值越小，构造的图形越接近真实，但计算量将增加
     */
    public void setUnitAngle(int unitAngle) {
        if (unitAngle < 0)
            throw new IllegalArgumentException("The unitAngle must be bigger than 0");
        if ((this.hEndAngle - this.hStartAngle) % unitAngle != 0)
            Log.e(TAG, "THe unitAngle should be divided by the difference value of the hEndAngle and the hStartAngle");
        if ((this.endAngle - this.startAngle) % unitAngle != 0)
            Log.e(TAG, "THe unitAngle should be divided by the difference value of the endAngle and the startAngle");
        this.unitAngle = unitAngle;
    }

    /**
     * @param torusHeight 螺旋管的高度梯度
     */
    public void setTorusHeight(float torusHeight) {
        if (torusHeight < 0)
            throw new IllegalArgumentException("The torusHeight must be bigger than 0");
        this.torusHeight = torusHeight;
    }

    public void initTorusData() {
        initData();
    }
    /**
     * 根据已知参数计算顶点和纹理坐标
     */
    private void initData() {
        int k = 0;
        float vAddUnit = torusHeight / (hEndAngle / unitAngle);
        pointCount = 6 * endAngle / unitAngle * hEndAngle / unitAngle;
        vertex = new float[pointCount * 3];
        texture = new float[pointCount * 2];

        for (int i = hStartAngle; i < hEndAngle; i = i + unitAngle) {        // XOZ平面切图，从Z轴开始，逆时针角度递增
            double iTemp = Math.toRadians(i);
            double iTemp2 = Math.toRadians(i + unitAngle);

            for (int j = startAngle; j < endAngle; j = j + unitAngle) {    // X'OY平面切图，从X'轴开始，逆时针角度递增
                double resultTemp = innerRadius * Math.cos(Math.toRadians(j));

                float x1 = (float) ((outerRadius + resultTemp) * Math.sin(iTemp));
                float x2 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j +unitAngle))) * Math.sin(iTemp));
                float x3 = (float) ((outerRadius + resultTemp) * Math.sin(iTemp2));
                float x4 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j + unitAngle))) * Math.sin(iTemp2));

                float y1 = (float) (innerRadius * Math.sin(Math.toRadians(j)) +  i / unitAngle * vAddUnit);
                float y2 = (float) (innerRadius * Math.sin(Math.toRadians(j + unitAngle)) + i / unitAngle * vAddUnit);
                float y3 = y1 + vAddUnit;
                float y4 = y2 + vAddUnit;

                float z1 = (float) ((outerRadius + resultTemp) * Math.cos(iTemp));
                float z2 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j +unitAngle))) * Math.cos(iTemp));
                float z3 = (float) ((outerRadius + resultTemp) * Math.cos(iTemp2));
                float z4 = (float) ((outerRadius + innerRadius * Math.cos(Math.toRadians(j + unitAngle))) * Math.cos(iTemp2));

                // 第一个三角形
                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                // 第二个三角形
                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

                vertex[k++] = x4;
                vertex[k++] = y4;
                vertex[k++] = z4;
            }
        }

        k = 0;
        int offSet = 180;   // 图片位置偏移
        for (int i = hEndAngle; i > hStartAngle; i = i - unitAngle) {        // 从右往左切割
            float x1 = (float)(i) / hEndAngle;
            //float x2 = x1;
            float x3 = (float)(i - unitAngle) / hEndAngle;
            //float x4 = x3;

            for (int j = startAngle; j < endAngle; j = j + unitAngle) {    // 从上到下切割
                float y1 = (float)(j + offSet) / endAngle;
                float y2 = (float)(j + offSet + unitAngle) / endAngle;
                //float y3 = y1;
                //float y4 = y2;

                // 第一个三角形贴图
                texture[k++] = x1;
                texture[k++] = y1;

                texture[k++] = x3;
                texture[k++] = y1;

                texture[k++] = x1;
                texture[k++] = y2;

                // 第二个三角形贴图
                texture[k++] = x1;
                texture[k++] = y2;

                texture[k++] = x3;
                texture[k++] = y1;

                texture[k++] = x3;
                texture[k++] = y2;
            }
        }

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(aTextureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glUniform1i(uTextureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, pointCount);
    }
}
