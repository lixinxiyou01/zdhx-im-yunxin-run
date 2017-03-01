/**
 * 北京百年育人教育投资有限公司 版权所有
 * <p/>
 * 文件:com.example.test.ResultListenerImpl.java
 * <p/>
 * 创建时间:2015年5月6日
 * <p/>
 * 创建人:马海明
 */
package volley;

import android.content.Context;

import zhwx.common.util.ToastUtil;


public class ResultListenerImpl implements ResultLinstener {

    private Context context;

    /**
     *
     */
    public ResultListenerImpl(Context context) {
        this.context = context;
    }


    @Override
    public void onSuccess(String response) {
        System.out.println(response);
    }

    @Override
    public void onServerError() {
    	ToastUtil.showMessage("服务器错误");
    }

    @Override
    public void onIOError() {
    	ToastUtil.showMessage("无法访问服务器,可能网络异常");
    }

    @Override
    public void onSetTag(String tag) {
    }

    @Override
    public void onTimeOutError() {
    	ToastUtil.showMessage("访问超时，请重试");
    }

    @Override
    public void onNoConnectionError() {
    	ToastUtil.showMessage("连接错误，可能访问地址有误");
    }

    @Override
    public void onError() {
    }
}
