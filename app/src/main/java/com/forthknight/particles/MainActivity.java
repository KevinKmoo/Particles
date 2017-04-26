package com.forthknight.particles;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private boolean mRendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                ||(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                &&(Build.FINGERPRINT.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("unknow")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86");

        final GLSurfaceView.Renderer renderer = new ParticlesRenderer(this);

//        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent != null){
//                    final float normalX = (motionEvent.getX()/ (float)view.getWidth()) * 2 - 1;
//                    final float normalY = -((motionEvent.getY()/ (float)view.getHeight()) * 2 - 1);
//                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                        mGLSurfaceView.queueEvent(new Runnable() {
//                            @Override
//                            public void run() {
//                                renderer.handleTouchPress(normalX , normalY);
//                            }
//                        });
//                    }else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
//                        mGLSurfaceView.queueEvent(new Runnable() {
//                            @Override
//                            public void run() {
//                                renderer.handleTouchDrag(normalX , normalY);
//                            }
//                        });
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

        if (supportsEs2){
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setRenderer(renderer);
            mRendererSet = true;
        }else {
            Toast.makeText(this , "This device does not support OpenGL ES 2.0." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        if (mRendererSet){
            mGLSurfaceView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRendererSet){
            mGLSurfaceView.onResume();
        }
    }
}
