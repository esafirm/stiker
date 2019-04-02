package nolambda.stiker

import androidx.annotation.DrawableRes
import nolambda.stiker.motionviews.viewmodel.Font
import nolambda.stiker.motionviews.viewmodel.Layer
import nolambda.stiker.motionviews.viewmodel.TextLayer
import nolambda.stiker.motionviews.widget.MotionView
import nolambda.stiker.motionviews.widget.entity.ImageEntity
import nolambda.stiker.motionviews.widget.entity.TextEntity

class EntityFactory {

    fun updateTextEntity(textEntity: TextEntity, result: TextInputResult): TextEntity {
        val layer = textEntity.layer
        layer.text = result.text
        layer.font = Font().apply {
            color = result.color
            size = TextLayer.Limits.INITIAL_FONT_SIZE
            typeface = result.typeface
        }
        return textEntity
    }

    fun createTextEntity(motionView: MotionView, result: TextInputResult): TextEntity {
        val width = motionView.width
        val height = motionView.height
        val textLayer = createTextLayer(result)
        return TextEntity(textLayer, width, height)
    }

    private fun createTextLayer(result: TextInputResult) =
        TextLayer().apply {
            font = Font().apply {
                text = result.text
                color = result.color
                size = TextLayer.Limits.INITIAL_FONT_SIZE
                typeface = result.typeface
            }
        }

    fun createImageEntity(motionView: MotionView, @DrawableRes res: Int): ImageEntity {
        val width = motionView.width
        val height = motionView.height
        val bitmapProvider = ImageEntity.ResourceBitmapProvider(motionView.context.applicationContext, res)
        return ImageEntity(Layer(), bitmapProvider, width, height)
    }
}
