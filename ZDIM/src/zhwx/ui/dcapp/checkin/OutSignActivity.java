package zhwx.ui.dcapp.checkin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.checkin.model.CostData;
import zhwx.ui.dcapp.checkin.model.Cust;

/**   
 * @Title: CIStatisticsActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.checkin
 * @author Li.xin @ zdhx
 * @date 2016年9月26日 下午3:11:05 
 */
public class OutSignActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog mPostingdialog;
	
	private EditText noteET;
	
	private boolean isHasCust = false;
	
	private LinearLayout custContener;
	
	private RelativeLayout rootLay;
	
	private RadioGroup checkRG;
	
	private String json;
	
	private CostData ct;
	
	private String savaJson;
	
	private List<Cust> custList = new ArrayList<Cust>();
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_ci_outsign;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_checkin);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "提交","外勤备注", this);
		setImmerseLayout(getTopBarView(),1);
		rootLay = (RelativeLayout) findViewById(R.id.rootLay);
		rootLay.setVisibility(View.INVISIBLE);
		noteET = (EditText) findViewById(R.id.noteET);
		custContener = (LinearLayout) findViewById(R.id.custContener);
		checkRG = (RadioGroup) findViewById(R.id.checkRG);
		checkRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				if (radioId == R.id.yesRB) {
					isHasCust = true;
					custContener.setVisibility(View.VISIBLE);
				} else {
					isHasCust = false;
					custContener.setVisibility(View.INVISIBLE);
				}
			}
		});
		getNotice();
	}
	
	
	
	private void getNotice(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getCostInfo(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(json);
						}

					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							mPostingdialog.dismiss();
						}
					}, 5);
				}
			}
		}).start();
	}
	
	private void refreshData1(String json) {
		mPostingdialog.dismiss();
		if (json.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		System.out.println(json);
		ct = new Gson().fromJson(json, CostData.class);
		noteET.setText(ct.getRemark());
		if ("0".equals(ct.getStatus())) {
			 
		} else {
			RadioButton yesRB = (RadioButton) findViewById(R.id.yesRB);
			yesRB.setChecked(true);
			isHasCust = true;
			RadioButton noRB = (RadioButton) findViewById(R.id.noRB);
			noRB.setChecked(false);
			custContener.setVisibility(View.VISIBLE);
		}
		custList = ct.getCostList();
		formCustData();
	}
	
	public void formCustData(){
		custContener.removeAllViews();
		if (custList!=null && custList.size() != 0) {
			for (Cust cust : custList) {
				final RelativeLayout dataItem = (RelativeLayout) View.inflate(context, R.layout.list_item_ci_cust, null);
				Button delBT = (Button) dataItem.findViewById(R.id.delBT);
				delBT.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						custContener.removeView(dataItem);
					}
				});
				custContener.addView(dataItem);	
				dataItem.setVisibility(View.VISIBLE);
				EditText costCountET = (EditText) dataItem.findViewById(R.id.custCountET);
				costCountET.setText(cust.getCost());
				final Spinner custKindSP = (Spinner) dataItem.findViewById(R.id.custKindSP);
				custKindSP.setAdapter(new CostTypeAdapter(context, ct.getCostTypeList()));
				custKindSP.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
						custKindSP.setTag(ct.getCostTypeList().get(position).getCode());
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						
					}
				});
				for (int i = 0; i < ct.getCostTypeList().size(); i++) {
					if (ct.getCostTypeList().get(i).getName().equals(cust.getName())) {
						custKindSP.setSelection(i);
					}
				}
			}
		}

		final RelativeLayout addItem = (RelativeLayout) View.inflate(context, R.layout.list_item_ci_add, null);
		Button addBT = (Button) addItem.findViewById(R.id.addBT);
		addBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				custContener.removeView(addItem);
				final RelativeLayout custItem = (RelativeLayout) View.inflate(context, R.layout.list_item_ci_cust, null);
				Button delBT = (Button) custItem.findViewById(R.id.delBT);
				delBT.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						custContener.removeView(custItem);
					}
				});
				final Spinner custKindSP = (Spinner) custItem.findViewById(R.id.custKindSP);
				custKindSP.setAdapter(new CostTypeAdapter(context, ct.getCostTypeList()));
				custKindSP.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
						custKindSP.setTag(ct.getCostTypeList().get(position).getCode());
					}
					
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						
					}
				});
				custContener.addView(custItem);
				custContener.addView(addItem);
			}
		});
		custContener.addView(addItem);
		rootLay.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		mPostingdialog = new ECProgressDialog(this, "正在提交");
		mPostingdialog.show();
		String coustType = "";
		String coust = "";
		if (isHasCust) {
			for (int i = 0; i < custContener.getChildCount()-1; i++) {
				RelativeLayout dataItem = (RelativeLayout) custContener.getChildAt(i);
				EditText costCountET = (EditText) dataItem.findViewById(R.id.custCountET);
				Spinner custKindSP = (Spinner) dataItem.findViewById(R.id.custKindSP);
				coustType += custKindSP.getTag()+",";
				coust += costCountET.getText()+",";
				if (StringUtil.isBlank(costCountET.getText().toString())) {
					ToastUtil.showMessage("有报销项未填写金额");
					costCountET.setError("未填写金额");
					mPostingdialog.dismiss();
					return;
				}
			}
		}
		
		map.put("id", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		map.put("coustType", new ParameterValue(coustType));
		map.put("coust", new ParameterValue(coust));
		map.put("remark", new ParameterValue(noteET.getText().toString()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					savaJson = UrlUtil.save(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							mPostingdialog.dismiss();
							finish();
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							ToastUtil.showMessage("请求失败，请稍后重试");
							mPostingdialog.dismiss();
						}
					}, 5);
				}
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			onSave();
			break;
		}
	}
}
