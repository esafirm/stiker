package nolambda.stiker.motionviews.utils;

public class MotionViewDependencyProvider {

    private static MotionViewDependencyProvider INSTANCE;

    public static MotionViewDependencyProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MotionViewDependencyProvider();
        }
        return INSTANCE;
    }

    private FontProvider fontProvider;

    public void setFontProvider(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
    }

    public FontProvider getFontProvider() {
        return fontProvider;
    }
}
