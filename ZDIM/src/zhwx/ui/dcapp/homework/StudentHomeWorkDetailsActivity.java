package zhwx.ui.dcapp.homework;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.util.file.IntentUtilForUikit;

import java.io.File;
import java.util.HashMap;

import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DownloadAsyncTask;
import zhwx.common.util.FileUtils;
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.NumberProgressBar;
import zhwx.common.view.dialog.ECProgressDialog;


public class StudentHomeWorkDetailsActivity extends BaseActivity {
	
	private Activity context;
	
	private String detailsjson;
	
	private String id;
	
	private TextView subjectview, minutes, contentTV, calsses,statusTV,imageSizeTV,titleTV;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private HomeWorkDetail homeWorkDetails;
	
	private ECProgressDialog progressDialog;
	
	private FrameLayout top_bar1;
	
	private RelativeLayout fileLay;
	
	private Button btnSubmit,checkResultBT;
	
	@Override
	protected int getLayoutId() {return R.layout.activity_studenthomework_detail;}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		progressDialog = new ECProgressDialog(context);
		id = getIntent().getStringExtra("id");
		initview();
		detailsdata();
	}

	public void initview() {
		top_bar1 = (FrameLayout) findViewById(R.id.top_bar1);
		setImmerseLayout(top_bar1);
		subjectview = (TextView) findViewById(R.id.subjectview);
		minutes = (TextView) findViewById(R.id.minutes);
		contentTV = (TextView) findViewById(R.id.matter);
		titleTV = (TextView) findViewById(R.id.titleTV);
		calsses = (TextView) findViewById(R.id.calsses);
		statusTV = (TextView) findViewById(R.id.statusTV);
		imageSizeTV = (TextView) findViewById(R.id.imageSizeTV);
		fileLay = (RelativeLayout) findViewById(R.id.fileLay);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		checkResultBT = (Button) findViewById(R.id.checkResultBT);
	}

	public void detailsdata() {
		progressDialog.setPressText("正在获取数据");
		progressDialog.show();
		new ProgressThreadWrap(this, new RunnableWrap() {

			@Override
			public void run() {
				try {
					map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
					map.put("studentWorkId", new ParameterValue(id));
					detailsjson = UrlUtil.getHomeWorkDetail(ECApplication.getInstance().getV3Address(), map);

					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							setdata(detailsjson);
						}
					}, 5);
				} catch (Exception e) {
				}

			}
		}).start();	
	}

	public void setdata(String detailsjson) {
		progressDialog.dismiss();
		final Gson gson = new Gson();
		homeWorkDetails = gson.fromJson(detailsjson,HomeWorkDetail.class);
		contentTV.setText(homeWorkDetails.getContent());
		titleTV.setText("作业名称：" + homeWorkDetails.getTitle());
		calsses.setText("作业对象：" + homeWorkDetails.getWorkEclass());
		minutes.setText("提交截止至：" + homeWorkDetails.getEndTime());
		subjectview.setText("学科：" + homeWorkDetails.getCourseName());
		
		if (HomeWork.STATE_NO.equals(homeWorkDetails.getStatus())) {
			statusTV.setTextColor(Color.parseColor("#FF0000"));
			btnSubmit.setVisibility(View.VISIBLE);
			checkResultBT.setVisibility(View.GONE);
		} else if (HomeWork.STATE_SUBMIT.equals(homeWorkDetails.getStatus())){
			statusTV.setTextColor(Color.parseColor("#36BFB5"));
			btnSubmit.setVisibility(View.GONE);
			checkResultBT.setVisibility(View.VISIBLE);
		} else if (HomeWork.STATE_OVER.equals(homeWorkDetails.getStatus())){
			statusTV.setTextColor(Color.parseColor("#999999"));
			btnSubmit.setVisibility(View.GONE);
			checkResultBT.setVisibility(View.GONE);
		}
		statusTV.setText(homeWorkDetails.getStatusName());
		imageSizeTV.setText(homeWorkDetails.getFileSize());
		if (StringUtil.isNotBlank(homeWorkDetails.getAttachmentUrl())) {
			fileLay.setVisibility(View.VISIBLE);
			final NumberProgressBar bar = (NumberProgressBar) findViewById(R.id.amd_progressBar);
			final ImageView imageView = (ImageView) findViewById(R.id.amd_image_download);
			final TextView title = (TextView) findViewById(R.id.amd_item_title);
			title.setText(homeWorkDetails.getAttachmentName());
			ImageView iconIV = (ImageView) findViewById(R.id.fileKindIV);
			iconIV.setImageResource(FileUtils.getFileIcon(homeWorkDetails.getAttachmentName()));
			File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				idr = context.getFilesDir();
			}
			
			File file = new File(idr, (String) title.getText());
			if (file.exists()) {
				imageView.setTag(R.drawable.amd_list_item_open);
				imageView.setTag(R.drawable.amd_list_item_open, file);
				imageView.setImageResource(R.drawable.amd_list_item_open);
			} else {
				imageView.setImageResource(R.drawable.amd_list_item_download);
			}
			fileLay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (imageView.getTag() == null) {// 下载处理
						bar.setVisibility(View.VISIBLE);
						DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.DownloadResponser() {
							@Override
							public void predownload() {
								imageView.setImageResource(R.drawable.amd_list_item_pause);
								imageView.setTag(R.drawable.amd_list_item_pause);
							}

							@Override
							public void downloading(int progress, int position) {
								bar.setProgress(progress);
							}

							@Override
							public void downloaded(File file, int position) {
								bar.setVisibility(View.INVISIBLE);
								imageView.setImageResource(R.drawable.amd_list_item_open);
								imageView.setTag(R.drawable.amd_list_item_open);
								imageView.setTag(R.drawable.amd_list_item_open, file);
							}

							@Override
							public void canceled(int position) {
								imageView.setImageResource(R.drawable.amd_list_item_download);
								imageView.setTag(null);
								bar.setVisibility(View.INVISIBLE);

							}
						}, context);
						downloadAsyncTask.execute(UrlUtil.getImgUrl(ECApplication.getInstance().getV3Address() + homeWorkDetails.getAttachmentUrl(),  ECApplication.getInstance().getV3LoginMap()), "aaa", 0 + "", title.getText().toString());
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

								}else if(Constant.ATTACHMENT_BMP.equals(lastName)){
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
		}
	}
	
	/**
	 * 提交答案
	 * @param v
	 */
	public void onSubmet(View v) {
		Intent intent = new Intent(context, SubmitHomeWorkActivity.class);
		intent.putExtra("studentWorkId", homeWorkDetails.getStudentWorkId());
		startActivityForResult(intent, 110);
	}
	
	/**
	 * 查看答案
	 * @param v
	 */
	public void onCheckResult(View v) {
		Intent intent = new Intent(context, ResultDetailActivity.class);
		intent.putExtra("result", homeWorkDetails.getResult());
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 999) {
			setResult(999);
			finish();
		}
		
	}
}
