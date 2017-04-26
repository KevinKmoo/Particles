package com.forthknight.particles.objects;


import com.forthknight.particles.util.Geometry;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;


/**
 * Created by xiongyikai on 2017/4/24.
 */

public class ObjectBuilder {

    private static final int FLOATS_PER_VERTEX = 3;
    private float[] mVertexData;
    private int mOffset = 0;

    private ArrayList<DrawCommand> mDrawList = new ArrayList<>();

    private ObjectBuilder(int vertexNums){
        mVertexData = new float[vertexNums * FLOATS_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    public static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints){
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder objectBuilder = new ObjectBuilder(size);

        Geometry.Circle puckTop =
                new Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius);

        objectBuilder.appendCircle(puckTop , numPoints);
        objectBuilder.appendOpenCylinder(puck , numPoints);

        return objectBuilder.build();
    }

    public static GeneratedData createMallet(Geometry.Point center ,
                                      float radius , float height , int numPoints){

        int size = (sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints))*2;

        ObjectBuilder objectBuilder = new ObjectBuilder(size);

        // 先创建底部的圆柱体
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle =
                new Geometry.Circle( center.translateY(-baseHeight), radius);
        Geometry.Cylinder baseCylinder =
                new Geometry.Cylinder( baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);
        objectBuilder.appendCircle(baseCircle, numPoints);
        objectBuilder.appendOpenCylinder(baseCylinder, numPoints);

        //再创建把手的圆柱体
        float handleHeight = height * 0.75f; float handleRadius = radius / 3f;
        Geometry.Circle handleCircle =
                new Geometry.Circle( center.translateY(height * 0.5f), handleRadius);
        Geometry.Cylinder handleCylinder =
                new Geometry.Cylinder( handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);
        objectBuilder.appendCircle(handleCircle, numPoints);
        objectBuilder.appendOpenCylinder(handleCylinder, numPoints);

        return objectBuilder.build();
    }

    private void appendCircle(Geometry.Circle circle , int numPoints){
        final int startVertex = mOffset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        mVertexData[mOffset++] = circle.center.x;
        mVertexData[mOffset++] = circle.center.y;
        mVertexData[mOffset++] = circle.center.z;

        for (int i = 0; i <= numPoints ; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            mVertexData[mOffset++] = circle.center.x
                    + (float) (circle.radius * Math.cos(angleInRadians));
            mVertexData[mOffset++] = circle.center.y;
            mVertexData[mOffset++] = circle.center.z
                    + (float) (circle.radius * Math.sin(angleInRadians));
        }

        mDrawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN , startVertex , numVertices);
            }
        });

    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder , int numPoints){
        final int startVertex = mOffset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);

        final float yStart = cylinder.center.y - cylinder.height/2f;
        final float yEnd = cylinder.center.y + cylinder.height/2f;

        for (int i = 0; i <= numPoints ; i++) {
            float angleInRadians = ((float)i / (float) numPoints) * ((float) Math.PI *2f);

            mVertexData[mOffset++] = cylinder.center.x
                    + (float) (cylinder.radius * Math.cos(angleInRadians));
            mVertexData[mOffset++] = yStart;
            mVertexData[mOffset++] = cylinder.center.z
                    + (float)(cylinder.radius*Math.sin(angleInRadians));

            mVertexData[mOffset++] = cylinder.center.x
                    + (float) (cylinder.radius * Math.cos(angleInRadians));
            mVertexData[mOffset++] = yEnd;
            mVertexData[mOffset++] = cylinder.center.z
                    + (float)(cylinder.radius*Math.sin(angleInRadians));

        }

        mDrawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP , startVertex , numVertices);
            }
        });

    }

    public static interface DrawCommand {
        void draw();
    }

    public static class GeneratedData {

        public final float[] vertexData;
        public final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    private GeneratedData build() {
        return new GeneratedData(mVertexData, mDrawList);
    }

}
