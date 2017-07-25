package com.netease.nim.demo.main.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.util.string.StringUtil;

import zhwx.common.util.IntentUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.ui.webapp.WebAppActivity;
import zhwx.ui.webapp.view.loadview.LoadingView;


@SuppressLint("SetJavaScriptEnabled")
public class ApplicationFragmentWeb extends TFragment {

	private Activity context;

	public static WebView webAppWV;

	private LoadingView loadView;


	private RelativeLayout loadingLay;

	private View v;

	private boolean isEditing = false;

	private boolean isLoaded = false;

	private boolean isFirstLoad = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.activity_webapp, container, false);
		return v;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		System.out.println(ECApplication.getInstance().getCurrentIMUser().getAppCenterUrl());
		webAppWV = (WebView) v.findViewById(R.id.webAppWV);
		loadView = (LoadingView) v.findViewById(R.id.loadView);
		loadingLay = (RelativeLayout) v.findViewById(R.id.loadingLay);
		WebSettings settings = webAppWV.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setAllowFileAccess(true);//启用或禁止WebView访问文件数据
		settings.setBlockNetworkImage(false);//是否显示网络图像  false 为显示


		webAppWV.addJavascriptInterface(new Object() {

			@JavascriptInterface
			public void openNativeApp(String code) {
				IntentUtil.openApp(context,code);
			}


			@JavascriptInterface
			public void openWebApp(String url) {

				if (url.contains("session/checkLogin")) {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(url);
					intent.setData(content_url);
					startActivity(intent);

				} else {
					startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));
				}
			}

			@JavascriptInterface
			public void openEdit(String open) {
				if ("open".equals(open)) {
					isEditing = true;
				} else {
					isEditing = false;
				}
			}

			@JavascriptInterface
			public void tokenTimeOut() {
				LogoutHelper.logout();
				// 启动登录
				LoginActivity.start(context);
				context.finish();
			}
		}, "nativeMobileDom");

		webAppWV.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});

		webAppWV.loadUrl(ECApplication.getInstance().getCurrentIMUser().getAppCenterUrl());
		webAppWV.setWebViewClient(new HelloWebViewClient());
		webAppWV.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					isLoaded = true;
					loadView.setVisibility(View.GONE);
					loadingLay.setVisibility(View.GONE);
				} else {

				}
				super.onProgressChanged(view, newProgress);
			}
		});

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isFirstLoad && !isLoaded) {
			webAppWV.loadUrl(ECApplication.getInstance().getCurrentIMUser().getAppCenterUrl());
			loadView.setVisibility(View.VISIBLE);
			loadingLay.setVisibility(View.VISIBLE);
		}
		isFirstLoad = false;

		if(StringUtil.isEmpty(ECApplication.getInstance().getFlag())) {
			ECAlertDialog buildAlert = ECAlertDialog.buildPositiveAlert(context, R.string.action_settings, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					ECApplication.getInstance().saveFlag();
				}
			});
			buildAlert.setTitle("提示");
			buildAlert.setMessage("1、长按应用图标可开启编辑模式\n\n" +
					"2、编辑模式下点击图标可将应用从常用应用分组中添加、移除\n\n" +
					"3、编辑模式下常用应用分组图标可拖动排序");
			buildAlert.show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
	}

	//Web视图
	private class HelloWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onPageFinished(WebView view,String url)  //加载结束
		{
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			ToastUtil.showMessage(errorCode);
		}
	}
}
