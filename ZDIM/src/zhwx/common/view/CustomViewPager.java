package zhwx.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by LX on 2016/11/24.
 */

public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }


    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll) {
            super.scrollTo(x, y);
        }
    }
}