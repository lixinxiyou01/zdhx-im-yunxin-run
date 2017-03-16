package zhwx.ui.circle;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.Constant;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.CommentAndReply;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.SchoolCircle;
import zhwx.common.model.Thumbsup;
import zhwx.common.util.InputTools;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECListDialog;
import zhwx.common.view.imagegallery.ViewImageInfo;


public class SchoolCircleListAdapter extends BaseAdapter {
	
	private Activity context;
	
	private List<SchoolCircle> list;
	
	private boolean mBusy = false;
	
	private ImageLoader mImageLoader;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ImageView headIV;
	
	private ImageView tipIV;
	
	private TextView tipTV;
	
	private LinearLayout recordTipTv;
	
	public static int REFRESH_FLAG = 1;
	
	public static final int REFRESH_FLAG_TEXT = 0;
	public static final int REFRESH_FLAG_ALL = 1;
	
	public static final int MAX_THUMBUP = 10;
	
	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	
	public SchoolCircleListAdapter(Activity context, List<SchoolCircle> list,ImageView headIV, LinearLayout recordTipTv, TextView tipTV, ImageView tipIV) {
		super();
		this.context = context;
		this.list = list;
		this.headIV = headIV;
		this.tipIV = tipIV;
		this.tipTV = tipTV;
		this.recordTipTv = recordTipTv;
		mImageLoader = new ImageLoader(context);
		mImageLoader.DisplayImage(ECApplication.getInstance().getAddress()+ECApplication.getInstance().getCurrentIMUser().getHeadPortraitUrl(), headIV, false);
//		if(MainActivity.counts != null && Integer.parseInt(MainActivity.counts.get(2).getCount()) > 0){
//			this.recordTipTv.setVisibility(View.VISIBLE);
//			this.tipTV.setText(MainActivity.counts.get(2).getCount()+"条新消息");
//			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + MainActivity.counts.get(2).getHeadUrl(), tipIV,
//					false);
//		}else{
//			this.recordTipTv.setVisibility(View.INVISIBLE);
//		}
	}
	
	@Override
	public int getCount() {
		mImageLoader.DisplayImage(ECApplication.getInstance().getAddress()+ECApplication.getInstance().getCurrentIMUser().getHeadPortraitUrl(), headIV, false);
//		if(MainActivity.counts != null && Integer.parseInt(MainActivity.counts.get(2).getCount()) > 0){
//			recordTipTv.setVisibility(View.VISIBLE);
//			tipTV.setText(MainActivity.counts.get(2).getCount()+"条新消息");
//			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + MainActivity.counts.get(2).getHeadUrl(), tipIV,
//					false);
//		}else{
//			recordTipTv.setVisibility(View.INVISIBLE);
//		}
		return list.size();
	}
	
	
	@Override
	public SchoolCircle getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, R.layout.list_notice_item, null);
			holder.noticeImgIV = (ImageView) convertView.findViewById(R.id.noticeImgIV);
			holder.thumbIV = (ImageView) convertView.findViewById(R.id.thumbIV);
			holder.noticeImgGV = (GridView) convertView.findViewById(R.id.noticeImgGV);
			holder.noticeContentTV = (TextView) convertView.findViewById(R.id.noticeContentTV);
			holder.noticeSendTimeTV = (TextView) convertView.findViewById(R.id.noticeSendTimeTV);
			holder.thumbsupTV = (TextView) convertView.findViewById(R.id.thumbsupTV);
			holder.senderTV = (TextView) convertView.findViewById(R.id.senderTV);
			holder.headIV = (ImageView) convertView.findViewById(R.id.headIV);
			holder.deleteBT = (ImageButton) convertView.findViewById(R.id.deleteBT);
			holder.commentBT = (ImageButton) convertView.findViewById(R.id.commentBT);
			holder.thumbsupBT = (ImageButton) convertView.findViewById(R.id.thumbsupBT);
			holder.commentContener = (LinearLayout) convertView.findViewById(R.id.commentContener);
			holder.cuLay = (LinearLayout) convertView.findViewById(R.id.cuLay);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		if (REFRESH_FLAG == REFRESH_FLAG_ALL) {
			if(list.get(position).getPicUrls() != null && list.get(position).getPicUrls().size() != 0){
				if(list.get(position).getPicUrls().size() == 1){
					holder.noticeImgIV.setVisibility(View.VISIBLE);
					holder.noticeImgGV.setVisibility(View.GONE);
					if (!mBusy) {
						mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + getItem(position).getPicUrls().get(0).getSmallPicUrl(), holder.noticeImgIV, false);
					} 
				}else{
					holder.noticeImgIV.setVisibility(View.GONE);
					holder.noticeImgGV.setVisibility(View.VISIBLE);
					if (!mBusy) {
						holder.noticeImgGV.setAdapter(new ImageGirdAdapter(context, getItem(position).getPicUrls()));
						Tools.setGridViewHeightBasedOnChildren4(holder.noticeImgGV);
					}
				}
			}else{
				holder.noticeImgIV.setVisibility(View.GONE);
				holder.noticeImgGV.setVisibility(View.GONE);
			}
			holder.headIV.setImageResource(R.drawable.defult_head_img);
			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + getItem(position).getHeadPortraitUrl(), holder.headIV, false);
		}
		
		
		//评论列表
		List<CommentAndReply> commentAndReplies = new ArrayList<CommentAndReply>();
		for (int i = 0; i < list.get(position).getComments().size(); i++) {
			CommentAndReply comment = new CommentAndReply();
			comment.setKind(Constant.COMMENT);
			comment.setContent(list.get(position).getComments().get(i).getContent());
			comment.setUserId(list.get(position).getComments().get(i).getUserId());
			comment.setUserName(list.get(position).getComments().get(i).getUserName());
			comment.setConmmentId(list.get(position).getComments().get(i).getId());
			commentAndReplies.add(comment);
			if(list.get(position).getComments().get(i).getReplys()!=null){
				for (int j = 0; j < list.get(position).getComments().get(i).getReplys().size(); j++) {
					CommentAndReply reply = new CommentAndReply();
					reply.setKind(Constant.REPLY);
					reply.setContent(list.get(position).getComments().get(i).getReplys().get(j).getContent());
					reply.setReplyId(list.get(position).getComments().get(i).getReplys().get(j).getId());
					reply.setConmmentId(list.get(position).getComments().get(i).getId());
					reply.setContent(list.get(position).getComments().get(i).getReplys().get(j).getContent());
					reply.setReplyUserId(list.get(position).getComments().get(i).getReplys().get(j).getReplyUserId());
					reply.setReplyUserName(list.get(position).getComments().get(i).getReplys().get(j).getReplyUserName());
					reply.setTargetUserId(list.get(position).getComments().get(i).getReplys().get(j).getTargetUserId());
					reply.setTargetUserName(list.get(position).getComments().get(i).getReplys().get(j).getTargetUserName());
					commentAndReplies.add(reply);
				}
			}
		}
		
		//评论  linearLayout 自定义listview
		int childCount = holder.commentContener.getChildCount();
		int index = -1;
		for (CommentAndReply commentAndReply : commentAndReplies) {
			index++;
			LinearLayout commentItem = null;
			if(index < childCount){
				commentItem = (LinearLayout) holder.commentContener.getChildAt(index);
			}else{
				commentItem = (LinearLayout) View.inflate(context, R.layout.list_comment_item, null);
				holder.commentContener.addView(commentItem);
			}
			TextView contentTV = (TextView) commentItem.findViewById(R.id.contentTV);
			commentItem.setVisibility(View.VISIBLE);
			commentAndReply.showData(commentAndReply, commentItem, context);
			contentTV.setTag(R.integer.tag_data, commentAndReply);
			addListener(contentTV, position);
		}
		for (index++; index < childCount; index++) {  //把未使用的复用view设置成不可见
			holder.commentContener.getChildAt(index).setVisibility(View.GONE);
		}	
		
		//点赞
		String thymbString = "";
		if(getItem(position).getThumbsups().size() > MAX_THUMBUP){
			for (int i = 0; i < MAX_THUMBUP; i++) {
				if(i != getItem(position).getThumbsups().size()-1){
					thymbString += getItem(position).getThumbsups().get(i).getUserName()+", ";
				}else{
					thymbString += getItem(position).getThumbsups().get(i).getUserName();
				}
			}
			thymbString += "等"+getItem(position).getThumbsups().size()+"人觉得很赞";
		}else{
			for (int i = 0; i < getItem(position).getThumbsups().size(); i++) {
				if(i != getItem(position).getThumbsups().size()-1){
					thymbString += getItem(position).getThumbsups().get(i).getUserName()+", ";
				}else{
					thymbString += getItem(position).getThumbsups().get(i).getUserName();
				}
			}
		}	
		
		if(thymbString.length() > 0){
			holder.thumbsupTV.setVisibility(View.VISIBLE);
			holder.thumbsupTV.setText(thymbString);
			holder.thumbIV.setVisibility(View.VISIBLE);
		}else{
			holder.thumbsupTV.setText("");
			holder.thumbsupTV.setVisibility(View.GONE);
			holder.thumbIV.setVisibility(View.GONE);
		}
		
		//没有评论和点赞 隐藏
		if(thymbString.length()==0&&commentAndReplies.size()==0){
			holder.cuLay.setVisibility(View.GONE);
		}else{
			holder.cuLay.setVisibility(View.VISIBLE);
		}
		
		if(list.get(position).getIsThumbsup().equals("false")){
			holder.thumbsupBT.setSelected(false);
		}else{
			holder.thumbsupBT.setSelected(true);
		}
		
		holder.senderTV.setText(list.get(position).getUserName());
		if(StringUtil.isNotBlank(list.get(position).getContent())){
			holder.noticeContentTV.setText(list.get(position).getContent());
			holder.noticeContentTV.setVisibility(View.VISIBLE);
		}else{
			holder.noticeContentTV.setVisibility(View.GONE);
		}
		
		//格式化时间
		holder.noticeSendTimeTV.setText(list.get(position).getPublishTime());
		if(ECApplication.getInstance().getCurrentIMUser().getId().equals(getItem(position).getUserId())){
			holder.deleteBT.setVisibility(View.VISIBLE);
		}else{
			holder.deleteBT.setVisibility(View.INVISIBLE);
		}
		addListener(holder,position);
		return convertView;
	}
	
	private void addListener(TextView commentItem,final int mainPosition){
		commentItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final CommentAndReply car = (CommentAndReply) view.getTag(R.integer.tag_data);
				if(car.getKind()==Constant.COMMENT){ //回复评论
					if(ECApplication.getInstance().getCurrentIMUser().getId().equals(car.getUserId())){
						//自己发的  可以删掉
						ECListDialog dialog = new ECListDialog(context, new String[]{context.getString(R.string.menu_del)});
		                dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
		                    @Override
		                    public void onDialogItemClick(Dialog d, int position) {
		                    	deleteMomentComment(car,mainPosition);
		                    }
		                });
		                dialog.setTitle("删除此评论");
		                dialog.show(); 
					}else{
						//回复其他人
						EditText mEditTextContent = (EditText) context.findViewById(R.id.et_sendmessage);
						LinearLayout rl_bottom = (LinearLayout) context.findViewById(R.id.rl_bottom);
						rl_bottom.setVisibility(View.VISIBLE);
						rl_bottom.requestFocus();
						SchoolCircleActivity.commentPosition = mainPosition;
						SchoolCircleActivity.commentKind = Constant.REPLY;
						SchoolCircleActivity.comment = car;
						mEditTextContent.setHint("回复"+car.getUserName());
						InputTools.KeyBoard(mEditTextContent, "open");
					}
				}else if(car.getKind()==Constant.REPLY){ //再回复
					if(ECApplication.getInstance().getCurrentIMUser().getId().equals(car.getReplyUserId())){
						//自己发的  可以删掉
						ECListDialog dialog = new ECListDialog(context, new String[]{context.getString(R.string.menu_del)});
		                dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
		                    @Override
		                    public void onDialogItemClick(Dialog d, int position) {
		                    	deleteCommentReply(car,mainPosition);
		                    }
		                });
		                dialog.setTitle("删除此回复");
		                dialog.show(); 
					}else{
						//回复其他人
						EditText mEditTextContent = (EditText) context.findViewById(R.id.et_sendmessage);
						LinearLayout rl_bottom = (LinearLayout) context.findViewById(R.id.rl_bottom);
						rl_bottom.setVisibility(View.VISIBLE);
						rl_bottom.requestFocus();
						SchoolCircleActivity.commentPosition = mainPosition;
						SchoolCircleActivity.commentKind = Constant.REPLY;
						SchoolCircleActivity.comment = car;
						mEditTextContent.setHint("回复"+car.getReplyUserName());
						InputTools.KeyBoard(mEditTextContent, "open");
					}
				}
			}
		});
	}
	private void addListener(final Holder holder,final int mainPosition){
		
		holder.headIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", list.get(mainPosition).getUserId()));
			}
		});
		holder.senderTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", list.get(mainPosition).getUserId()));
			}
		});
		holder.deleteBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.delete_comment_tip, null, new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	deleteMoment(mainPosition,list.get(mainPosition).getId());
	                }
	            });
	            buildAlert.setTitle("删除动态");
	            buildAlert.show();
			}
		});
		holder.commentBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EditText mEditTextContent = (EditText) context.findViewById(R.id.et_sendmessage);
				LinearLayout rl_bottom = (LinearLayout) context.findViewById(R.id.rl_bottom);
				rl_bottom.setVisibility(View.VISIBLE);
				rl_bottom.requestFocus();
				SchoolCircleActivity.commentPosition = mainPosition;
				SchoolCircleActivity.commentKind = Constant.COMMENT;
				mEditTextContent.setHint("回复内容");
				InputTools.KeyBoard(mEditTextContent, "open");
			}
		});
		holder.thumbsupBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//点赞动画
				holder.thumbsupBT.startAnimation(AnimationUtils.loadAnimation(
						context, R.anim.dianzan_anim));
				holder.thumbsupBT.setClickable(false);
				map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
				if(!list.get(mainPosition).getIsThumbsup().equals("true")){
					map.put("momentId", new ParameterValue(list.get(mainPosition).getId()));
					map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
					new ProgressThreadWrap(context, new RunnableWrap() {
						@Override
						public void run() {
							try {
								final String flag = UrlUtil.thumbsup(ECApplication.getInstance().getAddress(),map);  //赞
								handler.postDelayed(new Runnable() {
									public void run() {
										if(flag.length()>0){
											SchoolCircleActivity.allCircles.get(mainPosition).getThumbsups().add(new Thumbsup(ECApplication.getInstance().getCurrentIMUser().getName()));
											SchoolCircleActivity.allCircles.get(mainPosition).setIsThumbsup("true");
											if(SchoolCircleActivity.adapter != null){
												REFRESH_FLAG = REFRESH_FLAG_TEXT;
												SchoolCircleActivity.adapter.notifyDataSetChanged();
											}
											holder.thumbsupBT.setClickable(true);
										}
									}
								}, 5);
							} catch (Exception e) {
								e.printStackTrace();
								handler.postDelayed(new Runnable() {
									public void run() {
										holder.thumbsupBT.setClickable(true);
									}
								}, 5);
							}
						}
					}).start();
				}else{
					map.put("momentId", new ParameterValue(list.get(mainPosition).getId()));
					map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
					new ProgressThreadWrap(context, new RunnableWrap() {
						@Override
						public void run() {
							try {
								UrlUtil.cancelThumbsup(ECApplication.getInstance().getAddress(),map);  //取消赞
								handler.postDelayed(new Runnable() {
									public void run() {
										for (int i = 0; i < SchoolCircleActivity.allCircles.get(mainPosition).getThumbsups().size(); i++) {
											if(ECApplication.getInstance().getCurrentIMUser().getName().equals(SchoolCircleActivity.allCircles.get(mainPosition).getThumbsups().get(i).getUserName())){
												SchoolCircleActivity.allCircles.get(mainPosition).getThumbsups().remove(i);
											}
										}
										SchoolCircleActivity.allCircles.get(mainPosition).setIsThumbsup("false");
										REFRESH_FLAG = REFRESH_FLAG_TEXT;
										SchoolCircleActivity.adapter.notifyDataSetChanged();
									}
								}, 5);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		holder.noticeImgIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//单张
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo = new ViewImageInfo("", ECApplication.getInstance().getAddress()+list.get(mainPosition).getPicUrls().get(0).getBigPicUrl()); 
				urls.add(imageInfo);
				CCPAppManager.startChattingImageViewAction(context,0, urls);
			}
		});
		holder.noticeImgGV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//多张
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo;
				for (int i = 0; i < list.get(mainPosition).getPicUrls().size(); i++) {
					imageInfo = new ViewImageInfo("", ECApplication.getInstance().getAddress()+list.get(mainPosition).getPicUrls().get(i).getBigPicUrl()); 
					urls.add(imageInfo);
				}
				CCPAppManager.startChattingImageViewAction(context,position , urls);
			}
		});
	}
	
	class Holder{
		private ImageView noticeImgIV,headIV,thumbIV;
		private GridView noticeImgGV;
		private TextView noticeContentTV;
		private TextView noticeSendTimeTV;
		private TextView senderTV;
		private TextView thumbsupTV;
		private ImageButton deleteBT,thumbsupBT,commentBT;
		private LinearLayout commentContener,cuLay;
	}
	
	//删除评论
	public void deleteMomentComment(final CommentAndReply comment,final int mainPosition){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("commentId", new ParameterValue(comment.getConmmentId()));
		map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					UrlUtil.deleteMomentComment(ECApplication.getInstance().getAddress(),map);  //删除评论 发送请求
					handler.postDelayed(new Runnable() {
						public void run() {
							for (int i = 0; i < SchoolCircleActivity.allCircles.get(mainPosition).getComments().size(); i++) {
								if(comment.getConmmentId().equals(SchoolCircleActivity.allCircles.get(mainPosition).getComments().get(i).getId())){
									SchoolCircleActivity.allCircles.get(mainPosition).getComments().remove(i);
									break;
								}
							}
							REFRESH_FLAG = REFRESH_FLAG_TEXT;
							SchoolCircleListAdapter.this.notifyDataSetChanged();
						}
					}, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	//删除回复
	public void deleteCommentReply(final CommentAndReply comment,final int mainPosition){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("replyId", new ParameterValue(comment.getReplyId()));
		map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					UrlUtil.deleteCommentReply(ECApplication.getInstance().getAddress(),map);  //删除回复 发送请求
					handler.postDelayed(new Runnable() {
						public void run() {
							for (int i = 0; i < SchoolCircleActivity.allCircles.get(mainPosition).getComments().size(); i++) {
								if(comment.getConmmentId().equals(SchoolCircleActivity.allCircles.get(mainPosition).getComments().get(i).getId())){
									for (int j = 0; j < SchoolCircleActivity.allCircles.get(mainPosition).getComments().get(i).getReplys().size(); j++) {
										if(comment.getReplyId().equals(SchoolCircleActivity.allCircles.get(mainPosition).getComments().get(i).getReplys().get(j).getId())){
											SchoolCircleActivity.allCircles.get(mainPosition).getComments().get(i).getReplys().remove(j);
											break;
										}
									}
								}
							}
							REFRESH_FLAG = REFRESH_FLAG_TEXT;
							SchoolCircleListAdapter.this.notifyDataSetChanged();
						}
					}, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	//删除动态
	public void deleteMoment(final int mainPosition,String momentId){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("momentId", new ParameterValue(momentId));
		map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					UrlUtil.deleteMoment(ECApplication.getInstance().getAddress(),map); 
					handler.postDelayed(new Runnable() {
						public void run() {
							REFRESH_FLAG = REFRESH_FLAG_ALL;
							SchoolCircleActivity.allCircles.remove(mainPosition);
							SchoolCircleListAdapter.this.notifyDataSetChanged();
						}
					}, 5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
