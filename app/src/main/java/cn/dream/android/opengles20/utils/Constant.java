package cn.dream.android.opengles20.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class Constant {

    public static float[][] yArray;

    public static final float LAND_HIGH_ADJUST = -2f;   // 陆地的高度调整值
    public static final float LAND_HIGHEST = 20f;       // 陆地最大高差

    /**
     * 从灰度图片中加载陆地上每个顶点的高度
     * @param resources
     * @param index
     * @return
     */
    public static float[][] loadLandforms(Resources resources, int index) {
        Bitmap bt = BitmapFactory.decodeResource(resources, index);
        int colsPlusOne = bt.getWidth();
        int rowsPlusOne = bt.getHeight();
        float[][] result = new float[rowsPlusOne][colsPlusOne];
        for (int i = 0; i < rowsPlusOne; i++) {
            for (int j = 0; j < colsPlusOne; j++) {
                int color = bt.getPixel(j, i);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int h = (r + g + b) / 3;
                result[i][j] = h * LAND_HIGHEST / 255 + LAND_HIGH_ADJUST;
            }
        }
        return result;
    }

    /**
     * 1.浮点算法：Gray=R*0.3+G*0.59+B*0.11
     * 2.整数方法：Gray=(R*30+G*59+B*11)/100
     * 3.移位方法：Gray =(R*76+G*151+B*28)>>8;
     * 4.平均值法：Gray=（R+G+B）/3;
     * 5.仅取绿色：Gray=G；
     */
}