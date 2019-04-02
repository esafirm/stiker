package nolambda.stiker.motionviews.serialized

import nolambda.stiker.motionviews.viewmodel.Layer

import java.io.Serializable

open class BaseEntitySavedState(
    val layer: Layer,
    val matrixValues: FloatArray,
    val holyScale: Float,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val borderPaintData: PaintData
) : Serializable
