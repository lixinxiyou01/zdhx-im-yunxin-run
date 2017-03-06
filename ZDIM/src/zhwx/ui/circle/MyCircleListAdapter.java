package zhwx.ui.circle;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.Date;
import java.util.List;

import zhwx.common.model.Moment;
import zhwx.common.util.DateUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;


public class MyCircleListAdapter extends BaseAdapter {

	private Activity context;

	private List<Moment> list;

	private ImageLoader mImageLoader;

	private ImageView headIV;

	private String headUrl;

	public MyCircleListAdapter(Activity context, List<Moment> list,
			ImageView headIV, String headUrl) {
		super();
		this.context = context;
		this.list = list;
		this.headIV = headIV;
		this.headUrl = headUrl;
		mImageLoader = new ImageLoader(context);
		if(headUrl!=null){
			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + headUrl, headIV,
					false);
		}
	}

	@Override
	public int getCount() {
		if(headUrl!=null){
			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + headUrl, headIV,
					false);
		}
		return list.size();
	}

	@Override
	public Moment getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, R.layout.list_item_mycircle,
					null);
			holder.dayTV = (TextView) convertView.findViewById(R.id.dayTV);
			holder.monthTV = (TextView) convertView.findViewById(R.id.monthTV);
			holder.weekTV = (TextView) convertView.findViewById(R.id.weekTV);
			holder.contentTV = (TextView) convertView
					.findViewById(R.id.contentTV);
			holder.imgCountTV = (TextView) convertView
					.findViewById(R.id.imgCountTV);
			holder.imgIV = (ImageView) convertView.findViewById(R.id.imgIV);
			holder.dateLay = (LinearLayout) convertView
					.findViewById(R.id.dateLay);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (getItem(position).getPicUrls() != null
				&& getItem(position).getPicUrls().size() != 0) {
			mImageLoader.DisplayImage(
					ECApplication.getInstance().getAddress() + getItem(position).getPicUrls().get(0)
									.getSmallPicUrl(), holder.imgIV, false);
			holder.imgCountTV.setText("共" + getItem(position).getPicUrls().size() + "张");
			holder.imgCountTV.setVisibility(View.VISIBLE);
			holder.imgIV.setVisibility(View.VISIBLE);
			holder.contentTV.setBackgroundResource(0);
		} else {
			holder.contentTV.setBackgroundResource(R.color.light_gray);
			holder.imgCountTV.setVisibility(View.GONE);
			holder.imgIV.setVisibility(View.GONE);
		}
		holder.dateLay.setVisibility(View.VISIBLE);
		if (getItem(position).getPublishTime() != null) {
			String[] time = getItem(position).getPublishTime().split(" ");
			Date data = DateUtil.getDate(time[0]);
			holder.dayTV.setText(data.getDate() < 10 ? "0" + data.getDate()
					: data.getDate() + "");
			holder.monthTV.setText(data.getMonth() + 1 + "月");
			holder.weekTV.setText(DateUtil.getWeekStr(time[0]));
			if (position > 0) {
				String[] beforeDate = getItem(position - 1).getPublishTime()
						.split(" ");
				if (time[0].equals(beforeDate[0])) {
					holder.dateLay.setVisibility(View.INVISIBLE);
				} else {
					holder.dateLay.setVisibility(View.VISIBLE);
				}
			}
		}
		holder.contentTV.setText(getItem(position).getContent());
		addListener(holder, position);
		return convertView;
	}

	private void addListener(Holder holder, final int mainPosition) {

	}

	class Holder {
		private TextView dayTV, monthTV, weekTV, imgCountTV;
		private TextView contentTV;
		private ImageView imgIV;
		private LinearLayout dateLay;
	}
}
