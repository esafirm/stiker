package nolambda.stiker.motionviews.widget;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nolambda.stiker.motionviews.serialized.BaseEntitySavedState;
import nolambda.stiker.motionviews.serialized.ImageEntitySavedState;
import nolambda.stiker.motionviews.serialized.PaintData;
import nolambda.stiker.motionviews.serialized.TextEntitySavedState;
import nolambda.stiker.motionviews.viewmodel.TextLayer;
import nolambda.stiker.motionviews.widget.entity.ImageEntity;
import nolambda.stiker.motionviews.widget.entity.MotionEntity;
import nolambda.stiker.motionviews.widget.entity.TextEntity;

public class MotionViewSavedState extends View.BaseSavedState {

    private List<BaseEntitySavedState> entities = new ArrayList<>();

    public MotionViewSavedState(Parcelable parcelable, ArrayList<MotionEntity> rawEntities) {
        super(parcelable);
        for (MotionEntity entity : rawEntities) {
            entities.add(entity.serialize());
        }
    }

    protected MotionViewSavedState(Parcel in) {
        super(in);
        this.entities = new ArrayList<>();
        in.readList(this.entities, MotionEntity.class.getClassLoader());
    }

    public List<MotionEntity> getEntities() {
        List<MotionEntity> motionEntities = new ArrayList<>();
        for (BaseEntitySavedState entity : entities) {
            if (entity instanceof TextEntitySavedState) {
                TextEntitySavedState textSavedState = (TextEntitySavedState) entity;
                motionEntities.add(restoreTextEntity(textSavedState));
            }
            if (entity instanceof ImageEntitySavedState) {
                ImageEntitySavedState imageEntitySavedState = (ImageEntitySavedState) entity;
                motionEntities.add(restoreImageEntity(imageEntitySavedState));
            }
        }
        return motionEntities;
    }

    private ImageEntity restoreImageEntity(ImageEntitySavedState savedState) {
        ImageEntity imageEntity = new ImageEntity(
                savedState.getLayer(),
                savedState.getBitmapProvider(),
                savedState.getCanvasWidth(),
                savedState.getCanvasHeight()
        );
        imageEntity.setBorderPaint(createPaint(savedState.getBorderPaintData()));
        return imageEntity;
    }

    private TextEntity restoreTextEntity(TextEntitySavedState textSavedState) {
        TextEntity textEntity = new TextEntity(
                ((TextLayer) textSavedState.getLayer()),
                textSavedState.getCanvasWidth(),
                textSavedState.getCanvasHeight()
        );
        textEntity.setBorderPaint(createPaint(textSavedState.getBorderPaintData()));
        return textEntity;
    }

    private Paint createPaint(PaintData paintData) {
        Paint paint = new Paint();
        paint.setStrokeWidth(paintData.getStrokeSize());
        paint.setAntiAlias(true);
        paint.setColor(paintData.getColor());
        return paint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.entities);
    }

    public static final Creator<MotionViewSavedState> CREATOR = new Creator<MotionViewSavedState>() {
        @Override
        public MotionViewSavedState createFromParcel(Parcel source) {
            return new MotionViewSavedState(source);
        }

        @Override
        public MotionViewSavedState[] newArray(int size) {
            return new MotionViewSavedState[size];
        }
    };
}
