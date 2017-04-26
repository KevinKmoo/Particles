package com.forthknight.particles.data;


import com.forthknight.particles.Constants;
import com.forthknight.particles.program.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by xiongyikai on 2017/4/24.
 */

public class Table {

    private final VertexArray mVertexData;

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTE_PRE_FLOAT;

    private static final float[] VERTEX_DATA = new float[]{
            // Order of coordinates: X, Y, S, T

            // Triangle Fan
            0f,    0f, 0.5f, 0.5f,
            -0.5f, -0.8f,   0f, 0.9f,
            0.5f, -0.8f,   1f, 0.9f,
            0.5f,  0.8f,   1f, 0.1f,
            -0.5f,  0.8f,   0f, 0.1f,
            -0.5f, -0.8f,   0f, 0.9f
    };

    public Table(){
        mVertexData = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram program){
        mVertexData.setVertexAttribPointer(0 ,
                program.getPositionAttributeLocation() ,
                POSITION_COMPONENT_COUNT , STRIDE);
        mVertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT ,
                program.getTextureCoordinatesAttributeLocation() ,
                TEXTURE_COORDINATES_COMPONENT_COUNT , STRIDE);
    }

    public void draw(){
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

}
