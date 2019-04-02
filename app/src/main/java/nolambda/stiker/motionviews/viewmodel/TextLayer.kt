package nolambda.stiker.motionviews.viewmodel

class TextLayer : Layer() {

    var text: String = ""
    var font: Font = Font()

    override fun reset() {
        super.reset()
        this.text = ""
        this.font = Font()
    }

    override val maxScale: Float
        get() = Limits.MAX_SCALE

    override val minScale: Float
        get() = Limits.MIN_SCALE

    override fun initialScale(): Float {
        return Limits.INITIAL_SCALE
    }

    interface Limits {
        /**
         * limit text size to view bounds
         * so that users don't put small font size and scale it 100+ times
         */

        companion object {
            const val MAX_SCALE = 2.0F;
            const val MIN_SCALE = 0.2F;

            const val MIN_BITMAP_HEIGHT = 0.13F;

            const val FONT_SIZE_STEP = 0.008F;

            const val INITIAL_FONT_SIZE = 0.2F;
            const val INITIAL_FONT_COLOR = 0xff000000;

            const val INITIAL_SCALE = 0.8F; // set the same to avoid text scaling
        }
    }

}
