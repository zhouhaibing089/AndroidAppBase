package seaice.app.appbase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import seaice.app.appbase.R;

public class PopupMenuAdapter extends ActionSheetAdapter {

    public PopupMenuAdapter(Context context, String[] actions, int[] icons) {
        super(context, null, null, actions, icons, false);
    }

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    protected int getFirstRowBackgroundResource(int section, int row) {
        return R.drawable.action_sheet_first_bg;
    }

    @Override
    public View getHeader(ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_menu_top_triangle, parent, false);
    }
}
