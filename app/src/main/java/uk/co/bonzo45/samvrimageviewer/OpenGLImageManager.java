package uk.co.bonzo45.samvrimageviewer;

import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.List;

/**
 * Manages the images that are being displayed in the application.
 *
 * Each image has a square (rendered as two triangles) which it is textured onto.
 */
public class OpenGLImageManager {

    // Store a bunch of squares.
    private int currentImage;
    List<OpenGLGeometryHelper> squares;

    float[] modelMatrix;

    public OpenGLImageManager (List<Bitmap> bitmaps, int vertexShader, int fragmentShader, float[] modelMatrix) {
        squares = new LinkedList<>();
        this.modelMatrix = modelMatrix;

        for (int i = 0; i < bitmaps.size(); i++) {
            OpenGLGeometryHelper square = createSquare(i, bitmaps.get(i), vertexShader, fragmentShader, modelMatrix.clone());
            squares.add(square);
        }
    }

    /**
     * Adds a new image to the start of the list. Pushes one off the end.
     * @param id - an id (for logging)
     * @param bitmap - an image to texture onto the shape
     * @param vertexShader - the vertex shader reference
     * @param fragmentShader - the fragment shader reference
     */
    public void pushToStart(int id, Bitmap bitmap, int vertexShader, int fragmentShader) {
        OpenGLGeometryHelper square = createSquare(id, bitmap, vertexShader, fragmentShader, modelMatrix.clone());
        squares.remove(squares.size() - 1);
        squares.add(0, square);
    }

    /**
     * Adds a new image to the start of the list. Pushes one off the end.
     * @param id - an id (for logging)
     * @param bitmap - an image to texture onto the shape
     * @param vertexShader - the vertex shader reference
     * @param fragmentShader - the fragment shader reference
     */
    public void pushToEnd(int id, Bitmap bitmap, int vertexShader, int fragmentShader) {
        OpenGLGeometryHelper square = createSquare(id, bitmap, vertexShader, fragmentShader, modelMatrix.clone());
        squares.remove(0);
        squares.add(square);
    }

    /**
     * Creates a new image and square.
     * @param id - for logging
     * @param bitmap - for the texture
     * @param vertexShader - vertex shader reference
     * @param fragmentShader - fragment shader reference
     * @param modelMatrix - model matrix for the square
     * @return
     */
    private OpenGLGeometryHelper createSquare(int id, Bitmap bitmap, int vertexShader, int fragmentShader, float[] modelMatrix) {
        OpenGLGeometryHelper square = new OpenGLGeometryHelper(WorldData.SQUARE_COORDS, WorldData.SQUARE_NORMALS, vertexShader, fragmentShader, "Square_" + id);
        square.modelMatrix = modelMatrix;
        square.setTexture(Utility.loadTextures(bitmap)[0], WorldData.SQUARE_TEXTURE_COORDS);
        return square;
    }

    public int numberOfImages() {
        return squares.size();
    }
}
