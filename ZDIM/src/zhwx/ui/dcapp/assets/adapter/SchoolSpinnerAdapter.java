package zhwx.ui.dcapp.assets.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.List;

import zhwx.ui.dcapp.assets.model.ApplyInfos;


public class SchoolSpinnerAdapter extends ArrayAdapter<ApplyInfos.School> {
	private Activity context;
	
	public SchoolSpinnerAdapter(Activity context,List<ApplyInfos.School> area) {
		super(context, android.R.layout.simple_list_item_1, area);
		this.context = context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getName());
		return item;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item =  View.inflate(getContext(), R.layout.spinner_items1, null);
		}
		TextView tv = (TextView) item.findViewById(R.id.spinnerTV);
		tv.setText(getItem(position).getName());
		if (getItem(position).isSelected()) {
			tv.setTextColor(context.getResources().getColor(R.color.main_bg_assets));
		} else {
			tv.setTextColor(context.getResources().getColor(R.color.dark_gray));
		} 
		return item;
	}
}
