package com.forthknight.particles;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import com.forthknight.particles.objects.ParticleShooter;
import com.forthknight.particles.objects.ParticleSystem;
import com.forthknight.particles.program.ParticleShaderProgram;
import com.forthknight.particles.util.Geometry;
import com.forthknight.particles.util.MatrixHelper;
import com.forthknight.particles.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by xiongyikai on 2017/4/26.
 */

public class ParticlesRenderer implements GLSurfaceView.Renderer{

    private Context mContext;

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mViewProjectionMatrix = new float[16];

    private ParticleShaderProgram mParticleShaderProgram;
    private ParticleSystem mParticleSystem;
    private ParticleShooter mRedShooter;
    private ParticleShooter mGreenShooter;
    private ParticleShooter mBlueShooter;
    private float mGlobleStartTime;

    public ParticlesRenderer(Context context){
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0f ,0f , 0f, 0f);

        mParticleShaderProgram = new ParticleShaderProgram(mContext);
        mParticleSystem = new ParticleSystem(10000);
        mGlobleStartTime = System.nanoTime();

        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;

        mRedShooter = new ParticleShooter(new Geometry.Point(-1 , 0 , 0) ,
                particleDirection , Color.rgb(255, 50, 5) , angleVarianceInDegrees , speedVariance);
        mGreenShooter = new ParticleShooter(new Geometry.Point(0 , 0 , 0) ,
                particleDirection , Color.rgb(25, 255, 25) , angleVarianceInDegrees , speedVariance);
        mBlueShooter = new ParticleShooter(new Geometry.Point(1 , 0 , 0) ,
                particleDirection , Color.rgb(5, 50, 255) , angleVarianceInDegrees , speedVariance);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE , GL_ONE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0 , 0 , width , height);

        MatrixHelper.perspectiveM(mProjectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);
        setIdentityM(mViewMatrix, 0);
        translateM(mViewMatrix, 0, 0f, -1.5f, -5f);
        multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0,
                mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        float currentTime = (System.nanoTime() - mGlobleStartTime) / 1000000000f;

        mRedShooter.addParticles(mParticleSystem , currentTime , 5);
        mGreenShooter.addParticles(mParticleSystem , currentTime , 5);
        mBlueShooter.addParticles(mParticleSystem , currentTime , 5);

        mParticleShaderProgram.useProgram();
        int textureId = TextureHelper.loadTexture(mContext , R.drawable.particle_texture);
        mParticleShaderProgram.setUniforms(mViewProjectionMatrix , currentTime , textureId);
        mParticleSystem.bindData(mParticleShaderProgram);
        mParticleSystem.draw();
    }
}
