package com.forthknight.particles.data;


import com.forthknight.particles.objects.ObjectBuilder;
import com.forthknight.particles.program.ColorShaderProgram;
import com.forthknight.particles.util.Geometry;

import java.util.List;

/**
 * Created by xiongyikai on 2017/4/25.
 */

public class Puck {

    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float mRadius , mHeight;

    private final VertexArray mVertexArray;

    private final List<ObjectBuilder.DrawCommand> mDrawList;

    public Puck(float radius , float height , int numPoints){
        mRadius = radius;
        mHeight = height;

        ObjectBuilder.GeneratedData data = ObjectBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0,0,0) , radius , height), numPoints);

        mVertexArray = new VertexArray(data.vertexData);
        mDrawList = data.drawList;
    }

    public void bindData(ColorShaderProgram program){
        mVertexArray.setVertexAttribPointer(0 ,
                program.getPositionAttributeLocation() , POSITION_COMPONENT_COUNT , 0);
    }

    public void draw(){
        for (ObjectBuilder.DrawCommand command :
                mDrawList) {
            command.draw();
        }
    }

}
