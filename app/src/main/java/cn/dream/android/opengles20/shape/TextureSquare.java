package cn.dream.android.opengles20.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-6-20.
 * TextureSquare
 */

public class TextureSquare {

    private float[] vertex = new float[]{
            0.8f, 0.8f, 0,
            0.8f, -0.8f, 0,
            -0.8f, 0.8f, 0,
            -0.8f, -0.8f, 0
    };

//    private float[] color = new float[]{
//            0.9f, 0.1f, 0.2f, 1f,
//            0.1f, 0.9f, 0.2f, 1f,
//            0.1f, 0.2f, 0.9f, 1f,
//            0.9f, 0.9f, 0.2f, 1f
//    };

    private float[] texture = new float[]{
            1, 0,
            1, 1,
            0, 0,
            0, 1
    };

    private int mProgram;
    private int uMVPMatrixHandle;
    private int vertexHandle;
//    private int colorHandle;
    private int textureHandle;

    private FloatBuffer vertexBuffer;
//    private FloatBuffer colorBuffer;
    private FloatBuffer textureBuffer;

    private int[] texturesId = new int[1];

    public TextureSquare(Context context) {
        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);
//        colorBuffer = BufferUtil.toFloatBuffer(color);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.wall);    // 图片的宽、高必须是2的倍数

        mProgram = ShaderUtil.createProgram(ShaderUtil.VERTEX_CODE, ShaderUtil.FRAGMENT2_CODE);

        vertexHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
//        colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glGenTextures(1, texturesId, 0);                     // 获取产生的纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[0]);  // 绑定纹理id

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);      // GL_NEAREST设置MIN采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);       // GL_LINEAR设置MAG采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);    // 沿着S轴方向拉伸
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);    // 沿着T轴方向拉伸

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0); // 实际加载纹理进显存，参数解释：纹理类型；纹理的层次，０表示基本图像，可以理解为直接贴图；；纹理边框尺寸　
        bitmap.recycle();                                       // 加载纹理成功后回收bitmap
    }


    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
//        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
//        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);                 // 设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesId[0]);  // 绑定纹理id

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
