package com.netease.nim.demo.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.fragment.TFragment;

import zhwx.ui.circle.ClassCircleActivity;
import zhwx.ui.circle.SchoolCircleActivity;

public class CircleFragment extends TFragment {
	private RelativeLayout schoolCircleBT;
	private RelativeLayout classCircleBT;
	public static TextView unReadCircleCountTV1;
	public static TextView unReadCircleCountTV2;
	public static LinearLayout sMomentLay;
	public static LinearLayout cMomentLay;
	public static ImageView sMomentIV;
	public static ImageView cMomentIV;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.circle_activity, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		schoolCircleBT = findView(R.id.schoolCircleBT);
		schoolCircleBT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), SchoolCircleActivity.class);
				startActivity(intent);
				unReadCircleCountTV1.setVisibility(View.GONE);
			}
		});
		classCircleBT = findView(R.id.classCircleBT);
		classCircleBT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), ClassCircleActivity.class);
				startActivity(intent);
				unReadCircleCountTV2.setVisibility(View.GONE);
			}
		});
		unReadCircleCountTV1 = findView(R.id.unReadCircleCountTV1);
		unReadCircleCountTV2 = findView(R.id.unReadCircleCountTV2);
		sMomentLay = findView(R.id.sMomentLay);
		cMomentLay = findView(R.id.cMomentLay);
		sMomentIV = findView(R.id.sMomentIV);
		cMomentIV = findView(R.id.cMomentIV);
	}
}
