package zhwx.ui.dcapp.assets.view.pancel;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.netease.nim.demo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

import zhwx.common.base.BaseActivity;
import zhwx.ui.dcapp.assets.GrantActivity;
import zhwx.ui.dcapp.assets.ReSingActivity;
import zhwx.ui.dcapp.storeroom.GrantByHandActivity;
import zhwx.ui.dcapp.storeroom.GrantForApplyActivity;
import zhwx.ui.dcapp.storeroom.SReSingActivity;


public class PicelBoradActivity extends BaseActivity implements OnClickListener,ISketchPadCallback {

	private Button cleanBtn;
	private ImageView delAllBtn;
	private RelativeLayout line;
	private SketchPadView m_sketchPad = null;
	/**
	 * 最初颜色 
	 */
	public static int color = Color.BLUE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "确定","签字", this);
		findView();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	/**
	 * 找到控件
	 */
	private void findView() {
		delAllBtn =(ImageView) findViewById(R.id.cleanAllBtn);
		delAllBtn.setOnClickListener(this);
		line = (RelativeLayout) findViewById(R.id.line);
		m_sketchPad = (SketchPadView) findViewById(R.id.sketchpad);
		m_sketchPad.setCallback(PicelBoradActivity.this);
		Bitmap bitmap = null;
//		try {
//			bitmap = BitmapFactory.decodeStream(getAssets().open("paper.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		initPaint(bitmap);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cleanAllBtn:
		    m_sketchPad.clearAllStrokes();
			break;
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			m_sketchPad.setEnabled(false);
			Bitmap bmBitmap = m_sketchPad.getCanvasSnapshot();
			GrantActivity.sourBitmap = bmBitmap;
			GrantByHandActivity.sourBitmap = bmBitmap;
			GrantForApplyActivity.sourBitmap = bmBitmap;
			ReSingActivity.sourBitmap = bmBitmap;
			SReSingActivity.sourBitmap = bmBitmap;
			m_sketchPad.cleanDrawingCache();
			m_sketchPad.clearAllStrokes();
			setResult(121);
			finish();
			break;
		}
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data==null){
			return;
		}
		Uri uri = data.getData();  
		ContentResolver cr = this.getContentResolver();
		try {  
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
            /* 将Bitmap设定到ImageView */  
            m_sketchPad.setBkBitmap(bitmap);  
        } catch (FileNotFoundException e) {
        	
        }  
		super.onActivityResult(requestCode, resultCode, data);
	 }

	/**
	 * 初始化画笔
	 * 
	 * @author hh
	 * 
	 */
	public void initPaint(Bitmap bitmap) {
		m_sketchPad = new SketchPadView(PicelBoradActivity.this, null);
		m_sketchPad.setCallback(PicelBoradActivity.this);
		line.removeAllViews();
		line.addView(m_sketchPad);
//		m_sketchPad.setBkBitmap(bitmap);
	}


	/**
	 * toast
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-10-17 下午3:01:31
	 * @param str
	 */
	public void showToast(String str) {
		Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapUtil.setFree();
	}


    public String getStrokeFilePath()
    {
        File sdcarddir = android.os.Environment.getExternalStorageDirectory();
        String strDir = sdcarddir.getPath() + "/DCIM/sketchpad/";
        String strFileName = getStrokeFileName();
        File file = new File(strDir);
        if (!file.exists())
        {
            file.mkdirs();
        }
        
        String strFilePath = strDir + strFileName;
        
        return strFilePath;
    }
    public String getStrokeFileName()
    {
        String strFileName = "";
        
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONDAY);
        int date = rightNow.get(Calendar.DATE);
        int hour = rightNow.get(Calendar.HOUR);
        int minute = rightNow.get(Calendar.MINUTE);
        int second = rightNow.get(Calendar.SECOND);
        
        strFileName = String.format("%02d%02d%02d%02d%02d%02d.png", year, month, date, hour, minute, second);
        return strFileName;
    }
	@Override
	public void onTouchDown(SketchPadView obj, MotionEvent event) {
		
	}
	
	@Override
	public void onTouchUp(SketchPadView obj, MotionEvent event) {
		
	}
	
	@Override
	public void onDestroy(SketchPadView obj) {
		
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_pancel_board;
	}
}
