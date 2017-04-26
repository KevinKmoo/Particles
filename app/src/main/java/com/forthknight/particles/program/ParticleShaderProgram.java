package com.forthknight.particles.program;

import android.content.Context;

import com.forthknight.particles.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by xiongyikai on 2017/4/26.
 */

public class ParticleShaderProgram extends ShaderProgram{

    private int mUMatrixLocation;
    private int mUTimeLocation;
    private int mAPositionLocation;
    private int mAColorLocation;
    private int mADirectionVectorLocation;
    private int mAParticleStartTimeLocation;
    private int mUTextureUnitLocation;

    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);

        mUMatrixLocation = glGetUniformLocation(mProgram , U_MATRIX);
        mUTimeLocation = glGetUniformLocation(mProgram , U_TIME);
        mUTextureUnitLocation = glGetUniformLocation(mProgram , U_TEXTURE_UNIT);

        mAPositionLocation = glGetAttribLocation(mProgram , A_POSITION);
        mAColorLocation = glGetAttribLocation(mProgram , A_COLOR);
        mADirectionVectorLocation = glGetAttribLocation(mProgram , A_DIRECTION_VECTOR);
        mAParticleStartTimeLocation = glGetAttribLocation(mProgram , A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix , float elapsedTime , int textureId){
        glUniformMatrix4fv(mUMatrixLocation , 1 , false , matrix , 0);
        glUniform1f(mUTimeLocation , elapsedTime);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D , textureId);
        glUniform1i(mUTextureUnitLocation , 0);
    }

    public int getAPositionLocation(){
        return mAPositionLocation;
    }

    public int getAColorLocation(){
        return mAColorLocation;
    }

    public int getADirectionVectorLocation(){
        return mADirectionVectorLocation;
    }

    public int getAParticleStartTimeLocation(){
        return mAParticleStartTimeLocation;
    }
}
