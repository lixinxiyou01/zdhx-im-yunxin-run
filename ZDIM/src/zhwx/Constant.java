package zhwx;

/**
 * 常量
 * @Title: ConstantForUikit.java
 * @author Li.Xin @ 中电和讯
 */
public class Constant {

	/** 智微校服务端地址 **/
//	public static final String SERVER_ADDRESS_DEFULT = "http://im.bjcjl.net/im"; //jljy
	public static final String SERVER_ADDRESS_DEFULT = "http://im.zdhx-edu.com/im"; //正式
//	public static final String SERVER_ADDRESS_DEFULT = "http://hwzx.zdhx-edu.com/im"; //汇文中学
//	public static final String SERVER_ADDRESS_DEFULT = "http://tj.zdhx-edu.com/im"; //天津
	public static final String SERVER_ADDRESS_DEFULT_TEST = "http://www.zdhx-edu.com/im"; //中电
	public static final String SERVER_ADDRESS_DEFULT_TEST1 = "http://192.168.1.8:9998/im"; //测试

	//V3 http://117.78.48.224:12000/zddc


	public static final class YunXin {
		/** 云信AppId-智微校 **/
		public static final String APPKEY = "e7b43b346eaf69308a902fd1b654caec";
		public static final String SECRET  = "9a15f6fd0ac7";
	}

	/** IM用户身份 **/
	public static final String USER_TEACHER = "0";  //教师	
	public static final String USER_STUDENT = "1";	//学生
	public static final String USER_PARENT  = "2";	//家长k
	public static final String USER_ADMIN   = "3";	//管理员.0
	public static final String USER_OTHER   = "4";	//其他
	
	/** V3用户类型常量 */
	public static final String USER_TYPE_STUDENT = "0";
	public static final String USER_TYPE_PATRIARCH = "1";
	public static final String USER_TYPE_TEACHER = "2";
	public static final String USER_TYPE_OTHER = "3";
	
	/** 圈子 */
	public static final String CIRCLE_PUBLICFLAG_PRIVATE = "0";	//不公开
	public static final String CIRCLE_PUBLICFLAG_PUBLIC = "1";	//公开
	public static final int COMMENT = 1;
	public static final int REPLY = 2;	
	
	/** 班级圈发送权限 */
	public static final String CLASS_CIRCLE_CAN    = "1";	   			//可发
	public static final String CLASS_CIRCLE_CANNOT = "0";			//不可发

	/** 圈子类型 */
	public static final String CIRCLE_KIND_SCHOOL = "0";	   	//校友圈
	public static final String CIRCLE_KIND_CLASS  = "1";			//班级墙报
	
	/** 圈子消息类型 */
	public static final String RECORD_KIND_COMMENT = "0";		//评论
	public static final String RECORD_KIND_REPLY   = "1";			//回复
	public static final String RECORD_KIND_THUMBUP = "2";		//赞

	/** 消息已读状态 */
	public static final String NOTICE_READ_NO  = "0";	  //未读
	public static final String NOTICE_READ_YES = "1";	  //已读
	
	/** 通知已读状态 */
	public static final String NOTICE_UNREAD = "0";	  //未读
	public static final String NOTICE_READ   = "2";	  //已读	
	
	/** 消息标记状态 */
	public static final String NOTICE_MARK_NO  = "0";	  //标记
	public static final String NOTICE_MARK_YES = "1";	  //未标记

	/** WebAppUrl */
	public static final String WEBAPP_URL_NEWS = "/ne/newsMobile!index.action";	 //校内新闻
	public static final String WEBAPP_URL_WEEKCALENDAR = "/wc/mobile/weekRecordData!index.action";	 //周历
	public static final String WEBAPP_URL_PUBLICITY = "/pu/publicityMobile!index.action";	 //公示
	public static final String WEBAPP_URL_WAGE = "/fi/mobile/salaryData!index.action";	 //我的工资（陈经纶）
	public static final String WEBAPP_URL_WAGE2 = "/bd/webApp/wagemanagement2";	 //工资（牛栏山）
	public static final String WEBAPP_URL_VIEWCOURSEMOBILE = "/bd/mobile/viewCourseMobile!table.action"; //课表
	public static final String WEBAPP_URL_CAMPUSBULLETIN = "/bd/webApp/campusBulletin";	 //校园公告
	public static final String WEBAPP_URL_QUESTION = "/bd/webApp/questionnaire";	 //调查问卷
	public static final String WEBAPP_URL_ANNOUNCEMENT = "/bd/webApp/announcement";	 //系统公告
	public static final String WEBAPP_URL_MESS = "/bd/webApp/canteenmanage";	//食堂
	public static final String WEBAPP_URL_HOMEWORK_CJL = "/il/homeWork!mobileWorkStatisticsIndex.action";	//陈经纶作业
	public static final String WEBAPP_URL_TECH_MANAGE = "http://58.132.20.69/iphoneLoginUserCas";	//课题管理
	public static final String WEBAPP_URL_TECH_JLLT = "http://58.132.20.62/bbs/forum.php";	//经纶论坛
	
	/** 附件扩展名 */
	public static final String ATTACHMENT_DOC  = "doc";	
	public static final String ATTACHMENT_DOCX = "docx";	
	public static final String ATTACHMENT_PPT  = "ppt";	
	public static final String ATTACHMENT_PPTX = "pptx";	
	public static final String ATTACHMENT_PDF  = "pdf";	
	public static final String ATTACHMENT_TXT  = "txt";	
	public static final String ATTACHMENT_XLS  = "xls";	
	public static final String ATTACHMENT_XLSX = "xlsx";	
	public static final String ATTACHMENT_JPG  = "jpg";	
	public static final String ATTACHMENT_JPEG = "jpeg";	
	public static final String ATTACHMENT_GIF  = "gif";	
	public static final String ATTACHMENT_PNG  = "png";	
	public static final String ATTACHMENT_BMP  = "bmp";	
	public static final String ATTACHMENT_RAR  = "rar";	
	public static final String ATTACHMENT_ZIP  = "zip";	
}
