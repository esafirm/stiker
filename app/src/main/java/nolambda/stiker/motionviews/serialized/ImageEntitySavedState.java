package nolambda.stiker.motionviews.serialized;

import nolambda.stiker.motionviews.viewmodel.Layer;
import nolambda.stiker.motionviews.widget.entity.ImageEntity;

public class ImageEntitySavedState extends BaseEntitySavedState {
    private final ImageEntity.BitmapProvider bitmapProvider;

    public ImageEntitySavedState(Layer layer,
                                 float[] matrixValues,
                                 float holyScale,
                                 int canvasWidth,
                                 int canvasHeight,
                                 PaintData borderPaintData,
                                 ImageEntity.BitmapProvider bitmapProvider) {
        super(layer, matrixValues, holyScale, canvasWidth, canvasHeight, borderPaintData);
        this.bitmapProvider = bitmapProvider;
    }

    public ImageEntity.BitmapProvider getBitmapProvider() {
        return bitmapProvider;
    }
}
