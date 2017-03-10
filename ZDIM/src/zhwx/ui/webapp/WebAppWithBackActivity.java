package zhwx.ui.webapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.netease.nim.demo.R;

import java.io.File;

import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.util.DownloadAsyncTask;
import zhwx.common.util.IMUtils;
import zhwx.common.util.IntentUtil;
import zhwx.common.util.ToastUtil;
import zhwx.ui.webapp.view.loadview.LoadingView;

@SuppressLint("SetJavaScriptEnabled")
public class WebAppWithBackActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
	
	private WebView webAppWV;
	
	private String url;
	
	private ProgressBar progressBar1;

	private LoadingView loadView;
	
	private RelativeLayout mainLay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"教科研", this);
		mainLay = (RelativeLayout) findViewById(R.id.mainLay);
		url = getIntent().getStringExtra("webUrl");
		System.out.println(url);
		webAppWV = (WebView) findViewById(R.id.webAppWV);
		loadView = (LoadingView) findViewById(R.id.loadView);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
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
			public void openTreeList() { 
				startActivityForResult(new Intent(context, WebAppSelectActivity.class), 111);
            } 
			
			@JavascriptInterface
			public void toFinishActivity() { 
				finish();
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
	            } else {
	            	progressBar1.setProgress(newProgress);
	            }
	            super.onProgressChanged(view, newProgress);
	        }
		});
		
		// webview 下载监听
		webAppWV.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
                DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
					@Override
					public void predownload() {
						
					}

					@Override
					public void downloading(int progress, int position) {
						if (progress < 100){
							ToastUtil.showMessage("正在加载：" + progress + "%");
						} else {
							ToastUtil.showMessage("请选择打开方式");
						}
					}

					@Override
					public void downloaded(File file, int position) {
						if(file!=null){
							String lastName = IMUtils.getExtensionName(file.getName());
							try {
								if(Constant.ATTACHMENT_DOC.equals(lastName)){
									startActivity(IntentUtil.getWordFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_DOCX.equals(lastName)){
									startActivity(IntentUtil.getWordFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_PPT.equals(lastName)){
									startActivity(IntentUtil.getPptFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_PPTX.equals(lastName)){
									startActivity(IntentUtil.getPptFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_XLS.equals(lastName)){
									startActivity(IntentUtil.getExcelFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_XLSX.equals(lastName)){
									startActivity(IntentUtil.getExcelFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_PDF.equals(lastName)){
									startActivity(IntentUtil.getPdfFileIntent(file.getPath()));
									
								}else if(Constant.ATTACHMENT_TXT.equals(lastName)){
									startActivity(IntentUtil.getTextFileIntent(file.getPath(), false));

								}else if(Constant.ATTACHMENT_JPG.equals(lastName)){
									startActivity(IntentUtil.getImageFileIntent(file.getPath()));

								}else if(Constant.ATTACHMENT_PNG.equals(lastName)){
									startActivity(IntentUtil.getImageFileIntent(file.getPath()));

								}else{
									startActivity(IntentUtil.getAllFileIntent(file.getPath()));
								}
							} catch (Exception e) {
								e.printStackTrace();
								ToastUtil.showMessage("设备中未安装相应的查看程序");
							}
						}else{
							ToastUtil.showMessage("文件错误");
						}
					}

					@Override
					public void canceled(int position) {
						
					}
				}, context);
				downloadAsyncTask.execute(url,"aaa","8");
			}
		});
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
}
