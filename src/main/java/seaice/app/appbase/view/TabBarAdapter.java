package seaice.app.appbase.view;

import android.graphics.drawable.Drawable;

public interface TabBarAdapter {

    /**
     * 总共有多少个Tab
     *
     * @return Tab的数量
     */
    int getCount();

    /**
     * 每个Tab的图标
     *
     * @param position 位置
     * @return 图标信息
     */
    Drawable getIcon(int position, float offset);

    /**
     * 每个Tab的标题
     *
     * @param position 位置
     * @return 图标信息
     */
    String getTitle(int position);

    /**
     * 每个Tab的字体颜色
     *
     * @param offset 偏移
     * @return 颜色
     */
    int getTitleColor(float offset);
}
