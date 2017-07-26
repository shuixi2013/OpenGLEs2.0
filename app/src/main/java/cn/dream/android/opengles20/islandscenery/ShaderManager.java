package cn.dream.android.opengles20.islandscenery;

import cn.dream.android.opengles20.utils.ShaderUtil;

/**
 * Created by lgb on 17-7-26.
 * ShaderManager
 */

public class ShaderManager {
    private static final int codeCount = 5;

    private static int[] mProgram = new int[codeCount];
    private static String[] vertexCodes = new String[]{
            Island.VERTEX_CODE,
            ShaderUtil.VERTEX_CODE,
            WavingWater.VERTEX_CODE,
            CoconutTree.VERTEX_CODE,
            CoconutTree.VERTEX_CODE
    };
    private static String[] fragCodes = new String[]{
            Island.FRAGMENT_CODE,
            ShaderUtil.FRAGMENT2_CODE,
            WavingWater.FRAGMENT2_CODE,
            CoconutTree.FRAGMENT_CODE,
            CoconutTree.FRAGMENT_CODE
    };

    public static void createAllProgram() {
        for (int i = 0; i < codeCount; i++) {
            mProgram[i] = ShaderUtil.createProgram(vertexCodes[i], fragCodes[i]);
        }
    }

    public static int getIslandProgram() {
        return mProgram[0];
    }

    public static int getIslandSkyProgram() {
        return mProgram[1];
    }

    public static int getWavingWaterProgram() {
        return mProgram[2];
    }

    public static int getCoconutTreeProgram() {
        return mProgram[3];
    }

    public static int getCoconutLeafProgram() {
        return mProgram[4];
    }
}
