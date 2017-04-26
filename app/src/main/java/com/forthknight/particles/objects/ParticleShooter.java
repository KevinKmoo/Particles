package com.forthknight.particles.objects;


import com.forthknight.particles.util.Geometry;

import java.util.Random;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

/**
 * Created by xiongyikai on 2017/4/26.
 */

public class ParticleShooter {

    private final Geometry.Point mPosition;
    private final Geometry.Vector mDirectionVector;
    private final int mColor;

    private final float mAngleVariance;
    private final float mSpeedVariance;

    private final Random mRandom = new Random();

    private float[] mRotationMatrix = new float[16];
    private float[] mDirection = new float[4];
    private float[] mResultVector = new float[4];

    public ParticleShooter(Geometry.Point position , Geometry.Vector directionVector , int color ,
                           float angleVariance , float speedVariance){
        mPosition = position;
        mDirectionVector = directionVector;
        mColor = color;
        mAngleVariance = angleVariance;
        mSpeedVariance = speedVariance;

        mDirection[0] = directionVector.x;
        mDirection[1] = directionVector.y;
        mDirection[2] = directionVector.z;
    }

    public void addParticles(ParticleSystem particleSystem , float currentTime , int count){

        for (int i = 0; i < count; i++) {

            setRotateEulerM(mRotationMatrix, 0,
                    (mRandom.nextFloat() - 0.5f) * mAngleVariance,
                    (mRandom.nextFloat() - 0.5f) * mAngleVariance,
                    (mRandom.nextFloat() - 0.5f) * mAngleVariance);

            multiplyMV(
                    mResultVector, 0,
                    mRotationMatrix, 0,
                    mDirection, 0);

            float speedAdjustment = 1f + mRandom.nextFloat() * mSpeedVariance;

            Geometry.Vector thisVector = new Geometry.Vector(
                    mResultVector[0] * speedAdjustment,
                    mResultVector[1] * speedAdjustment,
                    mResultVector[2] * speedAdjustment);

            particleSystem.addParticle(mPosition , mColor , thisVector , currentTime);
        }
    }

}
