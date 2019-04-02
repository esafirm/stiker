package nolambda.stiker.motionviews.serialized

import nolambda.stiker.motionviews.viewmodel.Layer

class TextEntitySavedState(
    layer: Layer,
    matrixValues: FloatArray,
    holyScale: Float,
    canvasWidth: Int,
    canvasHeight: Int,
    borderPaintData: PaintData
) : BaseEntitySavedState(layer, matrixValues, holyScale, canvasWidth, canvasHeight, borderPaintData)
