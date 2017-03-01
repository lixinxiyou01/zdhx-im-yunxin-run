package com.photoselector.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.photoselector.R;
import com.photoselector.domain.PhotoSelectorDomain;
import com.photoselector.model.AlbumModel;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoItem.onItemClickListener;
import com.photoselector.ui.PhotoItem.onPhotoItemCheckedListener;
import com.photoselector.util.AnimationUtil;
import com.photoselector.util.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoSelectorActivity extends Activity implements onItemClickListener, onPhotoItemCheckedListener,
		OnItemClickListener, OnClickListener {

	public static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_CAMERA = 1;

	public static final String RECCENT_PHOTO = "最近照片";

	private GridView gvPhotos;
	private ListView lvAblum;
	private Button btnOk;
	private TextView tvAlbum, tvPreview, tvTitle;
	private PhotoSelectorDomain photoSelectorDomain;
	private PhotoSelectorAdapter photoAdapter;
	private AlbumAdapter albumAdapter;
	private RelativeLayout layoutAlbum;
	public static ArrayList<PhotoModel> selected;
	private Uri photoUri;
	private Uri uri;
	public static int canSelectCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_photoselector);
		canSelectCount = getIntent().getIntExtra("canSelectCount", 9);
		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder() //
				.considerExifParams(true) // 调整图片方向
				.resetViewBeforeLoading(true) // 载入之前重置ImageView
				.showImageOnLoading(R.drawable.ic_picture_loading) // 载入时图片设置为黑色
				.showImageOnFail(R.drawable.ic_picture_loadfailed) // 加载失败时显示的图片
				.delayBeforeLoading(0) // 载入之前的延迟时间
				.build(); //
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.defaultDisplayImageOptions(defaultDisplayImageOptions).memoryCacheExtraOptions(200, 400)
				.threadPoolSize(5).build();
		
		ImageLoader.getInstance().init(config);
		
		photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

		selected = new ArrayList<PhotoModel>();

		tvTitle = (TextView) findViewById(R.id.tv_title_lh);
		gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
		lvAblum = (ListView) findViewById(R.id.lv_ablum_ar);
		btnOk = (Button) findViewById(R.id.btn_right_lh);
		tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
		tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
		layoutAlbum = (RelativeLayout) findViewById(R.id.layout_album_ar);

		btnOk.setOnClickListener(this);
		tvAlbum.setOnClickListener(this);
		tvPreview.setOnClickListener(this);

		photoAdapter = new PhotoSelectorAdapter(getApplicationContext(), new ArrayList<PhotoModel>(),
				CommonUtils.getWidthPixels(this), this, this, this);
		gvPhotos.setAdapter(photoAdapter);

		albumAdapter = new AlbumAdapter(getApplicationContext(), new ArrayList<AlbumModel>());
		lvAblum.setAdapter(albumAdapter);
		lvAblum.setOnItemClickListener(this);

		findViewById(R.id.bv_back_lh).setOnClickListener(this); // 返回

		photoSelectorDomain.getReccent(reccentListener); // 更新最近照片
		photoSelectorDomain.updateAlbum(albumListener); // 跟新相册信息
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_right_lh)
			ok(); // 选完照片
		else if (v.getId() == R.id.tv_album_ar)
			album();
		else if (v.getId() == R.id.tv_preview_ar)
			priview();
		else if (v.getId() == R.id.tv_camera_vc)
			catchPicture();
		else if (v.getId() == R.id.bv_back_lh)
			finish();
	}

	/** 拍照 */
	private void catchPicture() {
		  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  SimpleDateFormat timeStampFormat = new SimpleDateFormat(
		  "yyyy_MM_dd_HH_mm_ss");
		  String filename = timeStampFormat.format(new Date());
		  ContentValues values = new ContentValues();
		  values.put(Media.TITLE, filename);
		  photoUri = getContentResolver().insert(
		  MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		  intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		  CommonUtils.launchActivityForResult(this, intent, REQUEST_CAMERA);
	
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
			if (data != null && data.getData() != null) {
				uri = data.getData();
			}
			if(uri==null){
				if (photoUri != null) {
					uri = photoUri;
				}else{
					return;
				}
			}
			PhotoModel photoModel = new PhotoModel(CommonUtils.query(getApplicationContext(), uri));
			selected.clear();
			selected.add(photoModel);
			ok();
		}
	}

	/** 完成 */
	private void ok() {
		if (selected.isEmpty()) {
			setResult(RESULT_CANCELED);
		} else {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("正在为您节省流量");
			pd.setCancelable(false);
			pd.show();
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("photos", selected);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
		}
		finish();
	}

	/** 预览照片 */
	private void priview() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("photos", selected);
		CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	private void album() {
		if (layoutAlbum.getVisibility() == View.GONE) {
			popAlbum();
		} else {
			hideAlbum();
		}
	}

	/** 弹出相册列表 */
	private void popAlbum() {
		layoutAlbum.setVisibility(View.VISIBLE);
		new AnimationUtil(getApplicationContext(), R.anim.translate_up_current).setLinearInterpolator().startAnimation(
				layoutAlbum);
	}

	/** 隐藏相册列表 */
	private void hideAlbum() {
		new AnimationUtil(getApplicationContext(), R.anim.translate_down).setLinearInterpolator().startAnimation(
				layoutAlbum);
		layoutAlbum.setVisibility(View.GONE);
	}

	/** 清空选中的图片 */
	private void reset() {
		selected.clear();
		tvPreview.setText("预览");
		tvPreview.setEnabled(false);
		btnOk.setText("确定");
	}

	@Override
	/** 点击查看照片 */
	public void onItemClick(int position) {
		Bundle bundle = new Bundle();
		if (tvAlbum.getText().toString().equals(RECCENT_PHOTO))
			bundle.putInt("position", position - 1);
		else
			bundle.putInt("position", position);
		bundle.putString("album", tvAlbum.getText().toString());
		CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	@Override
	/** 照片选中状态改变之后 */
	public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			//选择照片数量限制
			if (canSelectCount>selected.size()) {
				selected.add(photoModel);
				tvPreview.setEnabled(true);
				photoModel.setChecked(isChecked);
			}else{
				Toast.makeText(PhotoSelectorActivity.this, "本次最多选择"+canSelectCount+"张", Toast.LENGTH_SHORT).show();
				//超过限制后  把check状态改回false
				buttonView.setChecked(false);
				return;
			}
		} else {
			photoModel.setChecked(isChecked);
			selected.remove(photoModel);
		}
		btnOk.setText("确定("+selected.size()+"/"+canSelectCount+")");
		tvPreview.setText("预览(" + selected.size() + ")");  //修改预览数量
		if (selected.isEmpty()) {
			tvPreview.setEnabled(false);
			tvPreview.setText("预览");
			btnOk.setText("确定");
		}
	}

	@Override
	public void onBackPressed() {
		if (layoutAlbum.getVisibility() == View.VISIBLE) {
			hideAlbum();
		} else
			super.onBackPressed();
	}

	@Override
	/** 相册列表点击事件 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
		for (int i = 0; i < parent.getCount(); i++) {
			AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
			if (i == position)
				album.setCheck(true);
			else
				album.setCheck(false);
		}
		albumAdapter.notifyDataSetChanged();
		hideAlbum();
		tvAlbum.setText(current.getName());
		tvTitle.setText(current.getName());
		
		// 更新照片列表
		if (current.getName().equals(RECCENT_PHOTO))
			photoSelectorDomain.getReccent(reccentListener);
		else
			photoSelectorDomain.getAlbum(current.getName(), reccentListener); // 获取选中相册的照片
	}

	/** 获取本地图库照片回调 */
	public interface OnLocalReccentListener {
		public void onPhotoLoaded(List<PhotoModel> photos);
	}

	/** 获取本地相册信息回调 */
	public interface OnLocalAlbumListener {
		public void onAlbumLoaded(List<AlbumModel> albums);
	}

	private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
		@Override
		public void onAlbumLoaded(List<AlbumModel> albums) {
			albumAdapter.update(albums);
		}
	};

	private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
		@Override
		public void onPhotoLoaded(List<PhotoModel> photos) {
			if (tvAlbum.getText().equals(RECCENT_PHOTO))
				photos.add(0, new PhotoModel());
			photoAdapter.update(photos);
			gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
			//TODO  换相册后  重置
			reset();
		}
	};
}
