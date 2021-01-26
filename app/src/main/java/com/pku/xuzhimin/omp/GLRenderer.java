package com.pku.xuzhimin.omp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {
    private Context context;
    private int programId;
    private int aPositionHandle;
    private FloatBuffer vertexBuffer;

    private final float[] vertexData = {
            0f,0f,0f,
            1f,1f,0f,
            -1f,1f,0f,
            -1f,-1f,0f,
            1f,-1f,0f
    };

    private final float[] projectionMatrix=new float[16];
    private int uMatrixHandle;

    private final short[] indexData = {
            0,1,2,
            0,2,3,
            0,3,4,
            0,4,1
    };
    private ShortBuffer indexBuffer;

    public GLRenderer(Context context) {
        this.context = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(indexData.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);
        indexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShader = ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShader,fragmentShader);
        aPositionHandle = GLES20.glGetAttribLocation(programId,"aPosition");
        uMatrixHandle = GLES20.glGetUniformLocation(programId,"uMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = width > height ? (float)width/height : (float)height/width;
        if (width>height){
            Matrix.orthoM(projectionMatrix,0,-ratio,ratio,-1f,1f,-1f,1f);//m, mOffset, left, right, bottom, top, near, far
        } else {
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-ratio,ratio,-1f,1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,projectionMatrix,0);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,indexData.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);
    }
}
