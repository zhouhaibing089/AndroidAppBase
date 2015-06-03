package seaice.app.appbase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import seaice.app.appbase.R;

public class PopupMenuAdapter extends ActionSheetAdapter {

    public PopupMenuAdapter(Context context, String[] actions, int[] icons) {
        super(context, null, null, actions, icons, false);
    }

//    @Override
//    public View getRow(int section, int row) {
//        View view = super.getRow(section, row);
//        if (row == 0) {
//            if (getRowCount(0) == 1) {
//                view.setBackgroundResource(R.drawable.action_sheet_last_bg);
//            } else {
//                view.setBackgroundResource(R.drawable.action_sheet_bg);
//            }
//        }
//        return view;
//    }

    @Override
    public View getHeader() {
        return LayoutInflater.from(mContext).inflate(R.layout.item_menu_top_triangle, null);
    }
}
