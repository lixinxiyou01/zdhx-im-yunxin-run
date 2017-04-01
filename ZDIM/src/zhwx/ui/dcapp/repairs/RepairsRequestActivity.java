package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.tagview.Tag;
import zhwx.common.view.tagview.TagListView;
import zhwx.ui.dcapp.assets.adapter.IdAndNameSpinnerAdapter;
import zhwx.ui.dcapp.repairs.model.FaultList;

/**
 * @author Li.xin @ 中电和讯
 */
public class RepairsRequestActivity extends BaseActivity implements View.OnClickListener {
	
	private Activity context;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;

	private List<PhotoModel> nowPhotos = new ArrayList<PhotoModel>();

	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();

	public static final int MAX_IMG_COUNT = 6; // 选择图数量上限

	private List<File> sendFiles = new ArrayList<File>();

	private GridView repairFileGV;

	private ImageGVAdapter imgAdapter;

	private HashMap<String, ParameterValue> loginMap;

	private String infoJson;

	private String orderFlag;

	private String goodsId;

	private String goodsName;

    private Animation shake; //表单必填项抖动

	private TextView deviceNameTV,requsetUserTV;

	private String indexJson;

	private FaultList fList;

	private TagListView commentFaultGV;

	private final List<Tag> mTags = new ArrayList<Tag>();

	private Spinner schoolSP,buildSP,floorSP,classSP;

	private EditText faultDescriptionET,mobileET;

	private String roomId = "";

	private TextView faultDescriptionTV,phoneTV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"报修单", this);
		goodsId = getIntent().getStringExtra("id");
		goodsName = getIntent().getStringExtra("name");
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);//加载动画资源文件
		initView();
		getIndex();
	}

	private void initView() {
		deviceNameTV = (TextView) findViewById(R.id.deviceNameTV);
		deviceNameTV.setText(goodsName);
		requsetUserTV = (TextView) findViewById(R.id.requsetUserTV);
		requsetUserTV.setText(ECApplication.getInstance().getCurrentIMUser().getName());
		repairFileGV = (GridView) findViewById(R.id.repairFileGV);
		nowPhotos.add(null);
		imgAdapter = new ImageGVAdapter();
		repairFileGV.setAdapter(imgAdapter);
		Tools.setGridViewHeightBasedOnChildren3(repairFileGV);
		commentFaultGV = (TagListView) findViewById(R.id.commentFaultGV);
		schoolSP = (Spinner) findViewById(R.id.schoolSP);
		buildSP = (Spinner) findViewById(R.id.buildSP);
		floorSP = (Spinner) findViewById(R.id.floorSP);
		classSP = (Spinner) findViewById(R.id.classSP);
		faultDescriptionET = (EditText) findViewById(R.id.faultDescriptionET);
		mobileET = (EditText) findViewById(R.id.mobileET);
		faultDescriptionTV = (TextView) findViewById(R.id.faultDescriptionTV);
		phoneTV = (TextView) findViewById(R.id.phoneTV);
		mobileET.setText(ECApplication.getInstance().getCurrentIMUser().getMobileNum());

	}

	private void getIndex(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("goodsId", new ParameterValue(goodsId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getMalfunctionPlaceList(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(indexJson);
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

	private void refreshData1(String indexJson) {
		System.out.println(indexJson);
		if (indexJson.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		fList = new Gson().fromJson(indexJson, FaultList.class);
//		for (FaultList.FaultListBean faultListBean : fList.getFaultList()) {
//			Tag tag = new Tag();
//			tag.setId(faultListBean.getId());
//			tag.setChecked(true);
//			tag.setTitle(faultListBean.getName());
//			mTags.add(tag);
//		}
//		commentFaultGV.setTags(mTags);


		schoolSP.setAdapter(new IdAndNameSpinnerAdapter(context,fList.getSchoolList()));
		if (StringUtil.isNotBlank(fList.getCurrSchoolId())) {
			for (int i = 0; i < fList.getSchoolList().size(); i++) {
				if (fList.getCurrSchoolId().equals(fList.getSchoolList().get(i).getId())) {
					schoolSP.setSelection(i);
				}
			}
		}
		schoolSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, final int schoolPosition, long id) {
				buildSP.setAdapter(new IdAndNameSpinnerAdapter(context,fList.getSchoolList().get(schoolPosition).getBuildingList()));
				buildSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, final int bPosition, long id) {
						floorSP.setAdapter(new IdAndNameSpinnerAdapter(context,fList.getSchoolList().get(schoolPosition)
								.getBuildingList().get(bPosition).getFloorList()));
						floorSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent, View view, final int fPosition, long id) {
								classSP.setAdapter(new IdAndNameSpinnerAdapter(context,fList.getSchoolList().get(schoolPosition)
										.getBuildingList().get(bPosition).getFloorList().get(fPosition).getRoomList()));
								classSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
									@Override
									public void onItemSelected(AdapterView<?> parent, View view, int rPosition, long id) {
										roomId = fList.getSchoolList().get(schoolPosition).getBuildingList().get(bPosition).getFloorList()
												.get(fPosition).getRoomList().get(rPosition).getId();
									}

									@Override
									public void onNothingSelected(AdapterView<?> parent) {

									}
								});
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {

							}
						});
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		mPostingdialog.dismiss();
	}

	//提交
	public void onOrder(View v) {
		
		if(!checkOrderList()) {
			return;
		}
		mPostingdialog = new ECProgressDialog(this, "正在提交报修单");
		mPostingdialog.show();
		loginMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map = new HashMap<String, ParameterValue>();
		map.put("malfunctionDescribe",new ParameterValue(faultDescriptionET.getEditableText().toString())); //故障描述
		map.put("malfunctionPlaceId",new ParameterValue(roomId)); 											//故障地点
		map.put("telNumber",new ParameterValue(mobileET.getEditableText().toString())); 					//电话
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));		//报修人Id
		String malfunctionIds = goodsId + "," + formArry();
		map.put("malfunctionIds",new ParameterValue(malfunctionIds)); 										//维修物,故障Ids
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					if(sendFiles.size() == 0) {
						loginMap.putAll(map);
						orderFlag = UrlUtil.submitRepairApply(ECApplication.getInstance().getV3Address(),loginMap);
					} else {
						orderFlag = UrlUtil.submitRepairApply(ECApplication.getInstance().getV3Address(), sendFiles,loginMap,map);
					}
					handler.postDelayed(new Runnable() {
						public void run() {
							if (orderFlag.contains("ok")) {
								ToastUtil.showMessage("报修单已提交");
								setResult(886);
								finish();
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("提交出错，请重试");
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
	
	public boolean checkOrderList() {
		boolean pass = true;
		if (StringUtil.isBlank(faultDescriptionET.getEditableText().toString())) {
			faultDescriptionTV.startAnimation(shake);
			pass = false;
		}
		if (StringUtil.isBlank(mobileET.getEditableText().toString())) {
			//验证手机号
			phoneTV.startAnimation(shake);
			pass = false;
		} else {
			//验证手机号
			if (!IMUtils.isMobile(mobileET.getEditableText().toString())) {
				ToastUtil.showMessage("手机号不正确");
				phoneTV.startAnimation(shake);
				pass = false;
			}
		}
		return pass;
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
		Tools.setGridViewHeightBasedOnChildren3(repairFileGV);
	}

	public String formArry() {
		String result = "";
//		for (FaultList.FaultListBean f : fList.getFaultList()) {
//			if (f.isChecked()) {
//				result += (f.getId() + ",");
//			}
//		}

		for (Tag tag : commentFaultGV.getTags()) {
			if (!tag.isChecked()) {
				System.out.println(tag.getTitle() + "::" + tag.getId());
				result += (tag.getId() + ",");
			}
		}
		return result;
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
				Tools.setGridViewHeightBasedOnChildren3(repairFileGV);
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
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 退出提示
	 */
	private void showTips() {
		 ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.question_repair, null, new DialogInterface.OnClickListener() {
			 @Override
                public void onClick(DialogInterface dialog, int which) {
				 	finish();
                }
            });
            buildAlert.setTitle("放弃本次编辑");
            buildAlert.show();
    }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showTips();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
		return R.layout.activity_repair_request;
	}
}
