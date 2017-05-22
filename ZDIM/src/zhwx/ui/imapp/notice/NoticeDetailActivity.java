package zhwx.ui.imapp.notice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.util.file.IntentUtilForUikit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.CommonUtils;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.DownloadAsyncTask;
import zhwx.common.util.FileUtils;
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.NumberProgressBar;
import zhwx.common.view.ObservableWebView;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECListDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.imapp.notice.model.V3Notice;

public class NoticeDetailActivity extends BaseActivity {
	
	private Activity context;
	
	private TextView amd_sendUser,amd_time,amd_title,amd_kind,attachmentCountTV;
	
	private ListView amd_listview;
	
	private View amd_view;
	
	private ObservableWebView contentWV;
	
	private GifView gifView;
	
	private Button amd_expand;
	
	private V3Notice v3Notice;
	
	private String noticeId;
	
	private ImageView attachmentIV;
	
	private int lenth = 20;
	
	private PopupWindow mPopFooter;
	
	private View footerView;
	
	private Handler mHandler = new Handler();
	
	private TextView statisticsBT;
	
	private String delFlag = "";
	private String markFlag = "";
	private String noticeJson = "";
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ImageButton markBT,deleteBT,transmitBT;
	
	private RelativeLayout transmitLay;
	
	private int position;
	
	private int TYPE = -1;
	
	public static int TYPE_SEND = 4;
	
	public static int TYPE_ELSE = 0;
	
	private ECListDialog dialog;
	
	private ECProgressDialog progressDialog;
	
	private FrameLayout top_bar;
	
	private boolean canFinish = false;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		v3Notice = (V3Notice) getIntent().getSerializableExtra("notice");
		noticeId = getIntent().getStringExtra("noticeId");
		position = getIntent().getIntExtra("position", -1);
		TYPE = getIntent().getIntExtra("type", 0);
		initView();
		setImmerseLayout(top_bar);
		if(TYPE == TYPE_SEND) {
			statisticsBT.setVisibility(View.VISIBLE);
		}
	}
	
	private Runnable mRunnable = new Runnable() {
		public void run() {
			if (!mPopFooter.isShowing()) {
				showActionBar();
			}
		}
	};
	
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		attachmentCountTV = (TextView) findViewById(R.id.attachmentCountTV);
		amd_sendUser =  (TextView) findViewById(R.id.amd_sendUser);
		amd_time = (TextView) findViewById(R.id.amd_time);
		amd_title = (TextView) findViewById(R.id.amd_title);
		amd_view = findViewById(R.id.amd_view);
		amd_expand = (Button) findViewById(R.id.amd_expand);
		attachmentIV = (ImageView) findViewById(R.id.attachmentIV);
		amd_listview = (ListView) findViewById(R.id.amd_listview);
		gifView = (GifView) findViewById(R.id.gif1);
		gifView.setGifImage(R.drawable.gif_loding1);
		contentWV = (ObservableWebView) findViewById(R.id.contentWV);
		contentWV.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
			
			@Override
			public void onScroll(int dx, int dy) {
				if(amd_listview.getVisibility() == View.VISIBLE) {
					amd_listview.setVisibility(View.GONE);
					amd_view.setVisibility(amd_listview.getVisibility());
					amd_expand.setText(amd_listview.getVisibility() == View.GONE?"显示":"隐藏");
				}
				
				//隐藏操作栏
				if(dy > lenth) {
					if(mPopFooter != null) {
						canFinish = false;
						closeActionBar();
					}
				}
				//显示操作栏
				if(dy < -lenth) {
					showActionBar();
				}
			}
		});
		
		WebSettings settings = contentWV.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setAllowFileAccess(true);// 启用或禁止WebView访问文件数据
		settings.setBlockNetworkImage(false);// 是否显示网络图像 false 为显示
		contentWV.setInitialScale(300);
		contentWV.setWebViewClient(new HelloWebViewClient());
		amd_expand.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				amd_listview.setVisibility(amd_listview.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
				amd_view.setVisibility(amd_listview.getVisibility());
				amd_expand.setText(amd_listview.getVisibility() == View.GONE?"显示":"隐藏");
				if(amd_listview.isShown()){
					canFinish = false;
					closeActionBar();
				}else {
					showActionBar();
				}
			}
		});
		amd_listview.setOnItemClickListener(new OnItemClickListener() {// 下载按键处理

			@Override
			public void onItemClick(AdapterView<?> arg0, View item, int arg2, long arg3) {
				final ImageView imageView = (ImageView) item.findViewById(R.id.amd_image_download);
				TextView textView = (TextView) item.findViewById(R.id.amd_item_title);
				String title = (String) textView.getText();
				if (imageView.getTag() == null) {// 下载处理
					final NumberProgressBar progressBar = (NumberProgressBar) item.findViewById(R.id.amd_progressBar);
					progressBar.setVisibility(View.VISIBLE);
					DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
						@Override
						public void predownload() {
							imageView.setImageResource(R.drawable.amd_list_item_pause);
							imageView.setTag(R.drawable.amd_list_item_pause);
						}

						@Override
						public void downloading(int progress, int position) {
							progressBar.setProgress(progress);
						}

						@Override
						public void downloaded(File file, int position) {
							progressBar.setVisibility(View.INVISIBLE);
							imageView.setImageResource(R.drawable.amd_list_item_open);
							imageView.setTag(R.drawable.amd_list_item_open);
							imageView.setTag(R.drawable.amd_list_item_open, file);
						}

						@Override
						public void canceled(int position) {
							imageView.setImageResource(R.drawable.amd_list_item_download);
							imageView.setTag(null);
							progressBar.setVisibility(View.INVISIBLE);

						}
					}, context);
					downloadAsyncTask.execute(UrlUtil.getImgUrl(item.getTag().toString(),  ECApplication.getInstance().getLoginMap()), "aaa", arg2 + "", title);
					System.out.println(UrlUtil.getImgUrl(ECApplication.getInstance().getAddress()+item.getTag().toString(), ECApplication.getInstance().getLoginMap()));
					imageView.setTag(R.drawable.amd_list_item_pause, downloadAsyncTask);
					return;
				}

				switch ((Integer) imageView.getTag()) {
				case R.drawable.amd_list_item_pause:// 暂停处理
					DownloadAsyncTask downloadAsyncTask = (DownloadAsyncTask) imageView.getTag(R.drawable.amd_list_item_pause);
					downloadAsyncTask.cancel(true);
					break;

				case R.drawable.amd_list_item_open: // 打开处理
					
					File file = (File) imageView.getTag(R.drawable.amd_list_item_open);
					if(file!=null){
						String lastName = IMUtils.getExtensionName(file.getName());
						try {
							if(Constant.ATTACHMENT_DOC.equals(lastName)){
								startActivity(IntentUtilForUikit.getWordFileIntent(file.getPath(),context));
								
							}else if(Constant.ATTACHMENT_DOCX.equals(lastName)){
								startActivity(IntentUtilForUikit.getWordFileIntent(file.getPath(),context));
								
							}else if(Constant.ATTACHMENT_PPT.equals(lastName)){
								startActivity(IntentUtilForUikit.getPptFileIntent(file.getPath(),context));
								
							}else if(Constant.ATTACHMENT_PPTX.equals(lastName)){
								startActivity(IntentUtilForUikit.getPptFileIntent(file.getPath(),context));
								
							}else if(Constant.ATTACHMENT_XLS.equals(lastName)){
								startActivity(IntentUtilForUikit.getExcelFileIntent(file.getPath(),context));
								
							}else if(Constant.ATTACHMENT_XLSX.equals(lastName)){
								startActivity(IntentUtilForUikit.getExcelFileIntent(file.getPath(),context));
								
							}else if(Constant.ATTACHMENT_PDF.equals(lastName)){
								startActivity(IntentUtilForUikit.getPdfFileIntent(file.getPath(),context));

							}else if(Constant.ATTACHMENT_TXT.equals(lastName)){
								startActivity(IntentUtilForUikit.getTextFileIntent(file.getPath(), false));

							}else if(Constant.ATTACHMENT_JPG.equals(lastName)){
								startActivity(IntentUtilForUikit.getImageFileIntent(file.getPath(),context));

							}else if(Constant.ATTACHMENT_PNG.equals(lastName)){
								startActivity(IntentUtilForUikit.getImageFileIntent(file.getPath(),context));

							}else if(Constant.ATTACHMENT_GIF.equals(lastName)){
								startActivity(IntentUtilForUikit.getImageFileIntent(file.getPath(),context));

							}else{
								startActivity(IntentUtilForUikit.getAllFileIntent(file.getPath(),context));
							}
						} catch (Exception e) {
							e.printStackTrace();
							ToastUtil.showMessage("设备中未安装相应的查看程序");
						}
					}else{
						ToastUtil.showMessage("文件错误");
					}
					break;
				}
			}
		});
		
		statisticsBT = (TextView) findViewById(R.id.statisticsBT);
		statisticsBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, StatisticsActivity.class).putExtra("id", v3Notice.getId()));
			}
		});
		
		if (v3Notice != null) {
			refreshView();
		} else {
			getData();
		}
	}
	
	private void getData() {
		progressDialog = new ECProgressDialog(context,"正在获取详情");
		progressDialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("id", new ParameterValue(noticeId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					noticeJson = UrlUtil.getNoticeDetail(ECApplication.getInstance().getAddress(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(noticeJson);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
				}
			}
		}).start();
	}
	
	public void refreshData(String json) {
		System.out.println(json);
		if (!json.contains("<html>")) {
			v3Notice = new Gson().fromJson(json, V3Notice.class);
			if (v3Notice != null) {
				refreshView();
			}
		}
		progressDialog.dismiss();
	}
	
	public void refreshView() {
		initPopMenu();
		amd_sendUser.setText("发送人: "+ (v3Notice.getSendUser()==null?"我":v3Notice.getSendUser()));
		amd_time.setText("发送时间: "+v3Notice.getSendTime());
		amd_title.setText(v3Notice.getTitle());
		System.out.println(UrlUtil.getUrl(v3Notice.getUrl(), getMap(v3Notice.getId())));
		contentWV.loadUrl(UrlUtil.getUrl(v3Notice.getUrl(), getMap(v3Notice.getId())));
		if(v3Notice.getAttachmentFlag().size() == 0){
			amd_expand.setVisibility(View.GONE);
			attachmentIV.setVisibility(View.GONE);
			attachmentCountTV.setVisibility(View.GONE);
		} else {
			amd_expand.setVisibility(View.VISIBLE);
			attachmentIV.setVisibility(View.VISIBLE);
			attachmentCountTV.setVisibility(View.VISIBLE);
		}
		attachmentCountTV.setText(v3Notice.getAttachmentFlag().size() + "个附件");
		amd_listview.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				convertView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_item_amd, parent, false);
				ImageView imageView = (ImageView) convertView.findViewById(R.id.amd_image_download);
				ImageView iconIV = (ImageView) convertView.findViewById(R.id.imageView1);
				iconIV.setImageResource(FileUtils.getFileIcon(v3Notice.getAttachmentFlag().get(position).getName()));
				TextView title = (TextView) convertView.findViewById(R.id.amd_item_title);
				TextView numTV = (TextView) convertView.findViewById(R.id.numTV);
				title.setText(v3Notice.getAttachmentFlag().get(position).getName());
				numTV.setText((position+1)<10? "0" + (position+1) + "." : (position+1) + ".");
				convertView.setTag(v3Notice.getAttachmentFlag().get(position).getUrl());
				File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					idr = context.getFilesDir();
				}
				
				File file = new File(idr, (String) title.getText());
				//TODO 文件与本地文件 名称&&长度相同  视为已下载
				if (file.exists() && file.length() != 0) {
					imageView.setTag(R.drawable.amd_list_item_open);
					imageView.setTag(R.drawable.amd_list_item_open, file);
					imageView.setImageResource(R.drawable.amd_list_item_open);
				} else {
					imageView.setImageResource(R.drawable.amd_list_item_download);
				}
				
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return v3Notice.getAttachmentFlag().size();
			}
		});
	}
	
	
	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		 @Override  
         public void onPageFinished(WebView view,String url)  //加载结束
         {  
			 gifView.setVisibility(View.GONE);
	         super.onPageFinished(view, url);
         }  
	}
	
	/**
	 * 构建请求参数Map
	 * 
	 * @return
	 */
	private Map<String, ParameterValue> getMap(String messageId) {
		Map<String, ParameterValue> map = ECApplication.getInstance().getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("widthPx", new ParameterValue(DensityUtil.px2dip(context, CommonUtils.getWidthPixels(context))+""));
		return map;
	}
	
	@Override
	// 设置回退
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(contentWV.canGoBack()){
				contentWV.goBack(); // goBack()表示返回WebView的上一页面
			}else{
				finish();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 初始化popupWindow
	 */
	private void initPopMenu() {
		
		// 底部pop
		footerView = context.getLayoutInflater().inflate(R.layout.notice_detail_edit_footer, null);
		if (mPopFooter == null) {
			mPopFooter = new PopupWindow(footerView, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, true);
		}
		
		mPopFooter.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				if(canFinish) {
					finish();
				}
			}
		});
		
		markBT = (ImageButton) footerView.findViewById(R.id.markBT);
		markBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				lightonForattention(v3Notice.getId(), Constant.NOTICE_MARK_YES.equals(v3Notice.getAttentionFlag()) ? false:true);
			}
		});
		deleteBT = (ImageButton) footerView.findViewById(R.id.deleteBT);
		deleteBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showTips();
			}
		});
		transmitBT = (ImageButton) footerView.findViewById(R.id.transmitBT);
		transmitLay = (RelativeLayout) footerView.findViewById(R.id.transmitLay);
		if(NoticeActivity.couldSendflag!=null) {
			if("1".equals(NoticeActivity.couldSendflag.trim())) {
				transmitLay.setVisibility(View.VISIBLE);
			}
		}
		transmitBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog = new ECListDialog(context , new String[]{"带原文转发","忽略原文转发"});
				dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {
                    	Intent intent = new Intent(context,SendNewNoticeActivity.class);
                    	Bundle bundle = new Bundle();
                    	bundle.putSerializable("notice", v3Notice);
                    	intent.putExtras(bundle);
                        if (position == 0) {
        					intent.putExtra("Type",  SendNewNoticeActivity.TRANSMIT_ALL);
                        	startActivity(intent);
                        } else if (position == 1) {
                        	intent.putExtra("Type",  SendNewNoticeActivity.TRANSMIT_NOT_ALL);
                        	startActivity(intent);
                        }
                    }
                });
				dialog.setTitle("转发");
				dialog.show();
			}
		});
		
		if(Constant.NOTICE_MARK_YES.equals(v3Notice.getAttentionFlag())){
			markBT.setImageResource(R.drawable.btn_collect_yes);
		}else{
			markBT.setImageResource(R.drawable.btn_collect_no);
		}
		mHandler.postDelayed(mRunnable, 500);
	}
	
	public void showActionBar(){
		canFinish = true;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			IMUtils.setPopupWindowTouchModal(mPopFooter, false);
			if (!mPopFooter.isShowing()) {
				mPopFooter.setFocusable(true);
				mPopFooter.setOutsideTouchable(false);
				mPopFooter.setAnimationStyle(R.style.PopupAnimation1);
				mPopFooter.showAtLocation(context.getWindow().getDecorView(),Gravity.BOTTOM, 0, 0);
			}
		} else {
			if (!mPopFooter.isShowing()) {
				mPopFooter.setFocusable(false);
				mPopFooter.setOutsideTouchable(true);
				mPopFooter.setAnimationStyle(R.style.PopupAnimation1);
				mPopFooter.showAtLocation(context.getWindow().getDecorView(),Gravity.BOTTOM, 0, 0);
			}
		}
	}
	
	/**
	 * 将消息置成删除
	 * @param messageId
	 * @return
	 */
	public synchronized  void setMessageDelete(String messageId) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					delFlag = UrlUtil.setNoticeDelete(ECApplication.getInstance().getAddress(), map);
					System.out.println("delFlag" + delFlag);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (delFlag.contains("ok")) {
								ToastUtil.showMessage("已删除");
								if(position != -1) {
									NoticeActivity.allDataList.remove(position);
									setResult(100);
								}
								finish();
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
				}
			}
		}).start();
	}
	
	/**
	 * 将消息置成标记/取消标记
	 * @param messageId
	 * @return
	 */
	public synchronized void lightonForattention(String messageId,boolean isMark) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		if (isMark) {
			new ProgressThreadWrap(this, new RunnableWrap() {
				@Override
				public void run() {
					try {
						markFlag = UrlUtil.ajaxLighton(ECApplication.getInstance().getAddress(), map);
						handler.postDelayed(new Runnable() {
							public void run() {
								if (markFlag.contains("ok")) {
									ToastUtil.showMessage("已关注");
									markBT.setImageResource(R.drawable.btn_collect_yes);
									v3Notice.setAttentionFlag(Constant.NOTICE_MARK_YES);
									if(position != -1) {
										NoticeActivity.allDataList.get(position).setAttentionFlag(Constant.NOTICE_MARK_YES);
										setResult(100);
									}
								}
							}
						}, 5);
					} catch (IOException e) {
						e.printStackTrace();
						ToastUtil.showMessage("请求失败，请稍后重试");
					}
				}
			}).start();
		}else{
			new ProgressThreadWrap(this, new RunnableWrap() {
				@Override
				public void run() {
					try {
						markFlag = UrlUtil.ajaxLightoff(ECApplication.getInstance().getAddress(), map);
						System.out.println("markFlag" + markFlag);
						handler.postDelayed(new Runnable() {
							public void run() {
								if (markFlag.contains("ok")) {
									ToastUtil.showMessage("已移除关注");
									markBT.setImageResource(R.drawable.btn_collect_no);
									v3Notice.setAttentionFlag(Constant.NOTICE_MARK_NO);
									if(position != -1) {
										NoticeActivity.allDataList.get(position).setAttentionFlag(Constant.NOTICE_MARK_NO);
										setResult(100);
									}
								}
							}
						}, 5);
					} catch (IOException e) {
						e.printStackTrace();
						ToastUtil.showMessage("请求失败，请稍后重试");
					}
				}
			}).start();
		}
	}
	
	/**
	 * 删除提示
	 */
	private void showTips() {
		 ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.notice_delete, null, new DialogInterface.OnClickListener() {
			 @Override
                public void onClick(DialogInterface dialog, int which) {
				 	setMessageDelete(v3Notice.getId());
				 	System.out.println("sourceId"+v3Notice.getId());
                }
            });
            buildAlert.setTitle("删除");
            buildAlert.show();
    }
	
	public void closeActionBar(){
		mPopFooter.dismiss();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_noticedetail;
	}
}
