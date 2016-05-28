package uk.co.bonzo45.samvrimageviewer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends GvrActivity implements GvrView.StereoRenderer {

    // Constants
    private static final String TAG = "SamMainActivity"; // For logging.
    private static final int COORDS_PER_VERTEX = 3; // (x, y, z)

    private static final String SOUND_FILE = "cube_sound.wav";

    private static final float MIN_MODEL_DISTANCE = 3.0f;
    private static final float MAX_MODEL_DISTANCE = 7.0f;

    // Buffers
    private FloatBuffer squareVertices;
    private FloatBuffer squareColours;
    private FloatBuffer squareNormals;

    private FloatBuffer floorVertices;
    private FloatBuffer floorColors;
    private FloatBuffer floorNormals;

    // OpenGL 'Program'
    private int squareProgram;
    private int floorProgram;

    // OpenGL 'Variables'
    private int squarePositionParam;
    private int squareNormalParam;
    private int squareColorParam;
    private int squareModelParam;
    private int squareModelViewParam;
    private int squareModelViewProjectionParam;
    private int squareLightPosParam;

    private int floorPositionParam;
    private int floorNormalParam;
    private int floorColorParam;
    private int floorModelParam;
    private int floorModelViewParam;
    private int floorModelViewProjectionParam;
    private int floorLightPosParam;

    // ---- Head/Eye Drawing
    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelViewProjection;
    private float[] modelView;
    private float[] tempPosition;
    private float[] headRotation;
    private final float[] lightPosInEyeSpace = new float[4];

    // Model?
    private float[] modelFloor;
    protected float[] modelPosition;
    protected float[] modelSquare;

    // Everything for Octogon
    OpenGLGeometryHelper octogon;

    ///= ????
    private float floorDepth = 20f;

    // Audio
    private GvrAudioEngine gvrAudioEngine;
    private volatile int soundId = GvrAudioEngine.INVALID_ID;

    // Vibrator
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the GVR View and set the renderer to this activity (which is a StereoRenderer)...
        GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        gvrView.setVRModeEnabled(false);
        gvrView.setRenderer(this);
        gvrView.setTransitionViewEnabled(false);
        gvrView.setOnCardboardBackButtonListener(
                new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
        setGvrView(gvrView);

        camera = new float[16];
        // Build the camera matrix. Point it in the -z direction.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, 0.1f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];

        modelSquare = new float[16];
        modelFloor = new float[16];


        tempPosition = new float[4];
        // Model first appears directly in front of user.
        modelPosition = new float[] {0.0f, 0.0f, 10.0f};
        headRotation = new float[4];
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize 3D audio engine.
        gvrAudioEngine = new GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
    }

    @Override
    public void onPause() {
        gvrAudioEngine.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        gvrAudioEngine.resume();
    }

    /**
     * Called once every frame (before it's drawn)...
     * @param headTransform
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getHeadView(headView, 0);
        // Update Data // Animations // Physics

        // Get the head position.
        headTransform.getHeadView(headView, 0);

        // Update the 3d audio engine with the most recent head rotation.
        headTransform.getQuaternion(headRotation, 0);
        gvrAudioEngine.setHeadRotation(
                headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
        // Regular update call to GVR audio engine.
        gvrAudioEngine.update();

        Utility.checkGLError(TAG, "onReadyToDraw");
    }

    /**
     * Called twice every frame (to draw each eye's view).
     * @param eye
     */
    @Override
    public void onDrawEye(Eye eye) {
        // Change some OpenGL flags?
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Utility.checkGLError(TAG, "colorParam");

        // point in square -> Model Matrix -> point in world -> View Matrix -> point in view world -> Projection Matrix -> point in pixel space
        // (0, 0, 10) = head(camera2(0, 0, 10));
        // head^-1(0, 0, 10) = camera2(0, 0, 10));


        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Get the 'eye positon' of the light.
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, WorldData.LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build ModelView and ModelViewProjection matrices for calculating cube position and light.
        float zNear = 0.1f;
        float zFar = 100f;
        float[] perspective = eye.getPerspective(zNear, zFar);
        Matrix.multiplyMM(modelView, 0, view, 0, modelSquare, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawSquare();

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, modelFloor, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawFloor();

        // Draw an OCTOGON!
        octogon.draw(view, perspective, lightPosInEyeSpace);
    }

    /**
     * Draw the square.
     *
     * <p>We've set all of our transformation matrices. Now we simply pass them into the shader.
     */
    public void drawSquare() {
        GLES20.glUseProgram(squareProgram);

        GLES20.glUniform3fv(squareLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(squareModelParam, 1, false, modelSquare, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(squareModelViewParam, 1, false, modelView, 0);

        // Set the position of the square
        GLES20.glVertexAttribPointer(
                squarePositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, squareVertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(squareModelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the square, again for shading
        GLES20.glVertexAttribPointer(squareNormalParam, 3, GLES20.GL_FLOAT, false, 0, squareNormals);
        GLES20.glVertexAttribPointer(squareColorParam, 4, GLES20.GL_FLOAT, false, 0, squareColours);

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(squarePositionParam);
        GLES20.glEnableVertexAttribArray(squareNormalParam);
        GLES20.glEnableVertexAttribArray(squareColorParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        Utility.checkGLError(TAG, "Drawing Square");
    }

    /**
     * Draw the floor.
     *
     * <p>This feeds in data for the floor into the shader. Note that this doesn't feed in data about
     * position of the light, so if we rewrite our code to draw the floor first, the lighting might
     * look strange.
     */
    public void drawFloor() {
        GLES20.glUseProgram(floorProgram);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(floorLightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(floorModelParam, 1, false, modelFloor, 0);
        GLES20.glUniformMatrix4fv(floorModelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(floorModelViewProjectionParam, 1, false, modelViewProjection, 0);
        GLES20.glVertexAttribPointer(
                floorPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, floorVertices);
        GLES20.glVertexAttribPointer(floorNormalParam, 3, GLES20.GL_FLOAT, false, 0, floorNormals);
        GLES20.glVertexAttribPointer(floorColorParam, 4, GLES20.GL_FLOAT, false, 0, floorColors);

        GLES20.glEnableVertexAttribArray(floorPositionParam);
        GLES20.glEnableVertexAttribArray(floorNormalParam);
        GLES20.glEnableVertexAttribArray(floorColorParam);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 24);

        Utility.checkGLError(TAG, "drawing floor");
    }


    /**
     * Called after we've finished rendering a frame.
     * Use if you want to overlay something (e.g. to help align phone in cardboard).
     * Viewport is for entire screen (not single eye).
     * @param viewport
     */
    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    /**
     * Called when the size of the surface (screen?) changes.
     * @param i - width
     * @param i1 - height
     */
    @Override
    public void onSurfaceChanged(int i, int i1) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * Called when the rendering surface (screen?) is first initialized.
     * Do initial set up stuff here?
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Log.i(TAG, "onSurfaceCreated");

        // Set Dark Background Colour - can't actually see this changing anything?
        GLES20.glClearColor(0.5f, 0.1f, 0.1f, 0.5f);


        // Create a buffer to store the position of the square.
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(WorldData.SQUARE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        squareVertices = bbVertices.asFloatBuffer();
        squareVertices.put(WorldData.SQUARE_COORDS);
        squareVertices.position(0);

        // Create a buffer to store the colours of the square.
        ByteBuffer bbColours = ByteBuffer.allocateDirect(WorldData.SQUARE_COLOURS.length * 4);
        bbColours.order(ByteOrder.nativeOrder());
        squareColours = bbColours.asFloatBuffer();
        squareColours.put(WorldData.SQUARE_COLOURS);
        squareColours.position(0);

        // Create a buffer to store the normals of the square.
        ByteBuffer bbNormals = ByteBuffer.allocateDirect(WorldData.SQUARE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        squareNormals = bbNormals.asFloatBuffer();
        squareNormals.put(WorldData.SQUARE_NORMALS);
        squareNormals.position(0);

        // Square
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(WorldData.FLOOR_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        floorVertices = bbFloorVertices.asFloatBuffer();
        floorVertices.put(WorldData.FLOOR_COORDS);
        floorVertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(WorldData.FLOOR_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        floorNormals = bbFloorNormals.asFloatBuffer();
        floorNormals.put(WorldData.FLOOR_NORMALS);
        floorNormals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(WorldData.FLOOR_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        floorColors = bbFloorColors.asFloatBuffer();
        floorColors.put(WorldData.FLOOR_COLORS);
        floorColors.position(0);

        // Load the OpenGL Shaders
        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

        squareProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(squareProgram, vertexShader);
        GLES20.glAttachShader(squareProgram, passthroughShader);
        GLES20.glLinkProgram(squareProgram);
        GLES20.glUseProgram(squareProgram);

        Utility.checkGLError(TAG, "Square Program");

        // Get the OpenGL variable positions.
        squarePositionParam = GLES20.glGetAttribLocation(squareProgram, "a_Position");
        squareNormalParam = GLES20.glGetAttribLocation(squareProgram, "a_Normal");
        squareColorParam = GLES20.glGetAttribLocation(squareProgram, "a_Color");
        squareModelParam = GLES20.glGetUniformLocation(squareProgram, "u_Model");
        squareModelViewParam = GLES20.glGetUniformLocation(squareProgram, "u_MVMatrix");
        squareModelViewProjectionParam = GLES20.glGetUniformLocation(squareProgram, "u_MVP");
        squareLightPosParam = GLES20.glGetUniformLocation(squareProgram, "u_LightPos");

        Utility.checkGLError(TAG, "Square program params");

        floorProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(floorProgram, vertexShader);
        GLES20.glAttachShader(floorProgram, gridShader);
        GLES20.glLinkProgram(floorProgram);
        GLES20.glUseProgram(floorProgram);

        Utility.checkGLError(TAG, "Floor program");

        floorModelParam = GLES20.glGetUniformLocation(floorProgram, "u_Model");
        floorModelViewParam = GLES20.glGetUniformLocation(floorProgram, "u_MVMatrix");
        floorModelViewProjectionParam = GLES20.glGetUniformLocation(floorProgram, "u_MVP");
        floorLightPosParam = GLES20.glGetUniformLocation(floorProgram, "u_LightPos");

        floorPositionParam = GLES20.glGetAttribLocation(floorProgram, "a_Position");
        floorNormalParam = GLES20.glGetAttribLocation(floorProgram, "a_Normal");
        floorColorParam = GLES20.glGetAttribLocation(floorProgram, "a_Color");

        Utility.checkGLError(TAG, "Floor program params");

        Matrix.setIdentityM(modelFloor, 0);
        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0); // Floor appears below user.

        // Octogon
        octogon = new OpenGLGeometryHelper(WorldData.OCTOGON_COORDS, WorldData.OCTOGON_COLOURS, WorldData.OCTOGON_NORMALS, vertexShader, passthroughShader, "Octogon1");

        // Avoid any delays during start-up due to decoding of sound files.
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // Start spatial audio playback of SOUND_FILE at the model postion. The returned
                        //soundId handle is stored and allows for repositioning the sound object whenever
                        // the cube position changes.
                        gvrAudioEngine.preloadSoundFile(SOUND_FILE);
                        soundId = gvrAudioEngine.createSoundObject(SOUND_FILE);
                        gvrAudioEngine.setSoundObjectPosition(
                                soundId, modelPosition[0], modelPosition[1], modelPosition[2]);
                        gvrAudioEngine.playSound(soundId, true /* looped playback */);
                    }
                })
                .start();

        updateModelPosition();

        // Should we do something else here?
        Utility.checkGLError(TAG, "onSurfaceCreated");
    }

    /**
     * Updates the cube model position.
     */
    protected void updateModelPosition() {
        Log.i(TAG, "modelPosition1: (" + modelPosition[0] + ", " + modelPosition[1] + ", " + modelPosition[2] + ")");

        Matrix.setIdentityM(modelSquare, 0);
        Matrix.translateM(modelSquare, 0, modelPosition[0], modelPosition[1], modelPosition[2]);

        Log.i(TAG, "modelPosition:2 (" + modelPosition[0] + ", " + modelPosition[1] + ", " + modelPosition[2] + ")");

        Matrix.setIdentityM(octogon.modelMatrix, 0);
        //Matrix.scaleM(modelOctogon, 0, 0.2f, 0.2f, 0.2f);
        Matrix.translateM(octogon.modelMatrix, 0, modelPosition[0] + 2, modelPosition[1], modelPosition[2]);

        Log.i(TAG, "modelPosition3: (" + modelPosition[0] + ", " + modelPosition[1] + ", " + modelPosition[2] + ")");

        // Update the sound location to match it with the new cube position.
        if (soundId != GvrAudioEngine.INVALID_ID) {
            gvrAudioEngine.setSoundObjectPosition(
                    soundId, modelPosition[0], modelPosition[1], modelPosition[2]);
        }
        Utility.checkGLError(TAG, "updateCubePosition");
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    private int loadGLShader(int type, int resId) {
        String code = Utility.readRawTextFile(getResources().openRawResource(resId));
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }


    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");

        Log.i(TAG, "Camera before reset: (" + camera[0] + ", " + camera[1] + ", " + camera[2] + ")");
        // Point the user at the box again.
        Matrix.invertM(camera, 0, headView, 0);
        Matrix.rotateM(camera, 0, 180, 0.0f, 1.0f, 0.0f);
        // Always give user feedback.
        vibrator.vibrate(50);
    }
}
