package zhwx.ui.circle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.UserClass;
import zhwx.common.util.InputTools;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;

public class SendNewCircleActivity extends BaseActivity {

	private Activity context;

	private EditText circleET;

	private GridView circleGV;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 1212) {
				// TODO
			}
			return false;
		}
	});

	private List<PhotoModel> nowPhotos = new ArrayList<PhotoModel>();

	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();

	private List<File> sendFiles = new ArrayList<File>();

	private ImageGVAdapter adapter;

	private Bitmap bitmap;

	public static final int MAX_IMG_COUNT = 9; // 选择图数量上限

	private HashMap<String, ParameterValue> map;

	private String sendFlag;

	private RelativeLayout selectClassBT;

	private List<UserClass> userClasses;

	private TextView classTV;

	private String circleFlag = "school";

	private String departmentId = null;

	private Button sendNoticeBT;
	
	private FrameLayout top_bar;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		userClasses = (List<UserClass>) getIntent().getSerializableExtra(
				"classes");
		circleFlag = getIntent().getStringExtra("circleFlag");
		if(circleFlag == null){
			circleFlag = "school";
		}
		initView();
		setImmerseLayout(top_bar);
		shareFormOtherProg();
	}

	private void initView() {
		getTopBarView().setVisibility(View.GONE);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		circleET = (EditText) findViewById(R.id.circleET);
		circleET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				checkSend();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		circleGV = (GridView) findViewById(R.id.circleGV);
		classTV = (TextView) findViewById(R.id.classTV);
		sendNoticeBT = (Button) findViewById(R.id.sendNoticeBT);
		sendNoticeBT.setSelected(true);
		selectClassBT = (RelativeLayout) findViewById(R.id.selectClassBT);

		if (userClasses != null && userClasses.size() != 0) {
			selectClassBT.setVisibility(View.VISIBLE);
			if (userClasses.size() == 1) {
				classTV.setText("发送到：" + userClasses.get(0).getName());
				departmentId = userClasses.get(0).getId();
			} else {
				classTV.setText("选择发送到的班级");
				selectClassBT.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context,
								SelectClassActivity.class);
						intent.putExtra("classes", (Serializable) userClasses);
						intent.putExtra("circleFlag", "class");
						startActivityForResult(intent, 1);
					}
				});
			}
		} else {
			selectClassBT.setVisibility(View.GONE);
		}
		nowPhotos.add(null);
		adapter = new ImageGVAdapter();
		circleGV.setAdapter(adapter);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_sendnewcircle;
	}

	public void onSend(final View v) {
		InputTools.KeyBoard(circleET, "close");
		map = new HashMap<String, ParameterValue>();
		map.put("content", new ParameterValue(circleET.getEditableText()
				.toString()));
		map.put("tagId", new ParameterValue(""));
		map.put("publicFlag", new ParameterValue(Constant.CIRCLE_PUBLICFLAG_PUBLIC));
		map.put("location", new ParameterValue("大中国"));
		map.put("url", new ParameterValue(""));
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("动态发送中…");
		pd.setCancelable(false);
		pd.show();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					if ("school".equals(circleFlag)) {
						if (sendFiles.size() == 0) {
							sendFlag = UrlUtil.savePersonalMomentWithoutFile(ECApplication.getInstance().getAddress(),
									ECApplication.getInstance().getLoginMap(),
									map); // 发送请求
						} else {
							sendFlag = UrlUtil.savePersonalMoment(ECApplication.getInstance().getAddress(), sendFiles,
									ECApplication.getInstance().getLoginMap(),
									map); // 发送请求
						}
					} else if ("class".equals(circleFlag)) {
						if (departmentId == null) {
							ToastUtil.showMessage("未选择班级");
							pd.dismiss();
							return;
						}
						map.put("departmentId",new ParameterValue(departmentId));
						if (sendFiles.size() == 0) {
							sendFlag = UrlUtil.saveClassMomentWithoutFile(
									ECApplication.getInstance().getAddress(),
									ECApplication.getInstance().getLoginMap(),
									map); // 发送请求
						} else {
							sendFlag = UrlUtil.saveClassMoment(ECApplication
									.getInstance().getAddress(), sendFiles,
									ECApplication.getInstance().getLoginMap(),
									map); // 发送请求
						}
					}
					handler.postDelayed(new Runnable() {
						public void run() {
							pd.dismiss();
							ToastUtil.showMessage("已发送");
							setResult(101,new Intent().putExtra("flag", "refresh"));
							finish();
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					handler.postDelayed(new Runnable() {
						public void run() {
							pd.dismiss();
							ToastUtil.showMessage("错误");
							finish();
						}
					}, 5);
				}
			}
		}).start();
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
					if (position == nowPhotos.size() - 1) {

					} else {
						remove(position);
						for (int i = 0; i < nowPhotos.size(); i++) {
							if (nowPhotos.get(i) == null) {
								nowPhotos.remove(i);
							}
						}
						nowPhotos.add(null);
						checkSend();
						adapter.notifyDataSetChanged();
					}
				}
			});
			holder.imageGV.setOnClickListener(new OnClickListener() {

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (data != null) {
				classTV.setText("发送到：" + data.getStringExtra("className"));
				departmentId = data.getStringExtra("classId");
			}
		}
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
				checkSend();
				Compress();
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adapter != null) {
			adapter = null;
		}
		nowPhotos = null;
		if ((bitmap != null) && (!bitmap.isRecycled())) {
			bitmap.recycle();
		}
		System.gc();
	}

	public void checkSend() {
		if (nowPhotos.size() > 1
				|| circleET.getEditableText().toString().trim().length() != 0) {
			sendNoticeBT.setClickable(true);
			sendNoticeBT.setSelected(false);
		} else {
			sendNoticeBT.setSelected(true);
			sendNoticeBT.setClickable(false);
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

	public void remove(int position) {
		nowPhotos.remove(position);
		nowBmp.remove(position);
		sendFiles.remove(position);
	}

	/**
	 * 其他应用调用本分享功能
	 */
	private void shareFormOtherProg() {
		/* 比如通过Gallery方式来调用本分享功能 */
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action)) {
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				try {
					// Get resource path from intent
					Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

					// 返回路径
					String path = getRealPathFromURI(this, uri);
					System.out.println("path-->" + path);
					
					List<PhotoModel> photos = new ArrayList<PhotoModel>();
					photos.add(new PhotoModel(path));
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
					checkSend();
					Compress();
					adapter.notifyDataSetChanged();
					return;
				} catch (Exception e) {

				}
			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
				return;
			}
		}
	}

	/**
	 * 通过Uri获取文件在本地存储的真实路径
	 * 
	 * @param act
	 * @param contentUri
	 * @return
	 */
	public String getRealPathFromURI(Activity act, Uri contentUri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = act.managedQuery(contentUri, proj, // Which columns to
															// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
