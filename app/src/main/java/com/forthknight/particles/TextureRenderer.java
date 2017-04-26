package com.forthknight.particles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.forthknight.particles.data.Mallet;
import com.forthknight.particles.data.Puck;
import com.forthknight.particles.data.Table;
import com.forthknight.particles.program.ColorShaderProgram;
import com.forthknight.particles.program.TextureShaderProgram;
import com.forthknight.particles.util.Geometry;
import com.forthknight.particles.util.Logger;
import com.forthknight.particles.util.MatrixHelper;
import com.forthknight.particles.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by xiongyikai on 2017/4/24.
 */

public class TextureRenderer implements GLSurfaceView.Renderer{

    private static final String TAG = TextureRenderer.class.getSimpleName();

    private final Context mContext;

    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionViewMatrix = new float[16];
    private float[] mProjectionViewModelMatrix = new float[16];

    private float[] mInvertedViewProjectionMatrix = new float[16];

    private boolean mMalletPressed = false;
    private Geometry.Point mBlueMalletPosition;

    private Table mTable;
    private Mallet mMallet;
    private Puck mPuck;

    private TextureShaderProgram mTextureShaderProgram;
    private ColorShaderProgram mColorShaderProgram;

    private int mTexture;

    private final float mLeftBound = -0.5f;
    private final float mRightBound = 0.5f;
    private final float mFarBound = -0.8f;
    private final float mNearBound = 0.8f;

    private Geometry.Point mPreBlueMalletPosition;
    private Geometry.Point mPuckPosition;
    private Geometry.Vector mPuckVector;

    public TextureRenderer(Context context){
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mTable = new Table();
        mMallet = new Mallet(0.08f, 0.15f, 32);
        mPuck = new Puck(0.06f, 0.02f, 32);

        mTextureShaderProgram = new TextureShaderProgram(mContext);
        mColorShaderProgram = new ColorShaderProgram(mContext);

        mTexture = TextureHelper.loadTexture(mContext , R.drawable.air_hockey_surface);

        mBlueMalletPosition = new Geometry.Point(0 , mMallet.mHeight/2 , 0.4f);

        mPuckPosition = new Geometry.Point(0 , mPuck.mHeight / 2 , 0);
        mPuckVector = new Geometry.Vector(0 , 0 , 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0 , 0 , width , height);

        MatrixHelper.perspectiveM(mProjectionMatrix , 45 , (float)width / (float)height , 1f , 10f);
        setLookAtM(mViewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
//        setIdentityM(mModelMatrix, 0);
//        translateM(mModelMatrix, 0, 0f, 0f, -2.5f);
//        rotateM(mModelMatrix, 0 , -60 , 1 , 0 , 0);
//
//        final float[] temp = new float[16];
//        multiplyMM(temp, 0, mProjectionMatrix, 0, mModelMatrix, 0);
//        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        multiplyMM(mProjectionViewMatrix , 0 ,mProjectionMatrix , 0 , mViewMatrix , 0);

        //生成逆转矩阵
        invertM(mInvertedViewProjectionMatrix , 0 , mProjectionViewMatrix , 0);

        positionTableInScene();
        mTextureShaderProgram.useProgram();
        mTextureShaderProgram.setUniforms(mProjectionViewModelMatrix , mTexture);
        mTable.bindData(mTextureShaderProgram);
        mTable.draw();

        positionObjectInScence(0 , mMallet.mHeight / 2 , -0.4f);
        mColorShaderProgram.useProgram();
        mColorShaderProgram.setUniforms(mProjectionViewModelMatrix , 1 , 0 , 0);
        mMallet.bindData(mColorShaderProgram);
        mMallet.draw();

        positionObjectInScence(mBlueMalletPosition.x, mBlueMalletPosition.y , mBlueMalletPosition.z);
        mColorShaderProgram.setUniforms(mProjectionViewModelMatrix , 0 , 0 , 1);
        mMallet.draw();

        mPuckVector = mPuckVector.scale(0.99f);
        mPuckPosition = mPuckPosition.translate(mPuckVector);
        if (mPuckPosition.x < mLeftBound + mPuck.mRadius
                || mPuckPosition.x > mRightBound - mPuck.mRadius) {
            mPuckVector = new Geometry.Vector(-mPuckVector.x, mPuckVector.y, mPuckVector.z);
            mPuckVector = mPuckVector.scale(0.9f);
        }
        if (mPuckPosition.z < mFarBound + mPuck.mRadius
                || mPuckPosition.z > mNearBound - mPuck.mRadius) {
            mPuckVector = new Geometry.Vector(mPuckVector.x, mPuckVector.y, -mPuckVector.z);
            mPuckVector = mPuckVector.scale(0.9f);
        }
        mPuckPosition = new Geometry.Point(
                clamp(mPuckPosition.x, mLeftBound + mPuck.mRadius, mRightBound - mPuck.mRadius),
                mPuckPosition.y,
                clamp(mPuckPosition.z, mFarBound + mPuck.mRadius, mNearBound - mPuck.mRadius)
        );
        positionObjectInScence(mPuckPosition.x, mPuckPosition.y, mPuckPosition.z);
        mColorShaderProgram.setUniforms(mProjectionViewModelMatrix, 0.8f, 0.8f, 1f);
        mPuck.bindData(mColorShaderProgram);
        mPuck.draw();

//        mTextureShaderProgram.useProgram();
//        mTextureShaderProgram.setUniforms(mProjectionMatrix , mTexture);
//        mTable.bindData(mTextureShaderProgram);
//        mTable.draw();
//
//        mColorShaderProgram.useProgram();
//        mColorShaderProgram.setUniforms(mProjectionMatrix);
//        mMallet.bindData(mColorShaderProgram);
//        mMallet.draw();
    }

    private void positionTableInScene(){
        setIdentityM(mModelMatrix , 0);
        rotateM(mModelMatrix , 0 , -90 , 1 , 0 , 0);
        multiplyMM(mProjectionViewModelMatrix , 0 , mProjectionViewMatrix , 0 , mModelMatrix , 0);
    }

    private void positionObjectInScence(float x , float y , float z){
        setIdentityM(mModelMatrix , 0);
        translateM(mModelMatrix , 0 , x , y , z);
        multiplyMM(mProjectionViewModelMatrix , 0 , mProjectionViewMatrix , 0 , mModelMatrix , 0);
    }

    public void handleTouchPress(float normalizedX , float normalizedY){
        Geometry.Ray ray = convert2DPointToRay(normalizedX , normalizedY);
        Geometry.Sphere sphere = new Geometry.Sphere(mBlueMalletPosition , mMallet.mRadius);
        mMalletPressed = Geometry.intersects(sphere , ray);
    }

    public void handleTouchDrag(float normalizedX , float normalizedY){
        if (mMalletPressed){
            Geometry.Ray ray = convert2DPointToRay(normalizedX, normalizedY);

            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0),
                    new Geometry.Vector(0, 1, 0));

            Geometry.Point touchPoint = Geometry.intersectionPoint(ray , plane);

            mPreBlueMalletPosition = mBlueMalletPosition;

            mBlueMalletPosition = new Geometry.Point(
                    clamp(touchPoint.x , mLeftBound + mMallet.mRadius , mRightBound - mMallet.mRadius),
                    mMallet.mHeight/2 ,
                    clamp(touchPoint.z , 0 + mMallet.mRadius , mNearBound - mMallet.mRadius));

            float distance = Geometry.vectorBetween(mBlueMalletPosition , mPuckPosition).length();
            if (distance < (mMallet.mRadius + mPuck.mRadius)){
                mPuckVector = Geometry.vectorBetween(mPreBlueMalletPosition , mBlueMalletPosition);
            }
        }else {
            Logger.debug(TAG , "not touched");
        }
    }

    private Geometry.Ray convert2DPointToRay(float normalizedX , float normalizedY){
        final float[] nearPointNdc = {normalizedX , normalizedY , -1 , 1};
        final float[] farPointNdc = {normalizedX , normalizedY , 1 , 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld , 0 , mInvertedViewProjectionMatrix , 0 , nearPointNdc , 0);
        multiplyMV(farPointWorld , 0 , mInvertedViewProjectionMatrix , 0 , farPointNdc , 0);

        divideW(nearPointWorld);
        divideW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(
                nearPointWorld[0] , nearPointWorld[1] , nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(
                farPointWorld[0] , farPointWorld[1] , farPointWorld[2]);

        return new Geometry.Ray(nearPointRay , Geometry.vectorBetween(nearPointRay , farPointRay));
    }

    private void divideW(float[] vector){
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
}
