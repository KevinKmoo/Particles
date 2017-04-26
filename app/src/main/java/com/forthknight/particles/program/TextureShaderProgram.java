package com.forthknight.particles.program;

import android.content.Context;

import com.forthknight.particles.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by xiongyikai on 2017/4/24.
 */

public class TextureShaderProgram extends ShaderProgram{

    // Uniform locations
    private final int mUMatrixLocation;
    private final int mUTextureUnitLocation;
    // Attribute locations
    private final int mAPositionLocation;
    private final int mATextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        mUMatrixLocation = glGetUniformLocation(mProgram , U_MATRIX);
        mUTextureUnitLocation = glGetUniformLocation(mProgram , U_TEXTURE_UNIT);

        mAPositionLocation = glGetAttribLocation(mProgram , A_POSITION);
        mATextureCoordinatesLocation = glGetAttribLocation(mProgram , A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId) {
        //把矩阵传递给Shader程序
        glUniformMatrix4fv(mUMatrixLocation, 1, false, matrix, 0);
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);
        //告诉Texture Uniform Sampler用这个位置的Texture去渲染，从0开始
        glUniform1i(mUTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return mAPositionLocation;
    }
    public int getTextureCoordinatesAttributeLocation() {
        return mATextureCoordinatesLocation;
    }


}
