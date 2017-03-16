package zhwx.ui.dcapp.homework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.PicUrl;
import zhwx.common.util.Tools;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.imagegallery.ViewImageInfo;


public class ResultDetailActivity extends BaseActivity {
	
	private Activity context;
	
	private ImageLoader mImageLoader;
	
	private TextView noticeContentTV;
	
	private FrameLayout top_bar;
		
	private Result result;
	
	private ImageView noticeImgIV;
	
	private GridView noticeImgGV;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_homework_result_detail;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		result = (Result) getIntent().getSerializableExtra("result");
		mImageLoader = new ImageLoader(context);
		initView();
		setImmerseLayout(top_bar);
		refreshData();
		addListener();
	}
	
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar1);
		noticeImgGV = (GridView) findViewById(R.id.noticeImgGV);
		noticeImgIV = (ImageView) findViewById(R.id.noticeImgIV);
		noticeContentTV = (TextView) findViewById(R.id.noticeContentTV);
	}
	
	private void refreshData() {
		noticeContentTV.setText(result.getContent());
		if (result.getImageList()!=null&&result.getImageList().size()!=0) {
			if (result.getImageList().size()==1) {
				noticeImgIV.setVisibility(View.VISIBLE);
				noticeImgGV.setVisibility(View.GONE);
				mImageLoader.DisplayImage(ECApplication.getInstance().getV3Address() + result.getImageList().get(0), noticeImgIV, false);
			} else {
				noticeImgIV.setVisibility(View.GONE);
				noticeImgGV.setVisibility(View.VISIBLE);
				List<PicUrl> list = new ArrayList<PicUrl>();
				for (int i = 0; i < result.getImageList().size(); i++) {
					PicUrl picUrl = new PicUrl();
					picUrl.setBigPicUrl(result.getImageList().get(i));
					picUrl.setSmallPicUrl(result.getImageList().get(i));
					list.add(picUrl);
				}
				noticeImgGV.setAdapter(new ImageGirdAdapter(context, list));
				Tools.setGridViewHeightBasedOnChildren4(noticeImgGV);
			}
		}else{
			noticeImgIV.setVisibility(View.GONE);
			noticeImgGV.setVisibility(View.GONE);
		}
	}
	
	public void addListener(){
		
		noticeImgIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//单张
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo = new ViewImageInfo("", ECApplication.getInstance().getAddress()+result.getImageList().get(0));
				urls.add(imageInfo);
				CCPAppManager.startChattingImageViewAction(context,0, urls);
			}
		});
		noticeImgGV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//多张
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo;
				for (int i = 0; i < result.getImageList().size(); i++) {
					imageInfo = new ViewImageInfo("", ECApplication.getInstance().getV3Address()+result.getImageList().get(i)); 
					urls.add(imageInfo);
				}
				CCPAppManager.startChattingImageViewAction(context,position , urls);
			}
		});
	}
}
