package zhwx.ui.dcapp.score;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.List;

import zhwx.ui.dcapp.score.model.AllTermInfo;


public class TermSpinnerAdapter extends ArrayAdapter<AllTermInfo> {
	
	public TermSpinnerAdapter(Context context,
			List<AllTermInfo> area) {
		super(context, android.R.layout.simple_list_item_1, area);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getFullName());
		return item;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items1, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getFullName());
		return item;
	}
}
