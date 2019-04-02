package nolambda.stiker.motionviews.widget

import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.view.View

import java.util.ArrayList

import nolambda.stiker.motionviews.serialized.BaseEntitySavedState
import nolambda.stiker.motionviews.serialized.ImageEntitySavedState
import nolambda.stiker.motionviews.serialized.PaintData
import nolambda.stiker.motionviews.serialized.TextEntitySavedState
import nolambda.stiker.motionviews.viewmodel.TextLayer
import nolambda.stiker.motionviews.widget.entity.ImageEntity
import nolambda.stiker.motionviews.widget.entity.MotionEntity
import nolambda.stiker.motionviews.widget.entity.TextEntity

class MotionViewSavedState : View.BaseSavedState {

    private val serializedEntities = mutableListOf<BaseEntitySavedState>()

    constructor(parcelable: Parcelable, rawEntities: ArrayList<MotionEntity>) : super(parcelable) {
        rawEntities.forEach {
            val serialized = it.serialize()
            if (serialized != null) {
                serializedEntities.add(serialized)
            }
        }
    }

    protected constructor(parcelIn: Parcel) : super(parcelIn) {
        parcelIn.readList(this.serializedEntities, MotionEntity::class.java.classLoader)
    }

    fun getEntities(): List<MotionEntity> {
        val motionEntities = ArrayList<MotionEntity>()
        for (entity in serializedEntities) {
            if (entity is TextEntitySavedState) {
                motionEntities.add(restoreTextEntity(entity))
            }
            if (entity is ImageEntitySavedState) {
                motionEntities.add(restoreImageEntity(entity))
            }
        }
        return motionEntities
    }

    private fun restoreImageEntity(savedState: ImageEntitySavedState): ImageEntity {
        val imageEntity = ImageEntity(
            savedState.layer,
            savedState.bitmapProvider,
            savedState.canvasWidth,
            savedState.canvasHeight
        )
        imageEntity.borderPaint = createPaint(savedState.borderPaintData)
        return imageEntity
    }

    private fun restoreTextEntity(textSavedState: TextEntitySavedState): TextEntity {
        val textEntity = TextEntity(
            textSavedState.layer as TextLayer,
            textSavedState.canvasWidth,
            textSavedState.canvasHeight
        )
        textEntity.borderPaint = createPaint(textSavedState.borderPaintData)
        return textEntity
    }

    private fun createPaint(paintData: PaintData): Paint {
        val paint = Paint()
        paint.strokeWidth = paintData.strokeSize
        paint.isAntiAlias = true
        paint.color = paintData.color
        return paint
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeList(this.serializedEntities)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MotionViewSavedState> = object : Parcelable.Creator<MotionViewSavedState> {
            override fun createFromParcel(source: Parcel): MotionViewSavedState {
                return MotionViewSavedState(source)
            }

            override fun newArray(size: Int): Array<MotionViewSavedState> {
                return arrayOf()
            }
        }
    }
}
