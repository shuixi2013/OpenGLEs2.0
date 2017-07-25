package cn.dream.android.opengles20.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;


/**
 * Created by lgb on 17-7-25.
 * WavingFlag
 */

public class WavingFlag {

    private final static String TAG = WavingFlag.class.getSimpleName();

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
            "       float tz = sin(currAngle) * 0.1;\n" +
            "       gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y, tz, 1);\n" +         // 根据总变换矩阵计算此次绘制顶点的位置
            "   } else if(uType == 1.0) {\n" +
            "       float angleSpanZ = 4.0 * 3.14159265;\n" +
            "       float uHeightSpan = 0.75 * uWidthSpan;\n" +
            "       float startY = -uHeightSpan / 2.0;\n" +
            "       float currAngleZ =((aPosition.y - startY) / uHeightSpan) * angleSpanZ;\n" +
            "       float tzH = sin(currAngle - currAngleZ) * 0.1;\n" +
            "       gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y, tzH, 1);\n" +
            "   } else {\n" +
            "       float tzH = sin(currAngle) * 0.1;\n" +
            "       float angleSpanZ = 4.0 * 3.14159265;\n" +//纵向角度总跨度
            "       float uHeightSpan = 0.75 * uWidthSpan;\n" +//纵向长度总跨度
            "       float startY = -uHeightSpan / 2.0;\n" +//起始Y坐标
            "       float currAngleZ = uStartAngle + 3.14159265 / 3.0 + ((aPosition.y - startY) / uHeightSpan) * angleSpanZ;\n" +
            "       float tzZ = sin(currAngleZ) * 0.1;\n" +
            "       gl_Position = uMVPMatrix * vec4(aPosition.x,aPosition.y,tzH+tzZ,1);\n"+
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
    private float type = 0;

    private int mProgram;
    private int uMVPMatrixHandle;
    private int vertexHandle;
    private int uTypeHandle;
    private int uWidthSpanHandle;
    private int uStartAngleHandle;
    private int textureHandle;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int[] texturesId = new int[1];
    private float currStartAngle;
    private float widthSpan = 3.3f;

    public WavingFlag(Context context, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);    // 图片的宽、高严格来讲是2的倍数

        initData(bitmap.getWidth(), bitmap.getHeight());
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT2_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        uTypeHandle = GLES20.glGetUniformLocation(mProgram, "uType");
        uWidthSpanHandle = GLES20.glGetUniformLocation(mProgram, "uWidthSpan");
        uStartAngleHandle = GLES20.glGetUniformLocation(mProgram, "uStartAngle");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glGenTextures(1, texturesId, 0);                     // 获取产生的纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[0]);  // 绑定纹理id

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);      // 设置MIN时为最近采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);       // 设置MAG时为线性采样方式

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);           // 沿着S轴方向拉伸
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);           // 沿着T轴方向拉伸

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0); // 实际加载纹理进显存，参数解释：纹理类型；纹理的层次，０表示基本图像，可以理解为直接贴图；；纹理边框尺寸　
        bitmap.recycle();                                       // 加载纹理成功后回收bitmap
    }

    private void initData(int width, int height) {
        int step = 16;  // 因图片刚好是512*512
        int wCount = width / step;
        int hCount = height / step;

        vertexCount = wCount * hCount * 6;
        vertex = new float[3 * vertexCount];
        texture = new float[2 * vertexCount];

        int k = 0;
        for (int j = hCount; j > 0; j--) {
            float y1 = (float) j / hCount;
            float y2 = (float) (j - 1) / hCount;

            for (int i = 0; i < wCount; i++) {
                float x1 = (float) i / wCount;
                float x2 = (float) (i + 1) / wCount;

                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = 0;

                vertex[k++] = x2;
                vertex[k++] = y1;
                vertex[k++] = 0;

                vertex[k++] = x1;
                vertex[k++] = y2;
                vertex[k++] = 0;

                vertex[k++] = x2;
                vertex[k++] = y1;
                vertex[k++] = 0;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = 0;

                vertex[k++] = x1;
                vertex[k++] = y2;
                vertex[k++] = 0;
            }
        }

        k = 0;
        for (int j = 0; j < hCount; j++) {
            float y1 = (float) j / hCount;
            float y2 = (float) (j + 1) / hCount;

            for (int i = 0; i < wCount; i++) {
                float x1 = (float) i / wCount;
                float x2 = (float) (i + 1) / wCount;
                texture[k++] = x1;
                texture[k++] = y1;

                texture[k++] = x2;
                texture[k++] = y1;

                texture[k++] = x1;
                texture[k++] = y2;

                texture[k++] = x2;
                texture[k++] = y1;

                texture[k++] = x2;
                texture[k++] = y2;

                texture[k++] = x1;
                texture[k++] = y2;
            }
        }
        setUnit(3, 2);
        Log.d(TAG, "initData() vertexCount*3=" + vertexCount * 3 + " k=" + k);
    }

    public void setUnit(float horizontalUnit, float verticalUnit) {
        this.horizontalUnit = horizontalUnit;
        this.verticalUnit = verticalUnit;
        for (int i = 0; i < vertex.length; i = i + 3) {
            vertex[i] = vertex[i] * this.horizontalUnit;
            vertex[i + 1] = vertex[i + 1] * this.verticalUnit;
        }
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
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

    public void drawSelf() {
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[0]);  // 绑定纹理id

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        currStartAngle += (float) (Math.PI / 16);
    }
}
