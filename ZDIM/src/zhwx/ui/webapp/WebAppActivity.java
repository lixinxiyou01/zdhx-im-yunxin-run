package zhwx.ui.webapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.netease.nim.demo.R;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import java.io.File;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.util.IntentUtil;
import zhwx.common.util.ToastUtil;
import zhwx.ui.webapp.view.loadview.LoadingView;


@SuppressLint("SetJavaScriptEnabled")
public class WebAppActivity extends BaseActivity {

	private Activity context;

	private WebView webAppWV;

	private String url;

	private ProgressBar progressBar1;

	private LoadingView loadView;

	private RelativeLayout mainLay,loadingLay;



	public ValueCallback<Uri> mUploadMessage;
	public ValueCallback<Uri[]> mUploadMessageForAndroid5;

	public final static int FILECHOOSER_RESULTCODE = 1;
	public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
	private String mCameraFilePath;
	private Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		mainLay = (RelativeLayout) findViewById(R.id.mainLay);
		loadingLay = (RelativeLayout) findViewById(R.id.loadingLay);
		url = getIntent().getStringExtra("webUrl");
		System.out.println(url);
		webAppWV = (WebView) findViewById(R.id.webAppWV);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		loadView = (LoadingView) findViewById(R.id.loadView);
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
			public void openTreeList() {
				startActivityForResult(new Intent(context, WebAppSelectActivity.class), 111);
			}

			@JavascriptInterface
			public void toFinishActivity() {
				finish();
			}

			@JavascriptInterface
			public void tokenTimeOut() {
				LogoutHelper.logout();
				// 启动登录
				LoginActivity.start(context);
				context.finish();
			}

			@JavascriptInterface
			public void showToast(String msg) {
				ToastUtil.showMessage(msg);
			}

		}, "nativeMobileDom");

		webAppWV.loadUrl(url);
		webAppWV.setWebViewClient(new HelloWebViewClient());
		webAppWV.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar1.setVisibility(View.GONE);
					loadView.setVisibility(View.GONE);
					loadingLay.setVisibility(View.GONE);
				} else {
					progressBar1.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}

			//扩展浏览器上传文件
			//3.0++版本
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
				openFileChooserImpl(uploadMsg);
			}

			//3.0--版本
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooserImpl(uploadMsg);
			}
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				openFileChooserImpl(uploadMsg);
			}
			// For Android > 5.0
			public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
				openFileChooserImplForAndroid5(uploadMsg);
				return true;
			}
		});

//		// webview 下载监听
//		webAppWV.setDownloadListener(new DownloadListener() {
//
//			@Override
//			public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimetype, long contentLength) {
//				DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
//					@Override
//					public void predownload() {
//
//					}
//					@Override
//					public void downloading(int progress, int position) {
//
//						if (progress < 100){
//							ToastUtil.showMessage("正在下载：" + progress + "%");
//						} else {
//							ToastUtil.showMessage("请选择打开方式");
//						}
//					}
//
//					@Override
//					public void downloaded(File file, int position) {
//						if(file!=null){
//							String lastName = IMUtils.getExtensionName(file.getName());
//							try {
//								if(Constant.ATTACHMENT_DOC.equals(lastName)){
//									startActivity(IntentUtil.getWordFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_DOCX.equals(lastName)){
//									startActivity(IntentUtil.getWordFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_PPT.equals(lastName)){
//									startActivity(IntentUtil.getPptFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_PPTX.equals(lastName)){
//									startActivity(IntentUtil.getPptFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_XLS.equals(lastName)){
//									startActivity(IntentUtil.getExcelFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_XLSX.equals(lastName)){
//									startActivity(IntentUtil.getExcelFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_PDF.equals(lastName)){
//									startActivity(IntentUtil.getPdfFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_TXT.equals(lastName)){
//									startActivity(IntentUtil.getTextFileIntent(file.getPath(), false));
//
//								}else if(Constant.ATTACHMENT_JPG.equals(lastName)){
//									startActivity(IntentUtil.getImageFileIntent(file.getPath()));
//
//								}else if(Constant.ATTACHMENT_PNG.equals(lastName)){
//									startActivity(IntentUtil.getImageFileIntent(file.getPath()));
//
//								}else{
//									startActivity(IntentUtil.getAllFileIntent(file.getPath()));
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//								ToastUtil.showMessage("设备中未安装相应的查看程序");
//							}
//						}else{
//							ToastUtil.showMessage("文件错误");
//						}
//					}
//
//					@Override
//					public void canceled(int position) {
//
//					}
//				}, context);
//				downloadAsyncTask.execute(url,"aaa","8",contentDisposition);
//			}
//		});

		webAppWV.setDownloadListener(new MyWebViewDownLoadListener());
	}


	private class MyWebViewDownLoadListener implements DownloadListener{

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
									long contentLength) {
			System.out.println(url);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
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
	}
	/**
	 * 5.0以下
	 * @param uploadMsg
	 */
	private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
		mUploadMessage = uploadMsg;
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		Intent xxx= createChooserIntent(createCameraIntent());
		xxx.putExtra(Intent.EXTRA_INTENT, i);
		startActivityForResult(xxx, FILECHOOSER_RESULTCODE);
	}

	/**
	 * 5.0以上的
	 * @param uploadMsg
	 */
	private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
		Intent intent = new Intent(WebAppActivity.this, PhotoSelectorActivity.class);
		intent.putExtra("canSelectCount", 4);
		startActivityForResult(intent, 2);
		mUploadMessageForAndroid5 = uploadMsg;
	}

	private Intent createCameraIntent() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File externalDataDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM);
		File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
				File.separator + "browser-photos");
		cameraDataDir.mkdirs();
		mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
				System.currentTimeMillis() + ".jpg";
		uri=  Uri.fromFile(new File(mCameraFilePath));
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		return cameraIntent;
	}

	private Intent createChooserIntent(Intent ... intents) {
		Intent chooser = new Intent(Intent.ACTION_CHOOSER);
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
		chooser.putExtra(Intent.EXTRA_TITLE, "选择图片来源");
		return chooser;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 111) {
			if(data != null) {
				String userName = data.getExtras().getString("userName");
				String userId = data.getExtras().getString("userId");
				webAppWV.loadUrl("javascript:com.ue.bd.module.mobile.course.table.getUserCourseTable('"+userId + "','"+ userName + "')");
			}
		} else if(requestCode == 2 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
						.getSerializable("photos");
				if (photos == null){
					mUploadMessageForAndroid5.onReceiveValue(null);
					return;
				}
				Uri[] uris = new Uri[photos.size()];
				for (int i = 0; i < photos.size(); i++) {
					uris[i] = Uri.fromFile(new File(photos.get(i).getOriginalPath()));
				}
				mUploadMessageForAndroid5.onReceiveValue(uris);
			}else{
				mUploadMessageForAndroid5.onReceiveValue(null);
			}
			mUploadMessageForAndroid5 = null;
		}else if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = data == null || resultCode != RESULT_OK ? null: data.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}else{
			if (mUploadMessageForAndroid5 != null)
				mUploadMessageForAndroid5.onReceiveValue(null);
			if (mUploadMessage != null)
				mUploadMessage.onReceiveValue(null);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webAppWV.canGoBack()) {

			if (webAppWV.getUrl().endsWith("#")) { //重定向URL 直接结束
				finish();
			} else {
				webAppWV.goBack(); //goBack()表示返回WebView的上一页面
			}
			return true;
		}else{
			finish();
		}
		return false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_webapp;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
