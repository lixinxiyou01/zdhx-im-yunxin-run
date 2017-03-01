package zhwx.ui.dcapp.homework;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
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

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.InputTools;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;


public class SubmitHomeWorkActivity extends BaseActivity {

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

	private TextView sendNoticeBT;
	
	private FrameLayout top_bar;
	
	private String studentWorkId;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_submithomework;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		studentWorkId = getIntent().getStringExtra("studentWorkId");
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
		sendNoticeBT = (TextView) findViewById(R.id.sendNoticeBT);
		sendNoticeBT.setSelected(true);
		nowPhotos.add(null);
		adapter = new ImageGVAdapter();
		circleGV.setAdapter(adapter);
	}

	public void onSend(final View v) {
		InputTools.KeyBoard(circleET, "close");
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("content", new ParameterValue(circleET.getEditableText()
				.toString()));
		map.put("studnetWorkId", new ParameterValue(studentWorkId));
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("作业提交中");
		pd.setCancelable(false);
		pd.show();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < sendFiles.size(); i++) {
						System.out.println(sendFiles.get(i).getAbsolutePath());
					}
					sendFlag = UrlUtil.saveHomeWorkAnswer(ECApplication.getInstance().getV3Address(),sendFiles,ECApplication.getInstance().getV3LoginMap(),map); // 发送请求
					handler.postDelayed(new Runnable() {
						public void run() {
							pd.dismiss();
							if("ok".equals(sendFlag)) {
								ToastUtil.showMessage("作业已提交");
								setResult(999);
								finish();
							}
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
