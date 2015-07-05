package seaice.app.appbase.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import seaice.app.appbase.R;

/**
 * 开关组件
 *
 * @author zhb
 */
public class SwitchView extends View {


    private Spring spring;

    private float radius;
    /**
     * 开启颜色
     */
    private int onColor = Color.parseColor("#4ebb7f");
    /**
     * 关闭颜色
     */
    private int offBorderColor = Color.parseColor("#dadbda");
    /**
     * 灰色带颜色
     */
    private int offColor = Color.parseColor("#ffffff");
    /**
     * 手柄颜色
     */
    private int spotColor = Color.parseColor("#ffffff");
    /**
     * 边框颜色
     */
    private int borderColor = offBorderColor;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 开关状态
     */
    private boolean toggleOn = false;
    /**
     * 边框大小
     */
    private int borderWidth = 2;
    /**
     * 垂直中心
     */
    private float centerY;
    private float endX;
    /**
     * 手柄X位置的最小和最大值
     */
    private float spotMinX, spotMaxX;
    /**
     * 手柄大小
     */
    private int spotSize;
    /**
     * 手柄X位置
     */
    private float spotX;
    /**
     * 关闭时内部灰色带高度
     */
    private float offLineWidth;
    /** */
    private RectF rect = new RectF();
    /**
     * 默认使用动画
     */
    private boolean defaultAnimate = true;

    private OnSwitchChanged listener;

    private SwitchView(Context context) {
        super(context);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        spring.removeListener(springListener);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        spring.addListener(springListener);
    }

    public void setup(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);

        SpringSystem springSystem = SpringSystem.create();
        spring = springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 7));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(defaultAnimate);
            }
        });

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SwitchView);
        offBorderColor = typedArray.getColor(R.styleable.SwitchView_switchOffBorderColor, offBorderColor);
        onColor = typedArray.getColor(R.styleable.SwitchView_switchOnColor, onColor);
        spotColor = typedArray.getColor(R.styleable.SwitchView_switchSpotColor, spotColor);
        offColor = typedArray.getColor(R.styleable.SwitchView_switchOffColor, offColor);
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.SwitchView_switchBorderWidth, borderWidth);
        defaultAnimate = typedArray.getBoolean(R.styleable.SwitchView_switchAnimate, defaultAnimate);
        typedArray.recycle();

        borderColor = offBorderColor;
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        toggleOn = !toggleOn;
        takeEffect(animate);

        if (listener != null) {
            listener.onSwitch(toggleOn);
        }
    }

    public void toggleOn() {
        setToggleOn();
        if (listener != null) {
            listener.onSwitch(toggleOn);
        }
    }

    public void toggleOff() {
        setToggleOff();
        if (listener != null) {
            listener.onSwitch(toggleOn);
        }
    }

    public void setToggleOn() {
        setToggleOn(true);
    }

    public void setToggleOn(boolean animate) {
        toggleOn = true;
        takeEffect(animate);
    }

    public void setToggleOff() {
        setToggleOff(true);
    }

    public void setToggleOff(boolean animate) {
        toggleOn = false;
        takeEffect(animate);
    }

    private void takeEffect(boolean animate) {
        if (animate) {
            spring.setEndValue(toggleOn ? 1 : 0);
        } else {
            spring.setCurrentValue(toggleOn ? 1 : 0);
            calculateEffect(toggleOn ? 1 : 0);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Resources r = Resources.getSystem();
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.UNSPECIFIED || heightSize == MeasureSpec.AT_MOST) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        final int width = getWidth();
        final int height = getHeight();

        radius = Math.min(width, height) * 0.5f;
        centerY = radius;

        float startX = radius;
        endX = width - radius;
        spotMinX = startX + borderWidth;
        spotMaxX = endX - borderWidth;
        spotSize = height - 4 * borderWidth;
        spotX = toggleOn ? spotMaxX : spotMinX;
        offLineWidth = 0;
    }


    SimpleSpringListener springListener = new SimpleSpringListener() {
        @Override
        public void onSpringUpdate(Spring spring) {
            final double value = spring.getCurrentValue();
            calculateEffect(value);
        }
    };

    private int clamp(int value, int low, int high) {
        return Math.min(Math.max(value, low), high);
    }


    @Override
    public void draw(Canvas canvas) {
        //
        rect.set(0, 0, getWidth(), getHeight());
        paint.setColor(borderColor);
        canvas.drawRoundRect(rect, radius, radius, paint);

        if (offLineWidth > 0) {
            final float cy = offLineWidth * 0.5f;
            rect.set(spotX - cy, centerY - cy, endX + cy, centerY + cy);
            paint.setColor(offColor);
            canvas.drawRoundRect(rect, cy, cy, paint);
        }

        rect.set(spotX - 1 - radius, centerY - radius, spotX + 1.1f + radius, centerY + radius);
        paint.setColor(borderColor);
        canvas.drawRoundRect(rect, radius, radius, paint);

        final float spotR = spotSize * 0.5f;
        rect.set(spotX - spotR, centerY - spotR, spotX + spotR, centerY + spotR);
        paint.setColor(spotColor);
        canvas.drawRoundRect(rect, spotR, spotR, paint);

    }

    private void calculateEffect(final double value) {
        spotX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, spotMinX, spotMaxX);

        offLineWidth = (float) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, 10, spotSize);

        final int fb = Color.blue(onColor);
        final int fr = Color.red(onColor);
        final int fg = Color.green(onColor);

        final int tb = Color.blue(offBorderColor);
        final int tr = Color.red(offBorderColor);
        final int tg = Color.green(offBorderColor);

        int sb = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fb, tb);
        int sr = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fr, tr);
        int sg = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fg, tg);

        sb = clamp(sb, 0, 255);
        sr = clamp(sr, 0, 255);
        sg = clamp(sg, 0, 255);

        borderColor = Color.rgb(sr, sg, sb);

        postInvalidate();
    }

    public interface OnSwitchChanged {

        void onSwitch(boolean on);
    }


    public void setOnSwitchChanged(OnSwitchChanged onToggleChanged) {
        listener = onToggleChanged;
    }

    public boolean isAnimate() {
        return defaultAnimate;
    }

    public void setAnimate(boolean animate) {
        this.defaultAnimate = animate;
    }

}
