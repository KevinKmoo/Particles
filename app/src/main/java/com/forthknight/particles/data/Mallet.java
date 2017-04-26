package com.forthknight.particles.data;


import com.forthknight.particles.objects.ObjectBuilder;
import com.forthknight.particles.program.ColorShaderProgram;
import com.forthknight.particles.util.Geometry;

import java.util.List;

/**
 * Created by xiongyikai on 2017/4/24.
 */

public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 3;
//    private static final int COLOR_COMPONENT_COUNT = 3;
//    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
//            * Constants.BYTE_PRE_FLOAT;

//    private static final float[] VERTEXT_DATA = new float[]{
//            // Order of coordinates: X, Y, R, G, B
//            0f, -0.4f, 0f, 0f, 1f,
//            0f, 0.4f, 1f, 0f, 0f
//    };

    private VertexArray mVertexData;
    private List<ObjectBuilder.DrawCommand> mDrawList;

    public final float mRadius,mHeight;

    public Mallet(float radius , float height , int numPoints){
        mRadius = radius;
        mHeight = height;

        ObjectBuilder.GeneratedData data = ObjectBuilder.createMallet(
                new Geometry.Point(0 , 0 , 0 ) , radius , height , numPoints);

        mVertexData = new VertexArray(data.vertexData);
        mDrawList = data.drawList;
    }

    public void bindData(ColorShaderProgram program){
        mVertexData.setVertexAttribPointer(0 ,
                program.getPositionAttributeLocation() ,
                POSITION_COMPONENT_COUNT , 0);
    }

    public void draw(){
        for (ObjectBuilder.DrawCommand command :
                mDrawList) {
            command.draw();
        }
    }

}
