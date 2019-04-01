package nolambda.stiker.motionviews.serialized;

import java.io.Serializable;

public class PaintData implements Serializable {

    private final float strokeSize;
    private final int color;

    public PaintData(float strokeSize, int color) {
        this.strokeSize = strokeSize;
        this.color = color;
    }

    public float getStrokeSize() {
        return strokeSize;
    }

    public int getColor() {
        return color;
    }
}
