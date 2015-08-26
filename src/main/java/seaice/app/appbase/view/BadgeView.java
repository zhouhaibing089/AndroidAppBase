package seaice.app.appbase.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import seaice.app.appbase.R;

/**
 * A simple badge view which displays a red circle. also the message count.
 *
 * @author zhb
 */
public class BadgeView extends TextView {

    Paint mPaint;

    public BadgeView(Context context) {
        super(context);

        init();
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    void init() {
        mPaint = new TextPaint();
        mPaint.setColor(getResources().getColor(R.color.badge));
        mPaint.setAntiAlias(true);

        // transparency color
        setBackgroundColor(0x0);
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int radius = width > height ? (width / 2) : (height / 2);

        canvas.drawCircle(radius, radius, radius, mPaint);

        super.onDraw(canvas);
    }
}