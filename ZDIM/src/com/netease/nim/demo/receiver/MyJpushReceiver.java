package com.netease.nim.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.uikit.recent.RecentContactsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import zhwx.common.util.SharPreUtil;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.ui.dcapp.noticecenter.NoticeCenterActivity;

import static android.R.attr.key;

/**
 * Jpush自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyJpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (DemoCache.getAccount() == null) {
			//如果没有登录用户 不提醒推送
			return;
		}
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			
			final String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			//TODO
			Log.d(TAG,"[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			processCustomMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			processCustomMessage(context, bundle);
			insertMessage(context, bundle);
			
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

			if (DemoCache.getAccount() != null) {
				Intent intent1 = new Intent(context, NoticeCenterActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				SharPreUtil.saveField("haveNew", "");
				context.startActivity(intent1);
			} else {
				ToastUtil.showMessage("请登录");
				LoginActivity.start(context);
			}

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			
			Log.d(TAG,"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
			
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
			
		} else {
			
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		Log.d(TAG, "收到消息"+bundle.getString(JPushInterface.EXTRA_MESSAGE));
		Log.d(TAG, "收到消息"+bundle.getString(JPushInterface.EXTRA_EXTRA)); //自定义字段
		Log.d(TAG, "收到消息"+bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH)); //富文本地址
	}

	private void insertMessage(Context context, Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		try {
			JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
			Iterator<String> it =  json.keys();

			while (it.hasNext()) {
				String myKey = it.next().toString();
				sb.append("\nkey:" + key + ", value: [" +
						myKey + " - " +json.optString(myKey) + "]");
			}
		} catch (JSONException e) {
			Log.e(TAG, "Get message extra JSON error!");
		}
		Log.d(TAG, "收到通知"+bundle.getString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE"));
		if (StringUtil.isNotBlank(bundle.getString("cn.jpush.android.NOTIFICATION_CONTENT_TITLE"))) {
			SharPreUtil.saveField("haveNew", "1");
			SharPreUtil.saveField("noticeContent",bundle.getString("cn.jpush.android.ALERT"));
			//TODO 播放铃声
//			try {
//				IMUtils.playNotifycationMusic(CCPAppManager.getContext(), "avchat_ring.mp3");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			if(RecentContactsFragment.adapter != null){
				RecentContactsFragment.adapter.getCallback().onRecentContactsLoaded();
			}
		}
	}
}
