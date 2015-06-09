package seaice.app.appbase.adapter;

import android.content.Context;

import java.util.List;

import seaice.app.appbase.view.TableAdapter;

public abstract class BaseTableAdapter extends TableAdapter {

    public BaseTableAdapter(Context context) {
        super(context);
    }

    @Override
    public String getSectionHeader(int section) {
        if (getSectionCount() > 1) {
            return "";
        }
        return null;
    }

    @Override
    public String getSectionFooter(int section) {
        return null;
    }

    @Override
    public List<Object> getDataSet() {
        return null;
    }
}
