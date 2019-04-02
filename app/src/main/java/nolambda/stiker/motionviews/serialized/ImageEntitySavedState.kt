package nolambda.stiker.motionviews.serialized

import nolambda.stiker.motionviews.viewmodel.Layer
import nolambda.stiker.motionviews.widget.entity.ImageEntity

class ImageEntitySavedState(
    layer: Layer,
    matrixValues: FloatArray,
    holyScale: Float,
    canvasWidth: Int,
    canvasHeight: Int,
    borderPaintData: PaintData,
    val bitmapProvider: ImageEntity.BitmapProvider
) : BaseEntitySavedState(layer, matrixValues, holyScale, canvasWidth, canvasHeight, borderPaintData)
