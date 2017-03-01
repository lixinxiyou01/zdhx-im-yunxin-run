package zhwx.common.view.capture.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;


public class TitleView extends RelativeLayout {
	protected TextView tvLeft;
	protected TextView tvRight;
	protected TextView tvCenter;
	protected RelativeLayout rl;
	
	protected Integer rightImageSrc = 0;
	protected Integer leftImageSrc = 0;
	protected String centerValue = "";
	protected String rightValue = "";
	
	public void setRightImageOnClickListener(OnClickListener mOnClickListener){
		tvRight.setOnClickListener(mOnClickListener);
	}
	
	public void setLeftImageOnClickListener(OnClickListener mOnClickListener){
		tvLeft.setOnClickListener(mOnClickListener);
	}
	
	public TitleView(Context context) {
		super(context);	
		init();
	}
	
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.TitleViewAttrs);
		rightImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_rightImageSrc, 0);
		leftImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_leftImageSrc,0);
		centerValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		rightValue = attrsArray.getString(R.styleable.TitleViewAttrs_rightValue);
		init();
	}
	
	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.TitleViewAttrs);
		rightImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_rightImageSrc, 0);
		leftImageSrc = attrsArray.getResourceId(R.styleable.TitleViewAttrs_leftImageSrc,0);
		centerValue = attrsArray.getString(R.styleable.TitleViewAttrs_centerValue);
		rightValue = attrsArray.getString(R.styleable.TitleViewAttrs_rightValue);
		init();
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.common_title, this);
		tvLeft =  (TextView)findViewById(R.id.common_title_TV_left); 
		tvRight =  (TextView)findViewById(R.id.common_title_TV_right);
		tvCenter = (TextView) findViewById(R.id.common_title_TV_center);
		rl = (RelativeLayout) findViewById(R.id.common_title_RL);
		if(rightImageSrc != 0){
			tvRight.setBackgroundResource(rightImageSrc);
		}
		if(leftImageSrc != 0){
			tvLeft.setBackgroundResource(leftImageSrc);
			tvLeft.setVisibility(View.VISIBLE);
		}else{
			tvLeft.setVisibility(View.INVISIBLE);
		}
		if(!StringUtils.isNull(centerValue)){
			tvCenter.setText(centerValue);
			this.tvCenter.setVisibility(View.VISIBLE);
		}else{
			tvCenter.setVisibility(View.INVISIBLE);
		}	
		if(!StringUtils.isNull(rightValue)){
			tvRight.setText(rightValue);
		}else if(rightImageSrc != 0){

		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setTitle(String title){
		this.centerValue = title;
		if(this.tvCenter != null){
			this.tvCenter.setText(title);
			this.tvCenter.setVisibility(View.VISIBLE);
		}else{
			tvCenter.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setRightValue(String rightValue){
		this.rightValue = rightValue;
		if(this.tvRight != null){
			this.tvRight.setText(rightValue);
			this.tvRight.setVisibility(View.VISIBLE);
		}else{
			tvRight.setVisibility(View.INVISIBLE);
		}
	}

	public void setBG(int resid){
		rl.setBackgroundResource(resid);
	}
}