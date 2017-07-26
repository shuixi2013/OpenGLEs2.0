package cn.dream.android.opengles20.islandscenery;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.Mountain;
import cn.dream.android.opengles20.shape.SkyDome;
import cn.dream.android.opengles20.shape.WavingWater;
import cn.dream.android.opengles20.utils.Constant;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-26.
 * IsLandSceneryRenderer
 */

public class IsLandSceneryRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private int[] textureIds;
    private Mountain mountain;
    private SkyDome skyDome;
    private WavingWater wavingWater;

    private float rotateValue = 0;
    private float translateValue = 0;

    public IsLandSceneryRenderer(Context context) {
        this.context = context;
    }


    public void addRotateValue(float rotateValue) {
        this.rotateValue += rotateValue;
    }

    public void addTranslateValue(float translateValue) {
        this.translateValue += translateValue;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);

        Constant.LAND_HIGHEST = 3;
        Constant.LAND_HIGH_ADJUST = 0;
        Constant.yArray = Constant.loadLandforms(context.getResources(), R.mipmap.scale_land2);
        mountain = new Mountain(Constant.yArray);
        mountain.setStartDivider(0.5f);
        mountain.setSpanDivider(1.1f);

        skyDome = new SkyDome(50);
        wavingWater = new WavingWater(context, 100, 100, 0.3f, R.mipmap.ocean_water);
        wavingWater.setType(1);

        textureIds = new int[3];
        GLES20.glGenTextures(3, textureIds, 0);
        ShaderUtil.bindTextureId(context, textureIds, new int[]{R.mipmap.sand, R.mipmap.grass, R.mipmap.sky2});
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        MatrixState.setCamera(0, 5, 13, 0, 0, 0, 0, 1, 0);
        MatrixState.setSunLightPosition(200, 50, -50);
        MatrixState.setInitStack();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, translateValue);
        MatrixState.rotate(rotateValue, 0, 1, 0);
        skyDome.drawSelf(textureIds[2]);
        mountain.drawSelf(textureIds[0], textureIds[1]);
        wavingWater.drawSelf();
        MatrixState.popMatrix();
    }
}
