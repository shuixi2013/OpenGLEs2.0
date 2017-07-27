package cn.dream.android.opengles20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.androidannotations.annotations.EActivity;

import cn.dream.android.opengles20.activity.AlphaActivity_;
import cn.dream.android.opengles20.activity.CameraTestActivity_;
import cn.dream.android.opengles20.activity.ClipPlaneActivity_;
import cn.dream.android.opengles20.activity.CurveGeometryActivity_;
import cn.dream.android.opengles20.activity.GrayScaleImageActivity_;
import cn.dream.android.opengles20.activity.MistActivity_;
import cn.dream.android.opengles20.activity.PlaneActivity_;
import cn.dream.android.opengles20.activity.PolyhetronActivity_;
import cn.dream.android.opengles20.activity.ScissorActivity_;
import cn.dream.android.opengles20.activity.StencilActivity_;
import cn.dream.android.opengles20.activity.TextureActivity_;
import cn.dream.android.opengles20.activity.TextureEarthActivity_;
import cn.dream.android.opengles20.activity.TriangleActivity_;
import cn.dream.android.opengles20.activity.WavingFlagActivity_;
import cn.dream.android.opengles20.islandscenery.IsLandSceneryActivity_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onTriangle(View view) {
        startActivity(new Intent(this, TriangleActivity_.class));
    }

    public void onPlane(View view) {
        startActivity(new Intent(this, PlaneActivity_.class));
    }

    public void onPolyhetron(View view) {
        startActivity(new Intent(this, PolyhetronActivity_.class));
    }

    public void onTexture(View view) {
        startActivity(new Intent(this, TextureActivity_.class));
    }

    public void onTextureEarth(View view) {
        startActivity(new Intent(this, TextureEarthActivity_.class));
    }

    public void onCurveGeometry(View view) {
        startActivity(new Intent(this, CurveGeometryActivity_.class));
    }

    public void onMistTest(View view) {
        startActivity(new Intent(this, MistActivity_.class));
    }

    public void onGrayScaleImageTest(View view) {
        startActivity(new Intent(this, GrayScaleImageActivity_.class));
    }

    public void onScissor(View view) {
        startActivity(new Intent(this, ScissorActivity_.class));
    }

    public void onAlphaTest(View view) {
        startActivity(new Intent(this, AlphaActivity_.class));
    }

    public void onStencil(View view) {
        startActivity(new Intent(this, StencilActivity_.class));
    }

    public void onClipPlane(View view) {
        startActivity(new Intent(this, ClipPlaneActivity_.class));
    }

    public void onWavingFlag(View view) {
        startActivity(new Intent(this, WavingFlagActivity_.class));
    }

    public void onIsLandScenery(View view) {
        startActivity(new Intent(this, IsLandSceneryActivity_.class));
    }

    public void onCameraTest(View view) {
        startActivity(new Intent(this, CameraTestActivity_.class));
    }
}
