package seaice.app.appbase.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import seaice.app.appbase.R;

/**
 * The ListView which supports bounces effect..
 *
 * @author zhb
 */
public class BounceTableView extends TableView implements GestureDetector.OnGestureListener {

    View mHeaderView;

    int mHeaderPaddingTop;

    View mFooterView;

    int mFooterPaddingBottom;

    int mPaddingTop;

    int mLastY;

    boolean mBounceTop = true;

    boolean mBounceBottom = true;

    float mResistance = 0.6f;

    Spring mSpring;

    GestureDetector mGestureDetector;

    public BounceTableView(Context context) {
        super(context);

        init(context, null);
    }

    public BounceTableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.YBounceTableView);
//        mBounceTop = a.getBoolean(R.styleable.YBounceTableView_y_btv_bounce_top, true);
//        mBounceBottom = a.getBoolean(R.styleable.YBounceTableView_y_btv_bounce_bottom, true);
//        mResistance = a.getFloat(R.styleable.YBounceTableView_y_btv_resistance, 0.5f);
//        a.recycle();

        // create the HeaderView
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.item_bounce_header, this, false);
        // and add it into the header
        addHeaderView(mHeaderView);

        // create the FooterView
        mFooterView = LayoutInflater.from(context).inflate(R.layout.item_bounce_footer, this, false);
        // and add it into the footer
        addFooterView(mFooterView);

        // the animation system
        SpringSystem springSystem = SpringSystem.create();
        mSpring = springSystem.createSpring();
        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = 1 - (float) spring.getCurrentValue();
                mHeaderView.setPadding(0, (int) (mHeaderPaddingTop * value), 0, 0);
                mFooterView.setPadding(0, 0, 0, (int) (mFooterPaddingBottom * value));
                setPadding(0, (int) (mPaddingTop * value), 0, 0);
            }
        });

        // initialize the gesture detector..
        mGestureDetector = new GestureDetector(context, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mSpring.removeAllListeners();
        mSpring = null;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    protected void resetPadding() {
        mPaddingTop = getPaddingTop();
        mHeaderPaddingTop = mHeaderView.getPaddingTop();
        mFooterPaddingBottom = mFooterView.getPaddingBottom();
        mSpring.setCurrentValue(0, true);
        mSpring.setEndValue(1);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
