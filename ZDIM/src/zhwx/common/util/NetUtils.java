package zhwx.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.netease.nim.demo.ECApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * 网络相关 Created by General on 15/8/17.
 */
public class NetUtils {

	/**
	 * 判断网络是否可用
	 *
	 * @return
	 */
	public static boolean isNetworkConnected() {
		Context context = ECApplication.getInstance().getApplicationContext();
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 检测网络资源是否存在　
	 * 
	 * @param strUrl
	 * @return
	 */
	public static boolean isNetFileAvailable(String strUrl) {
		InputStream netFileInputStream = null;
		try {
			URL url = new URL(strUrl);
			URLConnection urlConn = url.openConnection();
			netFileInputStream = urlConn.getInputStream();
			if (null != netFileInputStream) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (netFileInputStream != null)
					netFileInputStream.close();
			} catch (IOException e) {
			}
		}
	}
}
