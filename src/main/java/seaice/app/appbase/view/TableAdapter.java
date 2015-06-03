package seaice.app.appbase.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

    public abstract int getRowCount(int section);

    public abstract View getRow(int section, int row);

    public View getHeader() {
        return null;
    }

    public View getFooter() {
        return null;
    }

    public abstract int getSectionCount();

    public String getSectionHeader(int section) {
        if (getSectionCount() >= 2) {
            return "";
        }
        return null;
    }

    public String getSectionFooter(int section) {
        return null;
    }

    public List<Object> getDataSet() {
        return null;
    }

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
        if (mHasHeader == 1 && position == 0) {
            return false;
        }
        if (mHasFooter == 1 && position == (mCount - 1)) {
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
        if (position == 0 && mHasHeader == 1) {
            return getHeader();
        }
            /* 如果是最后一个 */
        if (mHasFooter == 1 && position == (mCount - 1)) {
            return getFooter();
        }
        for (int section : mRangeMap.keySet()) {
            Range range = mRangeMap.get(section);
                /* 如果不在当前Section内, 继续进行 */
            if (position < range.start || position >= range.end) {
                continue;
            }
                /* 如果是当前Section的Header */
            if (position == range.start && range.hasHeader == 1) {
                return getSectionTextView(getSectionHeader(section));
            }
                /* 如果是当前Section的Footer */
            if (position == (range.end - 1) && range.hasFooter == 1) {
                return getSectionTextView(getSectionFooter(section));
            }
                /* 在当前Section行中 */
            int row = position - range.start - range.hasHeader;
            View rowView = getRow(section, row);
            if (rowView.getBackground() == null) {
                // 最后一行
                if ((row + 1) == getRowCount(section)) {
                    rowView.setBackgroundResource(R.drawable.tabcell_last_bg);
                } else {
                    rowView.setBackgroundResource(R.drawable.tabcell_bg);
                }
            }
            return rowView;
        }
        return convertView;
    }

    /* 关于设置Section的Header和Footer的View */
    protected View getSectionTextView(String text) {
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
        mHasHeader = getHeader() == null ? 0 : 1;
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
        mHasFooter = getFooter() == null ? 0 : 1;
        mCount += mHasFooter;

        super.notifyDataSetChanged();
    }

    /* 前闭后开 */
    protected class Range {
        int start;
        int end;
        int hasHeader;
        int hasFooter;
    }

}
