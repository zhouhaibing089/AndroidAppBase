package seaice.app.appbase.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public boolean hasHeader() {
        return false;
    }

    @Override
    public View getHeader(ViewGroup parent) {
        return null;
    }

    @Override
    public boolean hasFooter() {
        return false;
    }

    @Override
    public View getFooter(ViewGroup parent) {
        return null;
    }

    @Override
    public int getSectionCount() {
        return 1;
    }
}
