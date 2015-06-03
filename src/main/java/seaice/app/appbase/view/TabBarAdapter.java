package seaice.app.appbase.view;

/**
 * Created by zhb on 5/15/15.
 */
public interface TabBarAdapter {

    int getCount();

    int getIcon(int position);

    String getSVGIcon(int position);

    String getTitle(int position);

}
