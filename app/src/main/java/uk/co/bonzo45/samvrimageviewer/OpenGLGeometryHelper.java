package uk.co.bonzo45.samvrimageviewer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by sam on 28/05/16.
 */
public class OpenGLGeometryHelper {
    public static final int COORDS_PER_VERTEX = 3;

    // Name (for logging)
    public String name;

    // OpenGL Program
    public int openGLProgram;

    // Buffers to hold vertices, colours & normals.
    public FloatBuffer vertexBuffer;
    public FloatBuffer colourBuffer;
    public FloatBuffer normalBuffer;

    // Model Matrix (Object Space to World Space)
    public float[] modelMatrix;

    // OpenGL Parameter Identifiers.
    public int positionOpenGLParam;
    public int colorOpenGLParam;
    public int normalOpenGLParam;

    public int modelOpenGLParam;
    public int modelViewOpenGLParam;
    public int modelViewProjectionOpenGLParam;

    public int lightPositionOpenGLParam;

    public OpenGLGeometryHelper(float[] vertices, float[] colours, float[] normals, int vertexShader, int fragmentShader, String name) {
        this.name = name;

        modelMatrix = new float[16];

        // Create a buffer to store the position of the square.
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Create a buffer to store the colours of the square.
        ByteBuffer colourByteBuffer = ByteBuffer.allocateDirect(colours.length * 4);
        colourByteBuffer.order(ByteOrder.nativeOrder());
        colourBuffer = colourByteBuffer.asFloatBuffer();
        colourBuffer.put(colours);
        colourBuffer.position(0);

        // Create a buffer to store the normals of the square.
        ByteBuffer normalByteBuffer = ByteBuffer.allocateDirect(normals.length * 4);
        normalByteBuffer.order(ByteOrder.nativeOrder());
        normalBuffer = normalByteBuffer.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);

        openGLProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(openGLProgram, vertexShader);
        GLES20.glAttachShader(openGLProgram, fragmentShader);
        GLES20.glLinkProgram(openGLProgram);
        GLES20.glUseProgram(openGLProgram);

        Utility.checkGLError(name, "Program");

        // Get the OpenGL variable positions.
        positionOpenGLParam = GLES20.glGetAttribLocation(openGLProgram, "a_Position");
        normalOpenGLParam = GLES20.glGetAttribLocation(openGLProgram, "a_Normal");
        colorOpenGLParam = GLES20.glGetAttribLocation(openGLProgram, "a_Color");
        modelOpenGLParam = GLES20.glGetUniformLocation(openGLProgram, "u_Model");
        modelViewOpenGLParam = GLES20.glGetUniformLocation(openGLProgram, "u_MVMatrix");
        modelViewProjectionOpenGLParam = GLES20.glGetUniformLocation(openGLProgram, "u_MVP");
        lightPositionOpenGLParam = GLES20.glGetUniformLocation(openGLProgram, "u_LightPos");

        Utility.checkGLError(name, "Program Parameters");
    }

    /**
     * Draw the shape.
     *
     * Given the transformation matrices, pass them into the shader.
     */
    public void draw(float[] viewMatrix, float[] projectionMatrix, float[] lightPositionInEyeSpace) {
        float[] modelViewMatrix = new float[16];
        float[] modelViewProjectionMatrix = new float[16];
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        GLES20.glUseProgram(openGLProgram);

        GLES20.glUniform3fv(lightPositionOpenGLParam, 1, lightPositionInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelOpenGLParam, 1, false, modelMatrix, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelViewOpenGLParam, 1, false, modelViewMatrix, 0);

        // Set the position of the
        GLES20.glVertexAttribPointer(positionOpenGLParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(modelViewProjectionOpenGLParam, 1, false, modelViewProjectionMatrix, 0);

        // Set the normal positions of the , again for shading
        GLES20.glVertexAttribPointer(normalOpenGLParam, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);
        GLES20.glVertexAttribPointer(colorOpenGLParam, 4, GLES20.GL_FLOAT, false, 0, colourBuffer);

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(positionOpenGLParam);
        GLES20.glEnableVertexAttribArray(normalOpenGLParam);
        GLES20.glEnableVertexAttribArray(colorOpenGLParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBuffer.capacity() / 3);
        Utility.checkGLError(name, "Draw");
    }
}
