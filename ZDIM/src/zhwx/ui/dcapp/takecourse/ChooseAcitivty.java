package zhwx.ui.dcapp.takecourse;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import volley.RequestQueue;
import volley.Response;
import volley.VolleyError;
import volley.toolbox.StringRequest;
import volley.toolbox.Volley;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.ExpandableTextView;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.takecourse.listviewgroup.MailListView;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Elective;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Message;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemClickedListener;


public class ChooseAcitivty extends BaseActivity {
	private MailListView mListView;
	private ChooseAdapter mAdapter;
	private RequestQueue mRequestQueue;
	private Elective mElective;
	private String selectString;
	private FrameLayout top_bar;
	private Activity context;
	private ExpandableTextView noteTV;
	public static int selectedPosition = -1;
	private ECProgressDialog dialog;
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected int getLayoutId() {
		return R.layout.activity_choose_course;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		dialog = new ECProgressDialog(context);
		getTopBarView().setVisibility(View.GONE);
		mRequestQueue = Volley.newRequestQueue(this);
		noteTV = (ExpandableTextView) findViewById(R.id.noteTV);
		initButton();
		downLoadDate();
	}
	
	private void downLoadDate() {
		if (StringUtil.isBlank(MyCourseActivity.ecActivityId)) {
			ToastUtil.showMessage("数据异常，请稍后重试");
			return;
		}
		dialog.setPressText("获取数据中");
		dialog.show();
		Map<String, ParameterValue> map = ECApplication.getInstance().getV3LoginMap();
		map.put("userId", new ParameterValue(MyCourseActivity.userId));
		map.put("ecActivityId", new ParameterValue(MyCourseActivity.ecActivityId));
		StringRequest sRequest = new StringRequest(UrlUtil.toElectiveCourse(ECApplication.getInstance().getV3Address(), map),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						System.out.println(response);
						mElective = new Gson().fromJson(response,Elective.class);
						for (Elective.EcActivityCourseList iterable_element : mElective.getEcElectiveGroupList()) {
							for (Elective.EcActivityCourseList.EcActivityCourse course : iterable_element.getEcActivityCourseList()) {
								try {
									course.setSelectedint(Integer.parseInt(course.getSelected()));
								} catch (Exception e) {

								}
								if ("1".equals(course.getVersioned())) {
									course.setCantSelectReason("已封版");
								}
							}
						}
						initListview(mElective);
						initRule(mElective);
						computing();
						dialog.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
					}
				});
		mRequestQueue.add(sRequest);

		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
	}

	/**
	 * @param mElective2
	 */
	protected void initRule(Elective mElective2) {
		String ruleNote = "";
		String hourNote = "";
		String scoreNote = "";
		String maxCountNote = "";
		String couseNote = "";
		Elective.ElectiveRuleMap ruleMap = mElective2.getElectiveRuleMap();
		hourNote = "学时上限: " + (StringUtil.isBlank(ruleMap.getMaxHour())?"无":Integer.parseInt(ruleMap.getMaxHour())/100);
		scoreNote = "学分上限: " + (StringUtil.isBlank(ruleMap.getMaxHour())?"无":Integer.parseInt(ruleMap.getMaxScore())/100+" 分");
		maxCountNote = "最多选课: " + (StringUtil.isBlank(ruleMap.getMaxHour())?"无":ruleMap.getMaxCount() + " 门");
		couseNote = "冲突规则:";
		for (Elective.ElectiveRuleMap.ruleList rule : ruleMap.getRuleList()) {
			String name = "";
			String maxQuantity = rule.getMaxQuantity();
			String minQuantity = rule.getMinQuantity();
			for (Elective.ElectiveRuleMap.ruleList.Course course : rule.getCourseList()) {
				name += course.getCourseName()+",";
			}
			couseNote += ("【" + name.substring(0, name.length()-1) + "】中最少选择" +  minQuantity + "门最多选择"
			+ maxQuantity + "门;");
		}
		if (StringUtil.isNotBlank(couseNote)) {
			ruleNote =  hourNote + "\n" +scoreNote + "\n" + maxCountNote + "\n" + couseNote.substring(0, couseNote.length()-1);
		} else {
			ruleNote =  hourNote + "\n" +scoreNote + "\n" + maxCountNote;
		}
		noteTV.setText(ruleNote);
	}

	private void initButton() {
		findViewById(R.id.okbt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String names = null;
				for (Elective.EcActivityCourseList.EcActivityCourse iterable : getAllselectdecActivityCourse()) {
					if (iterable.getSelectedint() == 1) {
						if (names != null) {
							names += ("," + iterable.getCourseDisplayName());
						} else {
							names = iterable.getCourseDisplayName() + "";
						}
					}	
				}
				
				//TODO  N选N  最少选几个检测
				String tip = "";
				String ruleNames = "";
				if (mElective.getElectiveRuleMap().getRuleList() != null) {
					for (Elective.ElectiveRuleMap.ruleList rule : mElective.getElectiveRuleMap().getRuleList()) { //规则列表
						int minQuantity = Integer.parseInt(rule.getMinQuantity());
						int seletedCount = 0;
						for (Elective.ElectiveRuleMap.ruleList.Course course : rule.getCourseList()) {  //规则中的课程列表
							ruleNames += course.getCourseName()+",";
							for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllcActivityCourse()) { //全部课程列表
								if (course.getCourseId().equals(iterable_element.getCourseId())) {//全部课程在当前规则中的课程
									if (iterable_element.getSelectedint() == 1) { //已经选中
										seletedCount ++;
									} 
								}
							}
						}
						if (StringUtil.isNotBlank(ruleNames)) {
							tip = "【" + ruleNames.substring(0, ruleNames.length()-1) + "】中至少选择" + minQuantity + "门";
						}
						if(seletedCount < minQuantity) {
							ECAlertDialog buildAlert = ECAlertDialog.buildPositiveAlert(context, R.string.action_settings, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									
								}
							});
							buildAlert.setTitle("提示");
							buildAlert.setMessage(tip);
							buildAlert.show();
							return;
						}
					}
				}
				
				
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context,R.string.action_settings, null,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
							savaCheck();
						}	
				});
				buildAlert.setTitle("选课确认");
				if (StringUtil.isBlank(names)) {
					buildAlert.setMessage("没有选择任何课程，确认提交吗？");
				} else {
					buildAlert.setMessage("确认选择【" + names + "】吗？");
					
				}
				buildAlert.show();
			}
		});
	}

	public void savaCheck() {
		String courseIds = null;
		for (Elective.EcActivityCourseList iterable_element : mElective
				.getEcElectiveGroupList()) {
			for (Elective.EcActivityCourseList.EcActivityCourse iterable : iterable_element.getEcActivityCourseList()) {
				if (iterable.getSelectedint() == 1) {
					if (courseIds != null) {
						courseIds += ("," + iterable.getCourseId());
					} else {
						courseIds = iterable.getCourseId() + "";
					}
				}
			}
		}
		if (courseIds == null) {
			courseIds = "";
		}
		Map<String, ParameterValue> map = ECApplication.getInstance().getV3LoginMap();
		map.put("courseIds", new ParameterValue(courseIds));
		map.put("ecActivityId", new ParameterValue(MyCourseActivity.ecActivityId));
		map.put("alternativeCourse1", new ParameterValue(""));
		map.put("alternativeCourse2", new ParameterValue(""));
		
		StringRequest request = new StringRequest(UrlUtil.saveElectiveCourse(ECApplication.getInstance().getV3Address(), map),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println(response);
						if ("1".equals(response)) {
							ToastUtil.showMessage("成功！");
							ChooseAcitivty.this.finish();
						} else{
							ToastUtil.showMessage("response");
							downLoadDate();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		mRequestQueue.add(request);

	}

	private void initListview(Elective elective) {
		mListView = (MailListView) findViewById(R.id.listview_main);
		List<Message> messages = new ArrayList<Message>();

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				Message msg = new Message();
				msg.setGroupName("Group" + i);
				msg.setInfo("info" + j);
				messages.add(msg);
			}
		}
		mAdapter = new ChooseAdapter(ChooseAcitivty.this, elective, false);//
		// 第三个参数是：第一次填充listview时，分组是否展开
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickedListener(new ItemClickedListener() {

			@Override
			public void onItemClick(View item, int itemPosition) {
				Elective.EcActivityCourseList.EcActivityCourse schoolTermStudentCourse = (Elective.EcActivityCourseList.EcActivityCourse) mAdapter.getItem(itemPosition);
				
				if(schoolTermStudentCourse.getSelectedint() == -2) {
					return;
				}
				if(schoolTermStudentCourse.getVersioned().equals("1")) {
					ToastUtil.showMessage("该课程已封版关闭");
					return;
				}
				if (schoolTermStudentCourse.getSelectedint() == -1) {
					if ("1".equals(schoolTermStudentCourse.getSelected())) {
						schoolTermStudentCourse.setSelectedint(1);
						computing();
					} 
					return;
				}
				if (schoolTermStudentCourse.getSelectedint() == 1) {
					schoolTermStudentCourse.setSelectedint(0);
					computing();
				} else if (schoolTermStudentCourse.getSelectedint() == 0) {
					schoolTermStudentCourse.setSelectedint(1);
					computing();
				}
			}
		});

	}

	protected ArrayList<Elective.EcActivityCourseList.EcActivityCourse> getAllselectdecActivityCourse() {
		ArrayList<Elective.EcActivityCourseList.EcActivityCourse> ecActivityCourses = new ArrayList<Elective.EcActivityCourseList.EcActivityCourse>();
		for (Elective.EcActivityCourseList iterable_element : mElective
				.getEcElectiveGroupList()) {
			for (Elective.EcActivityCourseList.EcActivityCourse iterable : iterable_element
					.getEcActivityCourseList()) {
				if (iterable.getSelectedint() == 1) {
					ecActivityCourses.add(iterable);
				}
			}
		}
		return ecActivityCourses;

	}

	protected ArrayList<Elective.EcActivityCourseList.EcActivityCourse> getAllcActivityCourse() {
		ArrayList<Elective.EcActivityCourseList.EcActivityCourse> ecActivityCourses = new ArrayList<Elective.EcActivityCourseList.EcActivityCourse>();
		for (Elective.EcActivityCourseList iterable_element : mElective
				.getEcElectiveGroupList()) {
			for (Elective.EcActivityCourseList.EcActivityCourse iterable : iterable_element
					.getEcActivityCourseList()) {

				ecActivityCourses.add(iterable);

			}
		}
		return ecActivityCourses;

	}

	protected ArrayList<Elective.EcActivityCourseList.EcActivityCourse> getAllunSelectedcActivityCourse() {
		ArrayList<Elective.EcActivityCourseList.EcActivityCourse> ecActivityCourses = new ArrayList<Elective.EcActivityCourseList.EcActivityCourse>();
		for (Elective.EcActivityCourseList iterable_element : mElective.getEcElectiveGroupList()) {
			for (Elective.EcActivityCourseList.EcActivityCourse iterable : iterable_element
					.getEcActivityCourseList()) {

				if (iterable.getSelectedint() != 1) {
					ecActivityCourses.add(iterable);
				}
			}
		}
		return ecActivityCourses;

	}

	protected void deleteUnselected() {
		for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllunSelectedcActivityCourse()) {
			if ("0".equals(iterable_element.getStatus())) {
				continue;
			}

			iterable_element.setSelectedint(0);
		}
	}

	protected void computing() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				selectString = null;
				int hourcount = 0;// 学时
				int scorecount = 0;// 学分
				{// 累计学时学分
					for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllselectdecActivityCourse()) {
						if (iterable_element.getSelectedint() == 1) {
//							if (selectString == null) {
//								selectString = "已选课程："+ iterable_element.getCourseDisplayName();
//							} else {
//								selectString += "  ,  " + iterable_element.getCourseDisplayName();
//							}
							try {
								hourcount += Integer.parseInt(iterable_element.getHour());
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								scorecount += Integer.parseInt(iterable_element.getScore());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				{// 计算关于学时学分的状态
					int maxhour = 0;
					int maxschore = 0;
					try {
						maxhour += Integer.parseInt(mElective.getElectiveRuleMap().getMaxHour());
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						maxschore += Integer.parseInt(mElective.getElectiveRuleMap().getMaxScore());
					} catch (Exception e) {
						e.printStackTrace();
					}
					deleteUnselected();
					for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllunSelectedcActivityCourse()) {
						if ("1".equals(iterable_element.getVersioned())) {
							continue;
						}

						if (maxhour != 0) {
							int timecoutadd = hourcount;
							try {
								timecoutadd += Integer.parseInt(iterable_element.getHour());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (timecoutadd > maxhour) {
								iterable_element.setSelectedint(-2);
								iterable_element.setCantSelectReasonPreview("超出课时");
								iterable_element.setCantSelectReason("超出课时【"+ maxhour/100 +"】");
							}
						}

						if (maxschore != 0) {
							int scorecountadd = scorecount;
							try {
								scorecountadd += Integer.parseInt(iterable_element.getScore());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (scorecountadd > maxschore) {
								iterable_element.setSelectedint(-2);
								iterable_element.setCantSelectReasonPreview("超出学分");
								iterable_element.setCantSelectReason("超出学分上限【"+ maxschore/100 +"分】");
							}
						}
						{// 课数
							int maxcount = 0;
							try {
								maxcount += Integer.parseInt(mElective.getElectiveRuleMap().getMaxCount());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (maxcount == getAllselectdecActivityCourse().size() && maxcount != 0) {
								iterable_element.setSelectedint(-2);
								iterable_element.setCantSelectReasonPreview("选课上限");
								iterable_element.setCantSelectReason("已达选课数量上限【"+maxcount+"门】");
							}
						}

						{// 选课人数
							int maxcount = 0;
							int selectedNum = 0;
							try {
								maxcount += Integer.parseInt(iterable_element.getMaxCount() == null ? "100000" : iterable_element.getMaxCount());
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								selectedNum += Integer.parseInt(iterable_element.getSelectedNum());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (selectedNum >= maxcount && selectedNum != 0 && maxcount != 0) {
								iterable_element.setSelectedint(-1);
							}
						}
					}
				}

				//TODO N选N冲突规则
				if (mElective.getElectiveRuleMap().getRuleList()!=null) {
					for (Elective.ElectiveRuleMap.ruleList rule : mElective.getElectiveRuleMap().getRuleList()) { //规则列表
						int maxQuantity = Integer.parseInt(rule.getMaxQuantity());
						int seletedCount = 0;
						String names = "";
						for (Elective.ElectiveRuleMap.ruleList.Course course : rule.getCourseList()) {  //规则中的课程
							names += course.getCourseName()+",";
							for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllcActivityCourse()) { //全部课程
								if (course.getCourseId().equals(iterable_element.getCourseId())) {//全部课程在当前规则中的课程
									if (iterable_element.getSelectedint() == 1) { //已经选中
										seletedCount ++;
									}
								}
							}
						}

						if (seletedCount == maxQuantity) { //达到当前冲突上限
							for (Elective.ElectiveRuleMap.ruleList.Course course : rule.getCourseList()) {  //规则中的课程
								for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllcActivityCourse()) { //全部课程
									if (course.getCourseId().equals(iterable_element.getCourseId())) {//全部课程在当前规则中的课程
										if (iterable_element.getSelectedint() != 1 && iterable_element.getSelectedint() != -1) { //已经选中，已满
											iterable_element.setSelectedint(-2);
											iterable_element.setCantSelectReason("【" + (names.substring(0,names.length()-1)) + "】中只可选择" + maxQuantity + "门");
											iterable_element.setCantSelectReasonPreview("课程冲突");
										}
									}
								}
							}
						}
					}
				}

				//TODO 时间冲突检测  classTimeFlag时间冲突是否允许选择 1允许 0不允许
				if ("0".equals(mElective.getElectiveRuleMap().getClassTimeFlag())) {
					for (Elective.EcActivityCourseList.EcActivityCourse iterable_element : getAllselectdecActivityCourse()) { //选中的课程
						String selectTimeString = iterable_element.getTime(); // 5,11;2_2,3_4,4_5 ----- 第5,11周;星期2第2节,星期3第4节,星期4第5节
						if (StringUtil.isNotBlank(selectTimeString)) {  	  //如果上课时间  遍历其他课程  验证是否冲突
							String[] selectWeeks = selectTimeString.split(";")[0].split(",");   	  //周    {5,11}
							String[] selectDateAndCourse = selectTimeString.split(";")[1].split(","); //日、节 {2-2,3-4,4-5}
							for (Elective.EcActivityCourseList.EcActivityCourse course : getAllcActivityCourse()) {			      //全部课程
								String 	timeString	= course.getTime();
								if (StringUtil.isNotBlank(timeString) && (!iterable_element.getCourseId().equals(course.getCourseId()))) {//自己和自己不验证冲突
									String[] weeks= timeString.split(";")[0].split(",");   	          //周
									String[] dateAndCourses = timeString.split(";")[1].split(",");    //日、节
									if (isConflict(selectWeeks, weeks) && isConflict(selectDateAndCourse, dateAndCourses)) {
										if (iterable_element.getSelectedint() != -1) {                //已满
											course.setSelectedint(-2);
											course.setCantSelectReason("上课时间与【" + iterable_element.getCourseDisplayName() + "】冲突");
											course.setCantSelectReasonPreview("时间冲突");
										}
									}
								}
							}
						}
					}
				}
				handler.sendEmptyMessage(1);
			}
		}).start();
	}
	
	//判断两个数组是否有相同元素
	private boolean isConflict(String[] selected, String[] all) {
		boolean isConflict = false;
		for (int i = 0; i < selected.length; i++) {
			for (int j = 0; j < all.length; j++) {
				if (selected[i].equals(all[j])) {
					return true;
				}
			}
		}
		return isConflict; 
	} 
	
	@Override
	protected void onActivityResult(int requsetCode, int resultCode, Intent data) {
		super.onActivityResult(requsetCode, resultCode, data);
		if (resultCode == 100) {
			if (selectedPosition != -1) {
				Elective.EcActivityCourseList.EcActivityCourse schoolTermStudentCourse = (Elective.EcActivityCourseList.EcActivityCourse) mAdapter.getItem(selectedPosition);
				if (schoolTermStudentCourse.getSelectedint() != -1) {
					switch (schoolTermStudentCourse.getSelectedint()) {
					case 0:
						schoolTermStudentCourse.setSelectedint(1);
						break;
					case 1:
						schoolTermStudentCourse.setSelectedint(0);
						break;
					}
					computing();
				}
			}
		}
	}
}
