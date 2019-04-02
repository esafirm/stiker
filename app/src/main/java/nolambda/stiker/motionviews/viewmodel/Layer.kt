package nolambda.stiker.motionviews.viewmodel

import java.io.Serializable

open class Layer : Serializable {

    /**
     * rotation relative to the layer center, in degrees
     */
    var rotationInDegrees: Float = 0.toFloat()
        set(value) {
            assert(value in 0F..360F)
            field = value
        }

    var scale: Float = 0.toFloat()
    /**
     * top left X coordinate, relative to parent canvas
     */
    var x: Float = 0.toFloat()
    /**
     * top left Y coordinate, relative to parent canvas
     */
    var y: Float = 0.toFloat()
    /**
     * is layer flipped horizontally (by X-coordinate)
     */
    var isFlipped: Boolean = false

    protected open val maxScale: Float
        get() = Limits.MAX_SCALE

    protected open val minScale: Float
        get() = Limits.MIN_SCALE

    init {
        reset()
    }

    protected open fun reset() {
        this.rotationInDegrees = 0.0f
        this.scale = 1.0f
        this.isFlipped = false
        this.x = 0.0f
        this.y = 0.0f
    }

    fun postScale(scaleDiff: Float) {
        val newVal = scale + scaleDiff
        if (newVal in minScale..maxScale) {
            scale = newVal
        }
    }

    fun postRotate(rotationInDegreesDiff: Float) {
        this.rotationInDegrees += rotationInDegreesDiff
        this.rotationInDegrees %= 360.0f
    }

    fun postTranslate(dx: Float, dy: Float) {
        this.x += dx
        this.y += dy
    }

    fun flip() {
        this.isFlipped = !isFlipped
    }

    open fun initialScale(): Float {
        return Limits.INITIAL_ENTITY_SCALE
    }

    internal interface Limits {
        companion object {
            const val MIN_SCALE = 0.06f
            const val MAX_SCALE = 4.0f
            const val INITIAL_ENTITY_SCALE = 0.4f
        }
    }
}
