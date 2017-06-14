package cn.dream.android.opengles20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.androidannotations.annotations.EActivity;

import cn.dream.android.opengles20.activity.PlaneActivity_;
import cn.dream.android.opengles20.activity.TriangleActivity_;

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
}
