package com.forthknight.particles.program;

import android.content.Context;

import com.forthknight.particles.util.ShaderHelper;
import com.forthknight.particles.util.TextResouceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by xiongyikai on 2017/4/24.
 */

public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String U_COLOR = "u_Color";

    protected static final String U_TIME = "u_Time";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // Shader program
    protected final int mProgram;
    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        mProgram = ShaderHelper.buildProgram(
                TextResouceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResouceReader.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(mProgram);
    }

}
