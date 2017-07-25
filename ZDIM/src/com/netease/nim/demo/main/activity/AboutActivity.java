package com.netease.nim.demo.main.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import cn.sharesdk.framework.ShareSDK;
import onekeyshare.OnekeyShare;
import zhwx.common.base.CCPAppManager;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;

public class AboutActivity extends UI{

	private Activity context;

	private TextView versonTV,checkUpdataTV;

	private ImageLoader imageLoader;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		context = this;
		imageLoader = new ImageLoader(context);
		ToolBarOptions options = new ToolBarOptions();
		setToolBar(R.id.toolbar, options);

		findViews();
	}

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void findViews() {
		versonTV = (TextView) findViewById(R.id.versonTV);
		versonTV.setText(getString(R.string.demo_current_version1 , CCPAppManager.getVersion()));
		checkUpdataTV = (TextView) findViewById(R.id.checkUpdataTV);
		checkUpdataTV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastUtil.showMessage("正在检查版本更新，请稍等");
				PgyUpdateManager.register(AboutActivity.this,getString(R.string.file_provider),new UpdateManagerListener() {

					@Override
					public void onUpdateAvailable(final String result) {

						// 将新版本信息封装到AppBean中
						final AppBean appBean = getAppBeanFromString(result);

						final AlertDialog alertDialog = new AlertDialog.Builder(AboutActivity.this).create();
						alertDialog.show();
						Window window = alertDialog.getWindow();
						window.setContentView(R.layout.pyger_update_dialog);
						TextView umeng_update_content = (TextView) window.findViewById(R.id.umeng_update_content);
						umeng_update_content.setText("v" + appBean.getVersionName() + "版本更新日志：\n" + (StringUtil.isBlank(appBean.getReleaseNote())?"无":appBean.getReleaseNote()));
						Button umeng_update_id_ok = (Button) window.findViewById(R.id.umeng_update_id_ok);
						umeng_update_id_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								startDownloadTask(AboutActivity.this,appBean.getDownloadURL());
								alertDialog.dismiss();
							}
						});
						Button umeng_update_id_cancel = (Button) window.findViewById(R.id.umeng_update_id_cancel);
						umeng_update_id_cancel.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								alertDialog.dismiss();
							}
						});
					}

					@Override
					public void onNoUpdateAvailable() {
						ToastUtil.showMessage("已是最新版本");
					}
				});
			}
		});
	}

	public void onShare(View v) {


		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.pgyer.com/jljy"
				+ " ---- 分享自"
				+ ECApplication.getInstance().getCurrentIMUser().getName()
				+ "(经纶教育)");
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, "分享经纶教育"));//设置分享列表的标题，并且每次都显示分享列表


//		Intent shareIntent = new Intent();
//		shareIntent.setAction(Intent.ACTION_SEND);
//		shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.pgyer.com/zhwx"
//				+ " ---- 分享自"
//				+ ECApplication.getInstance().getCurrentIMUser().getName()
//				+ "("+ getResources().getString(R.string.app_name) + ")");
//		shareIntent.setType("text/plain");
//		startActivity(Intent.createChooser(shareIntent, "分享" + ResourceHelper.getStringById(this, R.string.app_name)));//设置分享列表的标题，并且每次都显示分享列表
//		callShare();
	}

	public void callShare(){
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.disableSSOWhenAuthorize();
		oks.setTitle("下载经纶教育");
		String text = "构筑于工作场景下的即时沟通工具，让科技创新成就教育未来！";
		oks.setText(text);
		oks.setImagePath(imageLoader.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher)).getAbsolutePath());//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("https://www.pgyer.com/zhwx_cjl_yun_android");
		oks.show(this);
	}
}
