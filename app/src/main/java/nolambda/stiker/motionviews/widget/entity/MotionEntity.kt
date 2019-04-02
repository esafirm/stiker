package nolambda.stiker.motionviews.widget.entity

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF

import androidx.annotation.IntRange
import nolambda.stiker.motionviews.serialized.BaseEntitySavedState
import nolambda.stiker.motionviews.utils.MathUtils
import nolambda.stiker.motionviews.viewmodel.Layer

abstract class MotionEntity(
    /**
     * data
     */
    protected open val layer: Layer,
    /**
     * width of canvas the entity is drawn in
     */
    @param:IntRange(from = 1) @field:IntRange(from = 0)
    protected var canvasWidth: Int,
    /**
     * height of canvas the entity is drawn in
     */
    @param:IntRange(from = 1) @field:IntRange(from = 0)
    protected var canvasHeight: Int
) {

    /**
     * transformation matrix for the entity
     */
    protected val matrix = Matrix()

    /**
     * true - entity is selected and need to draw it's border
     * false - not selected, no need to draw it's border
     */
    private var isSelected: Boolean = false
        set

    /**
     * maximum scale of the initial image, so that
     * the entity still fits within the parent canvas
     */
    protected var holyScale: Float = 0.toFloat()

    /**
     * Destination points of the entity
     * 5 points. Size of array - 10; Starting upper left corner, clockwise
     * last point is the same as first to close the circle
     * NOTE: saved as a field variable in order to avoid creating array in draw()-like methods
     */
    private val destPoints = FloatArray(10) // x0, y0, x1, y1, x2, y2, x3, y3, x0, y0
    /**
     * Initial points of the entity
     *
     * @see .destPoints
     */
    protected val srcPoints = FloatArray(10)  // x0, y0, x1, y1, x2, y2, x3, y3, x0, y0

    var borderPaint = Paint()

    private val pA = PointF()
    private val pB = PointF()
    private val pC = PointF()
    private val pD = PointF()

    abstract val width: Int
    abstract val height: Int

    /**
     * S - scale matrix, R - rotate matrix, T - translate matrix,
     * L - result transformation matrix
     *
     *
     * The correct order of applying transformations is : L = S * R * T
     *
     *
     * See more info: [Game Dev: Transform Matrix multiplication order](http://gamedev.stackexchange.com/questions/29260/transform-matrix-multiplication-order)
     *
     *
     * Preconcat works like M` = M * S, so we apply preScale -> preRotate -> preTranslate
     * the result will be the same: L = S * R * T
     *
     *
     * NOTE: postconcat (postScale, etc.) works the other way : M` = S * M, in order to use it
     * we'd need to reverse the order of applying
     * transformations : post holy scale ->  postTranslate -> postRotate -> postScale
     */
    protected fun updateMatrix() {
        // init matrix to E - identity matrix
        matrix.reset()

        val topLeftX = layer.x * canvasWidth
        val topLeftY = layer.y * canvasHeight

        val centerX = topLeftX + width.toFloat() * holyScale * 0.5f
        val centerY = topLeftY + height.toFloat() * holyScale * 0.5f

        // calculate params
        var rotationInDegree = layer.rotationInDegrees
        var scaleX = layer.scale
        val scaleY = layer.scale
        if (layer.isFlipped) {
            // flip (by X-coordinate) if needed
            rotationInDegree *= -1.0f
            scaleX *= -1.0f
        }

        // applying transformations : L = S * R * T

        // scale
        matrix.preScale(scaleX, scaleY, centerX, centerY)

        // rotate
        matrix.preRotate(rotationInDegree, centerX, centerY)

        // translate
        matrix.preTranslate(topLeftX, topLeftY)

        // applying holy scale - S`, the result will be : L = S * R * T * S`
        matrix.preScale(holyScale, holyScale)
    }

    fun absoluteCenterX(): Float {
        val topLeftX = layer.x * canvasWidth
        return topLeftX + width.toFloat() * holyScale * 0.5f
    }

    fun absoluteCenterY(): Float {
        val topLeftY = layer.y * canvasHeight

        return topLeftY + height.toFloat() * holyScale * 0.5f
    }

    fun absoluteCenter(): PointF {
        val topLeftX = layer.x * canvasWidth
        val topLeftY = layer.y * canvasHeight

        val centerX = topLeftX + width.toFloat() * holyScale * 0.5f
        val centerY = topLeftY + height.toFloat() * holyScale * 0.5f

        return PointF(centerX, centerY)
    }

    fun moveToCanvasCenter() {
        moveCenterTo(PointF(canvasWidth * 0.5f, canvasHeight * 0.5f))
    }

    fun moveCenterTo(moveToCenter: PointF) {
        val currentCenter = absoluteCenter()
        layer.postTranslate(
            1.0f * (moveToCenter.x - currentCenter.x) / canvasWidth,
            1.0f * (moveToCenter.y - currentCenter.y) / canvasHeight
        )
    }

    /**
     * For more info:
     * [StackOverflow: How to check point is in rectangle](http://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle)
     *
     * NOTE: it's easier to apply the same transformation matrix (calculated before) to the original source points, rather than
     * calculate the result points ourselves
     *
     * @param point point
     * @return true if point (x, y) is inside the triangle
     */
    fun pointInLayerRect(point: PointF): Boolean {

        updateMatrix()
        // map rect vertices
        matrix.mapPoints(destPoints, srcPoints)

        pA.x = destPoints[0]
        pA.y = destPoints[1]
        pB.x = destPoints[2]
        pB.y = destPoints[3]
        pC.x = destPoints[4]
        pC.y = destPoints[5]
        pD.x = destPoints[6]
        pD.y = destPoints[7]

        return MathUtils.pointInTriangle(point, pA, pB, pC) || MathUtils.pointInTriangle(point, pA, pD, pC)
    }

    /**
     * http://judepereira.com/blog/calculate-the-real-scale-factor-and-the-angle-of-rotation-from-an-android-matrix/
     *
     * @param canvas       Canvas to draw
     * @param drawingPaint Paint to use during drawing
     */
    fun draw(canvas: Canvas, drawingPaint: Paint?) {
        updateMatrix()

        canvas.save()

        drawContent(canvas, drawingPaint)

        if (isSelected) {
            // get alpha from drawingPaint
            val storedAlpha = borderPaint.alpha
            if (drawingPaint != null) {
                borderPaint.alpha = drawingPaint.alpha
            }
            drawSelectedBg(canvas)
            // restore border alpha
            borderPaint.alpha = storedAlpha
        }

        canvas.restore()
    }

    private fun drawSelectedBg(canvas: Canvas) {
        matrix.mapPoints(destPoints, srcPoints)
        canvas.drawLines(destPoints, 0, 8, borderPaint)
        canvas.drawLines(destPoints, 2, 8, borderPaint)
    }

    protected abstract fun drawContent(canvas: Canvas, drawingPaint: Paint?)

    open fun release() {
        // free resources here
    }

    // This should be override finalize
    protected fun finalize() {
        try {
            release()
        } catch (e: Exception) {
        }
    }

    open fun serialize(): BaseEntitySavedState? = null
}
