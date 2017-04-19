package zhwx.ui.imapp.notice;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.Attachment;
import zhwx.common.model.ECContacts;
import zhwx.common.model.ParameterValue;
import zhwx.common.plugin.FileExplorerActivity;
import zhwx.common.util.CommonUtils;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.FileUpLoadCallBack;
import zhwx.common.util.FileUtils;
import zhwx.common.util.IMUtils;
import zhwx.common.util.InputTools;
import zhwx.common.util.Log;
import zhwx.common.util.LogUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.imageLoader.AsyncImageLoader;
import zhwx.common.util.imageLoader.ImageCacheManager;
import zhwx.common.util.imageLoader.ImageLoader;
import zhwx.common.view.ObservableWebView;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.taggroup.TagGroup;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.db.ContactSqlManager;
import zhwx.ui.imapp.notice.model.H5Content;
import zhwx.ui.imapp.notice.model.SendFile;
import zhwx.ui.imapp.notice.model.V3Notice;


/**
 * @Title: SendNewNoticeActivity.java
 * @Package com.zdhx.edu.im.ui.v3.notice
 * @author Li.xin @ 中电和讯
 * @date 2015-12-7 下午1:06:33
 */
public class SendNewNoticeActivity extends BaseActivity {

	private Activity context;

	private PopupWindow mPop;

	private View view;

	private Button fujianBT,addReceiverBT;

	private Button imageBT,cameraBT,fileBT;

	private GridView fileGv;

	private FileGVAdapter adapter = new FileGVAdapter();

	private List<SendFile> files = new ArrayList<SendFile>();

	private List<File> fileList = new ArrayList<File>();

	private ImageLoader imageLoader;

	private TextView fujianCountTV,themCountTV,receiverTV,receiverListTV,receiverCountTV;

	private EditText contentET,themET;

	private TagGroup receiverET;

	private Handler handler = new Handler();

	private HashMap<String, ParameterValue> map;

	private String sendFlag;

	private int EDIT_MODE = -1;     //编辑模式

	public static int SENDNEW = 0;	//编辑模式-新邮件

	public static int TRANSMIT_ALL = 1;	//编辑模式-转发-带原文

	public static int TRANSMIT_NOT_ALL = 2;	//编辑模式-转发-不带原文

	private V3Notice v3Notice;

	private String h5Content;

	private H5Content content;

	private Button sendBT;

	private ECProgressDialog dialog;

	private ObservableWebView contentWV;

	private LinearLayout editOldLay;

	private TextView editOldTV;

	private boolean edited = false;

	private FrameLayout top_bar;

	private ListView receiverLV;

	private List<ECContacts> friendResults;

	//过滤出字母、数字和中文的正则表达式
	private String regexStr = "[\u4E00-\u9FA5]";

	public static Map<String, Node> positionMap = new HashMap<String, Node>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		imageLoader = new ImageLoader();

		getTopBarView().setVisibility(View.GONE);

		EDIT_MODE = getIntent().getIntExtra("Type", 0);

		initPopMenu();

		initView();

		setImmerseLayout(top_bar,1);


		if (EDIT_MODE != SENDNEW) {
			v3Notice = (V3Notice) getIntent().getSerializableExtra("notice");
			dialog = new ECProgressDialog(context);
			dialog.setPressText("数据加载中");
			dialog.show();
			if (EDIT_MODE == TRANSMIT_ALL) {
				WebSettings settings = contentWV.getSettings();
				contentWV.loadUrl(UrlUtil.getUrl(v3Notice.getUrl(), getMap(v3Notice.getId())));
				settings.setJavaScriptEnabled(true);
				settings.setUseWideViewPort(true);
				settings.setLoadWithOverviewMode(true);
				settings.setBuiltInZoomControls(true);
				settings.setDisplayZoomControls(false);
				settings.setAllowFileAccess(true);// 启用或禁止WebView访问文件数据
				settings.setBlockNetworkImage(false);// 是否显示网络图像 false 为显示
				contentWV.setInitialScale(300);
				contentWV.setWebViewClient(new HelloWebViewClient());
				editOldLay.setVisibility(View.VISIBLE);
			} else {
				getData();
			}
		}
	}

	private void getData() {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("id", new ParameterValue(v3Notice.getId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					h5Content = UrlUtil.getNoticeHtmlContent(ECApplication.getInstance().getAddress(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							System.out.println(h5Content);
							refreshData(h5Content);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					dialog.dismiss();
				}
			}
		}).start();
	}

	private void refreshData(String h5Content) {
		if(h5Content.contains("content")) {
			content = new Gson().fromJson(h5Content, H5Content.class);
			if (EDIT_MODE == TRANSMIT_ALL) {
				//TODO 解析图片
				System.out.println(content.getContent());
//			    URLImageParser imageGetter = new URLImageParser(contentET, context);
//				contentET.setText(Html.fromHtml(content.getContent(),imageGetter,null));
//				contentET.setText(Html.fromHtml(content.getContent()));
			} else {
				content.setContent("");
			}
			themET.setText("[转自" + v3Notice.getSendUser() + "]" + v3Notice.getTitle());
			List<Attachment> attachments = v3Notice.getAttachmentFlag();
			if(attachments != null && attachments.size() != 0) {
				for (Attachment attachment : attachments) {
					SendFile sendFile = new SendFile();
					sendFile.setKind(SendFile.ATTACHMENT);
					sendFile.setAttachment(attachment);
					files.add(sendFile);
				}
				adapter.notifyDataSetChanged();
				getFileListSize();
				Tools.setGridViewHeightBasedOnChildren2(fileGv);
			}
		}
		dialog.dismiss();
	}

	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		editOldLay = (LinearLayout) findViewById(R.id.editOldLay);
		editOldTV = (TextView) findViewById(R.id.editOldTV);
		editOldTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.question_notice_edit, null, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editOldLay.setVisibility(View.GONE);
						contentET.setText(Html.fromHtml(content.getContent()));
						edited = true;
					}
				});
				buildAlert.setTitle("提示");
				buildAlert.show();
			}
		});

		contentWV = (ObservableWebView) findViewById(R.id.contentWV);
		sendBT = (Button) findViewById(R.id.sendBT);
		contentET = (EditText) findViewById(R.id.contentET);
		contentET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPop.dismiss();
				if (SendNewNoticeActivity.positionMap == null || SendNewNoticeActivity.positionMap.size() == 0) {
					return;
				}
				changeToText();
			}
		});

		contentET.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean has) {
				if (has) {
					if (SendNewNoticeActivity.positionMap == null || SendNewNoticeActivity.positionMap.size() == 0) {
						return;
					}
					changeToText();
				}
			}
		});

//		if (EDIT_MODE == SENDNEW) {
//			contentET.setText("\n\n\n\n发自"+ getResources().getString(R.string.app_name) + "客户端\n");
//		} else {
//			contentET.setText("\n\n\n\n[" + ECApplication.getInstance().getCurrentIMUser().getName() + "]转发自"+ getResources().getString(R.string.app_name) + "客户端\n");
//		}

		themET = (EditText) findViewById(R.id.themET);
		themET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPop.dismiss();
				if (SendNewNoticeActivity.positionMap == null || SendNewNoticeActivity.positionMap.size() == 0) {
					return;
				}
				changeToText();
			}
		});

		themET.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean has) {
				if (has) {
					if (SendNewNoticeActivity.positionMap == null || SendNewNoticeActivity.positionMap.size() == 0) {
						return;
					}
					changeToText();
				}
			}
		});

		themET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if((100 - arg0.toString().length()) >= 10 && (100 - arg0.toString().length()) < 50) {
					themCountTV.setTextColor(Color.parseColor("#ebc426"));
				} else if ((100 - arg0.toString().length()) >= 50) {
					themCountTV.setTextColor(Color.parseColor("#00a650"));
				} else if ((100 - arg0.toString().length()) < 10) {
					themCountTV.setTextColor(Color.RED);
				}
				themCountTV.setText("还剩" + (100 - arg0.toString().length()));
			}
		});

		fujianBT = (Button) findViewById(R.id.fujianBT);
		fujianBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!mPop.isShowing()) {
					InputTools.HideKeyboard(arg0);
					mPop.setFocusable(false);
					mPop.setOutsideTouchable(true);
					mPop.setAnimationStyle(R.style.PopupAnimation1);
					mPop.showAtLocation(context.getWindow().getDecorView(),Gravity.BOTTOM, 0, 0);
					fujianBT.setSelected(true);
				} else {
					mPop.dismiss();
				}
			}
		});
		addReceiverBT = (Button) findViewById(R.id.addReceiverBT);
		fujianCountTV = (TextView) findViewById(R.id.fujianCountTV);
		themCountTV = (TextView) findViewById(R.id.themCountTV);
		fileGv = (GridView) findViewById(R.id.fileGv);
		fileGv.setAdapter(adapter);
		Tools.setGridViewHeightBasedOnChildren2(fileGv);
		onResume();
	}

	public void changeToText() {
		receiverET.setVisibility(View.GONE);
		addReceiverBT.setVisibility(View.GONE);
		receiverListTV.setVisibility(View.VISIBLE);
		receiverCountTV.setVisibility(View.VISIBLE);
		receiverCountTV.setText(SendNewNoticeActivity.positionMap.size()+"人");
		receiverListTV.setText(formArry1());
		disMissPop();
	}

	public void changeToEdit() {
		receiverET.setVisibility(View.VISIBLE);
		addReceiverBT.setVisibility(View.VISIBLE);
		receiverListTV.setVisibility(View.GONE);
		receiverCountTV.setVisibility(View.GONE);
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
			super.onPageFinished(view, url);
			getData();
		}
	}

	/**
	 * 构建请求参数Map
	 * @return
	 */
	private Map<String, ParameterValue> getMap(String messageId) {
		Map<String, ParameterValue> map = ECApplication.getInstance().getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("widthPx", new ParameterValue(DensityUtil.px2dip(context, CommonUtils.getWidthPixels(context))+""));
		return map;
	}

	private void initPopMenu() {
		// 顶部pop
		view = context.getLayoutInflater().inflate(
				R.layout.pop_sendnotice_fujian, null);
		if (mPop == null) {
			mPop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}
		mPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		if (mPop.isShowing()) {
			mPop.dismiss();
		}
		mPop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				fujianBT.setSelected(false);
			}
		});

		imageBT = (Button) view.findViewById(R.id.imageBT);
		imageBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,PhotoSelectorActivity.class);
				intent.putExtra("canSelectCount", 4);
				startActivityForResult(intent, 0);
			}
		});
		cameraBT = (Button) view.findViewById(R.id.cameraBT);
		cameraBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		fileBT = (Button) view.findViewById(R.id.fileBT);
		fileBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, FileExplorerActivity.class), 0x2a);
			}
		});

		receiverET = (TagGroup) findViewById(R.id.receiverET);
		receiverET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (formArry3().size() == 0) {
					startActivityForResult(new Intent(context, SeletePageOneActivity.class),100);
				}
			}
		});

		receiverET.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {

			@Override
			public void onDelete(TagGroup tagGroup, String tag) {
				//  删除人的时候把 map里面的也删掉
				for (Map.Entry<String, Node> entry : SendNewNoticeActivity.positionMap.entrySet()) {
					if (entry.getValue().getName().equals(tag)) {
						Log.e("删除"+ ":" + tag);
						SendNewNoticeActivity.positionMap.remove(entry.getKey());
						break;
					}
				}
			}

			@Override
			public void onAppend(TagGroup tagGroup, String tag) {

			}
		});

		receiverET.setOnLastTagEditListener(new TagGroup.OnLastTagEditListener() {

			@Override
			public void onEditChanged(final String text) {
				searcheFriend(text.toString());
				if (text.length() == 0) {
					disMissPop();
				}
			}
		});

		receiverTV = (TextView) findViewById(R.id.receiverTV);
		receiverTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (formArry3().size() == 0) {
					startActivityForResult(new Intent(context, SeletePageOneActivity.class),100);
				} else {
					startActivityForResult(new Intent(context, SelectedActivity.class),121);
				}
			}
		});

		receiverCountTV = (TextView) findViewById(R.id.receiverCountTV);
		receiverListTV = (TextView) findViewById(R.id.receiverListTV);
		receiverListTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 点击时变成编辑模式
				changeToEdit();
			}
		});

		receiverLV = (ListView)findViewById(R.id.receiverLV);
		receiverLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				ECContacts contacts = (ECContacts) parent.getAdapter().getItem(position);
				if (StringUtil.isNotBlank(contacts.getImId())) {
					Node node = new Node();
					node.setName(contacts.getNickname());
					SendNewNoticeActivity.positionMap.put(contacts.getImId(), node);
					receiverET.setTags(formArry3());
					disMissPop();
					changeToEdit();
				} else {
					ToastUtil.showMessage("该用户不可选");
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == 100){
			receiverET.setTags(formArry3());
			changeToEdit();
		}

		if (requestCode == 0x2a) {
			if (data == null) {
				return;
			}
		} else if (resultCode != RESULT_OK) {
			LogUtil.d("onActivityResult: bail due to resultCode=" + resultCode);
			return;
		}

		if(data != null && 0x2a == requestCode) {
			String path = data.getStringExtra("choosed_file_path");
			File file = new File(path);
			if(file != null && isFileNotHas(path)){
				SendFile sendFile = new SendFile();
				sendFile.setKind(SendFile.FILE);
				sendFile.setFile(file);
				files.add(sendFile);
			}
			adapter.notifyDataSetChanged();
			getFileListSize();
			Tools.setGridViewHeightBasedOnChildren2(fileGv);
			mPop.dismiss();
			return ;
		}
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
						.getSerializable("photos");
				for (PhotoModel photoModel : photos) {
					if(isFileNotHas(photoModel.getOriginalPath())) {
						File file = new File(photoModel.getOriginalPath());
						SendFile sendFile = new SendFile();
						sendFile.setKind(SendFile.FILE);
						sendFile.setFile(file);
						files.add(sendFile);
					}
				}
			}
		}
		adapter.notifyDataSetChanged();
		getFileListSize();
		Tools.setGridViewHeightBasedOnChildren2(fileGv);
		mPop.dismiss();
	}
	/**
	 * 已选择图片适配
	 */
	class FileGVAdapter extends BaseAdapter {

		public FileGVAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return files.size();
		}

		@Override
		public SendFile getItem(int position) {
			return files.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.gv_item_file,null);
				holder.imageGV = (ImageView) convertView.findViewById(R.id.imageGV);
				holder.fileKindIV = (ImageView) convertView.findViewById(R.id.fileKindIV);
				holder.delBT = (ImageView) convertView.findViewById(R.id.delBT);
				holder.fileLay = (LinearLayout) convertView.findViewById(R.id.fileLay);
				holder.imageLay = (RelativeLayout) convertView.findViewById(R.id.imageLay);
				holder.fileNameTV = (TextView) convertView.findViewById(R.id.fileNameTV);
				holder.fileSizeTV = (TextView) convertView.findViewById(R.id.fileSizeTV);
				holder.imageSizeTV = (TextView) convertView.findViewById(R.id.imageSizeTV);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (SendFile.FILE == getItem(position).getKind()) {
				if(Constant.ATTACHMENT_JPG.equals(IMUtils.getExtensionName(getItem(position).getFile().getAbsolutePath())) ||
						Constant.ATTACHMENT_PNG.equals(IMUtils.getExtensionName(getItem(position).getFile().getAbsolutePath())) ||
						Constant.ATTACHMENT_JPEG.equals(IMUtils.getExtensionName(getItem(position).getFile().getAbsolutePath()))||
						Constant.ATTACHMENT_GIF.equals(IMUtils.getExtensionName(getItem(position).getFile().getAbsolutePath()))) {
					holder.fileLay.setVisibility(View.GONE);
					holder.imageLay.setVisibility(View.VISIBLE);
					holder.imageGV.setImageBitmap(imageLoader.loadImageFromLocal(getItem(position).getFile().getAbsolutePath()));
					holder.imageSizeTV.setText(FileUtils.formatFileLength(getItem(position).getFile().length()));
					System.out.println(getItem(position).getFile().getAbsolutePath());
				} else {
					holder.fileLay.setVisibility(View.VISIBLE);
					holder.imageLay.setVisibility(View.GONE);
					holder.fileNameTV.setText(IMUtils.getFilename(getItem(position).getFile().getAbsolutePath()));
					holder.fileSizeTV.setText(FileUtils.formatFileLength(getItem(position).getFile().length()));
					holder.fileKindIV.setImageResource(FileUtils.getFileIcon(IMUtils.getFilename(getItem(position).getFile().getAbsolutePath())));
				}
			} else {
				holder.fileLay.setVisibility(View.VISIBLE);
				holder.imageLay.setVisibility(View.GONE);
				holder.fileNameTV.setText(getItem(position).getAttachment().getName());
				holder.fileSizeTV.setText("原文附件");
				holder.fileKindIV.setImageResource(FileUtils.getFileIcon(getItem(position).getAttachment().getName()));
			}
			addListener(holder, position);
			return convertView;
		}

		private void addListener(Holder holder, final int position) {
			holder.delBT.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					files.remove(position);
					adapter.notifyDataSetChanged();
					getFileListSize();
					Tools.setGridViewHeightBasedOnChildren2(fileGv);
				}
			});
		}

		class Holder {
			private ImageView imageGV,fileKindIV;
			private ImageView delBT;
			private TextView fileNameTV,fileSizeTV,imageSizeTV;
			private LinearLayout fileLay;
			private RelativeLayout imageLay;
		}
	}



	private void getFileListSize(){
		if(files.size() == 0) {
			fujianCountTV.setVisibility(View.GONE);
		} else {
			fujianCountTV.setVisibility(View.VISIBLE);
			fujianCountTV.setText(files.size()+"");
		}
	}



	/**
	 * 判断文件是否没有添加
	 * @param path
	 * @return
	 */
	private boolean isFileNotHas(String path){
		for (SendFile f : files) {
			if(f.getKind() == SendFile.FILE) {
				if (f.getFile().getAbsolutePath().equals(path)) {
					return false;
				}
			}
		}
		return true;
	}

	public void onAddReceiver(View v) {
		startActivityForResult(new Intent(context, SeletePageOneActivity.class),100);
	}

	// 检查表单
	public void onSend(View v) {

		if (SendNewNoticeActivity.positionMap != null) {
			if (SendNewNoticeActivity.positionMap.size() == 0) {
				ToastUtil.showMessage("未选择接收人");
				return;
			}
		}
		if (StringUtil.isBlank(themET.getEditableText().toString())) {
			ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.question_notice_send, null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					checkFuJian();
				}
			});
			buildAlert.setTitle("提示");
			buildAlert.show();
		} else {
			checkFuJian();
		}
	}

	public void checkFuJian() {
		if (contentET.getEditableText().toString().contains("附件") && files.size() == 0) {
			ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.question_notice_send1, null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendNotice();
				}
			});
			buildAlert.setTitle("提示");
			buildAlert.show();
		} else {
			sendNotice();
		}
	}

	/**
	 * 数据库找人
	 * @param toAddUsername2
	 */
	private void searcheFriend(String toAddUsername2) {
		Pattern p = Pattern.compile(regexStr);
		Matcher m = p.matcher(toAddUsername2);
		//搜索的不是汉字时  两个以上才可以搜索  不然数据量大比较卡
		if( !m.find() && toAddUsername2.length() == 1){
			return;
		}
		friendResults = ContactSqlManager.getContactByUsername(toAddUsername2);
		if(friendResults != null && friendResults.size() != 0){
			for (int i = 0; i < friendResults.size(); i++) {
				if("10089".equals(friendResults.get(i).getContactid())||"10088".equals(friendResults.get(i).getContactid())){
					friendResults.remove(i);
				}
			}
			receiverLV.setAdapter(new addContactAdapter(this));
			Tools.setListViewHeightBasedOnChildren(receiverLV);
			showReceiverPop();
		}else{
			friendResults = new ArrayList<ECContacts>();
			receiverLV.setAdapter(new addContactAdapter(this));
			disMissPop();
		}
	}

	public void showReceiverPop() {
		//TODO
		receiverLV.setVisibility(View.VISIBLE);
	}

	public void disMissPop() {
		receiverLV.setVisibility(View.GONE);

	}

	public class addContactAdapter extends BaseAdapter{
		private AsyncImageLoader imageLoader;
		private Bitmap bmp;
		public addContactAdapter(Activity context) {
			super();
			ImageCacheManager cacheMgr = new ImageCacheManager(context);
			imageLoader = new AsyncImageLoader(context, cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}
		@Override
		public int getCount() {
			return friendResults.size();
		}

		@Override
		public ECContacts getItem(int position) {
			return friendResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.list_search_friend_item, null);
				holder.avatar = (ImageView) convertView.findViewById(R.id.group_card_item_avatar_iv);
				holder.name = (TextView) convertView.findViewById(R.id.group_card_item_nick);
				holder.voipAccount = (TextView) convertView.findViewById(R.id.account);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			//将搜索关键字变色显示
			holder.name.setText(getItem(position).getNickname());
//			holder.voipAccount.setText(getItem(position).getContactid());
			bmp = imageLoader.loadBitmap(holder.avatar, ECApplication.getInstance().getAddress() + getItem(position).getRemark(), true);
			if(bmp == null) {
				holder.avatar.setImageResource(R.drawable.defult_head_img);
			} else {
				holder.avatar.setImageBitmap(bmp);
			}
			addListener(holder,position);
			return convertView;
		}

		private void addListener(Holder holder,final int position){

		}

		class Holder{
			private ImageView avatar;
			private TextView name;
			private TextView voipAccount;
		}
	}

	private ECProgressDialog progressDialog;

	//发通知
	public void sendNotice(){

		sendBT.setEnabled(false);
		map = new HashMap<String, ParameterValue>();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		map.put("subject", new ParameterValue(StringUtil.isBlank(themET.getEditableText().toString().trim())?"(无主题)":themET.getEditableText().toString().trim()));
		map.put("receiveUserId", new ParameterValue(formArry()));

		if (EDIT_MODE != SENDNEW) {
			map.put("attIds", new ParameterValue(formArry2()));
			map.put("sourceId", new ParameterValue(v3Notice.getId()));
			if(edited) {
				map.put("content", new ParameterValue(contentET.getEditableText().toString().trim()));
			} else {
				map.put("sourceContent", new ParameterValue(content.getContent()));
				map.put("content", new ParameterValue(contentET.getEditableText().toString().trim()));
				// 没编辑的时候  直接发送原文  并且附带 新内容
			}
		} else {
			map.put("content", new ParameterValue(contentET.getEditableText().toString().trim()));
		}

		progressDialog = new ECProgressDialog(this, "正在发送请求");
		progressDialog.setCancelable(false);
		progressDialog.show();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					for (SendFile f : files) {
						if(f.getKind() == SendFile.FILE) {
							fileList.add(f.getFile());
						}
					}
					if(fileList.size() == 0) {
						sendFlag = UrlUtil.sendNotice(ECApplication.getInstance().getAddress(),ECApplication.getInstance().getLoginMap(),map); // 发送请求
					} else {
						sendFlag = UrlUtil.sendNoticeFile(ECApplication.getInstance().getAddress(), fileList, ECApplication.getInstance().getLoginMap(), map, new FileUpLoadCallBack() {
							@Override
							public void upLoadProgress(final int fileCount, final int currentIndex, int currentProgress, final int allProgress) {

								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if(100 == allProgress) {
											progressDialog.setPressText("附件上传完成,正在发送");
										} else {
											progressDialog.setPressText("正在上传附件(" + (currentIndex + 1) + "/"+ fileCount + ") 总进度:" + allProgress + "%");
										}
									}
								},5);
							}
						}); // 发送请求
					}
					handler.postDelayed(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							if(sendFlag.contains("ok")) {
								ToastUtil.showMessage("已发送");
								finish();
							} else {
								ToastUtil.showMessage("发送失败");
							}
							sendBT.setEnabled(true);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					handler.postDelayed(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							ToastUtil.showMessage("错误");
							sendBT.setEnabled(true);
						}
					}, 5);
				}
			}
		}).start();
	}

	public String formArry() {
		String result = "";
		for (Map.Entry<String, Node> entry : SendNewNoticeActivity.positionMap.entrySet()) {
			result += entry.getKey() + ",";
		}
		return result;
	}

	public String formArry1() {
		String result = "";
		if(SendNewNoticeActivity.positionMap.size() == 0) {
			return result;
		}
		for (Map.Entry<String, Node> entry : SendNewNoticeActivity.positionMap.entrySet()) {
			result += entry.getValue().getName() + "、";
		}
		result = result.substring(0, result.lastIndexOf("、"));
		return result;
	}

	public String formArry2() {
		String result = "";
		for (SendFile f : files) {
			if(f.getKind() == SendFile.ATTACHMENT) {
				result += (f.getAttachment().getId() + ",");
			}
		}
		return result;
	}

	public List<Node> formArry3() {
		List<Node> nodes = new ArrayList<Node>();
		if(SendNewNoticeActivity.positionMap.size() == 0) {
			return nodes;
		}
		for (Map.Entry<String, Node> entry : SendNewNoticeActivity.positionMap.entrySet()) {
			nodes.add(entry.getValue());
		}
		return nodes;
	}


	@Override
	protected void onDestroy() {
		SeletePageOneActivity.parentPositionMap.clear();
		SendNewNoticeActivity.positionMap.clear();
		SeletePageOneActivity.positionMap_P.clear();
		SeletePageOneActivity.positionMap_S.clear();
		SeletePageOneActivity.positionMap_T.clear();
		SeletePageOneActivity.positionMap_G.clear();
		files.clear();
		files = null;
		super.onDestroy();
	}

	/**
	 * 退出提示
	 */
	private void showTips() {
		ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.question_notice, null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		buildAlert.setTitle("放弃当前编辑");
		buildAlert.show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showTips();
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_DEL && event.getRepeatCount() == 0){
			if (receiverET.isFocused()) {
//        		ToastUtil.showMessage("退格");
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBack(View v) {
		showTips();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_sendnew;
	}
}
