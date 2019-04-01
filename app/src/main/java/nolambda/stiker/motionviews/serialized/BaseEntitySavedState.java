package nolambda.stiker.motionviews.serialized;

import nolambda.stiker.motionviews.viewmodel.Layer;

import java.io.Serializable;

public class BaseEntitySavedState implements Serializable {

    private final Layer layer;
    private final float[] matrixValues;
    private final float holyScale;
    private final int canvasWidth;
    private final int canvasHeight;
    private final PaintData borderPaintData;

    public BaseEntitySavedState(Layer layer,
                                float[] matrixValues,
                                float holyScale,
                                int canvasWidth,
                                int canvasHeight,
                                PaintData borderPaintData) {
        this.layer = layer;
        this.matrixValues = matrixValues;
        this.holyScale = holyScale;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.borderPaintData = borderPaintData;
    }

    public PaintData getBorderPaintData() {
        return borderPaintData;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public Layer getLayer() {
        return layer;
    }

    public float[] getMatrixValues() {
        return matrixValues;
    }

    public float getHolyScale() {
        return holyScale;
    }
}
