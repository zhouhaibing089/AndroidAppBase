package seaice.app.appbase.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * The Image View which support the fixed ratio..
 *
 * @author zhb
 */
public class FixRatioImageView extends ImageView {

    /**
     * the ratio that we want to make
     */
    float mRatio = 0.618f;

    public FixRatioImageView(Context context) {
        super(context);
    }

    public FixRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mRatio);
            setMeasuredDimension(width, height);
        }
    }
}
