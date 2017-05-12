package zhwx.common.model;


import com.netease.nim.demo.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**   
 * @Title: Apps.java 
 * @Package com.zdhx.edu.im.common.entity
 * @Description: 应用
 * @author Li.xin @ 中电和讯
 * @date 2016-1-6 下午4:22:23 
 */
@SuppressWarnings("serial")
public class Apps implements Serializable {

//	private String[] appCode = ECApplication.getInstance().getCurrentIMUser().getAppCode().split(","); //应用权限

	/**
	 * 维护应用模块总列表
	 * 新增应用直接注入appList即可
	 * @return 
	 */
	public static List<Apps> bulidAllAppList() {
		List<Apps> appList = new ArrayList<Apps>();
		appList.add(new Apps("我的通知", R.drawable.icon_tongzhi,"no"));
		appList.add(new Apps("Z邮",R.drawable.icon_tongzhi,"zmail"));
		appList.add(new Apps("新闻",R.drawable.icon_news,"ne"));
		appList.add(new Apps("周历",R.drawable.icon_weekcalendar,"wc"));
		appList.add(new Apps("公示",R.drawable.icon_gongshi,"pu"));
		appList.add(new Apps("我的工资",R.drawable.icon_wage,"fi"));
		appList.add(new Apps("工资",R.drawable.icon_wage,"fi2"));
		appList.add(new Apps("查看课表",R.drawable.icon_classmanage,"course"));
		appList.add(new Apps("作业",R.drawable.icon_homework,"homework"));
		appList.add(new Apps("校园公告",R.drawable.icon_gonggao,"cb"));
		appList.add(new Apps("系统公告",R.drawable.icon_xitong,"an"));
		appList.add(new Apps("订车管理",R.drawable.icon_carmanager,"cm"));
		appList.add(new Apps("食堂管理",R.drawable.icon_cn,"cn"));
		appList.add(new Apps("作业",R.drawable.icon_homework,"hw"));
		appList.add(new Apps("成绩单",R.drawable.icon_score,"cj"));
		appList.add(new Apps("学生选课",R.drawable.icon_tackcourse,"tc"));
		appList.add(new Apps("学乐堂",R.drawable.icon_xlt,"xlt"));
		appList.add(new Apps("考勤",R.drawable.icon_kaoqin,"ci"));
		appList.add(new Apps("资产管理",R.drawable.icon_zichan,"as"));
		appList.add(new Apps("易耗品管理",R.drawable.icon_store,"sm"));
		appList.add(new Apps("教科研",R.drawable.icon_keyan,"tm"));
		appList.add(new Apps("调查问卷",R.drawable.icon_diaochawenjuan,"qn"));
		appList.add(new Apps("作业统计",R.drawable.icon_homework_cjl,"il"));
		appList.add(new Apps("报修",R.drawable.icon_repair,"rm"));
		return appList;
	}

	/**
	 * 应用分组列表
	 * @return
	 */
	public static List<AppGroup> getAppGroupList() {

		List<AppGroup> appGroupList = new ArrayList<>();

		/**校务办公*/
		AppGroup xwbgAg = new AppGroup("日常办公","xwbg");
		List<Apps> xwbgAppList = new ArrayList<Apps>();
		xwbgAppList.add(new Apps("我的通知", R.drawable.icon_tongzhi,"no"));
		xwbgAppList.add(new Apps("Z邮",R.drawable.icon_tongzhi,"zmail"));
		xwbgAppList.add(new Apps("周历",R.drawable.icon_weekcalendar,"wc"));
		xwbgAppList.add(new Apps("校园公告",R.drawable.icon_gonggao,"cb"));
		xwbgAppList.add(new Apps("我的工资",R.drawable.icon_wage,"fi"));
		xwbgAppList.add(new Apps("工资",R.drawable.icon_wage,"fi2"));
		xwbgAppList.add(new Apps("报修",R.drawable.icon_repair,"rm"));
		xwbgAppList.add(new Apps("资产管理",R.drawable.icon_zichan,"as"));
		xwbgAppList.add(new Apps("易耗品管理",R.drawable.icon_store,"sm"));
		xwbgAppList.add(new Apps("订车管理",R.drawable.icon_carmanager,"cm"));
		xwbgAppList.add(new Apps("食堂管理",R.drawable.icon_cn,"cn"));
		xwbgAppList.add(new Apps("调查问卷",R.drawable.icon_diaochawenjuan,"qn"));
		xwbgAppList.add(new Apps("公示",R.drawable.icon_gongshi,"pu"));
		xwbgAppList.add(new Apps("系统公告",R.drawable.icon_xitong,"an"));
		xwbgAg.setApps(xwbgAppList);
		appGroupList.add(xwbgAg);

		/**教务*/
		AppGroup jwAg = new AppGroup("教务","jw");
		List<Apps> jwAppList = new ArrayList<Apps>();
		jwAppList.add(new Apps("查看课表",R.drawable.icon_classmanage,"course"));
		jwAppList.add(new Apps("作业统计",R.drawable.icon_homework_cjl,"il"));
		jwAppList.add(new Apps("成绩单",R.drawable.icon_score,"cj"));
		jwAppList.add(new Apps("学生选课",R.drawable.icon_tackcourse,"tc"));
		jwAg.setApps(jwAppList);
		appGroupList.add(jwAg);

		/**教学*/
		AppGroup jxAg = new AppGroup("教学","jx");
		List<Apps> jxAppList = new ArrayList<Apps>();
		jxAppList.add(new Apps("作业",R.drawable.icon_homework,"homework"));
		jxAppList.add(new Apps("作业",R.drawable.icon_homework,"hw"));
		jxAg.setApps(jxAppList);
		appGroupList.add(jxAg);

		/**教科研*/
		AppGroup jkyAg = new AppGroup("教科研","jky");
		List<Apps> jkyAppList = new ArrayList<Apps>();
		jkyAppList.add(new Apps("教科研",R.drawable.icon_keyan,"tm"));
		jkyAg.setApps(jkyAppList);
		appGroupList.add(jkyAg);

		/**德育管理*/
		AppGroup dyglAg = new AppGroup("德育管理","dygl");
		List<Apps> dyglAppList = new ArrayList<Apps>();
		dyglAg.setApps(dyglAppList);
		appGroupList.add(dyglAg);

		/**资源*/
		AppGroup zyAg = new AppGroup("资源","zy");
		List<Apps> zyAppList = new ArrayList<Apps>();
		zyAg.setApps(zyAppList);
		appGroupList.add(zyAg);

		/**市区应用*/
		AppGroup sqyyAg = new AppGroup("市区应用","sqyy");
		List<Apps> sqyyAppList = new ArrayList<Apps>();
		sqyyAg.setApps(sqyyAppList);
		appGroupList.add(sqyyAg);

		/**其它*/
		AppGroup elseAg = new AppGroup("更多应用","else");
		List<Apps> elseAppList = new ArrayList<Apps>();
		elseAppList.add(new Apps("新闻",R.drawable.icon_news,"ne"));
		elseAppList.add(new Apps("学乐堂",R.drawable.icon_xlt,"xlt"));
		elseAppList.add(new Apps("考勤",R.drawable.icon_kaoqin,"ci"));
		elseAg.setApps(elseAppList);
		appGroupList.add(elseAg);

		return  appGroupList;
	}
	
	public static Map<String, Apps> defaultAppMap;
	
	/**
	 * 单例 获取默认应用列表
	 * @return
	 */
	public static synchronized Map<String, Apps> getDefaultAppMap() {
		if(defaultAppMap != null) {
			return defaultAppMap;
		}
		defaultAppMap = new HashMap<String, Apps>();
		for (Apps app : bulidAllAppList()) {
			defaultAppMap.put(app.getCode(), app);
		}
		return defaultAppMap;
	}
	
	/**
	 * 获取应用
	 * @return
	 */
	public static synchronized Apps getApp(String code) {
		if ("sr".equals(code)) {
			code = "sm";
		}
		Apps app = getDefaultAppMap().get(code);
		if (app == null) {
			app = new Apps("开发中", R.drawable.ic_launcher, code);
		}
		return app;
	}
	
	private String name = "";
	private int iconDrawable = 0;
	private String code;
	
	public Apps(String name, int icon, String code) {
		super();
		this.name = name;
		this.iconDrawable = icon;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIcon() {
		return iconDrawable;
	}
	public void setIcon(int icon) {
		this.iconDrawable = icon;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
