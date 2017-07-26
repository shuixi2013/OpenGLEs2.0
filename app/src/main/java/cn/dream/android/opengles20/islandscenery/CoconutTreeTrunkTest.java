package cn.dream.android.opengles20.islandscenery;

/**
 * Created by lgb on 17-7-26.
 * CircleTest 用于圆平分弧度的点的计算工具
 */

public class CoconutTreeTrunkTest {

    public static void main(String[] args) {

        float bottomRadius = 0.5f;      // 树干底部半径
        float topRadius = 0.3f;         // 树干顶部半径
        float treeHeight = 4f;          // 树干总高度
        float factor = 0.5f;            // 树干弯曲因子，也即偏移，越往上，树干越弯曲
        int section = 5;                // 树干可被切分的树干段数，而切割面则为section+1
        int angleStep = 30;             // 树干的圆切面的角度划分，最好能被360整除

        float vertex[] = new float[6 * 3 * 360 / angleStep * (section + 1)];

        int k = 0;
        for (int s = 0; s < section; s++) {
            float radius1 = bottomRadius - (bottomRadius - topRadius) / section * s;
            float radius2 = bottomRadius - (bottomRadius - topRadius) / section * (s + 1);

            float factor1 = factor * (float) s / section;
            float factor2 = factor * (float) (s + 1) / section;

            float y1 = treeHeight * (float) s / section;
            float y2 = y1;
            float y3 = treeHeight * (float) (s + 1) / section;
            float y4 = y3;

            for (int i = 0; i < 360; i = i + angleStep) {
                double sinI = Math.sin(Math.toRadians(i));
                double sinI2 = Math.sin(Math.toRadians(i + angleStep));
                float x1 = (float) (radius1 * sinI) + factor1;
                float x2 = (float) (radius1 * sinI2) + factor1;
                float x3 = (float) (radius2 * sinI) + factor2;
                float x4 = (float) (radius2 * sinI2) + factor2;

                double cosI = Math.cos(Math.toRadians(i));
                double cosI2 = Math.cos(Math.toRadians(i + angleStep));
                float z1 = (float) (radius1 * cosI);
                float z2 = (float) (radius1 * cosI2);
                float z3 = (float) (radius2 * cosI);
                float z4 = (float) (radius2 * cosI2);

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
    }
}
