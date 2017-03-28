package zhwx.ui.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;

/**
 * 修改密码
* @Description: TODO
* @Title: ChangePasswordActivity.java 
* @Package com.bj.android.hzth.parentcircle.activity 
* @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
* @date 2014-11-27 上午10:33:25
 */

public class ChangePasswordActivity extends BaseActivity {
	
	private Activity context;
	
	private EditText oldPwdET,newPwdET,newPwdET2;
	
	private HashMap<String, ParameterValue> map;
	
	private HashMap<String, ParameterValue> v3Map;
	
	private String upLoadFlag = "";
	
	private String upLoadFlag_v3 = "";
	
	private Handler handler = new Handler();
	
	private FrameLayout top_bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		initView();
		setImmerseLayout(top_bar);
	}

	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		oldPwdET = (EditText) findViewById(R.id.oldPwdET);
		newPwdET = (EditText) findViewById(R.id.newPwdET);
		newPwdET2 = (EditText) findViewById(R.id.newPwdET2);
		oldPwdET.requestFocus();
	}
	
	
	public void onSave(View v){
		if(oldPwdET.getEditableText().toString().equals(ECApplication.getInstance().getPassWord())){
			if(newPwdET.getEditableText().toString().length()>=6){
				if(newPwdET.getEditableText().toString().equals(newPwdET2.getEditableText().toString())){
					upLoadNewMessage(newPwdET.getEditableText().toString());
				}else{
					newPwdET2.setError(Html.fromHtml("<font color=#808183>" + "两次输入不一致" + "</font>"));
				}
			}else{
				newPwdET2.setError(Html.fromHtml("<font color=#808183>"
		                + "密码不能少于6位" + "</font>"));
			}
		}else{
			oldPwdET.setError(Html.fromHtml("<font color=#808183>"
	                + "原密码输入错误" + "</font>"));
		}
	}
	
	public void upLoadNewMessage(final String password){
		map = (HashMap<String, ParameterValue>)ECApplication.getInstance().getLoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		map.put("psw", new ParameterValue(password));
		
		v3Map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		v3Map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		v3Map.put("psw", new ParameterValue(password));
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("发送修改请求…");
		pd.setCancelable(false);
		pd.show();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					if (StringUtil.isNotBlank(ECApplication.getInstance().getCurrentIMUser().getV3Id())){
						upLoadFlag_v3 = UrlUtil.changePasswordFromMobile(ECApplication.getInstance().getV3Address(), v3Map);
					} else {
						upLoadFlag = UrlUtil.modifyUserPsw(ECApplication.getInstance().getAddress(),map);  //发送请求
					}
					handler.postDelayed(new Runnable() {
						public void run() {
							if (StringUtil.isNotBlank(ECApplication.getInstance().getCurrentIMUser().getV3Id())){
								if(upLoadFlag_v3.contains("ok")){
									ToastUtil.showMessage("修改成功！");
									ECApplication.getInstance().saveV3PassWord(password);
									finish();
								}else{
									ToastUtil.showMessage("修改失败！");
								}
							} else {
								if(upLoadFlag.contains("success")){
									ToastUtil.showMessage("修改成功！");
									ECApplication.getInstance().savePassWord(password);
									finish();
								}else{
									ToastUtil.showMessage("修改失败！");
								}
							}
							pd.dismiss();
						}
					}, 5);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.showMessage("修改失败！");
					pd.dismiss();
				}
			}
		}).start();
	}
	public void onBack(View v){
		finish();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_change_password;
	}
}
