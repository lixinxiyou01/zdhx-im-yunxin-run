package zhwx.common.model;

import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;

import zhwx.Constant;
import zhwx.ui.circle.MyCircleActivity;

public class CommentAndReply {
	private int kind;
	private String content;
	private String userId;
	private String userName;
	private String time;
	private String conmmentId;
	private String replyId;
	private String replyUserId;
	private String replyUserName;
	private String targetUserId;
	private String targetUserName;
	private TextView contentTV;
	public CommentAndReply() {
		super();
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getReplyUserId() {
		return replyUserId;
	}
	public void setReplyUserId(String replyUserId) {
		this.replyUserId = replyUserId;
	}
	public String getReplyUserName() {
		return replyUserName;
	}
	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}
	public String getTargetUserId() {
		return targetUserId;
	}
	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public String getTargetUserName() {
		return targetUserName;
	}
	public void setTargetUserName(String targetUserName) {
		this.targetUserName = targetUserName;
	}
	public String getConmmentId() {
		return conmmentId;
	}
	public void setConmmentId(String conmmentId) {
		this.conmmentId = conmmentId;
	}
	public String getReplyId() {
		return replyId;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	
	public void showData(final CommentAndReply andReply,LinearLayout commentItem,final Activity context){
		//点击回复者名字
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(andReply.getKind() == Constant.COMMENT){
					context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", andReply.getUserId()));
				}else{
					context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", andReply.getReplyUserId()));
				}
			}
		};
		//点击被回复者名字
		OnClickListener listener1 = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", andReply.getTargetUserId()));
			}
		};
		contentTV = (TextView) commentItem.findViewById(R.id.contentTV);
		final String replyUserName = andReply.getReplyUserName();
		final String targetUserName = andReply.getTargetUserName();
		final String userName = andReply.getUserName();
		String allStringOfReply = replyUserName+"回复"+targetUserName+":"+andReply.getContent();
		String allStringOfComment = userName + ":"+andReply.getContent();
		if(andReply.getKind()==Constant.COMMENT){
			SpannableStringBuilder builder = new SpannableStringBuilder(allStringOfComment);
			ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.name_blue));
			int start = allStringOfComment.indexOf(userName);
			int end = start + userName.length();
			builder.setSpan(new nolineClickSpan(listener), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			contentTV.setText(builder);
			contentTV.setMovementMethod(LinkMovementMethod.getInstance()); 
		}else{
			SpannableStringBuilder builder = new SpannableStringBuilder(allStringOfReply);
			ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.name_blue));
			int start = allStringOfReply.indexOf(replyUserName);
			int end = start + replyUserName.length();
			builder.setSpan(new nolineClickSpan(listener), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ForegroundColorSpan redSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.name_blue));
			int start1 = allStringOfReply.indexOf(targetUserName);
			int end1 = start1 + targetUserName.length();
			builder.setSpan(new nolineClickSpan(listener1), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.setSpan(redSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			contentTV.setText(builder);
			contentTV.setMovementMethod(LinkMovementMethod.getInstance()); 
		}
	}
	
	class nolineClickSpan extends ClickableSpan { 
		public OnClickListener mListener;
	    public nolineClickSpan(OnClickListener l) {
	    	super();
	    	mListener = l;
	    }

	    @Override
	    public void updateDrawState(TextPaint ds) {
	        ds.setUnderlineText(false); //去掉下划线
	    }

		@Override
		public void onClick(View view) {
			mListener.onClick(view);
		}
	}
}
