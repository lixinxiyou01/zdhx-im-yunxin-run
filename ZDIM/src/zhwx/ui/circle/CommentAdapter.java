package zhwx.ui.circle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.List;

import zhwx.Constant;
import zhwx.common.model.CommentAndReply;


public class CommentAdapter extends BaseAdapter {
	private Activity context;
	private List<CommentAndReply> list;
	public CommentAdapter(Activity context, List<CommentAndReply> list) {
		super();
		this.context = context;
		this.list = list;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public CommentAndReply getItem(int position) {
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
			convertView = View.inflate(context, R.layout.list_comment_item, null);
//			holder.replyUserNameTV = (TextView) convertView.findViewById(R.id.replyUserNameTV);
//			holder.targetUserNameTV = (TextView) convertView.findViewById(R.id.targetUserNameTV);
//			holder.replyStringTV = (TextView) convertView.findViewById(R.id.replyStringTV);
			holder.contentTV = (TextView) convertView.findViewById(R.id.contentTV);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if(getItem(position).getKind()== Constant.COMMENT){
			holder.replyUserNameTV.setText(getItem(position).getUserName());
			holder.contentTV.setText(getItem(position).getContent());
			holder.replyStringTV.setVisibility(View.GONE);
			holder.targetUserNameTV.setVisibility(View.GONE);
		}else{
			holder.replyStringTV.setVisibility(View.VISIBLE);
			holder.targetUserNameTV.setVisibility(View.VISIBLE);
			holder.replyUserNameTV.setText(getItem(position).getReplyUserName());
			holder.targetUserNameTV.setText(getItem(position).getTargetUserName());
			holder.contentTV.setText(getItem(position).getContent());
		}
		addListener(holder,position);
		return convertView;
	}
	
	private void addListener(Holder holder,final int mainPosition){
		holder.replyUserNameTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(list.get(mainPosition).getKind()==Constant.COMMENT){
					context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", list.get(mainPosition).getUserId()));
				}else{
					context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", list.get(mainPosition).getReplyUserId()));
				}
			}
		});
		holder.targetUserNameTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", list.get(mainPosition).getTargetUserId()));
			}
		});
	}
	class Holder{
		private TextView replyUserNameTV,targetUserNameTV,replyStringTV,contentTV;
	}
}
