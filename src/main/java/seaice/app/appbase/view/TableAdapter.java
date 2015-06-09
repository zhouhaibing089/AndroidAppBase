package seaice.app.appbase.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seaice.app.appbase.R;
import seaice.app.appbase.utils.AppUtils;

public abstract class TableAdapter extends BaseAdapter {

    /* 上下文环境 */
    protected Context mContext;

    /* 在ListView中的行数量 */
    protected int mCount = 0;

    /* 是否有Header */
    protected int mHasHeader = 0;

    /* 是否有Footer */
    protected int mHasFooter = 0;
    /* 每个Section的position范围 */
    protected Map<Integer, Range> mRangeMap;

    /* 初始化相关数据 */
    public TableAdapter(Context context) {
        mContext = context;
    }

    // 子类需要实现的方法
    public abstract int getRowCount(int section);

    public abstract View getRow(int section, int row, View convertView, ViewGroup parent);

    public abstract boolean hasHeader();

    public abstract View getHeader(ViewGroup parent);

    public abstract boolean hasFooter();

    public abstract View getFooter(ViewGroup parent);

    public abstract int getSectionCount();

    public abstract String getSectionHeader(int section);

    public abstract String getSectionFooter(int section);

    public abstract List<Object> getDataSet();

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        int rowCount = 0;
        for (int i = 0; i < getSectionCount(); ++i) {
            Range range = mRangeMap.get(i);

            if (position == range.start && range.hasHeader == 1) {
                return getSectionHeader(i);
            }
            if (position == (range.end - 1) && range.hasFooter == 1) {
                return getSectionFooter(i);
            }
            if (position >= range.start && position < range.end) {
                /* 锁定范围 */
                return getDataSet().get(rowCount + (position - range.start - range.hasHeader));
            }
            int count = range.end - range.start - range.hasHeader - range.hasFooter;
            rowCount += count;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
            /* Table的Header和Footer都是不可点的 */
        if (hasHeader() && position == 0) {
            return false;
        }
        if (hasFooter() && position == (mCount - 1)) {
            return false;
        }
            /* Section的Header和Footer都是不可点的 */
        for (int section : mRangeMap.keySet()) {
            Range range = mRangeMap.get(section);
                /* 如果是当前Section的Header */
            if (position == range.start && range.hasHeader == 1) {
                return false;
            }
                /* 如果是当前Section的Footer */
            if (position == (range.end - 1) && range.hasFooter == 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* 如果是第一个 */
        if (hasHeader() && position == 0) {
            return getHeader(parent);
        }
        /* 如果是最后一个 */
        if (hasFooter() && position == (mCount - 1)) {
            return getFooter(parent);
        }
        for (int section : mRangeMap.keySet()) {
            Range range = mRangeMap.get(section);
            /* 如果不在当前Section内, 继续进行 */
            if (position < range.start || position >= range.end) {
                continue;
            }
            /* 如果是当前Section的Header */
            if (position == range.start && range.hasHeader == 1) {
                return getSectionHeaderView(section, convertView, parent);
            }
            /* 如果是当前Section的Footer */
            if (position == (range.end - 1) && range.hasFooter == 1) {
                return getSectionFooterView(section, convertView, parent);
            }
            /* 在当前Section行中 */
            int row = position - range.start - range.hasHeader;
            View rowView = getRow(section, row, convertView, parent);
            // 设置背景
            int rowCount = getRowCount(section);
            int firstRowBg = getFirstRowBackgroundResource(section, row);
            int rowBg = getRowBackgroundResource(section, row);
            int lastRowBg = getLastRowBackgroundResource(section, row);
            int singleRowBg = getSingleRowBackgroundResource(section, row);
            if (rowCount == 1) {
                if (singleRowBg != 0) {
                    rowView.setBackgroundResource(singleRowBg);
                }
            } else if ((row + 1) == rowCount) {
                if (lastRowBg != 0) {
                    rowView.setBackgroundResource(lastRowBg);
                }
            } else if (row == 0) {
                if (firstRowBg != 0) {
                    rowView.setBackgroundResource(firstRowBg);
                }
            } else {
                if (rowBg != 0) {
                    rowView.setBackgroundResource(rowBg);
                }
            }
            return rowView;
        }
        return convertView;
    }

    /**
     * 第一行的背景
     *
     * @return 背景资源ID
     */
    protected int getFirstRowBackgroundResource(int section, int row) {
        return R.drawable.tabcell_bg;
    }

    /**
     * 每一行的背景
     *
     * @return 背景资源ID
     */
    protected int getRowBackgroundResource(int section, int row) {
        return R.drawable.tabcell_bg;
    }

    /**
     * 最后一行的背景
     *
     * @return 背景资源ID
     */
    protected int getLastRowBackgroundResource(int section, int row) {
        return R.drawable.tabcell_last_bg;
    }

    /**
     * 只有一行时的背景
     *
     * @return 背景资源ID
     */
    protected int getSingleRowBackgroundResource(int section, int row) {
        return R.drawable.tabcell_last_bg;
    }

    /* 关于设置Section的Header和Footer的View */
    protected View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        String text = getSectionHeader(section);
        if (convertView != null) {
            ((TextView) ((ViewGroup) convertView).getChildAt(0)).setText(text);
            return convertView;
        }
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        TextView textView = new TextView(mContext);
        textView.setText(text);
        container.addView(textView, getSectionTextLayoutParams());
        return container;
    }

    /* 设置Section的Footer的View */
    protected View getSectionFooterView(int section, View convertView, ViewGroup parent) {
        String text = getSectionFooter(section);
        if (convertView != null) {
            ((TextView) ((ViewGroup) convertView).getChildAt(0)).setText(text);
            return convertView;
        }
        LinearLayout container = new LinearLayout(mContext);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        TextView textView = new TextView(mContext);
        textView.setText(text);
        container.addView(textView, getSectionTextLayoutParams());
        return container;
    }

    protected LinearLayout.LayoutParams getSectionTextLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) (AppUtils.getPix(mContext, 16)), 0, 0, 0);
        return params;
    }

    @Override
    public void notifyDataSetChanged() {
        mRangeMap = new HashMap<>();
        /* TableView的Header */
        mHasHeader = hasHeader() ? 1 : 0;
        mCount = mHasHeader;
        for (int i = 0; i < getSectionCount(); ++i) {
            /* 每个Section中的header和footer */
            Range range = new Range();
            range.start = mCount;
            // 如果有Header
            range.hasHeader = getSectionHeader(i) == null ? 0 : 1;
            mCount += range.hasHeader;
            /* Section中行的数量 */
            mCount += getRowCount(i);
            // 如果有Footer
            range.hasFooter = getSectionFooter(i) == null ? 0 : 1;
            mCount += range.hasFooter;
            range.end = mCount;
            mRangeMap.put(i, range);
        }
        /* TableView的Footer */
        mHasFooter = hasFooter() ? 1 : 0;
        mCount += mHasFooter;

        super.notifyDataSetChanged();
    }

    static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = 0;

    static final int ITEM_VIEW_TYPE_SECTION_HEADER = 1;

    static final int ITEM_VIEW_TYPE_SECTION_FOOTER = 2;

    static final int ITEM_VIEW_TYPE_ROW = 3;

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        /* Header和Footer都没必要重用 */
        if (hasHeader() && position == 0) {
            return ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }
        if (hasFooter() && position == (mCount - 1)) {
            return ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }
        for (int section : mRangeMap.keySet()) {
            Range range = mRangeMap.get(section);
            /* 如果不在当前Section内, 继续进行 */
            if (position < range.start || position >= range.end) {
                continue;
            }
            /* 如果是当前Section的Header */
            if (position == range.start && range.hasHeader == 1) {
                return ITEM_VIEW_TYPE_SECTION_HEADER;
            }
            /* 如果是当前Section的Footer */
            if (position == (range.end - 1) && range.hasFooter == 1) {
                return ITEM_VIEW_TYPE_SECTION_FOOTER;
            }
            return ITEM_VIEW_TYPE_ROW;
        }
        return AdapterView.ITEM_VIEW_TYPE_IGNORE;
    }

    /* 前闭后开 */
    protected class Range {
        int start;
        int end;
        int hasHeader;
        int hasFooter;
    }
}
