/**   
* @Title: ObservableWebView.java 
* @Package com.zdhx.edu.im.ui.v3.notice
* @author Li.xin @ 中电和讯
* @date 2015-12-2 下午1:34:07 
*/
package zhwx.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**   
 * @Title: ObservableWebView.java 
 * @Package com.zdhx.edu.im.ui.v3.notice
 * @author Li.xin @ 中电和讯
 * @date 2015-12-2 下午1:34:07 
 */
public class ObservableWebView extends WebView {
	
    private OnScrollChangedCallback mOnScrollChangedCallback;
 
    public ObservableWebView(final Context context) {
        super(context);
    }
 
    public ObservableWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }
 
    public ObservableWebView(final Context context, final AttributeSet attrs,
                             final int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl,
                                   final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
 
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }
    }
 
    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }
 
    public void setOnScrollChangedCallback(
            final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }
 
    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedCallback {
        public void onScroll(int dx, int dy);
    }
}
