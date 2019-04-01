package nolambda.stiker.motionviews.widget.entity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nolambda.stiker.motionviews.serialized.BaseEntitySavedState;
import nolambda.stiker.motionviews.serialized.ImageEntitySavedState;
import nolambda.stiker.motionviews.serialized.PaintData;
import nolambda.stiker.motionviews.utils.MotionViewUtils;
import nolambda.stiker.motionviews.viewmodel.Layer;

public class ImageEntity extends MotionEntity {

    @NonNull
    private final Bitmap bitmap;

    @Nullable
    private BitmapProvider bitmapProvider;

    public ImageEntity(@NonNull Layer layer,
                       @NonNull BitmapProvider provider,
                       @IntRange(from = 1) int canvasWidth,
                       @IntRange(from = 1) int canvasHeight) {
        this(layer, provider.getImage(), canvasWidth, canvasHeight);
        this.bitmapProvider = provider;
    }

    public ImageEntity(@NonNull Layer layer,
                       @NonNull Bitmap bitmap,
                       @IntRange(from = 1) int canvasWidth,
                       @IntRange(from = 1) int canvasHeight) {
        super(layer, canvasWidth, canvasHeight);

        this.bitmap = bitmap;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        float widthAspect = 1.0F * canvasWidth / width;
        float heightAspect = 1.0F * canvasHeight / height;
        // fit the smallest size
        holyScale = Math.min(widthAspect, heightAspect);

        // initial position of the entity
        srcPoints[0] = 0;
        srcPoints[1] = 0;
        srcPoints[2] = width;
        srcPoints[3] = 0;
        srcPoints[4] = width;
        srcPoints[5] = height;
        srcPoints[6] = 0;
        srcPoints[7] = height;
        srcPoints[8] = 0;
        srcPoints[8] = 0;
    }

    @Override
    public void drawContent(@NonNull Canvas canvas, @Nullable Paint drawingPaint) {
        canvas.drawBitmap(bitmap, matrix, drawingPaint);
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public void release() {
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public BaseEntitySavedState serialize() {
        if (bitmapProvider == null) {
            throw new IllegalStateException("BitmapProvider must be used to serialize ImageEntity");
        }
        return new ImageEntitySavedState(
                getLayer(),
                MotionViewUtils.getMatrixValues(matrix),
                holyScale,
                canvasWidth,
                canvasHeight,
                new PaintData(getBorderPaint().getStrokeWidth(), getBorderPaint().getColor()),
                bitmapProvider
        );
    }

    public interface BitmapProvider extends Serializable {
        Bitmap getImage();
    }

    public static class FileBitmapProvider implements BitmapProvider {

        private final String filePath;

        public FileBitmapProvider(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public Bitmap getImage() {
            return BitmapFactory.decodeFile(filePath);
        }
    }

    public static class ResourceBitmapProvider implements BitmapProvider {
        private final int resourceId;
        private final Context context;

        public ResourceBitmapProvider(Context context, int resourceId) {
            this.resourceId = resourceId;
            this.context = context;
        }

        @Override
        public Bitmap getImage() {
            return BitmapFactory.decodeResource(context.getResources(), resourceId);
        }
    }
}
