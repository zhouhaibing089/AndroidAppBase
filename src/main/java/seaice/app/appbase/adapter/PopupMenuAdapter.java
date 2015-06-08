package seaice.app.appbase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import seaice.app.appbase.R;

public class PopupMenuAdapter extends ActionSheetAdapter {

    public PopupMenuAdapter(Context context, String[] actions, int[] icons) {
        super(context, null, null, actions, icons, false);
    }

    @Override
    public View getHeader() {
        return LayoutInflater.from(mContext).inflate(R.layout.item_menu_top_triangle, null);
    }
}
