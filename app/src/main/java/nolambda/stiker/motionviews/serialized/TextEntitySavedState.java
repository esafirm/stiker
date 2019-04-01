package nolambda.stiker.motionviews.serialized;

import nolambda.stiker.motionviews.viewmodel.Layer;

public class TextEntitySavedState extends BaseEntitySavedState {
    public TextEntitySavedState(Layer layer,
                                float[] matrixValues,
                                float holyScale,
                                int canvasWidth,
                                int canvasHeight,
                                PaintData borderPaintData) {
        super(layer, matrixValues, holyScale, canvasWidth, canvasHeight, borderPaintData);
    }
}
