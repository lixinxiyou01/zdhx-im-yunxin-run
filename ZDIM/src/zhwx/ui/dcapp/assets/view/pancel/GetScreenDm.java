package zhwx.ui.dcapp.assets.view.pancel;

import android.content.Context;
import android.util.DisplayMetrics;

public class GetScreenDm {
	
    public static int getDisplayMetricsW(Context cx) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }
    public static int getDisplayMetricsH(Context cx) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }
}
