package uk.co.bonzo45.samvrimageviewer;

/**
 * Created by sam on 25/05/16.
 */
public class WorldData {

    public static final float[] SQUARE_COORDS = new float[] {
        // Front face
        -1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f
//
//        // Right face
//        1.0f, 1.0f, 1.0f,
//        1.0f, -1.0f, 1.0f,
//        1.0f, 1.0f, -1.0f,
//        1.0f, -1.0f, 1.0f,
//        1.0f, -1.0f, -1.0f,
//        1.0f, 1.0f, -1.0f,
//
//        // Back face
//        1.0f, 1.0f, -1.0f,
//        1.0f, -1.0f, -1.0f,
//        -1.0f, 1.0f, -1.0f,
//        1.0f, -1.0f, -1.0f,
//        -1.0f, -1.0f, -1.0f,
//        -1.0f, 1.0f, -1.0f,
//
//        // Left face
//        -1.0f, 1.0f, -1.0f,
//        -1.0f, -1.0f, -1.0f,
//        -1.0f, 1.0f, 1.0f,
//        -1.0f, -1.0f, -1.0f,
//        -1.0f, -1.0f, 1.0f,
//        -1.0f, 1.0f, 1.0f,
//
//        // Top face
//        -1.0f, 1.0f, -1.0f,
//        -1.0f, 1.0f, 1.0f,
//        1.0f, 1.0f, -1.0f,
//        -1.0f, 1.0f, 1.0f,
//        1.0f, 1.0f, 1.0f,
//        1.0f, 1.0f, -1.0f,
//
//        // Bottom face
//        1.0f, -1.0f, -1.0f,
//        1.0f, -1.0f, 1.0f,
//        -1.0f, -1.0f, -1.0f,
//        1.0f, -1.0f, 1.0f,
//        -1.0f, -1.0f, 1.0f,
//        -1.0f, -1.0f, -1.0f
    };

    public static final float[] SQUARE_COLOURS = new float[] {
        // front, green
        0f, 0.5273f, 0.2656f, 1.0f,
        0f, 0.5273f, 0.2656f, 1.0f,
        0f, 0.5273f, 0.2656f, 1.0f,
        0f, 0.5273f, 0.2656f, 1.0f,
        0f, 0.5273f, 0.2656f, 1.0f,
        0f, 0.5273f, 0.2656f, 1.0f
//
//        // right, blue
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//
//        // back, also green
//        0f, 0.5273f, 0.2656f, 1.0f,
//        0f, 0.5273f, 0.2656f, 1.0f,
//        0f, 0.5273f, 0.2656f, 1.0f,
//        0f, 0.5273f, 0.2656f, 1.0f,
//        0f, 0.5273f, 0.2656f, 1.0f,
//        0f, 0.5273f, 0.2656f, 1.0f,
//
//        // left, also blue
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//        0.0f, 0.3398f, 0.9023f, 1.0f,
//
//        // top, red
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//
//        // bottom, also red
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f,
//        0.8359375f,  0.17578125f,  0.125f, 1.0f
    };

    public static final float[] SQUARE_NORMALS = new float[] {
        // Front face
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
//
//        // Right face
//        1.0f, 0.0f, 0.0f,
//        1.0f, 0.0f, 0.0f,
//        1.0f, 0.0f, 0.0f,
//        1.0f, 0.0f, 0.0f,
//        1.0f, 0.0f, 0.0f,
//        1.0f, 0.0f, 0.0f,
//
//        // Back face
//        0.0f, 0.0f, -1.0f,
//        0.0f, 0.0f, -1.0f,
//        0.0f, 0.0f, -1.0f,
//        0.0f, 0.0f, -1.0f,
//        0.0f, 0.0f, -1.0f,
//        0.0f, 0.0f, -1.0f,
//
//        // Left face
//        -1.0f, 0.0f, 0.0f,
//        -1.0f, 0.0f, 0.0f,
//        -1.0f, 0.0f, 0.0f,
//        -1.0f, 0.0f, 0.0f,
//        -1.0f, 0.0f, 0.0f,
//        -1.0f, 0.0f, 0.0f,
//
//        // Top face
//        0.0f, 1.0f, 0.0f,
//        0.0f, 1.0f, 0.0f,
//        0.0f, 1.0f, 0.0f,
//        0.0f, 1.0f, 0.0f,
//        0.0f, 1.0f, 0.0f,
//        0.0f, 1.0f, 0.0f,
//
//        // Bottom face
//        0.0f, -1.0f, 0.0f,
//        0.0f, -1.0f, 0.0f,
//        0.0f, -1.0f, 0.0f,
//        0.0f, -1.0f, 0.0f,
//        0.0f, -1.0f, 0.0f,
//        0.0f, -1.0f, 0.0f
    };

    public static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {
            0.0f, 2.0f, 0.0f, 1.0f
    };

    // The grid lines on the floor are rendered procedurally and large polygons cause floating point
    // precision problems on some architectures. So we split the floor into 4 quadrants.
    public static final float[] FLOOR_COORDS = new float[] {
            // +X, +Z quadrant
            200, 0, 0,
            0, 0, 0,
            0, 0, 200,
            200, 0, 0,
            0, 0, 200,
            200, 0, 200,

            // -X, +Z quadrant
            0, 0, 0,
            -200, 0, 0,
            -200, 0, 200,
            0, 0, 0,
            -200, 0, 200,
            0, 0, 200,

            // +X, -Z quadrant
            200, 0, -200,
            0, 0, -200,
            0, 0, 0,
            200, 0, -200,
            0, 0, 0,
            200, 0, 0,

            // -X, -Z quadrant
            0, 0, -200,
            -200, 0, -200,
            -200, 0, 0,
            0, 0, -200,
            -200, 0, 0,
            0, 0, 0,
    };

    public static final float[] FLOOR_NORMALS = new float[] {
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
    };

    public static final float[] FLOOR_COLORS = new float[] {
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
    };

    /**
     * Octogon
     **/
    public static final float OCTOGON_P1_X = 0.3827f;
    public static final float OCTOGON_P1_Y = 0.9239f;
    public static final float OCTOGON_P2_X = 0.923906f;
    public static final float OCTOGON_P2_Y = 0.382686f;
    public static final float OCTOGON_P3_X = 0.9239f;
    public static final float OCTOGON_P3_Y = -0.3827f;
    public static final float OCTOGON_P4_X = 0.382686f;
    public static final float OCTOGON_P4_Y = -.923906f;
    public static final float OCTOGON_P5_X = -0.382686f;
    public static final float OCTOGON_P5_Y = -0.923906f;
    public static final float OCTOGON_P6_X = -0.9239f;
    public static final float OCTOGON_P6_Y = -0.3827f;
    public static final float OCTOGON_P7_X = -0.923906f;
    public static final float OCTOGON_P7_Y = 0.382686f;
    public static final float OCTOGON_P8_X = -0.3827f;
    public static final float OCTOGON_P8_Y = 0.9239f;

    public static final float[] OCTOGON_COORDS = new float[] {
        // Point 1, Point 2, Zero
        OCTOGON_P1_X, OCTOGON_P1_Y, 0.0f,
        OCTOGON_P2_X, OCTOGON_P2_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 2, Point 3, Zero
        OCTOGON_P2_X, OCTOGON_P2_Y, 0.0f,
        OCTOGON_P3_X, OCTOGON_P3_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 3, Point 4, Zero
        OCTOGON_P3_X, OCTOGON_P3_Y, 0.0f,
        OCTOGON_P4_X, OCTOGON_P4_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 4, Point 5, Zero
        OCTOGON_P4_X, OCTOGON_P4_Y, 0.0f,
        OCTOGON_P5_X, OCTOGON_P5_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 5, Point 6, Zero
        OCTOGON_P5_X, OCTOGON_P5_Y, 0.0f,
        OCTOGON_P6_X, OCTOGON_P6_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 6, Point 7, Zero
        OCTOGON_P6_X, OCTOGON_P6_Y, 0.0f,
        OCTOGON_P7_X, OCTOGON_P7_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 7, Point 8, Zero
        OCTOGON_P7_X, OCTOGON_P7_Y, 0.0f,
        OCTOGON_P8_X, OCTOGON_P8_Y, 0.0f,
        0.0f, 0.0f, 0.0f,

        // Point 8, Point 1, Zero
        OCTOGON_P8_X, OCTOGON_P8_Y, 0.0f,
        OCTOGON_P1_X, OCTOGON_P1_Y, 0.0f,
        0.0f, 0.0f, 0.0f,
    };

    public static final float[] OCTOGON_COLOURS = new float[]{
            // front, green
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,

            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
            0f, 0.5273f, 0.2656f, 1.0f,
    };

    public static final float[] OCTOGON_NORMALS = new float[]{
            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
    };
}
