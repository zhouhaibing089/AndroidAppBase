package seaice.app.appbase.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import seaice.app.appbase.R;

/**
 * The ListView which supports Bounces Effect.
 *
 * @author zhb
 */
public class BounceListView extends ListView implements AbsListView.OnScrollListener {

    /**
     * the header view
     */
    View mHeaderView;
    /**
     * the footer view
     */
    View mFooterView;
    /**
     * current scroll state
     */
    int mCurrentScrollState;
    /**
     * the y coordinate of last motion event
     */
    int mLastY;

    public BounceListView(Context context) {
        super(context);

        init(context);
    }

    public BounceListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public BounceListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        // create the HeaderView
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.item_bounce_header, this, false);
        // and add it into the header
        addHeaderView(mHeaderView);

        // create the FooterView
        mFooterView = LayoutInflater.from(context).inflate(R.layout.item_bounce_footer, this, false);
        // and add it into the footer
        addFooterView(mFooterView);

        super.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // the new y position
        int y = (int) event.getY();
        switch (event.getAction()) {
            // the gesture is released
            case MotionEvent.ACTION_UP:
                // the first item is visible and the header view is not entire visible
                if (getFirstVisiblePosition() == 0 &&
                        (mHeaderView.getBottom() < 0 || mHeaderView.getTop() <= 0)) {
                    resetHeaderPadding();
                }
                // the last item is visible and the footer view is not entire visible
                ListAdapter adapter = getAdapter();
                if ((adapter != null && getLastVisiblePosition() == (adapter.getCount() - 1)) &&
                        (mFooterView.getTop() < getBottom() || mFooterView.getBottom() <= getBottom())) {
                    resetFooterPadding();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                // record the initial y position
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                changeHeaderPadding(event);
                changeFooterPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    protected void changeHeaderPadding(MotionEvent ev) {
        int histSize = ev.getHistorySize();

        for (int i = 0; i < histSize; i++) {
            int histY = (int) ev.getHistoricalY(i);
            int topPadding = ((histY - mLastY)) / 2;
            mHeaderView.setPadding(
                    mHeaderView.getPaddingLeft(),
                    topPadding,
                    mHeaderView.getPaddingRight(),
                    mHeaderView.getPaddingBottom());
        }
    }

    protected void changeFooterPadding(MotionEvent ev) {
        int histSize = ev.getHistorySize();

        for (int i = 0; i < histSize; ++i) {
            int histY = (int) ev.getHistoricalY(i);
            int bottomPadding = (mLastY - histY) / 2;
            mFooterView.setPadding(mFooterView.getPaddingLeft(),
                    mFooterView.getPaddingTop(),
                    mFooterView.getPaddingRight(),
                    bottomPadding);
        }
    }

    protected void resetHeaderPadding() {
        ValueAnimator animator = ValueAnimator.ofInt(mHeaderView.getPaddingTop(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mHeaderView.setPadding(
                        mHeaderView.getPaddingLeft(),
                        (Integer) valueAnimator.getAnimatedValue(),
                        mHeaderView.getPaddingRight(),
                        mHeaderView.getPaddingBottom());
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    protected void resetFooterPadding() {
        ValueAnimator animator = ValueAnimator.ofInt(mFooterView.getPaddingBottom(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFooterView.setPadding(
                        mFooterView.getPaddingLeft(),
                        mFooterView.getPaddingTop(),
                        mFooterView.getPaddingRight(),
                        (Integer) valueAnimator.getAnimatedValue());
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
            // when the first item is visible
            if (firstVisibleItem == 0) {
                mHeaderView.setVisibility(View.VISIBLE);
            } else {
                mHeaderView.setVisibility(View.GONE);
                resetHeaderPadding();
            }
            // when the last item is visible
            if (getLastVisiblePosition() == (totalItemCount - 1)) {
                mFooterView.setVisibility(View.VISIBLE);
            } else {
                mFooterView.setVisibility(View.GONE);
                resetFooterPadding();
            }
        }
    }
}