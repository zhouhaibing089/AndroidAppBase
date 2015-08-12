package seaice.app.appbase.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class OverScrollListView extends ListView implements OnScrollListener, View.OnTouchListener, android.widget.AdapterView.OnItemSelectedListener {

    protected static float BREAK_SPEED = 4f, ELASTICITY = 0.67f;

    public int nHeaders = 1, nFooters = 1, divHeight = 0, delay = 10;
    private int firstVis, visibleCnt, lastVis, totalItems, scrollState;
    private boolean bounce = true, rebound = false, recalculateVelocity = false, trackballEvent = false;
    private long flingTimestamp;
    private float velocity;
    private View measure;
    private GestureDetector gesture;
    private Handler mHandler = new Handler();

    public OverScrollListView(Context context) {
        super(context);
        initialize(context);
    }

    public OverScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public OverScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVis = firstVisibleItem;
        visibleCnt = visibleItemCount;
        totalItems = totalItemCount;
        lastVis = firstVisibleItem + visibleItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (scrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            rebound = true;
            mHandler.postDelayed(checkListViewTopAndBottom, delay);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> av, View v, int position, long id) {
        rebound = true;
        mHandler.postDelayed(checkListViewTopAndBottom, delay);
    }

    @Override
    public void onNothingSelected(AdapterView<?> av) {
        rebound = true;
        mHandler.postDelayed(checkListViewTopAndBottom, delay);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        trackballEvent = true;
        rebound = true;
        mHandler.postDelayed(checkListViewTopAndBottom, delay);
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gesture.onTouchEvent(event);
        return false;
    }

    private class gestureListener implements OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            rebound = false;
            recalculateVelocity = false;
            velocity = 0f;
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            rebound = true;
            recalculateVelocity = true;
            velocity = velocityY / 25f;
            flingTimestamp = System.currentTimeMillis();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            rebound = true;
            recalculateVelocity = false;
            velocity = 0f;
            return false;
        }
    }

    private void initialize(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final View v = new View(context);
        v.setMinimumHeight(Math.max(display.getWidth(), display.getHeight()));
        addHeaderView(v, null, false);
        addFooterView(v, null, false);

        gesture = new GestureDetector(new gestureListener());
        gesture.setIsLongpressEnabled(false);
        flingTimestamp = System.currentTimeMillis();
        setHeaderDividersEnabled(false);
        setFooterDividersEnabled(false);
        setOnTouchListener(this);
        setOnScrollListener(this);
        setOnItemSelectedListener(this);
    }

    /**
     * This should be called after you finish populating the listview !
     * This includes any calls to Adapter#notifyDataSetChanged()
     * and obviously every time you re-populate the listview.
     */
    public void initializeValues() {
        nHeaders = getHeaderViewsCount();
        nFooters = getFooterViewsCount();
        divHeight = getDividerHeight();
        firstVis = 0;
        visibleCnt = 0;
        lastVis = 0;
        totalItems = 0;
        scrollState = 0;
        rebound = true;
        setSelectionFromTop(nHeaders, divHeight);
        smoothScrollBy(0, 0);
        mHandler.postDelayed(checkListViewTopAndBottom, delay);
    }

    /**
     * Turns the bouncing animation on or off.
     *
     * @param bouncing {@code true } for bouncing effect (this is also the default), {@code false} to turn it off.
     */
    public void setBounce(boolean bouncing) {
        bounce = bouncing;
    }

    /**
     * Sets how fast the animation will be. Higher value means faster animation. Must be >= 1.05. Together with Elasticity <= 0.75 it will not bounce forever.
     *
     * @param breakSpeed Default is 4.0
     */
    public void setBreakSpeed(final float breakSpeed) {
        if (Math.abs(breakSpeed) >= 1.05f) {
            BREAK_SPEED = Math.abs(breakSpeed);
        }
    }

    /**
     * Sets how much it will keep bouncing. Lower value means less bouncing. Must be <= 0.75. Together with Breakspeed >= 1.05 it will not bounce forever.
     *
     * @param elasticity Default is 0.67
     */
    public void setElasticity(final float elasticity) {
        if (Math.abs(elasticity) <= 0.75f) {
            ELASTICITY = Math.abs(elasticity);
        }
    }

    public Runnable checkListViewTopAndBottom = new Runnable() {
        @Override
        public void run() {

            mHandler.removeCallbacks(checkListViewTopAndBottom);

            if (trackballEvent && firstVis < nHeaders && lastVis >= totalItems) {
                trackballEvent = false;
                rebound = false;
                return;
            }

            if (rebound) {

                if (firstVis < nHeaders) {

                    // hack to avoid strange behaviour when there aren't enough items to fill the entire listview
                    if (lastVis >= totalItems) {
                        smoothScrollBy(0, 0);
                        rebound = false;
                        recalculateVelocity = false;
                        velocity = 0f;
                    }

                    if (recalculateVelocity) {
                        recalculateVelocity = false;
                        velocity /= (1f + ((System.currentTimeMillis() - flingTimestamp) / 1000f));
                    }
                    if (firstVis == nHeaders) {
                        recalculateVelocity = false;
                    }
                    if (visibleCnt > nHeaders) {
                        measure = getChildAt(nHeaders);
                        if (measure.getTop() + velocity < divHeight) {
                            velocity *= -ELASTICITY;
                            if (!bounce || Math.abs(velocity) < BREAK_SPEED) {
                                rebound = false;
                                recalculateVelocity = false;
                                velocity = 0f;
                            } else {
                                setSelectionFromTop(nHeaders, divHeight + 1);
                            }
                        }
                    } else {
                        if (velocity > 0f) velocity = -velocity;
                    }
                    if (rebound) {
                        smoothScrollBy((int) -velocity, 0);
                        if (velocity > BREAK_SPEED) {
                            velocity *= ELASTICITY;
                            if (velocity < BREAK_SPEED) {
                                rebound = false;
                                recalculateVelocity = false;
                                velocity = 0f;
                            }
                        } else velocity -= BREAK_SPEED;
                    }

                } else if (lastVis >= totalItems) {

                    if (recalculateVelocity) {
                        recalculateVelocity = false;
                        velocity /= (1f + ((System.currentTimeMillis() - flingTimestamp) / 1000f));
                    }
                    if (lastVis == totalItems - nHeaders - nFooters) {
                        rebound = false;
                        recalculateVelocity = false;
                        velocity = 0f;
                    } else {
                        if (visibleCnt > (nHeaders + nFooters)) {
                            measure = getChildAt(visibleCnt - nHeaders - nFooters);
                            if (measure.getBottom() + velocity > getHeight() - divHeight) {
                                velocity *= -ELASTICITY;
                                if (!bounce || Math.abs(velocity) < BREAK_SPEED) {
                                    rebound = false;
                                    recalculateVelocity = false;
                                    velocity = 0f;
                                } else {
                                    setSelectionFromTop(lastVis - nHeaders - nFooters, getHeight() - divHeight - measure.getHeight() - 1);
                                }
                            }
                        } else {
                            if (velocity < 0f) velocity = -velocity;
                        }
                    }
                    if (rebound) {
                        smoothScrollBy((int) -velocity, 0);
                        if (velocity < -BREAK_SPEED) {
                            velocity *= ELASTICITY;
                            if (velocity > -BREAK_SPEED / ELASTICITY) {
                                rebound = false;
                                recalculateVelocity = false;
                                velocity = 0f;
                            }
                        } else velocity += BREAK_SPEED;
                    }

                } else if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

                    rebound = false;
                    recalculateVelocity = false;
                    velocity = 0f;
                }
                mHandler.postDelayed(checkListViewTopAndBottom, delay);
                return;
            }

            if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) return;

            if (totalItems == (nHeaders + nFooters) || firstVis < nHeaders) {
                setSelectionFromTop(nHeaders, divHeight);
                smoothScrollBy(0, 0);
            } else if (lastVis == totalItems) {
                int offset = getHeight() - divHeight;
                measure = getChildAt(visibleCnt - nHeaders - nFooters);
                if (measure != null) offset -= measure.getHeight();
                setSelectionFromTop(lastVis - nHeaders - nFooters, offset);
                smoothScrollBy(0, 0);
            }
        }
    };
}