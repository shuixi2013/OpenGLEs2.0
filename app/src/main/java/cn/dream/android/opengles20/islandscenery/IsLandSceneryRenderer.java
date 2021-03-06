package cn.dream.android.opengles20.islandscenery;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.dream.android.opengles20.R;
import cn.dream.android.opengles20.shape.Mountain;
import cn.dream.android.opengles20.utils.Constant;
import cn.dream.android.opengles20.utils.MatrixState;
import cn.dream.android.opengles20.utils.ShaderUtil;

import static cn.dream.android.opengles20.utils.Constant.yArray;

/**
 * Created by lgb on 17-7-26.
 * IsLandSceneryRenderer
 */

public class IsLandSceneryRenderer implements GLSurfaceView.Renderer {

    private Context context;

    public static int[] textureIds = new int[7];
    private int[] bitmapIds = new int[]{R.mipmap.sand, R.mipmap.grass,
            R.mipmap.sky2,
            R.mipmap.ocean_water,
            R.mipmap.tree_trunk,
            R.mipmap.tree_leaf,
            R.mipmap.ic_launcher
    };

    private Island island;
    private IslandSky islandSky;
    private WavingWater wavingWater;
    private List<CoconutTree> coconutTrees;
    private Reptile androidReptile;

    private float rotateValue = 0;

    public static float cx = 0;
    public static float cy = 5;
    public static float cz = 13;

    private float tx;
    private float ty = 1;
    private float tz;

    private final static float MAX_WIND_FORCE = 15;
    private final static float MIN_WIND_FORCE = 5;
    public static float windForce = MIN_WIND_FORCE;
    private boolean openWind = false;
    private boolean windFlag;
    public static float windAngle = (float) (Math.PI / 2);

    public IsLandSceneryRenderer(Context context) {
        this.context = context;
    }


    public void addRotateValue(float rotateValue) {
        this.rotateValue += rotateValue;
        tx = (float) (cx + Math.sin(Math.toRadians(this.rotateValue)) * 13);       // 观察目标点x坐标
        tz = (float) (cz - Math.cos(Math.toRadians(this.rotateValue)) * 13);       // 观察目标点z坐标
        MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
    }

    public void addTranslateValue(boolean isForward) {
        if (isForward) {
            cx += (float) (0.2 * Math.sin(Math.toRadians(this.rotateValue)));
            cz -= (float) (0.2 * Math.cos(Math.toRadians(this.rotateValue)));
        } else {
            cx -= (float) (0.2 * Math.sin(Math.toRadians(this.rotateValue)));
            cz += (float) (0.2 * Math.cos(Math.toRadians(this.rotateValue)));
        }

        tx = (float) (cx + Math.sin(Math.toRadians(this.rotateValue)) * 13);       // 观察目标点x坐标
        tz = (float) (cz - Math.cos(Math.toRadians(this.rotateValue)) * 13);       // 观察目标点z坐标
        MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);
        ShaderManager.createAllProgram();
        Constant.LAND_HIGHEST = 2;
        Constant.LAND_HIGH_ADJUST = 0;
        Constant.yArray = Constant.loadLandforms(context.getResources(), R.mipmap.scale_land2);
        island = new Island(Constant.yArray, ShaderManager.getIslandProgram());
        island.setStartDivider(0.3f);
        island.setSpanDivider(0.3f);

        islandSky = new IslandSky(50, ShaderManager.getIslandSkyProgram());

        wavingWater = new WavingWater(ShaderManager.getWavingWaterProgram(), 100, 100, 0.1f);
        wavingWater.setType(1);

        int posI = Constant.yArray.length / 2;
        int posJ = Constant.yArray[0].length / 2;
        float tX = -Mountain.UNIT_SIZE * (Constant.yArray.length - 1) / 2 + posI * Mountain.UNIT_SIZE;
        float tY = yArray[posJ][posI];
        float tZ = -Mountain.UNIT_SIZE * (Constant.yArray[0].length - 1) / 2 + posJ * Mountain.UNIT_SIZE;
        coconutTrees = new ArrayList<>();
        coconutTrees.add(new CoconutTree(ShaderManager.getCoconutTreeProgram(), tX, tY, tZ));

        posI = Constant.yArray.length / 2 + 5;
        posJ = Constant.yArray[0].length / 2;
        tX = -Mountain.UNIT_SIZE * (Constant.yArray.length - 1) / 2 + posI * Mountain.UNIT_SIZE;
        tY = yArray[posJ][posI];
        tZ = -Mountain.UNIT_SIZE * (Constant.yArray[0].length - 1) / 2 + posJ * Mountain.UNIT_SIZE;
        coconutTrees.add(new CoconutTree(ShaderManager.getCoconutTreeProgram(), tX, tY, tZ));

        GLES20.glGenTextures(textureIds.length, textureIds, 0);
        ShaderUtil.bindTextureId(context, textureIds, bitmapIds);

        startWindThread();

        androidReptile = new Reptile(ShaderManager.getReptileProgram(), 1, 13, 25);
    }

    private void startWindThread() {
        openWind  = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                openWind = true;
                while (openWind) {
                    if (windForce > MAX_WIND_FORCE)
                        windFlag = true;
                    if (windForce <= MIN_WIND_FORCE)
                        windFlag = false;
                    if (windFlag)
                        windForce -= 0.3;
                    else windForce += 0.3;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
        MatrixState.setSunLightPosition(200, 50, -50);
        MatrixState.setInitStack();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        MatrixState.pushMatrix();
        //MatrixState.translate(0, 0, translateValue);
        //MatrixState.rotate(rotateValue, 0, 1, 0);
        island.drawSelf(textureIds[0], textureIds[1]);
        islandSky.drawSelf(textureIds[2]);
        wavingWater.drawSelf(textureIds[3]);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        Collections.sort(coconutTrees);
        for (CoconutTree tree : coconutTrees)
            tree.drawSelf(textureIds[4], textureIds[5]);
        GLES20.glDisable(GLES20.GL_BLEND);

        androidReptile.drawSelf(textureIds[6]);

        MatrixState.popMatrix();
    }


    public void onDestroy() {
        openWind = false;
        androidReptile.onDestroy();
    }
}
