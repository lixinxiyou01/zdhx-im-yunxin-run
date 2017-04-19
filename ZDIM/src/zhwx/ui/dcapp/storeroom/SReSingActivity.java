package zhwx.ui.dcapp.storeroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import zhwx.common.util.DateUtil;
import zhwx.common.util.FileUpLoadCallBack;
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.assets.model.Granted;
import zhwx.ui.dcapp.assets.view.WaterImageUtil;
import zhwx.ui.dcapp.assets.view.pancel.PicelBoradActivity;
import zhwx.ui.dcapp.storeroom.model.ReSignDetail;

/**   
 * @author Li.xin @ zdhx
 * @date 2017-4-11 08:56:26
 */
public class SReSingActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
	private ReSignDetail model;
	private TextView userNameTV; //使用人
	private TextView departmentTV; //使用人
	private ListView assetLV;
	private GridView circleGV;
	
	private ImageGVAdapter adapter;	
	
	public static Bitmap sourBitmap;
	
	private List<PhotoModel> nowPhotos = new ArrayList<PhotoModel>();

	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();
	
	private List<File> sendFiles = new ArrayList<File>();
	
	public static final int MAX_IMG_COUNT = 1; // 选择图数量上限
	
	private String json;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private Granted entity;

	private String id;

	private String infoJson;
	
	private ImageView signIV;
	
	private File sdCard = android.os.Environment.getExternalStorageDirectory();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "提交","补签", this);
		getActivityLayoutView().setBackgroundColor(Color.parseColor("#ffffff"));
		context = this;
		entity = (Granted) getIntent().getSerializableExtra("model");
		id = getIntent().getStringExtra("id");
		getData();
	}
	
	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getReSignDetail(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (json.contains("<html>")) {
								ToastUtil.showMessage("数据异常");
								mPostingdialog.dismiss();
								return;
							}
							System.out.println(json);
							model = new Gson().fromJson(json, ReSignDetail.class);
							initView();
							mPostingdialog.dismiss();
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
	 * 
	 */
	private void initView() {
		signIV = (ImageView) findViewById(R.id.signIV);
		signIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, PicelBoradActivity.class),111);
			}
		});
		assetLV = (ListView) findViewById(R.id.assetLV);
		assetLV.setAdapter(new OrderListAdapter());
		Tools.setListViewHeightBasedOnChildren(assetLV);
		userNameTV = (TextView) findViewById(R.id.userNameTV);
		userNameTV.setText(model.getUserName());
		departmentTV = (TextView) findViewById(R.id.departmentTV);
		departmentTV.setText(model.getGrantTime());
		circleGV = (GridView) findViewById(R.id.circleGV);
		nowPhotos.add(null);
		adapter = new ImageGVAdapter();
		circleGV.setAdapter(adapter);
		Tools.setGridViewHeightBasedOnChildren4(circleGV);
	}
	
	/**
	  * @param :发放记录id；locationId：物理位置id；applicationRecordId：申请记录id
	  * ；userId：领用人id；departmentId：领用部门id；assetIds：资产ids（用，分割）
	 * 	移动端确认发放   
	 */
	private void grant(){
		if(sendFiles.size() == 0) {
			ToastUtil.showMessage("请签字");
			return;
		}
		final HashMap<String, ParameterValue> loginMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		mPostingdialog = new ECProgressDialog(this, "正在提交");
		mPostingdialog.show();
		map = new HashMap<String, ParameterValue>();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					infoJson = UrlUtil.saveReSign(ECApplication.getInstance().getV3Address(), sendFiles, loginMap, map, new FileUpLoadCallBack() {
						@Override
						public void upLoadProgress(int fileCount, int currentIndex, int currentProgress,int allProgress) {

						}
					});
					handler.postDelayed(new Runnable() {
						public void run() {
							System.out.println(infoJson);
							if ("ok".equals(infoJson.trim())) {
								ToastUtil.showMessage("补签成功！");
								setResult(120);
								finish();
							} else {
								
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
	
	public class OrderListAdapter extends BaseAdapter {
		
		public OrderListAdapter(Context context, List<AllAssets> list,
				int listFlag) {
			super();
		}

		public OrderListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return model.getGoodsList().size();
		}

		@Override
		public ReSignDetail.GoodsListBean getItem(int position) {
			return model.getGoodsList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_as_grantassets, null);
				holder = new ViewHolder();
				holder.asNameTV = (TextView) convertView.findViewById(R.id.asNameTV);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.asNameTV.setText((position + 1) + ". " + getItem(position).getGoodsName() + " * " + getItem(position).getGoodsCount());
			return convertView;
		}
		private class ViewHolder {
			private TextView asNameTV;
		}
	}
	
	
	/**
	 * 已选择图片适配
	 * 
	 * @author lenovo
	 * 
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
				convertView = View.inflate(context, R.layout.gv_item_image,
						null);
				holder.imageGV = (ImageView) convertView
						.findViewById(R.id.imageGV);
				holder.delBT = (ImageView) convertView.findViewById(R.id.delBT);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (nowPhotos.get(position) == null) {
				holder.imageGV.setImageResource(R.drawable.btn_add_pic);
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
			holder.delBT.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					remove(position);
					for (int i = 0; i < nowPhotos.size(); i++) {
						if (nowPhotos.get(i) == null) {
							nowPhotos.remove(i);
						}
					}
					nowPhotos.add(null);
					adapter.notifyDataSetChanged();
				
				}
			});
			holder.imageGV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {

					if (position == nowPhotos.size() - 1) {
						if (nowPhotos.get(nowPhotos.size() - 1) == null) {
							Intent intent = new Intent(context,PhotoSelectorActivity.class);
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
							adapter.notifyDataSetChanged();
						}
					} else {
						List<PhotoModel> nowPhoto = new ArrayList<PhotoModel>();
						nowPhoto.add(nowPhotos.get(position));
						Bundle bundle = new Bundle();
						bundle.putSerializable("photos",
								(Serializable) nowPhoto);
						CommonUtils.launchActivity(context,
								PhotoPreviewActivity.class, bundle);
					}
				}
			});
		}

		class Holder {
			private ImageView imageGV;
			private ImageView delBT;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
						.getSerializable("photos");
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
				adapter.notifyDataSetChanged();
			}
		}
		
		if (requestCode == 111) {
			if (sourBitmap != null) {
				Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_cjl);
				Bitmap watermarkBitmap = WaterImageUtil.createWaterMaskLeftTop(context,sourBitmap, waterBitmap,0,0);
//				Bitmap textBitmap = WaterImageUtil.drawTextToRightBottom(this, watermarkBitmap, "资产管理 " + DateUtil.getCurrDateSecondString(),25, Color.WHITE,10,10);
//				Bitmap textBitmap = WaterImageUtil.drawTextToCenter(this, watermarkBitmap, "资产管理 " + DateUtil.getCurrDateSecondString(),35, Color.WHITE);
				Bitmap textBitmap = WaterImageUtil.drawTextToCenter(this, watermarkBitmap, model.getUserName() + " " + DateUtil.getCurrDateSecondString() + "#低值易耗品",25, Color.WHITE);
				File tempFile = new File(sdCard, "sing.jpg");
				sendFiles.clear();
				sendFiles.add(tempFile);
				IMUtils.saveBitmapToLocal(tempFile, textBitmap);
				signIV.setImageBitmap(textBitmap);
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
					File fs = new File(PictureUtil.getAlbumDir(), "small_"
							+ f.getName());
					Bitmap bm = PictureUtil.getSmallBitmap(nowPhotos.get(i)
							.getOriginalPath());
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
	protected void onDestroy() {
		super.onDestroy();
		if (adapter != null) {
			adapter = null;
		}
		nowPhotos = null;
		System.gc();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			grant();
			break;
		}
	}
	
	public void remove(int position) {
		nowPhotos.remove(position);
		nowBmp.remove(position);
		sendFiles.remove(position);
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_sm_resing;
	}
}
