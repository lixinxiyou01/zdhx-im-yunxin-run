package zhwx.common.view.capture.core;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.HybridBinarizer;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.V3NoticeCenter;
import zhwx.common.util.JsonUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.capture.camera.CameraManager;
import zhwx.common.view.capture.executor.ResultHandler;
import zhwx.ui.dcapp.assets.AssetDetailActivity;

import static com.netease.nim.demo.team.TeamSynchroHelper.map;

/**
 * 二维码扫描
 * @author Li.Xin
 * 2014-9-26 上午10:49:11
 */
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback,OnClickListener {

	private static final String TAG = CaptureActivity.class.getSimpleName();
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private TitleView title;
	//private Button from_gallery;
	private final int from_photo = 010;
	static final int PARSE_BARCODE_SUC = 3035;
	static final int PARSE_BARCODE_FAIL = 3036;
	private String photoPath;
	private ProgressDialog mProgress;
	private String moduleCode;

	enum IntentSource {
		ZXING_LINK, NONE
	}

	Handler barHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				showDialog((String) msg.obj);
				break;
			case PARSE_BARCODE_FAIL:
				if (mProgress != null && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				new AlertDialog.Builder(CaptureActivity.this).setTitle("提示").setMessage("扫描失败！").setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
				break;
			}
			super.handleMessage(msg);
		}
	};

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		moduleCode = getIntent().getStringExtra("moduleCode");
		if (StringUtil.isBlank(moduleCode)) { //资产管理
			getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		} else if (V3NoticeCenter.NOTICE_KIND_REPAIR.equals(moduleCode)) { //报修
			getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		} else if ("login".equals(moduleCode)) {//扫码登录
			getTopBarView().setBackGroundColor(R.color.main_bg);
		}
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"扫一扫", this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		statusView = (TextView) findViewById(R.id.status_view);
	}

	public void showDialog(final String msg) {
		
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
		if (StringUtil.isBlank(moduleCode)) { //资产管理
			Intent intent = new Intent(this, AssetDetailActivity.class);
			intent.putExtra("assetsCode", msg);
			intent.putExtra("isAddMode", "isAddMode");
			startActivity(intent);
		} else if (V3NoticeCenter.NOTICE_KIND_REPAIR.equals(moduleCode)) { //报修
			ToastUtil.showMessage("报修扫码暂未开通");
		} else if ("login".equals(moduleCode)) { //扫码登录
			LoginCapture capture = null;
			if(msg.contains("u")&&msg.contains("i")){
				capture = JsonUtil.json2JavaPojo(msg, LoginCapture.class);
				map.put("uuid",new ParameterValue(capture.getU()));
				map.put("dataSource",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
				map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
				new ProgressThreadWrap(this, new RunnableWrap() {
					@Override
					public void run() {
						try {
							UrlUtil.twoDimensionCodeLogin(ECApplication.getInstance().getV3Address(), map);
							handler.postDelayed(new Runnable() {
								public void run() {
									ToastUtil.showMessage("扫码成功");
									CaptureActivity.this.finish();
								}
							}, 5);
						} catch (IOException e){
							e.printStackTrace();
						}
					}
				}).start();
			}else{
				ToastUtil.showMessage("二维码无效");
			}
		}
		restartPreviewAfterDelay(0L);
		finish();
	}

	public String parsLocalPic(String path) {
		String parseOk = null;
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF8");

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		// 缩放比
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + "   " + h);
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader2 = new QRCodeReader();
		Result result;
		try {
			result = reader2.decode(bitmap1, hints);
			Log.i("steven", "result:" + result);
			parseOk = result.getText();

		} catch (NotFoundException e) {
			parseOk = null;
		} catch (ChecksumException e) {
			parseOk = null;
		} catch (Exception e) {
			parseOk = null;
		}
		return parseOk;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("steven", "data.getData()" + data);
		if (data != null) {
			mProgress = new ProgressDialog(CaptureActivity.this);
			mProgress.setMessage("正在扫描...");
			mProgress.setCancelable(false);
			mProgress.show();
			final ContentResolver resolver = getContentResolver();
			if (requestCode == from_photo) {
				if (resultCode == RESULT_OK) {
					Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
					if (cursor.moveToFirst()) {
						photoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					}
					cursor.close();
					new Thread(new Runnable() {
						@Override
						public void run() {
							Looper.prepare();
							String result = parsLocalPic(photoPath);
							if (result != null) {
								Message m = Message.obtain();
								m.what = PARSE_BARCODE_SUC;
								m.obj = result;
								barHandler.sendMessage(m);
							} else {
								Message m = Message.obtain();
								m.what = PARSE_BARCODE_FAIL;
								m.obj = "扫描失败！";
								barHandler.sendMessage(m);
							}
							Looper.loop();
						}
					}).start();
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		handler = null;
		lastResult = null;
		resetStatusView();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		if (mProgress!= null) {
			mProgress.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 这里初始化界面，调用初始化相机
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	private static ParsedResult parseResult(Result rawResult) {
		return ResultParser.parseResult(rawResult);
	}

	// 解析二维码
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;

		ResultHandler resultHandler = new ResultHandler(parseResult(rawResult));

		boolean fromLiveScan = barcode != null;
		
		if (barcode == null) {
			Log.i("steven", "rawResult.getBarcodeFormat().toString():" + rawResult.getBarcodeFormat().toString());
			Log.i("steven", "resultHandler.getType().toString():" + resultHandler.getType().toString());
			Log.i("steven", "resultHandler.getDisplayContents():" + resultHandler.getDisplayContents());
		} else {
			showDialog(resultHandler.getDisplayContents().toString());
		}
	}

	// 初始化照相机，CaptureActivityHandler解码
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name)); 
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.confirm, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_capture;
	}
}
