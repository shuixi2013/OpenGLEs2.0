package cn.dream.android.opengles20.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by lgb on 17-6-13.
 * ShaderUtil:  着色器脚本加载、编译
 */

public class ShaderUtil {

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec4 aColor;\n" +                                    // 顶点颜色
            "attribute vec2 aTexture;\n" +                                  // 顶点纹理
            "varying  vec4 vColor;\n" +                                     // 用于传递给片元着色器的易变变量
            "varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1);\n" +          // 根据总变换矩阵计算此次绘制顶点的位置
            "   vColor = aColor;\n" +                                       // 将接收的顶点颜色传递给片元着色器
            "   vTexture = aTexture;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "varying  vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "void main() {\n" +
            "   gl_FragColor = vColor;\n" +                                 // 给片源附上颜色值
            "}";

    public final static String FRAGMENT2_CODE = "precision mediump float;\n" +
            "uniform  sampler2D sTexture;\n" +                              // 纹理采样器，代表一幅纹理
            "varying  vec4 vColor;\n" +                                     // 接收从顶点着色器传过来的易变变量
            "varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(sTexture, vTexture);\n" +          // 进行纹理采样
            "}";

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader vertexShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShade pixelShaderr");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        Log.e("ES20", "program=" + program);
        return program;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public static void bindTextureId(Context context, @NonNull int[] texturesId, @NonNull int[] bitmapsId) {
        if (texturesId.length != bitmapsId.length) {
            throw new IllegalArgumentException("texturesId length is not equal bitmapsId length");
        }
        for (int i = 0; i < texturesId.length; i++) {
            bindTextureId(context, texturesId[i], bitmapsId[i]);
        }
    }

    public static void bindTextureId(Context context, int textureId, int bitmapId) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);  // 绑定纹理id

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);      // 设置MIN时为最近采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);       // 设置MAG时为线性采样方式
        /**
         * GL_TEXTURE_MIN_FILTER与GL_TEXTURE_MAG_FILTER都需要设置，当纹理图比映射的图元大时，采用MIN；反之采用MAG。
         * MAG方式容易产生的锯齿明显、MIN则反之；通俗来讲就是把原材料放大缩小到规定大小
         *
         * 可选择的参数:GL_NEAREST,GL_LINEAR,GL_LINEAR_MIPMAP_LINEAR,GL_LINEAR_MIPMAP_NEAREST,GL_NEAREST_MIPMAP_LINEAR,GL_NEAREST_MIPMAP_NEAREST
         * 当GL_NEAREST时，为最近一个像素拉伸，容易产生锯齿效果
         * 当GL_LINEAR是，为对应点周围的加权平均值，平滑过度，消除锯齿，但有时候会很模糊
         * 若一张大图进行显示，会出现近处被放大而显示锯齿，远处缩小视图较清晰，所以用MIPMAP采样，原理：近处清晰，远处模糊；
         *      glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
         *      GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
         */


        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);           // 沿着S轴方向拉伸
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);           // 沿着T轴方向拉伸
        /**
         * 可选择的参数:GL_REPEAT,GL_CLAMP_TO_EDGE
         * 当GL_REPEAT时，texture如果坐标大于１，则会产生重复图样，带小数则显示图样对应的部分，
         *      如S=3.3，则重复3个图样，然后在重复0.3个图样切图
         * 当GL_REPEAT时，texture如果坐标大于１，则会产生图样截取拉伸，
         *      如T=3.3，则拉伸图样T方向最后一个像素至3.3位置
         */
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId);    // 图片的宽、高严格来讲是2的倍数
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0); // 实际加载纹理进显存，参数解释：纹理类型；纹理的层次，０表示基本图像，可以理解为直接贴图；；纹理边框尺寸　
        bitmap.recycle();                                       // 加载纹理成功后回收bitmap
    }

    public static String loadFromAssetsFile(String fname, Resources r) {
        String result = null;
        try {
            InputStream in = r.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
