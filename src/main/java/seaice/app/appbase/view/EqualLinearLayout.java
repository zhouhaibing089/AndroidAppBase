package seaice.app.appbase.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.percent.PercentLayoutHelper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * The subclass of {@link LinearLayout} which let its children have the same width.. I have
 * not found a good way to support this, but this should just be enough for current situation.
 *
 * @author zhb
 */
public class EqualLinearLayout extends LinearLayout {

    /**
     * The support library provides this..
     */
    PercentLayoutHelper mPercentLayoutHelper;

    public EqualLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPercentLayoutHelper = new PercentLayoutHelper(this);
    }

    /**
     * Borrowed from {@link android.support.percent.PercentFrameLayout}
     *
     * @param widthMeasureSpec  width measure
     * @param heightMeasureSpec height measure
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPercentLayoutHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mPercentLayoutHelper.handleMeasuredStateTooSmall()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Borrowed from {@link android.support.percent.PercentFrameLayout}
     *
     * @param changed is changed
     * @param left    left
     * @param top     top
     * @param right   right
     * @param bottom  bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPercentLayoutHelper.restoreOriginalParams();
    }

    /**
     * Override the layout params generator, use ourselves one..
     *
     * @param attrs attribute set
     * @return LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams
            implements PercentLayoutHelper.PercentLayoutParams {

        PercentLayoutHelper.PercentLayoutInfo mPercentLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mPercentLayoutInfo = PercentLayoutHelper.getPercentLayoutInfo(c, attrs);
        }

        public void setPercent(float percent) {
            if (mPercentLayoutInfo == null) {
                mPercentLayoutInfo = new PercentLayoutHelper.PercentLayoutInfo();
            }
            mPercentLayoutInfo.widthPercent = percent;
        }

        @Override
        public PercentLayoutHelper.PercentLayoutInfo getPercentLayoutInfo() {
            return mPercentLayoutInfo;
        }

        @Override
        protected void setBaseAttributes(@NonNull TypedArray a, int widthAttr, int heightAttr) {
            PercentLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }
}