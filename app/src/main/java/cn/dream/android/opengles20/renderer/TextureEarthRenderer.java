package cn.dream.android.opengles20.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.PointPlanet;
import cn.dream.android.opengles20.shape.TextureBall;
import cn.dream.android.opengles20.shape.TextureEarth;
import cn.dream.android.opengles20.shape.TextureSquare;
import cn.dream.android.opengles20.utils.MatrixState;

/**
 * Created by lgb on 17-6-20.
 * TextureRenderer
 */

public class TextureEarthRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = TextureEarthRenderer.class.getSimpleName();


    private Context context;
    private int[] texturesId;
    private PointPlanet smallPlanet;
    private PointPlanet bigPlanet;
    private TextureEarth textureEarth;
    private TextureEarth textureMoon;
    private TextureBall textureCloud;

    private float angleX, angleY;
    private float angleSelf, angleEarth, angleMoon;

    public TextureEarthRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated()");

        smallPlanet = new PointPlanet(200, 1, 80);
        bigPlanet = new PointPlanet(180, 2, 50);
        textureEarth = new TextureEarth(context, 1f, true);
        textureMoon = new TextureEarth(context, 0.5f, false);
        textureMoon.setADS(new float[]{
                0.05f, 0.05f, 0.025f, 1.0f,
                1.0f, 1.0f, 0.5f, 1.0f,
                0.3f, 0.3f, 0.15f, 1.0f});
        textureCloud = new TextureBall(context, 1.01f);
        textureCloud.setADS(new float[]{
                0.05f, 0.05f, 0.025f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                0.3f, 0.3f, 0.15f, 1.0f});
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);
        float radio = (float) width / height;
        MatrixState.setProjectFrustum(-radio, radio, -1, 1, 4, 100);
        MatrixState.setCamera(0,0,7.2f,0f,0f,0f,0f,1.0f,0.0f);
        MatrixState.setLightPosition(100, 5, 0);
        MatrixState.setInitStack();

        texturesId = new int[4];
        GLES20.glGenTextures(4, texturesId, 0);
        bindTexture(texturesId[0], R.mipmap.earth);
        bindTexture(texturesId[1], R.mipmap.earthn);
        bindTexture(texturesId[2], R.mipmap.moon);
        bindTexture(texturesId[3], R.mipmap.cloud);
    }

    private void bindTexture(int textureId, int bitmapId) {
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


        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);           // 沿着S轴方向拉伸
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);           // 沿着T轴方向拉伸
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

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleX, 1, 0, 0);
        MatrixState.rotate(angleY, 0, 1, 0);

        MatrixState.pushMatrix();
        MatrixState.rotate(angleSelf, 0, 1, 0);
        smallPlanet.drawSelf();
        bigPlanet.drawSelf();
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.rotate(angleEarth, 0, 1, 0);
        textureEarth.drawSelf(texturesId);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        textureCloud.drawSelf(texturesId[3]);
        GLES20.glDisable(GLES20.GL_BLEND);
        MatrixState.popMatrix();

        MatrixState.pushMatrix();
        MatrixState.rotate(angleEarth, 0, 1, 0);
        MatrixState.translate(2, 0, 0);
        MatrixState.rotate(angleMoon, 0, 1, 0);
        textureMoon.drawSelf(texturesId);
        MatrixState.popMatrix();

        MatrixState.popMatrix();

        angleSelf += 0.01f;
        angleEarth += 0.2f;
        angleMoon -= 0.8f;
    }

    public void addAngle(float angleX, float angleY) {
        this.angleX += angleX;
        this.angleY += angleY;
        float sunx=(float)(Math.cos(Math.toRadians(this.angleX))*100);
        float sunz=-(float)(Math.sin(Math.toRadians(this.angleY))*100);
        MatrixState.setLightPosition(sunx , 5, sunz);
    }

}
