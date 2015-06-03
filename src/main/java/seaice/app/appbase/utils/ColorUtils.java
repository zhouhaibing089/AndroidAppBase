package seaice.app.appbase.utils;

import android.graphics.Color;


public class ColorUtils {

    public static int getInterColor(int from, int to, float ratio) {
        int r = Color.red(from);
        int g = Color.green(from);
        int b = Color.blue(from);
        int rs = Color.red(to);
        int rg = Color.green(to);
        int rb = Color.blue(to);

        int red = r + (int) ((rs - r) * ratio);
        int green = g + (int) ((rg - g) * ratio);
        int blue = b + (int) ((rb - b) * ratio);

        return Color.rgb(red, green, blue);
    }
}
