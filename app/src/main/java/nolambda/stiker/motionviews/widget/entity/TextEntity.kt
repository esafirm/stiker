package nolambda.stiker.motionviews.widget.entity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.IntRange
import nolambda.stiker.motionviews.serialized.BaseEntitySavedState
import nolambda.stiker.motionviews.serialized.PaintData
import nolambda.stiker.motionviews.serialized.TextEntitySavedState
import nolambda.stiker.motionviews.utils.MotionViewDependencyProvider
import nolambda.stiker.motionviews.viewmodel.Layer
import nolambda.stiker.motionviews.viewmodel.TextLayer
import java.security.SecureRandom

class TextEntity(
    textLayer: TextLayer,
    @IntRange(from = 1) canvasWidth: Int,
    @IntRange(from = 1) canvasHeight: Int
) : MotionEntity(textLayer, canvasWidth, canvasHeight) {

    val id = random.nextInt()

    private val textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var bitmap: Bitmap? = null

    init {
        updateEntity(false)
    }

    override val layer: TextLayer
        get() = super.layer as TextLayer

    private fun updateEntity(moveToPreviousCenter: Boolean) {
        val bitmap = bitmap

        // save previous center
        val oldCenter = absoluteCenter()

        val newBmp = createBitmap(layer, bitmap)

        // recycle previous bitmap (if not reused) as soon as possible
        if (bitmap?.isRecycled?.not() == true && bitmap != newBmp) {
            bitmap.recycle()
        }

        this.bitmap = newBmp

        val width = newBmp.width.toFloat()
        val height = newBmp.height.toFloat()

        val widthAspect = 1.0f * canvasWidth / width

        // for text we always match text width with parent width
        this.holyScale = widthAspect

        // initial position of the entity
        srcPoints[0] = 0f
        srcPoints[1] = 0f
        srcPoints[2] = width
        srcPoints[3] = 0f
        srcPoints[4] = width
        srcPoints[5] = height
        srcPoints[6] = 0f
        srcPoints[7] = height
        srcPoints[8] = 0f
        srcPoints[8] = 0f

        if (moveToPreviousCenter) {
            // move to previous center
            moveCenterTo(oldCenter)
        }
    }

    /**
     * If reuseBmp is not null, and size of the new bitmap matches the size of the reuseBmp,
     * new bitmap won't be created, reuseBmp it will be reused instead
     *
     * @param textLayer text to draw
     * @param reuseBmp  the bitmap that will be reused
     * @return bitmap with the text
     */
    private fun createBitmap(textLayer: TextLayer, reuseBmp: Bitmap?): Bitmap {

        val boundsWidth = canvasWidth
        val fontProvider = MotionViewDependencyProvider.getInstance().fontProvider

        // init params - size, color, typeface
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = textLayer.font.size * canvasWidth
        textPaint.color = textLayer.font.color
        textPaint.typeface = fontProvider.getTypeface(textLayer.font.typeface)

        // drawing text guide : http://ivankocijan.xyz/android-drawing-multiline-text-on-canvas/
        // Static layout which will be drawn on canvas
        val sl = StaticLayout(
            textLayer.text, // - text which will be drawn
            textPaint,
            boundsWidth, // - width of the layout
            Layout.Alignment.ALIGN_CENTER, // - layout alignment
            1f, // 1 - text spacing multiply
            1f, // 1 - text spacing add
            true
        ) // true - include padding

        // calculate height for the entity, min - Limits.MIN_BITMAP_HEIGHT
        val boundsHeight = sl.height

        // create bitmap not smaller than TextLayer.Limits.MIN_BITMAP_HEIGHT
        val bmpHeight = (canvasHeight * Math.max(
            TextLayer.Limits.MIN_BITMAP_HEIGHT,
            1.0f * boundsHeight / canvasHeight
        )).toInt()

        // create bitmap where text will be drawn
        val bmp: Bitmap
        if (reuseBmp != null && reuseBmp.width == boundsWidth
            && reuseBmp.height == bmpHeight
        ) {
            // if previous bitmap exists, and it's width/height is the same - reuse it
            bmp = reuseBmp
            bmp.eraseColor(Color.TRANSPARENT) // erase color when reusing
        } else {
            bmp = Bitmap.createBitmap(boundsWidth, bmpHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bmp)
        canvas.save()

        // move text to center if bitmap is bigger that text
        if (boundsHeight < bmpHeight) {
            //calculate Y coordinate - In this case we want to draw the text in the
            //center of the canvas so we move Y coordinate to center.
            val textYCoordinate = ((bmpHeight - boundsHeight) / 2).toFloat()
            canvas.translate(0f, textYCoordinate)
        }

        //draws static layout on canvas
        sl.draw(canvas)
        canvas.restore()

        return bmp
    }

    override fun drawContent(canvas: Canvas, drawingPaint: Paint?) {
        val bitmap = bitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, matrix, drawingPaint)
        }
    }

    override val width: Int get() = bitmap?.width ?: 0
    override val height: Int get() = bitmap?.height ?: 0

    fun updateEntity() {
        updateEntity(true)
    }

    override fun release() {
        if (bitmap?.isRecycled?.not() == true) {
            bitmap!!.recycle()
        }
    }

    override fun serialize(): BaseEntitySavedState? {
        val matrixVal = FloatArray(9)
        matrix.getValues(matrixVal)
        return TextEntitySavedState(
            layer,
            matrixVal,
            holyScale,
            canvasWidth,
            canvasHeight,
            PaintData(borderPaint.strokeWidth, borderPaint.color)
        )
    }

    companion object {
        private val random = SecureRandom()
    }
}
