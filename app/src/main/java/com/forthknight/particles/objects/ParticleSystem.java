package com.forthknight.particles.objects;

import android.graphics.Color;

import com.forthknight.particles.Constants;
import com.forthknight.particles.data.VertexArray;
import com.forthknight.particles.program.ParticleShaderProgram;
import com.forthknight.particles.util.Geometry;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by xiongyikai on 2017/4/26.
 */

public class ParticleSystem {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT
            + VECTOR_COMPONENT_COUNT
            + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTE_PRE_FLOAT;

    private final float[] mParticles;
    private final VertexArray mVertexArray;
    private final int mMaxParticleCount;

    private int mCurrentParticleCount;
    private int mNextParticle;

    public ParticleSystem(int maxParticleCount){
        mParticles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        mVertexArray = new VertexArray(mParticles);
        mMaxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position , int color , Geometry.Vector directionVection ,
                            float startTime){
        final int particleOffset = mNextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        mNextParticle++;

        if (mCurrentParticleCount < mMaxParticleCount){
            mCurrentParticleCount++;
        }

        if (mNextParticle == mMaxParticleCount){
            mNextParticle = 0;
        }

        mParticles[currentOffset++] = position.x;
        mParticles[currentOffset++] = position.y;
        mParticles[currentOffset++] = position.z;
        mParticles[currentOffset++] = Color.red(color) / 255f;
        mParticles[currentOffset++] = Color.green(color) / 255f;
        mParticles[currentOffset++] = Color.blue(color) / 255f;
        mParticles[currentOffset++] = directionVection.x;
        mParticles[currentOffset++] = directionVection.y;
        mParticles[currentOffset++] = directionVection.z;
        mParticles[currentOffset++] = startTime;

        //更新在native的数据
        mVertexArray.updateBuffer(mParticles , particleOffset , TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram particleShaderProgram){
        mVertexArray.setVertexAttribPointer(0 ,
                particleShaderProgram.getAPositionLocation() , POSITION_COMPONENT_COUNT , STRIDE);
        mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT ,
                particleShaderProgram.getAColorLocation() , COLOR_COMPONENT_COUNT , STRIDE);
        mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT,
                particleShaderProgram.getADirectionVectorLocation() , VECTOR_COMPONENT_COUNT , STRIDE);
        mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT,
                particleShaderProgram.getAParticleStartTimeLocation() , PARTICLE_START_TIME_COMPONENT_COUNT , STRIDE);
    }

    public void draw(){
        glDrawArrays(GL_POINTS, 0, mCurrentParticleCount);
    }


}
