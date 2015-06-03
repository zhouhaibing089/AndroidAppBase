package seaice.app.appbase.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import seaice.app.appbase.R;
import seaice.app.appbase.adapter.PopupMenuAdapter;
import seaice.app.appbase.utils.AppUtils;

/**
 * 仿iOS中Navigation Bar式样的View
 *
 * @author 周海兵
 */
public class NavBarView extends RelativeLayout {

    private static final int DEFAULT_ITEM_BACKGROUND = R.drawable.navbar_item_bg;
    private static final float DEFAULT_ITEM_TEXT_SIZE = 9f;
    private static final int DEFAULT_LEFT_ICON = -1;
    private static final int DEFAULT_RIGHT_ICON = -1;
    private static final boolean DEFAULT_HAS_BACK_TITLE = true;
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#FFFFFFFF");
    private static final float DEFAULT_TITLE_SIZE = 10f;
    private static final float DEFAULT_ITEM_MARGIN = 8;
    /* 标题View */
    View mCenterItem;
    /* 标题 */
    String mTitle;
    /* BarItem的背景设置 */
    int mItemBackground;
    float mItemTextSize;
    /* 左按钮 */ View mLeftItem;
    int mLeftIcon;
    String mLeftText;
    /* 右按钮 */
    View mRightItem;
    int mRightIcon;
    String mRightText;
    /* 是否有Back Title */
    boolean mHasBackTitle;
    /* 标题的颜色 */
    int mTitleColor = DEFAULT_TITLE_COLOR;
    /* 标题的大小 */
    float mTitleSize = DEFAULT_TITLE_SIZE;
    /* 左右的边距 */
    float mItemMargin = DEFAULT_ITEM_MARGIN;
    /* 全屏的弹窗 */
    PopupWindow mPopupWindow;

    /* 消息窗口 */
    View mMessageView;

    public NavBarView(Context context) {
        super(context);
        init(null, 0);
    }

    public NavBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NavBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NavBarView, defStyle, 0);

        mTitle = a.getString(R.styleable.NavBarView_navTitle);
        mRightIcon = a.getResourceId(R.styleable.NavBarView_navRightIcon, DEFAULT_RIGHT_ICON);
        mLeftIcon = a.getResourceId(R.styleable.NavBarView_navLeftIcon, DEFAULT_LEFT_ICON);
        mRightText = a.getString(R.styleable.NavBarView_navRightText);
        mLeftText = a.getString(R.styleable.NavBarView_navLeftText);
        mTitleColor = a.getColor(R.styleable.NavBarView_navTitleColor, DEFAULT_TITLE_COLOR);
        mTitleSize = a.getDimension(R.styleable.NavBarView_navTitleSize,
                AppUtils.getPix(getContext(), DEFAULT_TITLE_SIZE));
        mItemMargin = a.getDimension(R.styleable.NavBarView_navItemMargin,
                AppUtils.getPix(getContext(), DEFAULT_ITEM_MARGIN));
        mItemBackground = a.getResourceId(R.styleable.NavBarView_navItemBackground,
                DEFAULT_ITEM_BACKGROUND);
        mItemTextSize = a.getDimension(R.styleable.NavBarView_navItemTextSize,
                AppUtils.getPix(getContext(), DEFAULT_ITEM_TEXT_SIZE));
        mHasBackTitle = a.getBoolean(R.styleable.NavBarView_navHasBackTitle, DEFAULT_HAS_BACK_TITLE);

        a.recycle();

        setCenterItem(mTitle);
        if (mRightIcon != DEFAULT_RIGHT_ICON) {
            setRightItem(mRightIcon);
        }
        if (mLeftIcon != DEFAULT_LEFT_ICON) {
            setLeftItem(mLeftIcon);
        }
        if (mRightText != null) {
            setRightItem(mRightText);
        }
        if (mLeftText != null) {
            setLeftItem(mLeftText);
        }

        /* 设置BackTitle */
        if (mHasBackTitle) {
            Context context = getContext();
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                Intent data = activity.getIntent();
                String backTitle = data.getStringExtra("title");
                if (backTitle != null) {
                    setBackTitle(backTitle);
                }
            }
        }
    }

    /* 辅助方法: 生成一个TextView */
    private TextView getTextView(String text, boolean title) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(mTitleColor);
        textView.setTextSize(title ? mTitleSize : mItemTextSize);
        textView.setText(text);

        return textView;
    }

    /* 设置左边按钮, 文字形式的按钮 */
    public void setLeftItem(String text) {
        LinearLayout container = new LinearLayout(getContext());
        container.setGravity(Gravity.CENTER);
        TextView textView = getTextView(text, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins((int) mItemMargin, 0, (int) mItemMargin, 0);
        container.addView(textView, params);
        container.setBackgroundResource(mItemBackground);
        container.setClickable(true);
        setLeftItem(container);
    }

    /* 设置左边按钮, 图片形式的按钮 */
    public void setLeftItem(int imgResId) {
        LinearLayout container = new LinearLayout(getContext());
        ImageView barItem = new ImageView(getContext());
        barItem.setImageResource(imgResId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // 居中显示
        container.setGravity(Gravity.CENTER);
        // 左右Margin
        params.setMargins((int) mItemMargin, 0, (int) mItemMargin, 0);
        container.addView(barItem, params);
        container.setClickable(true);
        container.setBackgroundResource(mItemBackground);
        setLeftItem(container);
    }

    /* 设置前一个页面的标题, 在这里其实就是Android中的Home Button */
    public void setBackTitle(String backTitle) {
        if (!mHasBackTitle) {
            return;
        }
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setClickable(true);
        container.setBackgroundResource(mItemBackground);

        ImageView backIcon = new ImageView(getContext());
        backIcon.setImageResource(R.mipmap.ic_ab_back_holo_dark_am);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // 左右Margin
        params.setMargins((int) mItemMargin, 0, 0, 0);
        container.addView(backIcon, params);

        TextView textView = getTextView(backTitle, false);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // 左右Margin
        textParams.setMargins(0, 0, (int) mItemMargin, 0);
        container.addView(textView, textParams);

        container.setGravity(Gravity.CENTER);
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
        setLeftItem(container);
    }

    /* 直接使用View作为左部按钮 */
    public void setLeftItem(View view) {
        removeLeftItem();
        addView(view, getLeftLayoutParams());
        mLeftItem = view;
    }

    /* 移除左边按钮 */
    public void removeLeftItem() {
        if (mLeftItem != null) {
            removeView(mLeftItem);
        }
    }

    /* 设置左边按钮的监听器 */
    public void setLeftItemOnClickListener(OnClickListener listener) {
        if (mLeftItem != null) {
            mLeftItem.setOnClickListener(listener);
        }
    }

    public void setCenterItem(View view) {
        removeCenterItem();
        addView(view, getCenterLayoutParams());
        mCenterItem = view;
    }

    /* 设置中间部分的标题 */
    public void setCenterItem(String text) {
        if (mCenterItem != null && mCenterItem instanceof TextView) {
            ((TextView) mCenterItem).setText(text);
        } else {
            setCenterItem(getTextView(text, true));
        }
    }

    /* 设置中间部分的View, 根据LayoutId */
    public void setCenterItem(int layoutId) {
        setCenterItem(LayoutInflater.from(getContext()).inflate(layoutId, null));
    }

    /* 设置标题 */
    public void setTitle(String title) {
        setCenterItem(title);
    }

    public void removeCenterItem() {
        if (mCenterItem != null) {
            removeView(mCenterItem);
        }
    }

    /* 设置右边按钮, 文字形式的按钮 */
    public void setRightItem(String text) {
        LinearLayout container = new LinearLayout(getContext());
        container.setGravity(Gravity.CENTER);
        TextView textView = getTextView(text, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins((int) mItemMargin, 0, (int) mItemMargin, 0);
        container.addView(textView, params);
        container.setBackgroundResource(mItemBackground);
        container.setClickable(true);
        setRightItem(container);
    }

    /* 设置右边按钮, 图片形式的按钮 */
    public void setRightItem(int imgResId) {
        LinearLayout container = new LinearLayout(getContext());
        ImageView barItem = new ImageView(getContext());
        barItem.setImageResource(imgResId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // 居中显示
        container.setGravity(Gravity.CENTER);
        // 左右Margin
        params.setMargins((int) mItemMargin, 0, (int) mItemMargin, 0);
        container.addView(barItem, params);
        container.setClickable(true);
        container.setBackgroundResource(mItemBackground);
        setRightItem(container);
    }

    /* 设置右边按钮, 任意View */
    public void setRightItem(View view) {
        removeRightItem();
        addView(view, getRightLayoutParams());
        mRightItem = view;
    }

    /* 设置右边触发的多选项菜单 */
    public void setRightActions(int imgResId, String[] actions, int[] icons, TableView.OnCellClickListener listener) {
        if (imgResId != -1) {
            setRightItem(imgResId);
        }

        if (mPopupWindow == null) {
            initPopupWindow();
            View contentView = mPopupWindow.getContentView();
            final TableView menuList = (TableView) contentView.findViewById(R.id.menuList);
            contentView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent ev) {
                    if (ev.getX() < menuList.getX()) {
                        mPopupWindow.dismiss();
                    }
                    if (ev.getY() > menuList.getY()) {
                        mPopupWindow.dismiss();
                    }
                    return true;
                }
            });
            menuList.setAdapter(new PopupMenuAdapter(getContext(), actions, null));
            if (listener != null) {
                menuList.setOnCellClickListener(listener);
            }
        }

        setRightItemOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.showAsDropDown(v);
                }
            }
        });
    }

    /* 初始一个PopupWindow */
    protected void initPopupWindow() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.popup_menu, null);
        mPopupWindow = new PopupWindow(rootView, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, false);
    }


    /* 移除右边按钮 */
    public void removeRightItem() {
        if (mRightItem != null) {
            removeView(mRightItem);
        }
    }

    /* 设置右部的点击时间处理方法 */
    public void setRightItemOnClickListener(OnClickListener listener) {
        if (mRightItem != null) {
            mRightItem.setOnClickListener(listener);
        }
    }

    /* 左部的布局参数 */
    protected LayoutParams getLeftLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(ALIGN_PARENT_LEFT);
        params.addRule(CENTER_VERTICAL);
        return params;
    }

    /* 中间的布局参数 */
    protected LayoutParams getCenterLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        return params;
    }

    /* 右部的布局参数 */
    protected LayoutParams getRightLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(CENTER_VERTICAL);
        return params;
    }

    /* PopupWindow是否正在显示 */
    public boolean isPopupShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /* 取消PopupWindow的显示 */
    public void dismissPopup() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private void initMessageView() {
        // mMessageView = LayoutInflater.from(getContext()).inflate()
    }

    /* 显示信息消息 */
    public void showInfoMessage(String title, String message) {
        if (mMessageView == null) {
            initMessageView();
        }

    }

    /* 显示错误消息 */
    public void showErrorMessage(String title, String message) {

    }
}
