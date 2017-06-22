package zhwx.ui.dcapp.repairs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.FileUpLoadCallBack;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.repairs.model.RequestFeedBackItem;

/**
 * @author Li.xin @ 中电和讯
 * 报修人反馈
 */
public class RequestFeedBackActivity extends BaseActivity implements View.OnClickListener {

	private Activity context;

	private String repairId;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private RequestFeedBackItem requestFeedBackItem;

	private RadioGroup repairStatusRG;

	private RatingBar suduRB,taiduRB,jishuBar,zhiliangRB,allRB;

	private String isFixed = "1";

	private EditText suggestET;

	private GridView feedBackFileGV;

	private List<PhotoModel> nowPhotos = new ArrayList<PhotoModel>();

	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();

	public static final int MAX_IMG_COUNT = 6; // 选择图数量上限

	private List<File> sendFiles = new ArrayList<File>();

	private ImageGVAdapter imgAdapter;

	private HashMap<String, ParameterValue> loginMap;

	private String sendFlag;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"报修反馈评价", this);
		repairId = getIntent().getStringExtra("repairId");
		initView();
		getData();
	}

	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在加载数据");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("reportId",new ParameterValue(repairId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String json = UrlUtil.getFeedbackContentList(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() { 
							refreshData(json);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mPostingdialog.dismiss();
						}
					}, 5);
				}
			}
		}).start();
	}
	
	private void refreshData(String json) {
		System.out.println(json);
		if (!json.contains("<html>")) {
			requestFeedBackItem = new Gson().fromJson(json, RequestFeedBackItem.class);
			suduRB.setRating(Float.parseFloat(requestFeedBackItem.getSpeedStr()));
			taiduRB.setRating(Float.parseFloat(requestFeedBackItem.getAttitudeStr()));
			jishuBar.setRating(Float.parseFloat(requestFeedBackItem.getTechnicalLevelStr()));
			zhiliangRB.setRating(Float.parseFloat(requestFeedBackItem.getQualityStr()));
			allRB.setRating(Float.parseFloat(requestFeedBackItem.getScoreStr()));
			suggestET.setText(StringUtil.isNotBlank(requestFeedBackItem.getSuggestion())?requestFeedBackItem.getSuggestion():"");
			if (requestFeedBackItem.getRepairFlag() != null && "0".equals(requestFeedBackItem.getRepairFlag())) {
				RadioButton radioButton = (RadioButton)findViewById(R.id.cantRepairRb);
				radioButton.setChecked(true);
			}
			mPostingdialog.dismiss();
		}
	}
	
	@SuppressLint("NewApi")
	private void initView() {
		repairStatusRG = (RadioGroup) findViewById(R.id.repairStatusRG);
		repairStatusRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.repairedRb:
						isFixed = "1";
						break;
					case R.id.cantRepairRb:
						isFixed = "0";
						break;
				}
			}
		});
		suggestET = (EditText) findViewById(R.id.suggestET);
		jishuBar = (RatingBar) findViewById(R.id.jishuBar);
		zhiliangRB = (RatingBar) findViewById(R.id.zhiliangRB);
		allRB = (RatingBar) findViewById(R.id.allRB);
		taiduRB = (RatingBar) findViewById(R.id.taiduRB);
		suduRB = (RatingBar) findViewById(R.id.suduRB);
		feedBackFileGV = (GridView) findViewById(R.id.feedBackFileGV);
		nowPhotos.add(null);
		imgAdapter = new ImageGVAdapter();
		feedBackFileGV.setAdapter(imgAdapter);
		Tools.setGridViewHeightBasedOnChildren3(feedBackFileGV);
	}

	public void onSend(View v) {
		mPostingdialog = new ECProgressDialog(this, "正在操作");
		mPostingdialog.show();
		loginMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map = new HashMap<String, ParameterValue>();
		map.put("reportId", new ParameterValue(repairId)); //报修单Id
		map.put("attitudeStr", new ParameterValue(taiduRB.getRating()+"")); //服务态度
		map.put("qualityStr", new ParameterValue(zhiliangRB.getRating()+"")); //维修质量
		map.put("scoreStr", new ParameterValue(allRB.getRating()+"")); //整体评价
		map.put("speedStr", new ParameterValue(suduRB.getRating()+""));//响应速度
		map.put("technicalLevelStr", new ParameterValue(jishuBar.getRating()+""));//技术水平
		map.put("repairFlag", new ParameterValue(isFixed));//是否修好
		map.put("suggestion", new ParameterValue(suggestET.getEditableText().toString()));//意见

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
//					final String sendFlag = UrlUtil.saveFeedBack(ECApplication.getInstance().getV3Address(), map);
					if(sendFiles.size() == 0) {
						loginMap.putAll(map);
						sendFlag = UrlUtil.saveFeedBack(ECApplication.getInstance().getV3Address(),loginMap);
					} else {
						sendFlag = UrlUtil.saveFeedBack(ECApplication.getInstance().getV3Address(), sendFiles, loginMap, map, new FileUpLoadCallBack() {
							@Override
							public void upLoadProgress(final int fileCount, final int currentIndex, int currentProgress, final int allProgress) {
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if(100 == allProgress) {
											mPostingdialog.setPressText("附件上传完成,正在发送");
										} else {
											mPostingdialog.setPressText("正在上传附件(" + (currentIndex + 1) + "/"+ fileCount + ") 总进度:" + allProgress + "%");
										}
									}
								},5);
							}
						});
					}
					handler.postDelayed(new Runnable() {
						public void run() {
							if (sendFlag.contains("ok")) {
								ToastUtil.showMessage("评价已提交");
								setResult(110);
								finish();
								mPostingdialog.dismiss();
							} else {
								ToastUtil.showMessage("提交出错");
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mPostingdialog.dismiss();
						}
					}, 5);
				}
			}
		}).start();
	}


	/**
	 * 已选择图片适配
	 * @author lenovo
	 */
	class ImageGVAdapter extends BaseAdapter {

		public ImageGVAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return nowPhotos.size();
		}

		@Override
		public PhotoModel getItem(int position) {
			return nowPhotos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.gv_item_image,null);
				holder.imageGV = (ImageView) convertView.findViewById(R.id.imageGV);
				holder.delBT = (ImageView) convertView.findViewById(R.id.delBT);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (nowPhotos.get(position) == null) {
				holder.imageGV.setImageResource(R.drawable.icon_rm_addpic);
				holder.delBT.setVisibility(View.INVISIBLE);
			} else {
				if (nowPhotos.get(position) != null) {
					holder.imageGV.setImageBitmap(nowBmp.get(position));
					holder.delBT.setVisibility(View.VISIBLE);
				}
			}
			addListener(holder, position);
			return convertView;
		}

		private void addListener(Holder holder, final int position) {
			holder.delBT.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == nowPhotos.size() - 1) {

					} else {
						remove(position);
						for (int i = 0; i < nowPhotos.size(); i++) {
							if (nowPhotos.get(i) == null) {
								nowPhotos.remove(i);
							}
						}
						nowPhotos.add(null);
						imgAdapter.notifyDataSetChanged();
					}
				}
			});
			holder.imageGV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {

					if (position == nowPhotos.size() - 1) {
						if (nowPhotos.get(nowPhotos.size() - 1) == null) {
							Intent intent = new Intent(context,
									PhotoSelectorActivity.class);
							intent.putExtra("canSelectCount", MAX_IMG_COUNT + 1 - nowPhotos.size());
							startActivityForResult(intent, 0);
						} else {
							remove(position);
							for (int i = 0; i < nowPhotos.size(); i++) {
								if (nowPhotos.get(i) == null) {
									nowPhotos.remove(i);
								}
							}
							nowPhotos.add(null);
							imgAdapter.notifyDataSetChanged();
						}
					} else {
						List<PhotoModel> nowPhoto = new ArrayList<PhotoModel>();
						nowPhoto.add(nowPhotos.get(position));
						Bundle bundle = new Bundle();
						bundle.putSerializable("photos",(Serializable) nowPhoto);
						CommonUtils.launchActivity(context,PhotoPreviewActivity.class, bundle);
					}
				}
			});
		}

		class Holder {
			private ImageView imageGV;
			private ImageView delBT;
		}
	}

	public void remove(int position) {
		nowPhotos.remove(position);
		nowBmp.remove(position);
		sendFiles.remove(position);
		Tools.setGridViewHeightBasedOnChildren3(feedBackFileGV);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras().getSerializable("photos");
				for (int i = 0; i < nowPhotos.size(); i++) {
					if (nowPhotos.get(i) == null) {
						nowPhotos.remove(i);
					}
				}
				for (PhotoModel photoModel : photos) {
					nowPhotos.add(photoModel);
				}
				if (nowPhotos.size() < MAX_IMG_COUNT) {
					nowPhotos.add(null);
				}
				if (nowPhotos == null || nowPhotos.isEmpty())
					return;
				Compress();
				imgAdapter.notifyDataSetChanged();
				Tools.setGridViewHeightBasedOnChildren3(feedBackFileGV);
			}
		}
	}

	// 压缩图片 并存储
	public void Compress() {
		nowBmp.clear();
		sendFiles.clear();
		try {
			for (int i = 0; i < nowPhotos.size(); i++) {
				if (nowPhotos.get(i) != null) {
					File f = new File(nowPhotos.get(i).getOriginalPath());
					File fs = new File(PictureUtil.getAlbumDir(), "small_"+ f.getName());
					Bitmap bm = PictureUtil.getSmallBitmap(nowPhotos.get(i).getOriginalPath());
					if (bm != null) {
						FileOutputStream fos = new FileOutputStream(fs);
						bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
						nowBmp.add(bm);
						sendFiles.add(fs);
					}
				} else {
					nowBmp.add(null);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
		}
	}
	@Override
	protected int getLayoutId() {
		return R.layout.activity_rm_request_feedback;
	}
}
