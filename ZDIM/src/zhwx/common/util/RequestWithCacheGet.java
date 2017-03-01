package zhwx.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.netease.nim.demo.login.LoginActivity;

import java.io.InvalidClassException;

import volley.RequestQueue;
import volley.toolbox.StringRequest;
import volley.toolbox.Volley;
import volley.Response.Listener;
import volley.Response.ErrorListener;

/**
 * @author Li.Xin 使用方法： RequestWithCache qCache = new
 *         RequestWithCache(context); String string =
 *         qCache.getRseponse("http://www.baidu.com", new RequestListener() {
 * @Override public void onResponse(String response) { method stub
 *           System.out.println("second : " + response); } }, new
 *           ErrorListener() {
 * @Override public void onErrorResponse(VolleyError error) { Auto-generated
 *           method stub
 * 
 *           } });
 * 
 */
public class RequestWithCacheGet {
	private Context mContext;
	private RequestQueue mRequestQueue;
	public static final String NOT_OUTOFDATE = "notoutofdate";
	public static final String NO_DATA = "nodata";
	private static final String STR_SIZE = "size";
	public static final String SHAREDPREFERENCES_NAME = "josncatch";
	public static final String SHAREDPREFERENCES_KEY = "josncatchkey";
	public boolean isOpenCache = true;

	/**
	 * @param context
	 */
	public RequestWithCacheGet(Context context) {
		mContext = context;
		mRequestQueue = Volley.newRequestQueue(context);

	}

	/**
	 * @param url
	 * @param listener  当前 RequestWithCache 类中的 下载完成监听
	 *            返回字符串RequestWithCache.NOT_OUTOFDATE代表新数据与缓存数据一致 否则返回新数据
	 * @param errorListener  出错监听 导 volley 中的包
	 * @return 返回 已经缓存 过的数据 没有的话返回RequestWithCache.NOT_DATA
	 * @author Li.Xin  注意不要导错包
	 */
	public String getRseponse(final String url, final RequestListener listener, ErrorListener errorListener) {
		Log.i("Url", url);
		if (CheckNetworkUtil.getAPNType(mContext) == CheckNetworkUtil.NO_NET) {
			ToastUtil.showMessage("网络连接不可用");
		}
		final SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
		final SharedPreferences sharedPreferenceskey = mContext.getSharedPreferences(SHAREDPREFERENCES_KEY, 0);
		Listener<String> netlistener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				//TODO  Token失效   重新登录
				Log.i("Response", response);
				if (response.contains("noToken")) {
					 Intent outIntent = new Intent(mContext, LoginActivity.class);
		             outIntent.putExtra("startFlag", "yes");
		             outIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                 try {
						ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_REGIST_AUTO, "", true);
					 } catch (InvalidClassException e) {
						e.printStackTrace();
					 }
	                 mContext.startActivity(outIntent);
	                 ((Activity) mContext).finish();
	                 ToastUtil.showMessage("当前登录已失效,请重新登录");
	                 return;
				}
				
				int sizekey = sharedPreferenceskey.getInt(getCutUrl(url) + STR_SIZE, -1);
				if (sizekey != -1 && sizekey == response.getBytes().length + response.hashCode()) {
					listener.onResponse(NOT_OUTOFDATE);
				} else {
					if (isOpenCache) {
						if (!response.contains("<html>")) {
							sharedPreferences.edit().putString(getCutUrl(url), response).commit();
							// 采用hashcode + size比较 不记hashcode 碰撞 基本精确比较两个字符
							sharedPreferenceskey.edit().putInt(getCutUrl(url) + STR_SIZE,response.getBytes().length + response.hashCode()).commit();
						} else {
							response = "[]";
							ToastUtil.showMessage("数据异常，请检查wifi登录状态");
						}
					}
					listener.onResponse(new String(response));
				}
			}
		};
		mRequestQueue.add(new StringRequest(url, netlistener, errorListener));
		return sharedPreferences.getString(getCutUrl(url), NO_DATA);// 返回文件读取的数据
	}

	/**
	 * 清除缓存 只是简单删掉本地文件中的数据
	 * 
	 */
	public void clearCache() {
		// 清楚缓存FIX ME
		mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0).edit().clear().commit();
	}
	
	// 去掉URL中的token
	public String getCutUrl(String url) {
		String newUrl = null;
		if (url.contains("&imToken=")) {
			int index = url.indexOf("&imToken=");
			String cut = url.substring(index, index+41); 
			newUrl = url.replace(cut, "");
		} else {
			return url;
		}
		return newUrl;
	}
	
	/**
	 * 清除缓存 只是简单删掉本地文件中的数据
	 * 
	 */
	public void clearCacheWithUrl(String Url) {
		mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, 0).edit().remove(Url).commit();
	}

	public interface RequestListener {
		/** Called when a response is received. */
		public void onResponse(String response);
	}

	public void isOpenCache(boolean isOpenCache) {
		this.isOpenCache = isOpenCache;
	}
}
