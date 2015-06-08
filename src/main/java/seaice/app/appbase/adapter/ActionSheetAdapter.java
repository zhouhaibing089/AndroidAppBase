package seaice.app.appbase.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import seaice.app.appbase.R;
import seaice.app.appbase.utils.AppUtils;
import seaice.app.appbase.view.TableAdapter;

public class ActionSheetAdapter extends TableAdapter {

    String mTitle;

    String mMessage;

    String[] mActions;

    int[] mIcons;

    boolean mCancel;

    public ActionSheetAdapter(Context context, String title, String message,
                              String[] actions, int[] icons, boolean cancel) {
        super(context);

        mTitle = title;
        mMessage = message;
        mActions = actions;
        mIcons = icons;
        mCancel = cancel;

        notifyDataSetChanged();
    }

    @Override
    public int getRowCount(int section) {
                /* Action列表 */
        return section == 0 ? mActions.length * 2 - 1 : 1;
    }

    @Override
    public View getRow(int section, int row, View convertView) {
        if (section == 1) {
            return getCancelView();
        }
        // 分隔符
        if (row % 2 == 1) {
            return getDividerView();
        }
        /* ActionSheet的每一行Action */
        View rowView = getActionView(mActions[row / 2],
                mIcons == null ? -1 : mIcons[row / 2]);
        int rowCount = getRowCount(section);
        if (rowCount == 1) {
            rowView.setBackgroundResource(mTitle == null ?
                    R.drawable.action_sheet_single_bg
                    : R.drawable.action_sheet_last_bg);
        } else if (row == 0) {
            rowView.setBackgroundResource(mTitle == null ?
                    R.drawable.action_sheet_first_bg
                    : R.drawable.action_sheet_bg);
        } else if (row == (rowCount - 1)) {
            rowView.setBackgroundResource(R.drawable.action_sheet_last_bg);
        } else {
            rowView.setBackgroundResource(R.drawable.action_sheet_bg);
        }
        return rowView;
    }

    @Override
    public int getSectionCount() {
        return mCancel ? 2 : 1;
    }

    @Override
    public String getSectionHeader(int section) {
        if (section == 0) {
            return null;
        }
        return super.getSectionHeader(section);
    }

    @Override
    protected View getSectionHeaderView(int section, View convertView) {
        LinearLayout container = new LinearLayout(mContext);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) AppUtils.getPix(mContext, 8)));
        return container;
    }

    @Override
    public View getHeader() {
        if (mTitle == null) {
            return null;
        }

        LinearLayout header = new LinearLayout(mContext);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout container = new LinearLayout(mContext);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) AppUtils.getPix(mContext, 48)));
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        TextView titleView = new TextView(mContext);
        titleView.setText(mTitle);
        titleView.setTextColor(mContext.getResources().getColor(R.color.actionSheetTitle));
        titleView.setTextSize(AppUtils.getPix(mContext, 9));
        titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        container.addView(titleView);

        container.setBackgroundResource(R.drawable.action_sheet_first_bg);

        header.addView(container);
        header.addView(getDividerView());

        return header;
    }

    /* 每一行的动作选项 */
    private View getActionView(String action, int iconResId) {
        /* 只有Action */
        if (iconResId == -1) {
            LinearLayout container = new LinearLayout(mContext);
            container.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (int) AppUtils.getPix(mContext, 48)));
            container.setGravity(Gravity.CENTER);
            container.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(mContext);
            textView.setText(action);
            textView.setTextColor(mContext.getResources().getColor(R.color.actionSheetAction));
            textView.setTextSize(AppUtils.getPix(mContext, 8));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            container.addView(textView, params);
            return container;
        }
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_menu_disclosure, null);
        TextView textView = (TextView) rootView.findViewById(R.id.menuText);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.menuIcon);
        textView.setText(action);
        imageView.setImageResource(iconResId);
        rootView.findViewById(R.id.menuDisclosure).setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View getCancelView() {
        LinearLayout container = new LinearLayout(mContext);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) AppUtils.getPix(mContext, 48)));
        container.setGravity(Gravity.CENTER);
        container.setOrientation(LinearLayout.VERTICAL);
        TextView cancelView = new TextView(mContext);
        cancelView.setText(mContext.getString(R.string.cancel));
        cancelView.setTextColor(mContext.getResources().getColor(R.color.actionSheetAction));
        cancelView.setTextSize(AppUtils.getPix(mContext, 8));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(cancelView, params);
        container.setBackgroundResource(R.drawable.action_sheet_cancel_bg);
        return container;
    }

    protected View getDividerView() {
        View divider = new View(mContext);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) AppUtils.getPix(mContext, 1)));
        divider.setBackgroundColor(mContext.getResources().getColor(R.color.actionSheetDivider));
        return divider;
    }
}
