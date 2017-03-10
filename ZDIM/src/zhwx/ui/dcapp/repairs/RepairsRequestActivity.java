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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;

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

	public static final int MAX_IMG_COUNT = 9; // 选择图数量上限

	private List<File> sendFiles = new ArrayList<File>();

	private GridView repairFileGV;

	private ImageGVAdapter adapter;
	
	private String infoJson;
	
	private String orderFlag;

	private String goodsId;

	private String goodsName;

    private Animation shake; //表单必填项抖动
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"报修单", this);
		goodsId = getIntent().getStringExtra("goodsId");
		goodsName = getIntent().getStringExtra("goodsName");
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);//加载动画资源文件
		initView();
	}

	private void initView() {
		repairFileGV = (GridView) findViewById(R.id.repairFileGV);
		nowPhotos.add(null);
		adapter = new ImageGVAdapter();
		repairFileGV.setAdapter(adapter);
		Tools.setGridViewHeightBasedOnChildren(repairFileGV);
	}

	//提交
	public void onOrder(View v) {
		
		mPostingdialog = new ECProgressDialog(this, "正在提交报修单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					orderFlag = UrlUtil.saveOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (orderFlag.contains("ok")) {
								ToastUtil.showMessage("报修单已提交");
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
	
//	public boolean checkOrderList() {
//		boolean pass = true;
////		dateTV,timeTV,addressTV,userCountTV;
//		if (StringUtil.isBlank(useDateET.getText().toString())) {
//			dateTV.startAnimation(shake);
//			pass = false;
//		}
//		if (StringUtil.isBlank(arriveTimeET.getText().toString())) {
//			timeTV.startAnimation(shake);
//			pass = false;
//		}
//		if (StringUtil.isBlank(addressET.getText().toString())) {
//			addressTV.startAnimation(shake);
//			pass = false;
//		}
//		if (StringUtil.isBlank(userCountET.getText().toString())) {
//			userCountTV.startAnimation(shake);
//			pass = false;
//		}
//
//		if (isBack) {
//			if (StringUtil.isBlank(backUseDateET.getText().toString())) {
//				backDateTV.startAnimation(shake);
//				pass = false;
//			}
//			if (StringUtil.isBlank(backArriveTimeET.getText().toString())) {
//				backTimeTV.startAnimation(shake);
//				pass = false;
//			}
//			if (StringUtil.isBlank(backAddressET.getText().toString())) {
//				backAddressTV.startAnimation(shake);
//				pass = false;
//			}
//			if (StringUtil.isBlank(backUserCountET.getText().toString())) {
//				backCountTV.startAnimation(shake);
//				pass = false;
//			}
//		}
//		return pass;
//	}

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
						adapter.notifyDataSetChanged();
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
							intent.putExtra("canSelectCount", MAX_IMG_COUNT + 1
									- nowPhotos.size());
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

	public void remove(int position) {
		nowPhotos.remove(position);
		nowBmp.remove(position);
		sendFiles.remove(position);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		
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
