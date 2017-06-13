package cn.dream.android.opengles20.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by lgb on 17-6-13.
 * BufferUtil
 */

public class BufferUtil {

    public static final FloatBuffer toFloatBuffer(float[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(data);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static final IntBuffer toIntBuffer(int[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);
        intBuffer.position(0);
        return intBuffer;
    }
}
