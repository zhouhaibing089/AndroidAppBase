package seaice.app.appbase.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import seaice.app.appbase.utils.AppUtils;

/**
 * The flexible ImageView which would adjust its size properly.
 *
 * @author zhb
 */
public class FlexibleImageView extends ImageView {

  static final int DEFAULT_SIZE_DP = 160;

  int mSize = 0;

  public FlexibleImageView(Context context) {
    this(context, null);

    init(context);
  }

  public FlexibleImageView(Context context, AttributeSet attrs) {
    super(context, attrs);

    init(context);
  }

  void init(Context context) {
    mSize = (int) AppUtils.getPix(context, DEFAULT_SIZE_DP);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    Drawable drawable = getDrawable();
    if (drawable == null) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } else {
      // we got the Image width
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);
      float reqWidth = drawable.getIntrinsicWidth();
      float reqHeight = drawable.getIntrinsicHeight();
      // more width
      if (reqWidth > reqHeight) {
        float ratio = reqWidth / reqHeight;
        if (ratio > 2.67) {
          setMeasuredDimension(mSize, (int) (mSize / 2.67));
        } else {
          setMeasuredDimension(width, (int) (width / ratio));
        }
      } else {
        float ratio = reqHeight / reqWidth;
        if (ratio > 2.67) {
          setMeasuredDimension((int) (mSize / 2.67), mSize);
        } else {
          setMeasuredDimension((int) (height / ratio), height);
        }
      }
    }
  }
}
