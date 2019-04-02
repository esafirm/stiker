package nolambda.stiker.motionviews.widget.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import nolambda.stiker.motionviews.serialized.BaseEntitySavedState
import nolambda.stiker.motionviews.serialized.ImageEntitySavedState
import nolambda.stiker.motionviews.serialized.PaintData
import nolambda.stiker.motionviews.utils.MotionViewUtils
import nolambda.stiker.motionviews.viewmodel.Layer

import java.io.Serializable

class ImageEntity(
    layer: Layer,
    private val bitmapProvider: BitmapProvider,
    @IntRange(from = 1) canvasWidth: Int,
    @IntRange(from = 1) canvasHeight: Int
) : MotionEntity(layer, canvasWidth, canvasHeight) {

    private val bitmap by lazy { bitmapProvider.image }

    init {
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()

        val widthAspect = 1.0f * canvasWidth / width
        val heightAspect = 1.0f * canvasHeight / height
        // fit the smallest size
        holyScale = Math.min(widthAspect, heightAspect)

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
    }

    public override fun drawContent(canvas: Canvas, drawingPaint: Paint?) {
        canvas.drawBitmap(bitmap, matrix, drawingPaint)
    }

    override val width: Int get() = bitmap.width

    override val height: Int get() = bitmap.height

    override fun release() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    override fun serialize(): BaseEntitySavedState? {
        return ImageEntitySavedState(
            layer,
            MotionViewUtils.getMatrixValues(matrix),
            holyScale,
            canvasWidth,
            canvasHeight,
            PaintData(borderPaint.strokeWidth, borderPaint.color),
            bitmapProvider
        )
    }

    interface BitmapProvider : Serializable {
        val image: Bitmap
    }

    class FileBitmapProvider(private val filePath: String) : BitmapProvider {
        override val image: Bitmap
            get() = BitmapFactory.decodeFile(filePath)
    }

    class ResourceBitmapProvider(
        private val context: Context,
        @DrawableRes private val resourceId: Int
    ) : BitmapProvider {
        override val image: Bitmap
            get() = BitmapFactory.decodeResource(context.resources, resourceId)
    }
}
