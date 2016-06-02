package uk.co.bonzo45.samvrimageviewer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Debug;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.google.vr.sdk.audio.GvrAudioEngine;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends GvrActivity implements GvrView.StereoRenderer, InteractionCallback {

    // Constants
    private static final String TAG = "SamMainActivity"; // For logging.
    private static final int COORDS_PER_VERTEX = 3; // (x, y, z)

    private static final String SOUND_FILE = "cube_sound.wav";

    private static final float MIN_MODEL_DISTANCE = 3.0f;
    private static final float MAX_MODEL_DISTANCE = 7.0f;

    // ---- Head/Eye Drawing
    private float[] camera;
    private float[] viewMatrix;
    private float[] headView;
    private float[] modelViewProjectionMatrix;
    private float[] modelViewMatrix;
    private float[] tempPosition;
    private float[] headRotation;
    private final float[] lightPosInEyeSpace = new float[4];

    // Model?
    private static final float UI_DISTANCE = 3.0f;
    protected float[] modelPosition;

    // Square, Floor & Octogon
    OpenGLGeometryHelper square;
    OpenGLGeometryHelper floor;
    OpenGLGeometryHelper octogon1;
    OpenGLGeometryHelper octogon2;

    ///= ????
    private float floorDepth = 20f;

    // Audio
    private GvrAudioEngine gvrAudioEngine;
    private volatile int soundId = GvrAudioEngine.INVALID_ID;
    protected float[] soundPosition;

    // Vibrator
    private Vibrator vibrator;

    // Images
    public final static int BITMAP_CACHE_SIZE = 5;

    private int imagesInFolder;

    List<Bitmap> bitmapCache;
    private int bitmapsInCache;
    private int bitmapCurrent;
    private int bitmapCacheStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Debug.startMethodTracing("onCreate");

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
        float eyeX = 0.0f, eyeY = 0.0f, eyeZ = 0.0f;
        float lookX = 0.0f, lookY = 0.0f, lookZ = -1.0f;
        float upX = 0.0f, upY = 1.0f, upZ = 0.0f;
        Matrix.setLookAtM(camera, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        viewMatrix = new float[16];
        modelViewMatrix = new float[16];
        modelViewProjectionMatrix = new float[16];

        tempPosition = new float[4];
        // Model first appears directly in front of user.
        //modelPosition = new float[] {0.0f, 0.0f, 10.0f};
        headRotation = new float[4];
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize 3D audio engine.
        gvrAudioEngine = new GvrAudioEngine(this, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
        soundPosition = new float[] {0.0f, 0.0f, UI_DISTANCE};

        // Pick a folder to view images from.
        Intent pickFolderIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(pickFolderIntent, 1);

    }

    @Override
    public void onDestroy() {
        Debug.stopMethodTracing();
        super.onDestroy();
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

        // See if we're looking at things:
        Matrix.multiplyMM(viewMatrix, 0, headView, 0, camera, 0);
        long time = System.currentTimeMillis();
        octogon1.updateBeingLookedAt(viewMatrix, 0.1f, 0.1f, time, this, 1);
        octogon2.updateBeingLookedAt(viewMatrix, 0.1f, 0.1f, time, this, 2);

        // Update the 3d audio engine with the most recent head rotation.
        headTransform.getQuaternion(headRotation, 0);
        gvrAudioEngine.setHeadRotation(
                headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
        // Regular update call to GVR audio engine.
        gvrAudioEngine.update();

        Utility.checkGLError(TAG, "onReadyToDraw");
    }

    /**
     * Called twice every frame (to draw each eye's viewMatrix).
     * @param eye
     */
    @Override
    public void onDrawEye(Eye eye) {
        // Change some OpenGL flags?
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Utility.checkGLError(TAG, "colorParam");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(viewMatrix, 0, eye.getEyeView(), 0, camera, 0);

        // Get the 'eye positon' of the light.
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, WorldData.LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build ModelView and ModelViewProjection matrices for calculating cube position and light.
        float zNear = 0.1f;
        float zFar = 100f;
        float[] perspective = eye.getPerspective(zNear, zFar);

        square.draw(viewMatrix, perspective, lightPosInEyeSpace);
        floor.draw(viewMatrix, perspective, lightPosInEyeSpace);
        octogon1.draw(viewMatrix, perspective, lightPosInEyeSpace);
        octogon2.draw(viewMatrix, perspective, lightPosInEyeSpace);
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

        // Load the OpenGL Shaders
        int colourVertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int passthroughFragmentShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);
        int gridFragmentShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        int textureVertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.texture_vertex);
        int textureFragmentShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.texture_fragment);

        square = new OpenGLGeometryHelper(WorldData.SQUARE_COORDS, WorldData.SQUARE_NORMALS, textureVertexShader, textureFragmentShader, "Square1");
        Matrix.setIdentityM(square.modelMatrix, 0);
        Matrix.translateM(square.modelMatrix, 0, 0.0f, 0.0f, UI_DISTANCE);
        Bitmap bitmap = bitmapFromResource(R.drawable.texture);
        int texture = loadTexture(bitmap);
        bitmap.recycle();
        square.setTexture(texture, WorldData.SQUARE_TEXTURE_COORDS);

        octogon1 = new OpenGLGeometryHelper(WorldData.OCTOGON_COORDS, WorldData.OCTOGON_NORMALS, colourVertexShader, passthroughFragmentShader, "Octogon1");
        Matrix.setIdentityM(octogon1.modelMatrix, 0);
        Matrix.rotateM(octogon1.modelMatrix, 0, 25, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(octogon1.modelMatrix, 0, 0.0f, 0.0f, UI_DISTANCE);
        Matrix.scaleM(octogon1.modelMatrix, 0, 0.25f, 0.25f, 0.25f);

        octogon2 = new OpenGLGeometryHelper(WorldData.OCTOGON_COORDS, WorldData.OCTOGON_NORMALS, colourVertexShader, passthroughFragmentShader, "Octogon2");
        Matrix.setIdentityM(octogon2.modelMatrix, 0);
        Matrix.rotateM(octogon2.modelMatrix, 0, -25, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(octogon2.modelMatrix, 0, 0.0f, 0.0f, UI_DISTANCE);
        Matrix.scaleM(octogon2.modelMatrix, 0, 0.25f, 0.25f, 0.25f);

        floor = new OpenGLGeometryHelper(WorldData.FLOOR_COORDS, WorldData.FLOOR_NORMALS, colourVertexShader, gridFragmentShader, "Floor");
        Matrix.setIdentityM(floor.modelMatrix, 0);
        Matrix.translateM(floor.modelMatrix, 0, 0, -floorDepth, 0); // Floor appears below user.

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
                                soundId, soundPosition[0], soundPosition[1], soundPosition[2]);
                        gvrAudioEngine.playSound(soundId, true /* looped playback */);
                    }
                })
                .start();

        // updateModelPosition();

        // Should we do something else here?
        Utility.checkGLError(TAG, "onSurfaceCreated");
    }

    /**
     * Updates the cube model position.
     */
    protected void updateModelPosition() {
        Matrix.setIdentityM(square.modelMatrix, 0);
        Matrix.translateM(square.modelMatrix, 0, modelPosition[0], modelPosition[1], modelPosition[2]);

        Matrix.setIdentityM(octogon1.modelMatrix, 0);
        //Matrix.scaleM(modelOctogon, 0, 0.2f, 0.2f, 0.2f);
        Matrix.translateM(octogon1.modelMatrix, 0, modelPosition[0] + 2, modelPosition[1], modelPosition[2]);

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

        // Point the user at the box again.
        Matrix.invertM(camera, 0, headView, 0);
        Matrix.rotateM(camera, 0, 180, 0.0f, 1.0f, 0.0f);

        // Always give user feedback.
        vibrator.vibrate(50);
    }

    @Override
    public void handleCallback(int identifier) {
        switch (identifier) {
            case 1:
                Log.i(TAG, "Previous Photo");
                bitmapCurrent = Math.max(0, bitmapCurrent - 1);
                square.setTexture(loadTexture(bitmapCache.get(bitmapCurrent)), WorldData.SQUARE_TEXTURE_COORDS);
                break;
            case 2:
                Log.i(TAG, "Next Photo");
                bitmapCurrent = Math.min(bitmapCache.size() - 1, bitmapCurrent + 1);
                square.setTexture(loadTexture(bitmapCache.get(bitmapCurrent)), WorldData.SQUARE_TEXTURE_COORDS);
                break;
            default:
                Log.i(TAG, "Unknown Callback ID");
        }
    }

    public int loadTexture(Bitmap bitmap) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public Bitmap bitmapFromResource(int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling

        // Read in the resource
        return BitmapFactory.decodeResource(getResources(), resourceId, options);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User has selected (or perhaps not) a folder.
        if (requestCode == 1) {
            // Check to see if the user selected a folder (they could have pressed back)
            if (resultCode == RESULT_OK) {
                // Get the folder that they picked, and all the files in it.
                DocumentFile folder = DocumentFile.fromTreeUri(getApplicationContext(), data.getData());
                DocumentFile[] files = folder.listFiles();

                // Create a cache to store some of the images in the folder.
                bitmapCache = new LinkedList<Bitmap>();
                imagesInFolder = 0;

                // Go through each file.
                for (int i = 0; i < files.length; i++) {
                    DocumentFile file = files[i];
                    // If it's an image, add it to the cache (if not full)
                    if (file.isFile() && file.getType().startsWith("image/")) {
                        imagesInFolder++;
                        if (bitmapCache.size() <= BITMAP_CACHE_SIZE) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(file.getUri());
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                bitmapCache.add(bitmap);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (bitmapCache.size() >= 1) {
                    bitmapCacheStart = 1;
                    bitmapCurrent = 1;
                }
                else {
                    bitmapCacheStart = 0;
                    bitmapCurrent = 0;
                }
            }
        }
    }
}
