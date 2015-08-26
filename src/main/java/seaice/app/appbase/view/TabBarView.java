package seaice.app.appbase.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import seaice.app.appbase.R;
import seaice.app.appbase.utils.AppUtils;

/**
 * 仿iOS中TabBar样式的View.
 *
 * @author 周海兵
 */
public class TabBarView extends LinearLayout implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final float DEFAULT_TITLE_SIZE = 0;
    private static final int DEFAULT_ICON_SIZE = 28;

    /* 标题的大小 */
    float mTitleSize;
    /* 图标的大小 */
    float mIconSize;
    /* 表示当前显示的是第几个tab, -1表示不可用 */
    private int mTabIndex = -1;
    /* 模型数据 */
    private TabBarAdapter mAdapter;
    /* ViewPager实例 */
    private ViewPager mPager;
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
        mTitleSize = a.getDimension(R.styleable.AppBaseTabBarView_titleSize,
                AppUtils.getPix(getContext(), DEFAULT_TITLE_SIZE));
        mIconSize = a.getDimension(R.styleable.AppBaseTabBarView_iconSize,
                AppUtils.getPix(getContext(), DEFAULT_ICON_SIZE));

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
     * @param pager ViewPager
     */
    public void setViewPager(ViewPager pager) {
        mPager = pager;
        pager.addOnPageChangeListener(this);

        setAdapter((TabBarAdapter) pager.getAdapter());
    }

    /**
     * 如果不是使用ViewPager, 则只需要设置adapter就可以
     *
     * @param adapter 数据适配
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
            icon.setImageDrawable(mAdapter.getIcon(i, 1));
            tab.addView(icon, getIconLayoutParams());
            mIconList.add(icon);
            TextView title = new TextView(getContext());
            title.setText(mAdapter.getTitle(i));
            if (mTitleSize != 0) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
            }
            tab.addView(title, getTitleLayoutParams());
            mTitleList.add(title);
            addView(tab, getTabLayoutParams());
        }

        // 当前tab设置为第一个
        changeTab(0);
    }

    List<OnTabChangeListener> mOnTabChangeListenerList;

    /* 当不是使用ViewPager时, 这里是另外一种替代方式 */
    public void addOnTabChangeListener(OnTabChangeListener listener) {
        if (mOnTabChangeListenerList == null) {
            mOnTabChangeListenerList = new ArrayList<>();
        }
        mOnTabChangeListenerList.add(listener);
    }

    /**
     * 每个tab中icon的布局参数
     *
     * @return 布局参数
     */
    protected LayoutParams getIconLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 每个tab中title的布局参数
     *
     * @return 布局参数
     */
    protected LayoutParams getTitleLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 每个tab在container中的布局参数
     *
     * @return 布局参数
     */
    protected LayoutParams getTabLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f / mAdapter.getCount();
        return params;
    }

    /**
     * tab容器
     *
     * @return 布局参数
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
        if (offset == 0f) {
            changeTab(position);
            return;
        }
        /* 选中色到普通色 */
        mTitleList.get(position).setTextColor(mAdapter.getTitleColor(offset));
        mIconList.get(position).setImageDrawable(mAdapter.getIcon(position, offset));
        /* 普通色到选中色 */
        mTitleList.get(position + 1).setTextColor(mAdapter.getTitleColor(1 - offset));
        mIconList.get(position + 1).setImageDrawable(mAdapter.getIcon(position + 1, 1 - offset));
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
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
            mTitleList.get(mTabIndex).setTextColor(mAdapter.getTitleColor(1));
            mIconList.get(mTabIndex).setImageDrawable(mAdapter.getIcon(mTabIndex, 1));
        }
        if (to >= 0 && to < mAdapter.getCount()) {
            mTitleList.get(to).setTextColor(mAdapter.getTitleColor(0));
            mIconList.get(to).setImageDrawable(mAdapter.getIcon(to, 0));
            if (mOnTabChangeListenerList != null) {
                for (OnTabChangeListener listener : mOnTabChangeListenerList) {
                    listener.onTabChange(mTabIndex, to);
                }
            }
            mTabIndex = to;
        }

    }

    /**
     * 添加红点
     *
     * @param position 第几个tab
     * @param count    红点上显示多少个数字
     */
    public void setBadge(int position, int count) {
        ViewGroup tab = (ViewGroup) getChildAt(position);
        View view = tab.getChildAt(0);
        // 已经显示红点了, 先删除
        if (view instanceof FrameLayout) {
            removeBadge(position);
            setBadge(position, count);
        } else if (view instanceof ImageView) {
            // 将原来的ImageView换成FrameLayout
            tab.removeView(view);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            // 新建一个FrameLayout
            FrameLayout layout = new FrameLayout(getContext());
            layout.setLayoutParams(params);
            tab.addView(layout, 0);

            // 添加原来的IconView
            FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(params.width, params.height);
            viewParams.gravity = Gravity.CENTER;
            layout.addView(view, viewParams);
            // 创建BadgeView
            BadgeView badgeView = new BadgeView(getContext());
            badgeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            if (count > 0) {
                badgeView.setText(count + "");
            }
            if (count > 99) {
                badgeView.setText("...");
            }
            // 有数字的大小为20dp, 无数字的大小为10dp
            int dp = count > 0 ? 18 : 10;
            FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(
                    (int) AppUtils.getPix(getContext(), dp),
                    (int) AppUtils.getPix(getContext(), dp));
            badgeParams.gravity = Gravity.TOP | Gravity.END;
            if (count <= 0) {
                badgeParams.setMargins(0, (int) AppUtils.getPix(getContext(), 2),
                        (int) AppUtils.getPix(getContext(), 6), 0);
            }
            badgeView.setLayoutParams(badgeParams);
            // 添加BadgeView
            layout.addView(badgeView);
        }
    }

    /**
     * 移除红点
     *
     * @param position 哪个位置的红点
     */
    public void removeBadge(int position) {
        ViewGroup tab = (ViewGroup) getChildAt(position);
        View view = tab.getChildAt(0);
        if (view instanceof FrameLayout) {
            FrameLayout layout = (FrameLayout) view;
            layout.removeAllViews();
            tab.removeView(layout);
            mIconList.get(position).setLayoutParams(getIconLayoutParams());
            tab.addView(mIconList.get(position), 0);
        }
    }

    /* tab变化的监听器 */
    public interface OnTabChangeListener {

        void onTabChange(int from, int to);
    }
}
