package cn.dream.android.opengles20.islandscenery;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-26.
 * CoconutTree　椰子树
 */

public class CoconutTree {

    private final static String TAG = CoconutTree.class.getSimpleName();

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" + // 总变换矩阵
            "attribute vec3 aPosition;\n" +                                 // 顶点位置
            "attribute vec2 aTexture;\n" +                                  // 顶点纹理
            "varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition, 1);\n" +         // 根据总变换矩阵计算此次绘制顶点的位置
            "   vTexture = aTexture;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +
            "uniform  sampler2D sTexture;\n" +                              // 纹理采样器，代表一幅纹理
            "varying  vec2 vTexture;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(sTexture, vTexture);\n" +          // 进行纹理采样
            "}";


    private float[] vertex;
    private float[] texture;

    private int vertexCount;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int mProgram;
    private int vertexHandle;
    private int textureHandle;
    private int sTextureHandle;
    private int uMVPMatrixHandle;

    private float[] topValue = new float[3];
    private CoconutLeaf[] coconutLeafs;
    private int[] leafAngles = new int[] {0, 45, 58, 90, 120, 170, 230, 250, 280, 300, 330};


    public CoconutTree(int mProgram, float posX, float posY, float posZ) {
        this.mProgram = mProgram;
        initData(posX, posY, posZ);

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);
        if (this.mProgram < 0) {
            this.mProgram = ShaderUtil.createProgram(VERTEX_CODE, FRAGMENT_CODE);
            Log.d(TAG, "CoconutTree() mProgram=" + this.mProgram);
        }

        vertexHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        textureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexture");
        sTextureHandle = GLES20.glGetUniformLocation(this.mProgram, "sTexture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");

        coconutLeafs = new CoconutLeaf[leafAngles.length];
        for (int i = 0; i < leafAngles.length; i++) {
            coconutLeafs[i] = new CoconutLeaf(ShaderManager.getCoconutLeafProgram(),
                    topValue[0], topValue[1], topValue[2], leafAngles[i]);

        }
    }

    private void initData(float posX, float posY, float posZ) {
        float bottomRadius = 0.1f;      // 树干底部半径
        float topRadius = 0.05f;        // 树干顶部半径
        float treeHeight = 3f;          // 树干总高度
        float factor = 0.2f;            // 树干弯曲因子，也即偏移，越往上，树干越弯曲
        int section = 10;               // 树干可被切分的树干段数，而切割面则为section+1
        int angleStep = 30;             // 树干的圆切面的角度划分，最好能被360整除

        vertexCount = 6 * 360 / angleStep * section;
        vertex = new float[3 * vertexCount];
        texture = new float[2 * vertexCount];

        int k = 0;
        for (int s = 0; s < section; s++) {
            float radius1 = bottomRadius - (bottomRadius - topRadius) / section * s ;
            float radius2 = bottomRadius - (bottomRadius - topRadius) / section * (s + 1);

            float factor1 = (float) (factor * Math.tan((float) s / section * Math.PI * 2 / 5));
            float factor2 = (float) (factor * Math.tan((float) (s + 1) / section * Math.PI * 2 / 5));

            float y1 = treeHeight * (float) s / section + posY;
            float y2 = y1;
            float y3 = treeHeight * (float) (s + 1) / section + posY;
            float y4 = y3;

            for (int i = 0; i < 360; i = i + angleStep) {
                double sinI = Math.sin(Math.toRadians(i));
                double sinI2 = Math.sin(Math.toRadians(i + angleStep));
                float x1 = (float) (radius1 * sinI) + factor1 + posX;
                float x2 = (float) (radius1 * sinI2) + factor1 + posX;
                float x3 = (float) (radius2 * sinI) + factor2 + posX;
                float x4 = (float) (radius2 * sinI2) + factor2 + posX;

                double cosI = Math.cos(Math.toRadians(i));
                double cosI2 = Math.cos(Math.toRadians(i + angleStep));
                float z1 = (float) (radius1 * cosI) + posZ;
                float z2 = (float) (radius1 * cosI2) + posZ;
                float z3 = (float) (radius2 * cosI) + posZ;
                float z4 = (float) (radius2 * cosI2) + posZ;

                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = z1;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = z2;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = z3;

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

        float factorTop = (float) (factor * Math.tan(Math.PI * 2 / 5));
        topValue[0] = factorTop + posX;
        topValue[1] = vertex[vertex.length - 2];
        topValue[2] = posZ;

        Log.d(TAG, "initData() vertex k=" + k + "  vertexCount*3=" + 3 * vertexCount);

        k = 0;
        for (int s = section; s > 0; s--) {
            float t1 = s;
            float t2 = t1;
            float t3 = (s - 1);
            float t4 = t3;

            for (int i = 0; i < 360; i = i + angleStep) {
                float s1 = (float) i / 360;
                float s2 = (float) (i + angleStep) / 360;
                float s3 = s1;
                float s4 = s2;

                texture[k++] = s1;
                texture[k++] = t1;

                texture[k++] = s2;
                texture[k++] = t2;

                texture[k++] = s3;
                texture[k++] = t3;

                texture[k++] = s2;
                texture[k++] = t2;

                texture[k++] = s3;
                texture[k++] = t3;

                texture[k++] = s4;
                texture[k++] = t4;
            }
        }
        Log.d(TAG, "initData() texture k=" + k + "  vertexCount*2=" + 2 * vertexCount);
    }

    public void drawSelf(int textureId, int textureId2) {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1f(sTextureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glEnable(GLES20.GL_BLEND);   // 开启混合
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); // 设置混合因子
        for (int i = 0; i < coconutLeafs.length; i++) {
            coconutLeafs[i].drawSelf(textureId2);
        }
        GLES20.glDisable(GLES20.GL_BLEND);  // 关闭混合
    }
}
