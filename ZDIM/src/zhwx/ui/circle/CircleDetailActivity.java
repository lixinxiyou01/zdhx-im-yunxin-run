package zhwx.ui.circle;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.ClassCircle;
import zhwx.common.model.CommentAndReply;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.Log;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.imagegallery.ViewImageInfo;

public class CircleDetailActivity extends BaseActivity {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private RequestWithCacheGet mRequestWithCache; 
	
	private String circleJson;
	
	private String circleId;
	
	private ImageLoader mImageLoader;
	
	private ClassCircle circle;
	
	private Handler handler = new Handler();
	
	private ImageView noticeImgIV,headIV,thumbIV;
	
	private GridView noticeImgGV;
	
	private TextView noticeContentTV;
	
	private TextView noticeSendTimeTV;
	
	private TextView senderTV;
	
	private TextView thumbsupTV;
	
	private Button deleteBT;
	
	private LinearLayout commentContener;
	
	private ECProgressDialog mPostingdialog;
	
	private LinearLayout cuLay;
	
	private String kind;
	
	private CommentAndReply comment;
	
	private FrameLayout top_bar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		mRequestWithCache = new RequestWithCacheGet(context);
		mImageLoader = new ImageLoader(context);
		circleId = getIntent().getStringExtra("momentId");
		kind = getIntent().getStringExtra("kind");
		initView();
		setImmerseLayout(top_bar);
		addListener();
		getData();
	}
	
	private void getData() {
		mPostingdialog = new ECProgressDialog(context, "数据加载中");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("momentId", new ParameterValue(circleId));
		try {
			circleJson = mRequestWithCache.getRseponse(UrlUtil.getMomentDetail(ECApplication.getInstance().getAddress(), map), new RequestWithCacheGet.RequestListener() {
				
				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:"+response);
						refreshData(response);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					 
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if ((circleJson != null && !circleJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:"+circleJson);
			refreshData(circleJson);
		}
	}
	
	private void refreshData(String json) {
		mPostingdialog.dismiss();
		if (!json.contains("commentCount")) {
			return;
		}
		Gson gson = new Gson();
		circle = gson.fromJson(json, ClassCircle.class);
		mImageLoader.DisplayImage(ECApplication.getInstance().getAddress()+circle.getHeadPortraitUrl(), headIV, false);
	
		if (StringUtil.isNotBlank(circle.getDepartmentName())) {
			senderTV.setText(circle.getDepartmentName());
		} else {
			senderTV.setText(circle.getUserName());
		}
	
		if (StringUtil.isNotBlank(circle.getContent())) {
			noticeContentTV.setText(circle.getContent());
		} else {
			noticeContentTV.setVisibility(View.GONE);
		}
		noticeSendTimeTV.setText(circle.getPublishTime());
		if (circle.getPicUrls() != null && circle.getPicUrls().size() != 0) {
			if (circle.getPicUrls().size()==1) {
				noticeImgIV.setVisibility(View.VISIBLE);
				noticeImgGV.setVisibility(View.GONE);
				mImageLoader.DisplayImage(ECApplication.getInstance().getAddress()+circle.getPicUrls().get(0).getSmallPicUrl(), noticeImgIV, false);
			} else {
				noticeImgIV.setVisibility(View.GONE);
				noticeImgGV.setVisibility(View.VISIBLE);
				noticeImgGV.setAdapter(new ImageGirdAdapter(context, circle.getPicUrls()));
				Tools.setGridViewHeightBasedOnChildren4(noticeImgGV);
			}
		}else{
			noticeImgIV.setVisibility(View.GONE);
			noticeImgGV.setVisibility(View.GONE);
		}
		//TODO 评论列表
		List<CommentAndReply> commentAndReplies = new ArrayList<CommentAndReply>();
		for (int i = 0; i < circle.getComments().size(); i++) {
			CommentAndReply comment = new CommentAndReply();
			comment.setKind(Constant.COMMENT);
			comment.setContent(circle.getComments().get(i).getContent());
			comment.setUserId(circle.getComments().get(i).getUserId());
			comment.setUserName(circle.getComments().get(i).getUserName());
			comment.setConmmentId(circle.getComments().get(i).getId());
			commentAndReplies.add(comment);
			if (circle.getComments().get(i).getReplys()!=null) {
				for (int j = 0; j < circle.getComments().get(i).getReplys().size(); j++) {
					CommentAndReply reply = new CommentAndReply();
					reply.setKind(Constant.REPLY);
					reply.setContent(circle.getComments().get(i).getReplys().get(j).getContent());
					reply.setReplyId(circle.getComments().get(i).getReplys().get(j).getId());
					reply.setConmmentId(circle.getComments().get(i).getId());
					reply.setContent(circle.getComments().get(i).getReplys().get(j).getContent());
					reply.setReplyUserId(circle.getComments().get(i).getReplys().get(j).getReplyUserId());
					reply.setReplyUserName(circle.getComments().get(i).getReplys().get(j).getReplyUserName());
					reply.setTargetUserId(circle.getComments().get(i).getReplys().get(j).getTargetUserId());
					reply.setTargetUserName(circle.getComments().get(i).getReplys().get(j).getTargetUserName());
					commentAndReplies.add(reply);
				}
			}
		}
		if (commentAndReplies.size() > 0) {
			int childCount = commentContener.getChildCount();
			int index = -1;
			for (CommentAndReply commentAndReply : commentAndReplies) {
				index++;
				LinearLayout commentItem = null;
				if (index<childCount) {
					commentItem = (LinearLayout) commentContener.getChildAt(index);
				} else {
					commentItem = (LinearLayout) View.inflate(context, R.layout.list_comment_item, null);
					commentContener.addView(commentItem);
				}
				TextView contentTV = (TextView) commentItem.findViewById(R.id.contentTV);
				commentAndReply.showData(commentAndReply, commentItem,context);
				contentTV.setTag(R.integer.tag_data, commentAndReply);
				contentTV.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						CommentAndReply car = (CommentAndReply) view.getTag(R.integer.tag_data);
					}
				});
			}
		}
		String thymbString = "";
		for (int i = 0; i < circle.getThumbsups().size(); i++) {
			if (i != circle.getThumbsups().size()-1) {
				thymbString += circle.getThumbsups().get(i).getUserName()+", ";
			} else {
				thymbString += circle.getThumbsups().get(i).getUserName();
			}
		}
		if (thymbString.length()>1) {
			thumbsupTV.setVisibility(View.VISIBLE);
			thumbsupTV.setText(thymbString);
			thumbIV.setVisibility(View.VISIBLE);
		} else {
			thumbsupTV.setText("");
			thumbsupTV.setVisibility(View.GONE);
			thumbIV.setVisibility(View.GONE);
		}
		
		//没有评论和点赞 隐藏
		if(thymbString.length()==0&&commentAndReplies.size()==0){
			cuLay.setVisibility(View.GONE);
		}else{
			cuLay.setVisibility(View.VISIBLE);
		}
	}
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		noticeImgIV = (ImageView) findViewById(R.id.noticeImgIV);
		noticeImgGV = (GridView) findViewById(R.id.noticeImgGV);
		noticeContentTV = (TextView) findViewById(R.id.noticeContentTV);
		noticeSendTimeTV = (TextView) findViewById(R.id.noticeSendTimeTV);
		thumbsupTV = (TextView) findViewById(R.id.thumbsupTV);
		senderTV = (TextView) findViewById(R.id.senderTV);
		headIV = (ImageView) findViewById(R.id.headIV);
		thumbIV = (ImageView) findViewById(R.id.thumbIV);
		deleteBT = (Button) findViewById(R.id.deleteBT);
		commentContener = (LinearLayout) findViewById(R.id.commentContener);
		cuLay =(LinearLayout) findViewById(R.id.cuLay);
	}
	public void addListener(){
		deleteBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.delete_comment_tip, null, new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface dialog, int which) {
//	                	deleteMoment(circle.getId());
	                }
	            });
	            buildAlert.setTitle("删除动态");
	            buildAlert.show();
			}
		});
		
		noticeImgIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//单张
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo = new ViewImageInfo("", ECApplication.getInstance().getAddress()+circle.getPicUrls().get(0).getBigPicUrl()); 
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
				for (int i = 0; i < circle.getPicUrls().size(); i++) {
					imageInfo = new ViewImageInfo("", ECApplication.getInstance().getAddress()+circle.getPicUrls().get(i).getBigPicUrl()); 
					urls.add(imageInfo);
				}
				CCPAppManager.startChattingImageViewAction(context,position , urls);
			}
		});
	}
	
	//删除动态
	public void deleteMoment(String momentId){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("momentId", new ParameterValue(momentId));
		map.put("kind", new ParameterValue(kind));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					UrlUtil.deleteMoment(ECApplication.getInstance().getAddress(),map);  //删除回复 发送请求
					handler.postDelayed(new Runnable() {
						public void run() {
							
						}
					}, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}	
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_circle_detail;
	}
}
