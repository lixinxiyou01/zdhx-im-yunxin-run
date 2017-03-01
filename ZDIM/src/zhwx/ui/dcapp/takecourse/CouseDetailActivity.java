package zhwx.ui.dcapp.takecourse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.netease.nim.demo.R;

import zhwx.common.base.BaseActivity;


@SuppressLint("SetJavaScriptEnabled")
public class CouseDetailActivity extends BaseActivity implements OnClickListener{

	private Activity context;

	private WebView webAppWV;

	private String url;

	private ProgressBar progressBar1;
	
	public static final int KIND_UNSELECT = 0;
	public static final int KIND_SELECT = 1;
	
	
	public static final int FLAG_FULL = -1;
	public static final int FLAG_DISABLE = -2;
	public static final int FLAG_UNSELECT = 0;
	public static final int FLAG_SELECTED = 1;
	
	private int kind = 0;
	
	private int flag = 0;
	
	private Button checkBT;
	
	private LinearLayout login_btnLay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"课程详情", this);
		url = getIntent().getStringExtra("webUrl");
		System.out.println(url);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		webAppWV = (WebView) findViewById(R.id.webAppWV);
		WebSettings settings = webAppWV.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setAllowFileAccess(true);// 启用或禁止WebView访问文件数据
		settings.setBlockNetworkImage(false);// 是否显示网络图像 false 为显示

		webAppWV.addJavascriptInterface(new Object() {

			@JavascriptInterface
			public void toFinishActivity() {
				finish();
			}

		}, "nativeMobileDom");

		webAppWV.loadUrl(url);
		webAppWV.setWebViewClient(new WebViewClient());
		webAppWV.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar1.setVisibility(View.GONE);
				} else {
					progressBar1.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		login_btnLay = (LinearLayout) findViewById(R.id.login_btnLay);
		flag = getIntent().getIntExtra("flag", 0);
		kind = getIntent().getIntExtra("kind", 0);
		checkBT = (Button) findViewById(R.id.checkBT);
		switch (kind) {
		case KIND_UNSELECT:
			login_btnLay.setVisibility(View.GONE);
			break;
		case KIND_SELECT:
			login_btnLay.setVisibility(View.VISIBLE);
			switch (flag) {
			case FLAG_FULL://已满
				checkBT.setText("已满");
				checkBT.setEnabled(false);
				break;
			case FLAG_DISABLE://不可选
				checkBT.setText("不可选");
				checkBT.setEnabled(false);
				break;
			case FLAG_UNSELECT://还未选择
				checkBT.setText("选择此课");
				break;
			case FLAG_SELECTED://已经选择
				checkBT.setText("取消选择");
				break;
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webAppWV.canGoBack()) {

			if (webAppWV.getUrl().endsWith("#")) { // 重定向URL 直接结束
				finish();
			} else {
				webAppWV.goBack(); // goBack()表示返回WebView的上一页面
			}
			return true;
		} else {
			finish();
		}
		return false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_webapp_news;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void onTack(View v) {
		setResult(100);
		finish();
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
