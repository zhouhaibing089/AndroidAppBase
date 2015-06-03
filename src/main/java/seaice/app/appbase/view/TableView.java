package seaice.app.appbase.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import seaice.app.appbase.R;

/**
 * 仿iOS中TableView式样的View
 *
 * @author 周海兵
 */
public class TableView extends ListView implements AdapterView.OnItemClickListener {

    /* 监听器 */
    OnCellClickListener mListener;

    public TableView(Context context) {
        super(context);
        init(null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TableView);

        /* 获取属性配置 */

        a.recycle();

        /* 取消Divider */
        setDivider(null);
        /* 取消背景 */
        setBackgroundColor(Color.parseColor("#00000000"));
        setSelector(new ColorDrawable(0x0));

        /* 点击的监听器 */
        setOnItemClickListener(this);
    }

    public void setOnCellClickListener(OnCellClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener == null) {
            return;
        }
        TableAdapter adapter = (TableAdapter) getAdapter();
        for (int section = 0; section < adapter.getSectionCount(); ++section) {
            TableAdapter.Range range = adapter.mRangeMap.get(section);
            if (position >= range.start && position < range.end) {
                mListener.onCellClick(parent, view, section, position - range.start - range.hasHeader, id);
                return;
            }
        }
    }

    /**
     * 在OnItemClickListener的代理, 可以方便地监听事件来自哪一个Section以及哪一行
     */
    public interface OnCellClickListener {

        void onCellClick(AdapterView<?> parent, View view, int section, int row, long id);
    }
}
