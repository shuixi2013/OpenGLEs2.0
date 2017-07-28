package cn.dream.android.opengles20.shape;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

import cn.dream.android.opengles20.utils.BufferUtil;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-7-28.
 * TwistCircle
 */

public class TwistCircle {

    private final static String TAG = TwistCircle.class.getSimpleName();

    public final static String VERTEX_CODE = "uniform mat4 uMVPMatrix;\n" +
            "attribute vec3 aVertex;\n" +           // 'attribute' :  supported in vertex shaders only
            "attribute vec2 aTexture;\n" +
            "varying vec2 vTexture;\n" +
            "uniform float uControl;" +
            "void main() {\n" +
            "   float pi = 3.141592653;\n" +
            "   vec3 tempVertex = aVertex;\n" +
            //"   float radians = 0.0;\n" +
            //"   radians = atan(tempVertex.x, tempVertex.y);\n" +
            "   gl_Position = uMVPMatrix * vec4(tempVertex, 1);\n" +
            "   vTexture = aTexture;\n" +
            "}";

    public final static String FRAGMENT_CODE = "precision mediump float;\n" +  // 精度　
            "varying vec2 vTexture;\n" +
            "uniform sampler2D uTexture;\n" +
            "void main() {" +
            "   gl_FragColor = texture2D(uTexture, vTexture);\n" + //vec4(0.1, 0.8, 0.2, 1);
            "}";

    private float[] vertex;
    private float[] texture;

    private int vertexCount;

    private float uControlLength;
    private boolean isRun;
    private boolean uControlFlag;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private int mProgram;
    private int vertexHandle;
    private int textureHandle;
    private int uControlHandle;
    private int uMVPMatrixHandle;

    public TwistCircle(int mProgram) {
        this.mProgram = mProgram;
        initData();

        vertexBuffer = BufferUtil.toFloatBuffer(vertex);
        textureBuffer = BufferUtil.toFloatBuffer(texture);

        vertexHandle = GLES20.glGetAttribLocation(this.mProgram, "aVertex");
        textureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexture");
        uControlHandle = GLES20.glGetUniformLocation(this.mProgram, "uControl");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");

        startTwistThread();
    }

    private void startTwistThread() {
        isRun = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRun = true;
                uControlFlag = false;
                while (isRun) {
                    if (uControlFlag)
                        uControlLength -= 0.05f;
                    else uControlLength += 0.05f;

                    if (uControlLength > 1)
                        uControlFlag = true;
                    if (uControlLength <= 0)
                        uControlFlag = false;

                    //Log.d(TAG, "startTwistThread() uControlLength=" + uControlLength);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initData() {
        long startTime = System.currentTimeMillis();
        float layerUnit = 0.05f;
        int layer = 20;
        int angleUnit = 5; // 能被360整除

        /*vertexCount = 6;
        vertex = new float[] {
                0.8f, 0.8f, 0,
                0.8f, -0.8f, 0,
                -0.8f, 0.8f, 0,
                0.8f, -0.8f, 0,
                -0.8f, 0.8f, 0,
                -0.8f, -0.8f, 0
        };
        texture = new float[]{
                1f, 0,
                1f, 1,
                0, 0,
                1f, 1,
                0, 0,
                0, 1
        };*/

        int k = 0;
        int l = 0;
        int maxAngle = 360;
        vertexCount = 6 * layer * (maxAngle / angleUnit);
        vertex = new float[3 * vertexCount];
        texture = new float[2 * vertexCount];


        for (int i = 0; i < layer; i++) {
            float radius1 = i * layerUnit;
            float radius2 = (i + 1) * layerUnit;

            for (int j = 0; j < maxAngle; j = j + angleUnit) {
                float x1 = (float) (radius1 * Math.sin(Math.toRadians(j)));
                float x2 = (float) (radius1 * Math.sin(Math.toRadians(j + angleUnit)));
                float x3 = (float) (radius2 * Math.sin(Math.toRadians(j)));
                float x4 = (float) (radius2 * Math.sin(Math.toRadians(j + angleUnit)));

                float y1 = (float) (radius1 * Math.cos(Math.toRadians(j)));
                float y2 = (float) (radius1 * Math.cos(Math.toRadians(j + angleUnit)));
                float y3 = (float) (radius2 * Math.cos(Math.toRadians(j)));
                float y4 = (float) (radius2 * Math.cos(Math.toRadians(j + angleUnit)));
                //Log.d(TAG, "initData() " + x1 + "," + y1 + "  " + x2 + "," + y2 + "  " + x3 + "," + y3 + "  " + x4 + "," + y4 + "  " );
                vertex[k++] = x1;
                vertex[k++] = y1;
                vertex[k++] = 0;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = 0;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = 0;

                vertex[k++] = x2;
                vertex[k++] = y2;
                vertex[k++] = 0;

                vertex[k++] = x3;
                vertex[k++] = y3;
                vertex[k++] = 0;

                vertex[k++] = x4;
                vertex[k++] = y4;
                vertex[k++] = 0;

                float divid = layer * layerUnit * 2;
                float s1 = x1 / divid + 0.5f;
                float s2 = x2 / divid + 0.5f;
                float s3 = x3 / divid + 0.5f;
                float s4 = x4 / divid + 0.5f;

                float t1 = -y1 / divid + 0.5f;
                float t2 = -y2 / divid + 0.5f;
                float t3 = -y3 / divid + 0.5f;
                float t4 = -y4 / divid + 0.5f;

                texture[l++] = s1;
                texture[l++] = t1;

                texture[l++] = s2;
                texture[l++] = t2;

                texture[l++] = s3;
                texture[l++] = t3;

                texture[l++] = s2;
                texture[l++] = t2;

                texture[l++] = s3;
                texture[l++] = t3;

                texture[l++] = s4;
                texture[l++] = t4;
            }
        }

        Log.d(TAG, "initData() time=" + (System.currentTimeMillis() - startTime) + "  k=" + k + "  vertexCount*3=" + vertexCount * 3);
    }

    public void drawSelf(int textureId) {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(uControlHandle, uControlLength);

        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(textureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    public void onDestroy() {
        isRun = false;
    }
}
