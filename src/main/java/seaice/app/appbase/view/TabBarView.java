package seaice.app.appbase.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.util.ArrayList;
import java.util.List;

import seaice.app.appbase.R;
import seaice.app.appbase.utils.AppUtils;
import seaice.app.appbase.utils.ColorUtils;

/**
 * 仿iOS中TabBar样式的View.
 *
 * @author 周海兵
 */
public class TabBarView extends LinearLayout implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#FF6E6E6E");
    private static final int DEFAULT_TITLE_SELECTED_COLOR = Color.parseColor("#FF33BBEE");
    private static final float DEFAULT_TITLE_SIZE = 0;
    private static final int DEFAULT_ICON_SIZE = 28;
    private static final boolean DEFAULT_USE_ANIMATION = true;
    /* 标题颜色 */
    int mTitleColor;
    /* 标题被选中时的颜色 */
    int mTitleSelectedColor;
    /* 标题的大小 */
    float mTitleSize;
    /* 图标的大小 */
    float mIconSize;
    /* 是否使用动画 */
    boolean mUseAnimation;
    /* 表示当前显示的是第几个tab, -1表示不可用 */
    private int mTabIndex = -1;
    /* 模型数据 */
    private TabBarAdapter mAdapter;
    /* ViewPager实例 */
    private ViewPager mPager;
    /* tab变化监听器 */
    private OnTabChangeListener mListener;
    /* 标题TextView的列表 */
    private List<TextView> mTitleList;
    /* 图标ImageView的列表 */
    private List<ImageView> mIconList;

    public TabBarView(Context context) {
        super(context);
        init(null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AppBaseTabBarView);

        /* 获取属性配置 */
        Resources r = getResources();
        mTitleColor = a.getColor(R.styleable.AppBaseTabBarView_titleColor, DEFAULT_TITLE_COLOR);
        mTitleSelectedColor = a.getColor(R.styleable.AppBaseTabBarView_titleSelectedColor,
                DEFAULT_TITLE_SELECTED_COLOR);
        mTitleSize = a.getDimension(R.styleable.AppBaseTabBarView_titleSize,
                AppUtils.getPix(getContext(), DEFAULT_TITLE_SIZE));
        mIconSize = a.getDimension(R.styleable.AppBaseTabBarView_iconSize,
                AppUtils.getPix(getContext(), DEFAULT_ICON_SIZE));
        mUseAnimation = a.getBoolean(R.styleable.AppBaseTabBarView_animate, DEFAULT_USE_ANIMATION);

        a.recycle();

        /* 横向排列 */
        setOrientation(HORIZONTAL);

        /* 其他属性初始化 */
        mTitleList = new ArrayList<>();
        mIconList = new ArrayList<>();
    }

    /**
     * 给ViewPager添加监听以及获取该ViewPager的数据模型
     *
     * @param pager
     */
    public void setViewPager(ViewPager pager) {
        mPager = pager;
        pager.setOnPageChangeListener(this);

        setAdapter((TabBarAdapter) pager.getAdapter());
    }

    /**
     * 如果不是使用ViewPager, 则只需要设置adapter就可以
     *
     * @param adapter
     */
    public void setAdapter(TabBarAdapter adapter) {
        mAdapter = adapter;

        /* 重置初始状态 */
        removeAllViews();
        mTitleList.clear();
        mIconList.clear();
        /* 创建Tab */
        for (int i = 0; i < mAdapter.getCount(); ++i) {
            ViewGroup tab = getTab();
            ImageView icon = new ImageView(getContext());
            icon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            icon.setMinimumWidth((int) mIconSize);
            icon.setMinimumHeight((int) mIconSize);
            icon.setImageDrawable(getInterDrawable(i, 0));
            tab.addView(icon, getIconLayoutParams());
            mIconList.add(icon);
            TextView title = new TextView(getContext());
            title.setText(mAdapter.getTitle(i));
            if (mTitleSize != 0) {
                title.setTextSize(mTitleSize);
            }
            tab.addView(title, getTitleLayoutParams());
            mTitleList.add(title);
            addView(tab, getTabLayoutParams());
        }

        // 当前tab设置为第一个
        changeTab(0);
    }

    /* 当不是使用ViewPager时, 这里是另外一种替代方式 */
    public void setOnTabChangeListener(OnTabChangeListener listener) {
        mListener = listener;
    }

    /**
     * 每个tab中icon的布局参数
     *
     * @return
     */
    protected LayoutParams getIconLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return params;
    }

    /**
     * 每个tab中title的布局参数
     *
     * @return
     */
    protected LayoutParams getTitleLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        return params;
    }

    /**
     * 每个tab在container中的布局参数
     *
     * @return
     */
    protected LayoutParams getTabLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f / mAdapter.getCount();
        return params;
    }

    /**
     * tab容器
     *
     * @return
     */
    protected ViewGroup getTab() {
        LinearLayout tab = new LinearLayout(getContext());
        tab.setOrientation(LinearLayout.VERTICAL);
        tab.setGravity(Gravity.CENTER);
        tab.setClickable(true);
        tab.setOnClickListener(this);
        return tab;
    }

    @Override
    public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
        if (offset == 0f || !mUseAnimation) {
            changeTab(position);
            return;
        }
        /* 选中色到普通色 */
        mTitleList.get(position).setTextColor(getInterColor(1 - offset));
        mIconList.get(position).setImageDrawable(getInterDrawable(position, 1 - offset));
        /* 普通色到选中色 */
        mTitleList.get(position + 1).setTextColor(getInterColor(offset));
        mIconList.get(position + 1).setImageDrawable(getInterDrawable(position + 1, offset));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * <p>
     * 获取正常色和选中色的中间颜色.
     * </p>
     * <ol>
     * <li>当ratio = 0时, 结果为正常色</li>
     * <li>当ratio = 1时, 结果为选中色</li>
     * </ol>
     *
     * @param ratio
     * @return
     */
    protected int getInterColor(float ratio) {
        return ColorUtils.getInterColor(mTitleColor, mTitleSelectedColor, ratio);
    }

    /**
     * <p>
     * 获取白色和选中色的中间颜色.
     * </p>
     * <ol>
     * <li>当ratio = 0时, 结果为白色</li>
     * <li>当ratio = 1时, 结果为选中色</li>
     * </ol>
     *
     * @param ratio 中间比例
     * @return 结果颜色
     */
    protected int getInterIconFillColor(float ratio) {
        return ColorUtils.getInterColor(Color.WHITE, mTitleSelectedColor, ratio);
    }

    /**
     * 获取标题颜色和选中颜色的中间颜色
     *
     * @param ratio 中间比例
     * @return 结果颜色
     */
    protected int getInterIconStrokeColor(float ratio) {
        return ColorUtils.getInterColor(mTitleColor, mTitleSelectedColor, ratio);
    }

    /**
     * <p>
     * 获取正常icon和选中icon的中间icon
     * </p>
     * <ol>
     * <li>当ratio = 0时, 图片为正常icon</li>
     * <li>当ratio = 1时, 图片为选中icon</li>
     * </ol>
     *
     * @param position
     * @param ratio
     * @return
     */
    protected Drawable getInterDrawable(int position, float ratio) {
        int interColor = getInterIconFillColor(ratio);
        int svgResId = mAdapter.getIcon(position);
        String svgResStr = AppUtils.getRawResAsString(getContext(), svgResId);
        // 使用中间色替换原图中的颜色
        SVG svg = SVGParser.getSVGFromResource(getResources(), svgResId, mTitleSelectedColor,
                interColor);
        return svg.createPictureDrawable();
    }

    /* 直接点击Tab跳转的动作 */
    @Override
    public void onClick(View v) {
        int position = indexOfChild(v);
        if (mPager != null) {
            mPager.setCurrentItem(position, false);
        } else {
            /* 手动修改Tab */
            changeTab(position);
        }
    }

    /* 变换Tab */
    protected void changeTab(int to) {
        if (mTabIndex == to) {
            return;
        }
        // 范围检查
        if (mTabIndex >= 0 && mTabIndex < mAdapter.getCount()) {
            mTitleList.get(mTabIndex).setTextColor(mTitleColor);
            mIconList.get(mTabIndex).setImageDrawable(getInterDrawable(mTabIndex, 0));
        }
        if (to >= 0 && to < mAdapter.getCount()) {
            mTitleList.get(to).setTextColor(mTitleSelectedColor);
            mIconList.get(to).setImageDrawable(getInterDrawable(to, 1));
            if (mListener != null) {
                mListener.onTabChange(mTabIndex, to);
            }
            mTabIndex = to;
        }

    }

    /* tab变化的监听器 */
    public interface OnTabChangeListener {

        void onTabChange(int from, int to);
    }
}
