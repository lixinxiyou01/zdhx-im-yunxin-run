/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package zhwx.common.util;


import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.netease.nim.demo.ECApplication;

/**
 * 用于控制狂点按钮后toast经久不去
 * @author zhangyp
 *
 */
public class ToastUtil {
	private static Handler handler = new Handler(Looper.getMainLooper());

	private static Toast toast = null;

	private static Object synObj = new Object();

	public static void showMessage(final String msg) {
		showMessage(msg, Toast.LENGTH_SHORT);
	}
	
	public static void showMessage(final int msg) {
		showMessage(msg, Toast.LENGTH_SHORT);
	}

	public static void showMessage(final CharSequence msg, final int len) {
		if (msg == null || msg.equals("")) {
			return;
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (synObj) { //加上同步是为了每个toast只要有机会显示出来
					if (toast != null) {
						//toast.cancel();
						toast.setText(msg);
						toast.setDuration(len);
					} else {
						toast = Toast.makeText(ECApplication.getInstance(), msg, len);
					}
					toast.show();
				}
			}
		});
	}

	public static void showMessage(final int msg, final int len) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (synObj) {
					if (toast != null) {
						//toast.cancel();
						toast.setText(msg);
						toast.setDuration(len);
					} else {
						toast = Toast.makeText(ECApplication.getInstance().getApplicationContext(), msg, len);
					}
					toast.show();
				}
			}
		});
	}
}
