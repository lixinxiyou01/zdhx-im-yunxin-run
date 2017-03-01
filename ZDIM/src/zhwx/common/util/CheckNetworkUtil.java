package zhwx.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 检查网络工具
 * @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
 * 2014-9-24 下午3:47:40
 */



public class CheckNetworkUtil {
	
	public static final int CMNET = 3;   
	public static final int CMWAP = 2;   
	public static final int WIFI = 1;   
	public static final int NO_NET = -1;
	public static boolean isOpenNetwork(Context con){
		ConnectivityManager connManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	} 
	
	/**   
     * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络   
     *    
     * @param context   
     * @return   
     */   
    public static int getAPNType(Context context) {   
        int netType = -1;   
        ConnectivityManager connMgr = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();   
  
        if (networkInfo == null) {   
            return netType;   
        }   
        int nType = networkInfo.getType();   
        System.out.println("networkInfo.getExtraInfo() is "  
                + networkInfo.getExtraInfo());   
        if (nType == ConnectivityManager.TYPE_MOBILE && StringUtil.isNotBlank(networkInfo.getExtraInfo())) {   
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {   
                netType = CMNET;   
            } else {   
                netType = CMWAP;   
            }   
        } else if (nType == ConnectivityManager.TYPE_WIFI) {   
            netType = WIFI;   
        }   
        return netType;   
    }   
}
