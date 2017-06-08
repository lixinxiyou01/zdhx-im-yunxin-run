package zhwx.common.util;

import android.util.Log;

import com.netease.nim.demo.ECApplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import zhwx.common.model.ParameterValue;


/**
 * 数据接口
 * 
 * @Title: UrlUtil.java
 * @Package com.bj.android.hzth.parentcircle.utils
 * @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
 * @date 2014-11-21 上午11:16:38
 */

public class UrlUtil {

	public static String getUrlResponse(String url) throws IOException {
		return getUrlResponse(url, null);
	}

	public static String getUrlResponse(String url,Map<String, ParameterValue> map) throws IOException {

		HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url)).openConnection());
		conn.setReadTimeout(10*1000);
		conn.setConnectTimeout(10*1000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		// POST必须大写
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		// 仅对当前请求自动重定向
		conn.setInstanceFollowRedirects(true);
		// header 设置编码
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// 连接
		conn.connect();
		Log.v("address", conn.getURL().toString());
		writeParameters(conn, map);
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			Log.v("error", "错误的相应代码：" + conn.getResponseCode());
			throw new IOException();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			result += temp + "\n";
		}
		reader.close();
		conn.disconnect();
		return result;
	}

	public static void writeParameters(HttpURLConnection conn,Map<String, ParameterValue> map) throws IOException {
		if (map == null) {
			return;
		}
		String content = "";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				content += (i == 0 ? "" : "&") + key + "="
						+ URLEncoder.encode(val, "utf-8");
				i++;
			}
		}
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		Log.e("NoCacheUrl", conn.getURL().toString() + "?" + content);
		out.flush();
		out.close();
	}


	/**
	 * 获取网络文件长度
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static long getFileSize(String url)  throws IOException {
		// 创建连接
		HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url)).openConnection());
		//处理下载读取长度为-1 问题
		conn.setRequestProperty("Accept-Encoding", "identity");
		conn.connect();
		// 获取文件大小
		long length = conn.getContentLength();
		return length;
	}

	public static String getUrl(String url, Map<String, ParameterValue> map) {
		url = url + "?";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(val, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return url;
	}
	
	public static String getUrlForString(String url, Map<String, String> map) {
		url = url + "?";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			try {
				if (StringUtil.isNotBlank(map.get(key))) {
					url += (i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(map.get(key), "utf-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			i++;
		}
		return url;
	}

	public static String getImgUrl(String url, Map<String, ParameterValue> map) {
		if (url.startsWith("http")) {
			Log.e("downLoadUrl",url);
			return  url;
		}
		url = url + "&";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			// String
			for (String val : map.get(key).getValues()) {
				try {
					url += (i == 0 ? "" : "&") + key + "="
							+ URLEncoder.encode(val, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		Log.e("downLoadUrl",url);
		return url;
	}

	public static String checkUrl(String url) {
		String result = url;
		if (url.startsWith("http://")) {
			url = url.replaceFirst("http://", "");
			if (url.contains("//")) {
				url = url.replaceAll("//", "/");
			}
			result = "http://" + url;
		} else {
			result = "http://" + url;
		}
		result = result.replaceAll("：", ":").replaceAll("：", ":")
				.replaceAll(" ", "");
		return result;
	}

	public static String checkBaseParameters(String Parameter) {
		if (Parameter == null) {
			Parameter = "";
		}
		return Parameter;
	}

	/**
	 * 提交表单带文件
	 * 
	 * @param url
	 * @param files
	 * @param map
	 * @return
	 * @throws IOException
	 */
	public static String commitWithFiles(String url, List<File> files,Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		System.out.println(url);
		if (map == null) {
			map = new HashMap<String, ParameterValue>();
		}
		HttpURLConnection conn = (HttpURLConnection) (new URL(checkUrl(url))
				.openConnection());
		conn.setReadTimeout(20*1000);
		conn.setConnectTimeout(20*1000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(true);
		conn.setChunkedStreamingMode(10240);
		conn.setRequestProperty("Charset", "utf-8");
		conn.setRequestProperty("connection", "keep-alive");
		String boundary = UUID.randomUUID().toString();
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);
		// 连接
		conn.connect();
		FileInputStream in = null;
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		// 普通参数
		StringBuffer sb = new StringBuffer();
		// 文件名称
		for (File file : files) {
			sb.append("--" + boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"uploadFileNames\"\r\n\r\n");
			sb.append(file.getName() + "\r\n");
			sb.append("--" + boundary + "\r\n");
		}
		// 其他参数
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			for (String val : map.get(key).getValues()) {
				String name = key;
				sb.append("--" + boundary + "\r\n");
				sb.append("Content-Disposition: form-data; name=\"" + name
						+ "\"\r\n\r\n");
				sb.append(val + "\r\n");
				sb.append("--" + boundary + "\r\n");
			}
		}
		out.write(sb.toString().getBytes("utf-8"));


		long allTotalSize = 0;  //附件总大小
		long hasUploadSize = 0; //已上传大小
		int  allProgress = 0;  //总上传进度
		for (int i = 0; i < files.size(); i++) {
			allTotalSize += files.get(i).length();
		}

		// 文件
		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			StringBuffer strBuffer = new StringBuffer();
			strBuffer.append("--" + boundary + "\r\n");
			strBuffer.append("Content-Disposition: form-data; name=uploadFiles; filename="
					+ file.getName() + "\r\n");
			strBuffer.append("Content-Type: application/octet-stream\r\n");
			strBuffer.append("\r\n");
			try {
				in = new FileInputStream(file);
				out.write(strBuffer.toString().getBytes());

				byte[] buffer = new byte[1024];
				int hasRead = -1;
				int length = 0,progress = 0;
				long totalSize = file.length();
				while ((hasRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, hasRead);
					length += hasRead;  //当前附件上传大小
					hasUploadSize += hasRead;  //总上传量
					progress = (int) ((length * 100) / totalSize);  //当前上传进度
					allProgress = (int) ((hasUploadSize * 100) / allTotalSize);  //总上传进度
					//上传进度回调
					if (callback != null) {
						callback.upLoadProgress(files.size(),i,progress,allProgress);
					}
				}
				out.write("\r\n".getBytes());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new IOException();
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {

				}
			}
		}
		out.write(("--" + boundary + "--\r\n").getBytes());
		out.flush();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			System.out.println(conn.getResponseCode());
			throw new IOException();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String result = "";
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			result += temp;
		}
		reader.close();
		conn.disconnect();
		return result;
	}

	public static String commitWithFiles(String url, File file,Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(file);
		return commitWithFiles(url, files, map,callback);
	}

	/************************************ 数据接口start ************************************************************/

	/**
	 * 验证登录
	 * 
	 * @throws IOException
	 */
	public static String checkLogin(Map<String, ParameterValue> map)
			throws IOException {
		return getUrlResponse(checkUrl(ECApplication.getInstance()
				.getV3Address()) + "/bd/welcome!ajaxValidationUser.action", map);
	}

	/**
	 * 根据登录名和登录密码检查登录情况，若登录失败，返回登录失败原因，若登录成功，返回登录用户的信息
	 * 
	 * @param baseUrl
	 * @param param
	 *            : loginName（登录名），password（登录密码）
	 *            登录失败，返回失败标志（successStatus）,失败原因（errorMsg） 登录成功，返回Map--id,
	 *            name, photoAddress, description, telephone, sex, birthDate,
	 *            job, area, email, qq
	 * @throws IOException
	 */
	public static String getUserWithLoginNameAndPassword(String baseUrl,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/bd/welcome/getUserWithLoginNameAndPassword"), map);
	}

	// 是否是我的好友
	public static String isMyBuddy(String baseUrl,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/buddy/isMyBuddy", map);
	}

	// 添加联系人
	public static String addFriend(String baseUrl,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/buddy/addContact", map);
	}

	// 申请添加好友
	// public static String addFriend(String baseUrl,Map<String, ParameterValue>
	// map) throws IOException{
	// return getUrlResponse(checkUrl(baseUrl)+"/bd/buddy/addFriend",map);
	// }
	// 删除好友
	public static String deleteFriend(String baseUrl,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/buddy/deleteContacts",
				map);
	}

	// 获取组织结构
	public static String getOrganzationTree(String baseUrl,
			Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl)
				+ "/bd/organization/organzationTreeJson", map);
	}

	/**
	 * 获取学校组织结构
	 * 
	 * @param baseUrl
	 * @param map organizationId
	 * @return
	 */
	public static String getDepartmentTreeJson(String baseUrl,
			Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl)
				+ "/bd/organization/getDepartmentTreeJson", map);
	}

	/**
	 * 获取班级组织结构
	 * 
	 * @param baseUrl
	 * @param map
	 *            null
	 * @return
	 */
	public static String getClassTreeJson(String baseUrl,
			Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/organization/getClassTreeJson",
				map);
	}

	/**
	 * 获取组织列表
	 * 
	 * @param baseUrl
	 * @param map
	 *            null
	 * @return
	 */
	public static String getOrganizationJson(String baseUrl) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/organization/getOrganizationJson");
	}

	/**
	 * 获取V3组织列表
	 *
	 * @param baseUrl
	 * @param map
	 *            null
	 * @return
	 */
	public static String getV3OrganizationJson(String baseUrl) throws Exception {
		return getUrlResponse(checkUrl(baseUrl)
				+ "/bd/welcome!ajaxGetSchoolNames.action");
	}

	// 获取好友
	public static String getBuddyJson(String baseUrl,
			Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/buddy/getBuddyJson", map);
	}

	/**
	 * 服务器找人
	 * 
	 * @param baseUrl
	 * @param organizationId
	 *            ,name
	 * @return
	 */
	public static String serachUserWithParam(String baseUrl,
			Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl)
				+ "/bd/user/serachUserWithParam", map);
	}

	/**
	 * 修改密码
	 * 
	 * @param baseUrl
	 * @param userId
	 *            ,psw
	 * @return
	 */
	public static String modifyUserPsw(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/user/modifyUserPsw", map);
	}

	/**
	 * 修改手机
	 * 
	 * @param baseUrl
	 * @param userId
	 *            ,mobileNum
	 * @return
	 */
	public static String updateMobileNum(String baseUrl,
			Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/user/updateMobileNum",
				map);
	}

	/**
	 * 修改电话号码和voipAccount
	 * 
	 * @param baseUrl
	 * @param userId
	 *            ,mobileNum,voipAccount
	 * @return
	 */
	public static String updateMobileNumAndVoipAccount(String baseUrl,
			Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl)
				+ "/bd/user/updateMobileNumAndVoipAccount", map);
	}

	/**
	 * 上传头像（用户id、头像图片）
	 * 
	 * @param baseUrl
	 * @param id
	 *            ,file
	 * @param map
	 * @throws IOException
	 */
	public static String saveHeadPortrait(String baseUrl, File file,
			Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/bd/user/saveHeadPortrait", loginMap), file,map,callback);
	}

	/**
	 * 圈子发状态
	 * 
	 * @param baseUrl
	 * @param content
	 *            内容 tagId 标签id publicFlag 是否公开 location 位置 url 外部链接
	 * @throws IOException
	 */
	public static String savePersonalMoment(String baseUrl, List<File> files,
			Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(
				getUrl(baseUrl + "/bd/momentsAndroid/savePersonalMoment",
						loginMap), files, map,callback);
	}

	/**
	 * 圈子发状态(没有文件)
	 * 
	 * @param baseUrl
	 * @param content
	 *            内容 tagId 标签id publicFlag 是否公开 location 位置 url 外部链接
	 * @throws IOException
	 */
	public static String savePersonalMomentWithoutFile(String baseUrl,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(getUrl(baseUrl + "/bd/momentsAndroid/savePersonalMomentWithoutFile",loginMap), map);
	}

	/**
	 * 获取圈子
	 * 
	 * @param baseUrl
	 * @param userId
	 *            pageNum
	 * @throws IOException
	 */
	public static String getPersonalMoments(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getPersonalMoments", map);
	}

	/**
	 * 评论圈子1
	 * 
	 * @param baseUrl
	 * @param momentId
	 *            content
	 * @throws IOException
	 */
	public static String saveMomentComment(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/saveMomentComment", map);
	}

	/**
	 * 回复评论2
	 * 
	 * @param baseUrl
	 * @param commentId
	 *            content targetUserId
	 * @throws IOException
	 */
	public static String saveCommentReply(String baseUrl,
			Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/saveCommentReply", map);
	}

	/**
	 * 赞圈子3
	 * 
	 * @param baseUrl
	 * @param momentId
	 * @throws IOException
	 */
	public static String thumbsup(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/thumbsup", map);
	}

	/**
	 * 取消赞圈子4
	 * 
	 * @param baseUrl
	 * @param momentId
	 * @throws IOException
	 */
	public static String cancelThumbsup(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/cancelThumbsup", map);
	}

	/**
	 * 删除动态5
	 * @param baseUrl
	 * @param momentId
	 * @throws IOException
	 */
	public static String deleteMoment(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/deleteMoment", map);
	}

	/**
	 * 删除评论6
	 * @param baseUrl
	 * @param commentId
	 * @throws IOException
	 */
	public static String deleteMomentComment(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/deleteMomentComment", map);
	}

	/**
	 * 删除评论的回复7
	 * @param baseUrl
	 * @param replyId
	 * @throws IOException
	 */
	public static String deleteCommentReply(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/deleteCommentReply", map);
	}

	/**
	 * 获取圈子未读数
	 * @param baseUrl
	 * @param time
	 * @throws IOException
	 */
	public static String getNewMomentCount(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/getNewMomentCount", map);
	}

	/**
	 * 获取个人空间
	 * @param baseUrl
	 * @param userId
	 * @param pageNum
	 * @throws IOException
	 */
	public static String getUserMoments(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getUserMoments",map);
	}

	/**
	 * 获取动态详情
	 * @param baseUrl
	 * @param momentId
	 * @throws IOException
	 */
	public static String getMomentDetail(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getMomentDetail",map);
	}

	/**
	 * 获取所有班级圈
	 * @param baseUrl
	 * @param pageNum
	 * @param userId
	 * @throws IOException
	 */
	public static String getAllClassMoments(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getAllClassMoments", map);
	}

	/**
	 * 获取某个班级圈
	 * @param baseUrl
	 * @param pageNum
	 * @param departmentId
	 * @throws IOException
	 */
	public static String getClassMoments(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getClassMoments",map);
	}

	/**
	 * 搜索班级
	 * 
	 * @param baseUrl
	 * @param keywords
	 * @throws IOException
	 */
	public static String findClassMoments(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/findClassMoments", map);
	}

	/**
	 * 获取用户所属班级
	 * @param baseUrl
	 * @param userId
	 * @param userKind
	 *            老师是0 学生家长是1
	 * @return operateFlag为2的为班主任，可以管理发布者
	 * @throws IOException
	 */
	public static String getUserClasses(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getUserClasses",map);
	}

	/**
	 * 获取班级动态的发布者
	 * 
	 * @param baseUrl
	 * @param departmentId
	 * @throws IOException
	 */
	public static String getClassPublishUser(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/getClassPublishUser", map);
	}

	/**
	 * 更新班级动态的发布者
	 * 
	 * @param baseUrl
	 * @param departmentId
	 * @param userIds
	 *            用户id用逗号隔开
	 * @throws IOException
	 */
	public static String updateClassPublishUser(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/momentsAndroid/updateClassPublishUser", map);
	}

	/**
	 * 保存班级动态
	 * @param baseUrl
	 * @param departmentId 部门id content 内容 tagId 标签id publicFlag 是否公开 location 位置 url
	 *            外部链接
	 * @throws IOException
	 */
	public static String saveClassMoment(String baseUrl, List<File> files,
			Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/bd/momentsAndroid/saveClassMoment", loginMap),
				files, map,callback);
	}

	/**
	 * 圈子发状态(没有文件)
	 * @param baseUrl
	 * @param content 内容 departmentId 部门id tagId 标签id publicFlag 是否公开 location 位置
	 *            url 外部链接
	 * @throws IOException
	 */
	public static String saveClassMomentWithoutFile(String baseUrl,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(getUrl(baseUrl + "/bd/momentsAndroid/saveClassMomentWithoutFile",loginMap), map);
	}

	/**
	 * 获取被评&赞论数
	 * @param baseUrl
	 * @param userId
	 *        time
	 * @throws IOException
	 */
	public static String getNewMomentRecordCount(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/getNewMomentRecordCount", map);
	}

	/**
	 * 统计用户登录
	 * @param baseUrl
	 * @param userId
	 *        terminal     android 0, ios 1, pc 2
	 */
	public static String saveOrUpdate(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/loginRecord/saveOrUpdate", map);
	}

	/**
	 * 获取被评论&攒详细列表
	 * @param baseUrl
	 * @param map
	 *            userId，time (不传获取所有的)，kind(0校友圈，1班级墙报)
	 * @throws IOException
	 */
	public static String getNewMomentRecordList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/momentsAndroid/getNewMomentRecordList", map);
	}

	/*************************** 消息中心 ***********************************/
	/**
	 * 获取未读消息列表
	 * @param baseUrl
	 * @param map（userId = 20130407173523547532888172723856 、pageFlag）
	 * @return url
	 * @throws IOException
	 */
	public static String getUnReadMessageList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData!getUnReadMessageList.action", map);
	}
	
	/**
	 * 获取全部消息列表
	 * @param baseUrl
	 * @param map（userId、pageFlag）
	 * @return url
	 * @throws IOException
	 */
	public static String getAllMessageList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/message/getList", map);
	}
	
	/**
	 * 把消息置成删除
	 * @param baseUrl
	 * @param map（ids）
	 * @return url
	 * @throws IOException
	 */
	public static String setMessageDelete(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/message/setDelete", map);
	}

	/**
	 * 把消息置成已读
	 * @param baseUrl
	 * @param map id
	 * @return url
	 * @throws IOException
	 */
	public static String setMessageRead(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/message/setRead", map);
	}

	/**
	 * 获取消息明细
	 * @param baseUrl
	 * @param map（id）
	 * @return url
	 * @throws IOException
	 */
	public static String getH5MessageContent(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData!getH5MessageContent.action", map);
	}

	/**
	 * 获取关注的通知
	 * @param baseUrl
	 * @param map
	 *            （id）
	 * @return url
	 * @throws IOException
	 */
	public static String getMyattentionList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData!getAttentionMessageList.action", map);
	}

	/**
	 * 关注通知
	 * @param baseUrl
	 * @param map noticeId 通知id
	 * @return url
	 * @throws IOException
	 */
	public static String lightonForattention(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/mobile/baseData!lightonMessageForattention.action", map);
	}

	/**
	 * 取消关注
	 * 
	 * @param baseUrl
	 * @param map noticeId 通知id
	 * @return url
	 * @throws IOException
	 */
	public static String lightoffForAttention(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/mobile/baseData!lightoffMessageForAttention.action", map);
	}

	/**
	 * 获取新消息条数
	 * @param baseUrl
	 * @param map（userId）
	 * @return url
	 * @throws IOException
	 */
	public static String getMessageCount(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/mobile/baseData!getMessageCount.action", map);
	}

	/**
	 * 全部标记为已读
	 * @param baseUrl
	 * @param map（userId）
	 * @return url
	 * @throws IOException
	 */
	public static String setAllRead(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/message/setAllRead", map);
	}

//	/**
//	 * 绑定推送服务
//	 * @param baseUrl
//	 * @param map
//	 *            （userId）
//	 * @return url
//	 * @throws IOException
//	 */
//	public static String setUserMobileFlag(String baseUrl,Map<String, ParameterValue> map) throws IOException {
//		return getUrlResponse(checkUrl(baseUrl) + "/bd/mobile/baseData!setUserMobileFlag.action", map);
//	}
	
	/**
	 * 绑定推送服务
	 * @param baseUrl
	 * @param map
	 *            （userId）
	 * @return url
	 * @throws IOException
	 */
	public static String setUserMobileFlag(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/message/updateUserTerminalId", map);
	}

	/**
	 * 验证账户 organizationId 组织id name 姓名 cardCode 证件号 loginName 登录名
	 * @throws IOException
	 */
	public static String validate(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/activation/validate",map);
	}

	/**
	 * 发送短信验证码 mobileNum 电话号码 code 验证码
	 * @throws IOException
	 */
	public static String sendSMSCode(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/activation/sendSMSCode",map);
	}

	/**
	 * 发送语音验证码 mobileNum 电话号码 code 验证码
	 * @throws IOException
	 */
	public static String sendVoiceCode(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/activation/sendVoiceCode", map);
	}

	/**
	 * 绑定手机 mobileNum 电话号码 userId 用户id
	 * @throws IOException
	 */
	public static String bindMobileNum(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/activation/bindMobileNum", map);
	}
	
	/**
	 * 扫码登陆	
	 * @param baseUrl
	 * @param map （uuid;dataSource;userId）
	 * @return url
	 * @throws IOException
	 */
	public static String twoDimensionCodeLogin(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/mobile/twoDimensionCodeLogin!saveUserLoginInfo.action",map);
	}

	/**
	 * 修改密码 password 密码 userId 用户id
	 * @throws IOException
	 */
	public static String resetPassword(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/activation/resetPassword", map);
	}

	/**
	 * 修改V3密码
	 * @param baseUrl
	 * @param id、psw
	 * @throws IOException
	 */
	public static String changePasswordFromMobile(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/bd/mobile/baseData!resetPassword.action"), map);
	}
	
	/**
	 * 获取短信验证码
	 * @param baseUrl
	 * @param loginName， mobileNum，organizationId
	 * @throws IOException
	 */
	public static String validataMoblieNum(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/bd/verify/validataMoblieNum"), map);
	}
	
	/**
	 * 用短信code修改密码
	 * @param baseUrl
	 * @param  userId，newPassword，code
	 * @throws IOException
	 */
	public static String modifyUserPswByCode(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/bd/verify/modifyUserPswByCode"), map);
	}

	/**
	 * 获取教师结构
	 */
	public static String getTeacherTree(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData!getTeacherTree.action", map);
	}

	/**
	 * 获取学生结构
	 */
	public static String getStudentTree(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData!getStudentTree.action", map);
	}

	/**
	 * 获取家长结构
	 */
	public static String getParentTree(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData!getParentTree.action", map);
	}
	
	
	
	/*********************************** 通知 ************************************************************/
	
	
	/**
	 * 获取通知详情
	 * id
	 * @throws IOException
	 */
	public static String getNoticeDetail(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/no/noticeMobile/getNoticeDetail", map);
	}

	/**
	 * 获取全部通知
	 * @param baseUrl userId 用户id pageFlag 第几页
	 * @throws IOException
	 */
	public static String getAllNoticeList(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getAllNoticeList"), map);
	}

	/**
	 * 获取发送的通知(发件箱)
	 * @param baseUrl  userId 用户id pageFlag 第几页
	 * @throws IOException
	 */
	public static String getSendNoticeList(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getSendNoticeList"), map);
	}

	/**
	 * 获取已读通知
	 * @param baseUrl userId 用户id pageFlag 第几页
	 * @throws IOException
	 */
	public static String getUnReadNoticeList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getUnReadNoticeList"), map);
	}

	/**
	 * 获取未读通知
	 * @param baseUrl
	 *            userId 用户id pageFlag 第几页
	 * @throws IOException
	 */
	public static String getUnDeleteNoticeList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getUnDeleteNoticeList"), map);
	}

	/**
	 * 获取已删除通知
	 * @param baseUrl userId 用户id pageFlag 第几页
	 * @throws IOException
	 */
	public static String getDeleteNoticeList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getDeleteNoticeList"), map);
	}

	/**
	 * 获取已关注通知
	 * @param baseUrl userId 用户id pageFlag 第几页
	 * @throws IOException
	 */
	public static String getAttentionNoticeList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getAttentionNoticeList"), map);
	}

	/**
	 * 删除通知
	 * @param baseUrl
	 * @param id 通知id
	 * @throws IOException
	 */
	public static String setNoticeDelete(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/setNoticeDelete"), map);
	}

	/**
	 * 设置通知为已读
	 * @param baseUrl
	 * @param id  通知id
	 * @throws IOException
	 */
	public static String setNoticeRead(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/setNoticeRead"), map);
	}

	/**
	 * 关注
	 * @param baseUrl
	 * @param id 通知id
	 * @throws IOException
	 */
	public static String ajaxLighton(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/ajaxLighton"), map);
	}

	/**
	 * 取消关注
	 * 
	 * @param baseUrl
	 * @param id
	 *            通知id
	 * @throws IOException
	 */
	public static String ajaxLightoff(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/ajaxLightoff"), map);
	}

	/**
	 * 通知内容
	 * @param baseUrl
	 * @param id 通知id widthPx 屏幕宽度
	 * @throws IOException
	 */
	public static String getH5NoticeContentForNative(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/getH5NoticeContent"), map);
	}
	
	/**
	 * 通知内容
	 * @param baseUrl
	 * @param id 通知id widthPx 屏幕宽度
	 * @throws IOException
	 */
	public static String getH5NoticeContent(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getH5NoticeContent"), map);
	}

	/**
	 * 获取未读数量
	 * @param baseUrl
	 * @param userId 用户id
	 * @throws IOException
	 */
	public static String getUnReadNoticeCount(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/getUnReadNoticeCount"), map);
	}

	/**
	 * 获取阅读统计
	 * @param baseUrl
	 * @param id 通知id
	 * @throws IOException
	 */
	public static String getReadDetailData(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/no/noticeMobile/getReadDetailData"), map);
	}

	/**
	 * 发/转发 通知
	 * @param baseUrl
	 * @param subject 主题 content 内容 receiveUserId 接收人id 逗号分隔 attIds 原附件id 逗号分隔
	 *            sourceId 原通知id
	 */
	public static String sendNotice(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/no/noticeMobile/sendNotice", loginMap),files, map,callback);
	}
	/**
	 * 发/转发 通知 (native版通知)
	 * @param baseUrl
	 * @param subject 主题 content 内容 receiveUserId 接收人id 逗号分隔 attIds 原附件id 逗号分隔
	 *            sourceId 原通知id
	 */
	public static String sendNoticeFile(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/no/noticeMobile/sendNoticeFile", loginMap),files, map,callback);
	}

	public static String sendNotice(String baseUrl,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(getUrl(baseUrl + "/no/noticeMobile/sendNotice", loginMap),map);
	}

	/**
	 * 获取通知内容的Html
	 * @param baseUrl
	 * @param map
	 * @throws IOException
	 */
	public static String getNoticeHtmlContent(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(baseUrl + "/no/noticeMobile/getNoticeHtmlContent", map);
	}

	/**
	 * 是否有发通知权限
	 * @param baseUrl
	 * @param map userId
	 * @throws IOException
	 */
	public static String ifNewNoticeGranted(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(baseUrl + "/bd/mobile/baseData/ifNewNoticeGranted", map);
	}

	/**
	 * 获取最近联系人
	 * @param baseUrl
	 * @param userId
	 * @throws IOException
	 */
	public static String getRecentReceiveUser(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getRecentReceiveUser"), map);
	}

	/**
	 * 获取常用联系人
	 * @param baseUrl
	 * @param userId
	 * @throws IOException
	 */
	public static String getTreeOfMyGroup(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/no/noticeMobile/getTreeOfMyGroup"), map);
	}
	
	/**
	 * 获取教师结构
	 */
	public static String getTeacherTreeForNative(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData/getTeacherTree", map);
	}

	/**
	 * 获取学生结构
	 */
	public static String getStudentTreeForNative(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData/getStudentTree", map);
	}

	/**
	 * 获取家长结构
	 */
	public static String getParentTreeForNative(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl) + "/bd/mobile/baseData/getParentTree", map);
	}

	/**
	 * 根据V3Id获取
	 * @param v3Id
	 */
	public static String getAccIdByV3Id(String baseUrl,Map<String, ParameterValue> map) throws Exception{
		return getUrlResponse(checkUrl(baseUrl) + "/bd/user/getAccIdByV3Id", map);
	}

	/************************** 作业 ***************************************************************************/
	/**
	 * 获取学科列表
	 */
	public static String getProjectList(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/homeWork/getSubjectList", map);
	}

	/**
	 * 获取班级列表
	 */
	public static String getClassList(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/homeWork/getClassList",map);
	}

	/**
	 * 教师新增或修改作业 （没有文件）
	 * 
	 * 参数： // homeWorkId 作业id(新增时不需要传) // content 内容 // subject 学科code //
	 * classIds 班级id(多个用逗号隔开) // workDate 作业日期 // workTime 预计时长(整数) //
	 * uploadFiles 文件 // uploadFileNames 文件名 //
	 */

	public static String getAddHomework(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		if (files.size() == 0) {
			return getUrlResponse(getUrl(baseUrl+ "/bd/homeWork/saveOrUpdateHomeWorkWithoutFile",loginMap), map);
		}
		return commitWithFiles(getUrl(baseUrl + "/bd/homeWork/saveOrUpdateHomeWork", loginMap),files, map,callback);
	}

	/**
	 * 获取教师作业列表 参数： userId 用户id pageNum 第几页
	 */
	public static String getTeacharList(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrl(baseUrl + "/bd/homeWork/getTeacherHomeWorkList", map);
	}

	/**
	 * 教师删除作业  参数： homeWorkId 作业id
	 */
	public static String getDelete(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/homeWork/deleteHomeWork", map);
	}

	/**
	 * 教师获取作业详情 参数： homeWorkId 作业id userId 用户id
	 */
	public static String getTcParticulars(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/homeWork/getTeacherHomeWorkDetail", map);
	}

	/**
	 * 获取学生作业列表 参数： userId 用户id pageNum 第几页
	 */
	public static String getStudentList(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrl(baseUrl+ "/bd/homeWork/getStudentHomeWorkList", map);
	}

	/**
	 * 学生接收作业 参数： userId 用户id homeWorkId 作业id
	 */
	public static String getStreceiveWork(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/homeWork/studentReceivedHomeWork", map);
	}

	/**
	 * 学生获取作业详情
	 * 
	 * 参数： userId 用户id homeWorkId 作业id
	 */
	public static String getStuParticulars(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl) + "/bd/homeWork/getStudentHomeWorkDetail", map);
	}
	
	
/** 牛栏山作业 */
	
	/**
	 * 学生获取课程List
	 * 参数：studentId 
	 */
	public static String getCourseList(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrl(baseUrl+ "/il/homeWork!getCourseList.action", map);
	}
	
	/**
	 * 学生获取作业List
	 * 参数：studentId=111&pageNo=1&courseId=222
	 */
	public static String getStudentHomeWork(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		
		return getUrl(checkUrl(baseUrl)+ "/il/homeWork!getStudentHomeWork.action", map);
	}
	
	/**
	 * 学生获取作业详情
	 * 参数：homeWorkId 
	 */
	public static String getHomeWorkDetail(String baseUrl,Map<String, ParameterValue> map) throws Exception {
		return getUrlResponse(checkUrl(baseUrl)+ "/il/homeWork!getHomeWorkDetail.action", map);
	}
	
	/**
	 * 学生提交作业
	 * studentWorkId=2222&content=3r23234&file=
	 */
	public static String saveHomeWorkAnswer(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
			Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/il/homeWork!saveHomeWorkAnswer.action", loginMap),files, map,callback);
	}
	
	/***************************************订车管理***********************************************/
	
	/**
	 * 获取公告  
	 */
	public static String getNotice(String baseUrl,Map<String, ParameterValue> map){
		return getUrl(baseUrl+ "/cm/carMobile!getNotice.action", map);
	}

	/**
	 * 引导页数据
	 * 参数： userId，operationCode=carmanage
	 */
	public static String getIndex(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/cm/carMobile!getIndex.action", map);
	}
	
	/**
	 * 我的订车单（全部）
	 * 参数：userId,status(状态分类),pageNum
	 */
	public static String getMyOrderCarList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/cm/carMobile!getMyOrderCarList.action", map);
	}
	/**
	 * 订车单管理 
	 * 参数pageNum,status和operationCode=carmanage
	 */
	public static String getCarManageList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/cm/carMobile!getCarManageList.action", map);
	}
	/**
	 * 出车列表
	 * 参数pageNum,status 待确认0  待结束1  待评价2
	 */								
	public static String getMyAssigncarList(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/cm/carMobile!getMyAssigncarList.action", map);
	}
	
	/**
	 * 订车单详情
	 * id
	 */
	public static String getOrderCarInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!getOrderCarInfo.action", map);
	}
	
	/**
	 * 订车单详情(司机)
	 * id
	 */
	public static String getAssignCarInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!getAssignCarInfo.action", map);
	}
	
	/**
	 * 订车单状态
	 * id
	 */
	public static String getOrderCarStatus(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!getOrderCarStatus.action", map);
	}
	
	/**
	 * 增加时获取常用地址和原因
	 */
	public static String getInputBaseInformation(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!getInputBaseInformation.action", map);
	}
	
	/**
	 * 保存申请单
	 * userId,departmentId,phone,useDate,arriveTime,address,userCount,personList,reason,instruction
	 * backDate,backCount,backAddress,backTime,backPersonList
	 */
	public static String saveOrderCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!saveOrderCar.action", map);
	}
	
	/**
	 * 删除订车单
	 * id
	 */
	public static String delOrderCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!delOrderCar.action", map);
	}
	
	/**
	 * 取消订车单
	 * id
	 */
	public static String cancelOrderCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!cancelOrderCar.action", map);
	}
	
	/**
	 * 不予派车
	 * id
	 */
	public static String unPassOrderCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!unPassOrderCar.action", map);
	}
	
	/**
	 * 结束派车
	 * id
	 */
	public static String passOrderCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!passOrderCar.action", map);
	}
	
	
	/**
	 * 获取车辆信息
	 * 订车单id  id
	 */
	public static String getCarsInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!getCarsInfo.action", map);
	}
	
	/**
	 * 到派车页面
	 * 参数：id，carId
	 */
	public static String toAssignCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!toAssignCar.action", map);
	}
	
	/**
	 * 生成派车单
	 * 参数：id，carId，driverId，useTime（时分），useAddress，leaveTime（时分）
	 */
	public static String saveAssignCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!saveAssignCar.action", map);
	}
	
	/**
	 * 审核订车单
	 * 参数：id，status( 1通过  2不通过) ，advice
	 */
	public static String checkOrderCar(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!checkOrderCar.action", map);
	}
	
	/**
	 * 确认接单
	 * 参数：assignCarId
	 */
	public static String driverEnsureBus(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!driverEnsureBus.action", map);
	}
	
	/**
	 * 结束任务
	 * 参数：assignCarId
	 */
	public static String driverEndTask(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!driverEndTask.action", map);
	}
	
	/**
	 * 司机评价
	 * 参数：assignCarId，realCount，realTime，realAddress，note
	 * backTime返程时间   backCount返程人数   backPersonList返程人员名单
	 */
	public static String driverFeedback(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!driverFeedback.action", map);
	}
	
	/**
	 * 到评价订车单页面
	 * 参数：id  订车单id
	 */
	public static String toCarUserFeedback(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!toCarUserFeedback.action", map);
	}
	
	/**
	 * 保存订车人评价
	 * saveCarUserFeedback
	 * 参数：id：订车单id
	 * resultJson 评价拼接的json[{oaCarId:派车记录id,satisfaction:2,4,5,feedback:意见}]
	 */
	public static String saveCarUserFeedback(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/cm/carMobile!saveCarUserFeedback.action", map);
	}
	
	
	
	/************************************ 成绩  ************************************************************/
	/**
	 * 获取个人成绩
	 * @param examId
	 * @param studentId
	 * @return
	 */
	public static String getStudentScoreJsonUrl(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/ex/mobile/examMobileTerminal!getStudentScoreJson.action", map);
	}

	/**
	 * 获 年级成绩
	 * @param kind
	 * @return
	 */
	public static String getGradeScoreJsonUrl(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/ex/mobile/examMobileTerminal!getGradeScoreJson.action", map);
	}

	/**
	 * 获 班级成绩
	 * @param eclassId
	 * @return
	 */
	public static String getEclassScoreListUrl(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/ex/mobile/examMobileTerminal!getEclassScoreList.action", map);
	}

	/**
	 * 获取学期集合
	 * @param baseUrl
	 * @param map
	 * @return
	 */
	public static String getSchoolTermAllJsonUrl(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/bd/mobile/baseData!getSchoolTermAllJson.action", map);
	}

	/**
	 * 获取exam
	 * @param schoolTermId
	 * @return
	 */
	public static String getExamListJsonWithSchoolTermIdJsonUrl(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl+ "/ex/mobile/examMobileTerminal!getExamListJsonWithSchoolTermId.action", map);
	}
	
	
	/**  ***************************************** 选课 *************************************************/
	/**
	 * 、到选课说明页面 参数：无 路径：/ec/mobile/ecMobileTerminal!toElectiveCourseNote.action
	 * 返回 ecActivityId 选课活动id ecActivityNote 选课说明 userId 当前用户id
	 */
	public static String toElectiveCourseNote(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl + "/ec/mobile/ecMobileTerminal!toElectiveCourseNote.action" , map);
	}

	/**
	 * @param baseUrl
	 * @param map
	 * @return 到学生选课 参数：userId ecActivityId preview
	 *         路径：/ec/mobile/ecMobileTerminal!toElectiveCourse.action 返回 .....
	 * 
	 *         ecActivity 选课活动信息key-id，kind（活动类型--0校本选课,1分班选课）
	 * 
	 *         electiveRuleMap
	 *         限制条件（课时上限、学分上限、数量上限、几选几、选课最大人数--校本、时间冲突是否允许选择--校本）
	 *         key-maxHour课时上限
	 *         ，maxScore学分上限，maxCount数量上限，classTimeFlag时间冲突是否允许选择1允许
	 *         0不允许，ruleList
	 *         ruleList是规则的集合，集合里是map-key：minQuantity（最少选几个），maxQuantity
	 *         （最多选几个），courseList（课程集合，内有属性courseId,courseName）
	 * 
	 *         ecElectiveGroupList
	 *         所有组的集合，groupId，groupName，groupNum,ecActivityCourseList
	 *         (内有属性id-活动课程id，courseId-bd课程，courseDisplayName，selected-学生是否已选
	 *         是1（字符串的1），否0，versioned-是否封板 是1，否0，selectedNum-课程已选数量)
	 * 
	 *         ecAlternativeCourse
	 *         备选课程key-alternative1Id，alternative2Id（这个就是活动课程的id）
	 * 
	 *         currentUser 当前用户id preview 是否预览，貌似没用
	 */
	public static String toElectiveCourse(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl + "/ec/mobile/ecMobileTerminal!toElectiveCourse.action" , map);
	}

	/**
	 * @param baseUrl
	 *            到我的选课 参数：无
	 *            路径：/ec/mobile/ecMobileTerminal!toMyElectiveCourse.action 返回
	 *            .....
	 * 
	 *            schoolTermInfoList 学期集合 key-id，fullName，startDate
	 *            schoolTermStudentCourseMap 学期对应的学生选课，key-学期id，value-学生选的课
	 *            schoolTermSumScoreMap 学期对应的总学分，key-学期id，value-总学分
	 * @param map
	 * @return
	 */
	public static String toMyElectiveCourse(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl + "/ec/mobile/ecMobileTerminal!toMyElectiveCourse.action" , map);
	}

	/**
	 * @param baseUrl
	 * @param map
	 *            保存选课结果 参数：courseIds（bd的课程id字符串，以逗号分隔） ecActivityId（选课活动id）
	 *            alternativeCourse1（备选课程1id） alternativeCourse2 （备选课程2id）
	 *            路径：/ec/mobile/ecMobileTerminal!saveElectiveCourse.action 返回
	 *            ..... 选课结果信息集合（长度是2），【0】saveSuccessMsg ，【1】saveFailedMsg
	 * @return
	 */
	public static String saveElectiveCourse(String baseUrl, Map<String, ParameterValue> map) {
		return getUrl(baseUrl + "/ec/mobile/ecMobileTerminal!saveElectiveCourse.action" , map);
	}

	/*************************************  资产管理    *****************************************************/
	/**
	 * 引导页数据
	 */
	public static String getIndexJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!getIndexJson.action", map);
	}

	/**
	 * 申请资产页面数据
	 * operationCode=CheckManage
	 */
	public static String getApplicationJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!getApplicationJson.action", map);
	}

	/**
	 * 发放列表
	 */
	public static String getGrantListJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!getGrantListJson.action"), map);
	}

	/**
	 * 按状态获取资产审核页面列表
	 * resultJson={"status":"1"}
	 */
	public static String getCheckListJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrl(checkUrl(baseUrl + "/ao/assetMobile!getCheckListJson.action"), map);
	}

	/**
	 * 提交审核
	 * {"checkResult":"2","checkReason":"无理","applyIds":"20160815140013662113161426764675"}
	 */
	public static String saveCheck(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!saveCheck.action"), map);
	}
	/**
	 * 提交资产申请
	 * json{"departmentId":申请部门id,"assetKindId":资产类别,"userId":审核人id,"schoolId":被申请校区id,"applyDate":申请日期,"demand":需求,"reason":用途}
	 */
	public static String saveApplication(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!saveApplication.action"), map);
	}

	/**
	 * 资产详情
	 * code 资产编码
	 */
	public static String getAssetInfoJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!getAssetInfoJson.action"), map);
	}

	/**
	 * 审核详情
	 * id
	 */
	public static String getCheckInfoJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!getCheckInfoJson.action"), map);
	}

	/**
	 * 发放详情
	 * id
	 */
	public static String getGrantInfoJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!getGrantInfoJson.action"), map);
	}

	/**
	 *  获取我的资产
	 */
	public static String getMyAssetListJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!getMyAssetListJson.action"), map);
	}

	/**
	 *  获取全部未发放资产
	 */
	public static String getInStockAssetListJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrl(checkUrl(baseUrl + "/ao/assetMobile!getInStockAssetListJson.action"), map);
	}

	/**
	 *  获取全部资产
	 */
	public static String getAllAssetListJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrl(checkUrl(baseUrl + "/ao/assetMobile!getAllAssetListJson.action"), map);
	}

	/**
	 *  按条件查找资产
	 *  json{"name":名称,"code":编码,"status":状态编码,"brandId":品牌id,"patternId":型号id,"purchaseDateStart":购置日期开始,
	 *  "purchaseDateEnd":购置日期截止,"locationId":存放地点,"kitFlag":是否成套,"pageNum":分页页码} &operationCode：AssetManage
	 */
	public static String getAssetListJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/ao/assetMobile!getInStockAssetListJson.action"), map);
	}

	/**
	 * 获取查找资产页面数据（类型列表  规格列表  状态列表  地点列表）
	 * operationCode=CheckManage
	 */
	public static String getAssetPageJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!getAssetPageJson.action", map);
	}
	/**
	 * 已归还详情
	 * id
	 */
	public static String getReturnInfoJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!getReturnInfoJson.action", map);
	}

	/**
	 * @param id:发放记录id；locationId：物理位置id；applicationRecordId：申请记录id
	 * ；userId：领用人id；departmentId：领用部门id；assetIds：资产ids（用，分割）
	 * 	移动端确认发放
	 */
	public static String saveGrant(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
								   Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/ao/assetMobile!saveGrant.action", loginMap),files, map,callback);
	}

	public static String saveGrant(String baseUrl,Map<String, ParameterValue> loginMap) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!saveGrant.action",loginMap);
	}


	/**
	 * 获取库房详情
	 * locationId
	 */
	public static String getInStockNumJson(String baseUrl,Map<String, ParameterValue> loginMap) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!getInStockNumJson.action",loginMap);
	}

	/**
	 * 获取补签通知
	 */
	public static String getNotSignedGrantNumJson(String baseUrl,Map<String, ParameterValue> loginMap) throws IOException {
		return getUrlResponse(checkUrl(baseUrl) + "/ao/assetMobile!getNotSignedGrantNumJson.action",loginMap);
	}
	
	
	/*********************************** 外出考勤 ******************************/
	/**
	 * 考勤
	        参数：flag -- start 上班  end下班
       address 
       userId
       equipType 设备型号
	 */
	public static String doAttendance(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/ta/teacherAttendanceMobile!doAttendance.action", map);
	}
	
	/**
	 * 补签
	        参数：startTime
         	address 
       		note
       		userId
       		equipType 设备型号
	 */
	public static String supplementSave(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/ta/teacherAttendanceMobile!supplementSave.action", map);
	}
	
	/**
	 * 考勤记录
	        参数：date   YY-MM-DD 不传默认当天
            userId
	       返回 : map(status,startTime,endTime)
	 */
	public static String attendance(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/ta/teacherAttendanceMobile!attendance.action", map);
	}
	
	/**
	 * 考勤统计
	       参数：userId
	 */
	public static String getListOfDate(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/ta/teacherAttendanceMobile!getListOfDate.action", map);
	}
	
	/***
	 * 接口  取到外出费用详情
	 * status 0没有花费      1有花费
	 * remark  外出详情
	 */
	public static String getCostInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/ta/teacherAttendanceMobile!getCostInfo.action", map);
	}
	
	/***
	 * 外出备注保存方法
	 *  参数：考勤人id  外出花费类型，花费金额  中间用逗号隔开
	 */
	
	public static String save(String baseUrl,Map<String, ParameterValue> map) throws IOException{
		return getUrlResponse(baseUrl+ "/ta/teacherAttendanceMobile!save.action", map);
	}

	/************************************易耗品管理  **********************************************************/

	/**
	 * 首页数字
	 * @param baseUrl
	 * @throws IOException
	 */
	public static String getUserOperation(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getUserOperation.action"), map);
	}

	/**
	 * 个人申领记录列表  分页    ）
	 * @param baseUrl
	 * @param  id ：申领人的id
	 * @throws IOException
	 */
	public static String MyApplyListData(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/applyReceiveRecord!listData.action"), map);
	}

	/**
	 * 个人申领记录详情
	 * @param baseUrl
	 * @param  id  申领记录id
	 * @throws IOException
	 */
	public static String getApplyRecordView(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getApplyRecordView.action"), map);
	}

	/**
	 * 申领页面数据（单号、申领部门list&部门审核人、领用类型list）
	 * @param baseUrl
	 * @param  id ：申领人的id
	 * @throws IOException
	 */
	public static String getNewApplyRecordInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getNewApplyRecordInfo.action"), map);
	}

	/**
	 * 提交申领
	 * @param baseUrl
	 * @param  resultJson=
	{"code":"20170113003","departmentId":"20130307155116931287536222991657","deptCheckUserId":
	"20140923142329769262316155434533","kind":"grsl","userId":"20140923142329769262316155434533","date":
	"2017-01-12","reason":"123","note":"456","goodsInfos":
	[{"goodsInfoId":"20161215145230657733134522844537","count":"4"},
	{"goodsInfoId":"20161215145142513113863726007316","count":"5"}]}]}
	deptCheckUserId 可以去掉或者为空，假如设置部门不需要审核的话
	 * @throws IOException
	 */
	public static String saveApplyReceiveRecord(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!saveApplyReceiveRecord.action"), map);
	}

	/**
	 * 物品分类树形列表数据
	 * @param baseUrl
	 * @throws IOException
	 */
	public static String getGoodsKindJson(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getGoodsKindJson.action"), map);
	}

	/**
	 * 具体物品分类下的物品list
	 * @param id : 类别id
	 * @throws IOException
	 */
	public static String getGoodsInfoPage(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getGoodsInfoPage.action"), map);
	}

	/**
	 * 待审核列表（部门） 分页
	 * @param id
	 * @throws IOException
	 */
	public static String auditListData(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/applyReceiveRecord!auditListData.action"), map);
	}

	/**
	 * 待审核列表（总务） 分页
	 * @param id
	 * @throws IOException
	 */
	public static String auditZWListData(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/applyReceiveRecord!auditZWListData.action"), map);
	}
	/**
	 * 查看待审核申领详情
	 * @param id
	 * @throws IOException
	 */
	public static String getApplyAuditView(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getApplyAuditView.action"), map);
	}
	/**
	 * 提交审核（通过or不通过 + 说明）
	 * @param resultJson=
	{"id":申领单id,"checkStatus":审核状态,"checkOpinion":审核意见,"kind":审核类型}

	审核状态 ：  0-未审核  1-部门不通过   2-部门通过    3-总务不通过   4-总务通过
	审核类型 :   dept-部门审核     zw-总务审核
	 * @throws IOException
	 */
	public static String saveCheckApplyReceiveRecord(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!saveCheckApplyReceiveRecord.action"), map);
	}

	/**
	 *待发放申领列表
	 * @param
	 * @throws IOException
	 */
	public static String toApplyListData(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/applyReceiveList!listData.action"), map);
	}

	/**
	 *（单号、出库类型list、校区仓库树形数据）
	 * @param kind=1&id=  kind : 1-android   2-IOS
	 * @throws IOException
	 */
	public static String getProvideGoodsDetal(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getProvideGoodsDetal.action"), map);
	}

	/**
	 * 提交发放（带签字图片）
	 * @param resultJson=
	{"applyReceiveId":"申领单id","code":"出库单号","date":"2017-01-12","warehouseId":
	"20161215144649001974178888537724","outKind":"grly","receiveUserId":
	"20140923142329769262316155434533","note":"好东
	西","signatureId":"20161226150029460238377955427093","locationId":
	"20150519204038324417928924235555","goodsInfos":
	[{"goodsInfoId":"20161215145230657733134522844537","count":"1","sum":"70"},
	{"goodsInfoId":"20161215145305724085501607875380","count":"1","sum":"300"}]}]}
	 * @throws IOException
	 */
	public static String saveProvideGoods(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!saveProvideGoods.action"), map);
	}

	public static String saveProvideGoods(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
										  Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/sr/storeroom!saveProvideGoods.action", loginMap),files, map,callback);
	}

	/**
	 * 出库单列表  分页
	 * @param
	 * @throws IOException
	 */
	public static String getOutWarehouseList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getOutWarehouseList.action"), map);
	}

	/**
	 * 按姓名搜索出库单
	 * @param
	 * @throws IOException
	 */
	public static String searchGetOutByUserName(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!searchGetOutByUserName.action"), map);
	}

	/**
	 * 出库单详情
	 * @param id ： 出库单id
	 * @throws IOException
	 */
	public static String getOutWarehouseView(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getOutWarehouseView.action"), map);
	}

	/**
	 * 删除出库单
	 * @param id ： 出库单id
	 * @throws IOException
	 */
	public static String outWarehouseDelete(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!outWarehouseDelete.action"), map);
	}

	/**
	 * 修改出库单页面数据
	 * @param id : 出库单id
	kind : 1-android   2-IOS
	 * @throws IOException
	 */
	public static String getUpdateOutWarehouseRecordInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getUpdateOutWarehouseRecordInfo.action"), map);
	}

	/**
	 * 提交修改出库单
	 * @param resultJson=
	{"id":"20170112182711137756898084944949","code":"20170112004","date":"2017-01-16","warehouseId":
	"20161215144649001974178888537724","outKind":"grly","receiveUserId":
	"20140923142329769262316155434533","note":"好东
	西","goodsInfos":[{"goodsInfoId":"20161215145230657733134522844537","count":"5","sum":"444"},
	{"goodsInfoId":"20161215145142513113863726007316","count":"3","sum":"555"}]}]}
	 * @throws IOException
	 */
	public static String updateWarehouseRecord(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!updateWarehouseRecord.action"), map);
	}

	/**
	 * 新增出库单页面数据
	 * @param kind : 1-android   2-IOS
	 * @throws IOException
	 */
	public static String getNewOutWarehouseRecordInfo(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getNewOutWarehouseRecordInfo.action"), map);
	}

	/**
	 * 提交新增出库单
	 * @param resultJson=
	{"code":"20170112004","date":"2017-01-12","warehouseId":
	"20161215144649001974178888537724","outKind":"grly","receiveUserId":
	"20140923142329769262316155434533","note":"好东西","goodsInfos":
	[{"goodsInfoId":"20161215145230657733134522844537","count":"4","sum":"500"},
	{"goodsInfoId":"20161215145142513113863726007316","count":"5","sum":"600"}]}]}
	 * @throws IOException
	 */
	public static String savaWarehouseRecord(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!savaWarehouseRecord.action"), map);
	}

	/**
	 * 物品统计列表 （名称、库存数量金额、出库数量金额、入库数量金额）
	 * @throws IOException
	 */
	public static String getGoodsStatistics(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getGoodsStatistics.action"), map);
	}

	/**
	 * 物品库存详情列表  （校区  库房 数量	金额）
	 * id
	 * @throws IOException
	 */
	public static String getGoodsStock(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getGoodsStock.action"), map);
	}
	/**
	 * 物品入库详情列表  （校区  库房 数量	金额）
	 * id
	 * @throws IOException
	 */
	public static String getGoodsIntoWarehouseDetal(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getGoodsIntoWarehouseDetal.action"), map);
	}
	/**
	 * 获取补签详情
	 * id
	 * @throws IOException
	 */
	public static String getReSignDetail(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getReSignDetail.action"), map);
	}

	/**
	 * 物品出库详情列表  （校区  库房 数量	金额）
	 * id
	 * @throws IOException
	 */
	public static String getGoodsOutWarehouseDetal(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getGoodsOutWarehouseDetal.action"), map);
	}

	/**
	 * 物品出库详情
	 * id
	 * @throws IOException
	 */
	public static String OutWareDetail(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!OutWareDetail.action"), map);
	}

	/**
	 * 物品出库详情
	 * id
	 * @throws IOException
	 */
	public static String getStoreHandleRecord(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/sr/storeroom!getStoreHandleRecord.action"), map);
	}


	/**
	 * 提交补签带签字图
	 *	id
	 */
	public static String saveReSign(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,
										  Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/sr/storeroom!saveReSign.action", loginMap),files, map,callback);
	}

	/*******************************易耗品管理end*********************************************/

	/**
	 * 云信群发生变化通知IM后台
	 *	map tid （云信群组ID）
	 */
	public static String synchroTeam(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/bd/neteaseTeam/mobile/synchroTeam"), map);
	}
	/******************************** 报修 ***************************************************/


	/**
	 * 请求首页权限和数字（报修管理没有功能）
	 *	userId
	 */
	public static String getIndexData(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!getIndexData.action"), map);
	}

	/**
	 * 获取故障信息
	 *	deviceId
	 */
	public static String getMalfunctionPlaceList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!getMalfunctionPlaceList.action"), map);
	}

	/**
	 * 获取设备一级分类
	 * 维修组Id
	 */
	public static String getDeviceKindLevelOneList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getDeviceKindLevelOneList.action"), map);
	}
	/**
	 * 获取设备二级分类
	 * levelOneId
	 */
	public static String getDeviceKindLevelTwoList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getDeviceKindLevelTwoList.action"), map);
	}

	/**
	 * 获取维修组
	 * levelOneId
	 */
	public static String getMaintenanceTeamList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getMaintenanceTeamList.action"), map);
	}

	/**
	 * 提交报修单
	 */
	public static String submitRepairApply(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/re/repairsRecordMobile!submitRepairApply.action", loginMap),files, map,callback);
	}

	public static String submitRepairApply(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!submitRepairApply.action"), map);
	}
	/**
	 * 获取我的报修单列表
	 * status，userId
	 */
	public static String getRepairRequestListByStatus(String baseUrl,Map<String, ParameterValue> map){
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getRepairRequestListByStatus.action"), map);
	}

	/**
	 * 获取我的维修单列表
	 * status，userId
	 */
	public static String getMyRepairListByStatus(String baseUrl,Map<String, ParameterValue> map){
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getMyRepairListByStatus.action"), map);
	}

	/**
	 * 获取我的维修单列表
	 * status，userId
	 */
	public static String getManageRepairListByStatus(String baseUrl,Map<String, ParameterValue> map){
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getManageRepairListByStatus.action"), map);
	}

	/**
	 * 获取报修单详情
	 * repairId
	 */
	public static String getRepairDetail(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!getRepairDetail.action"), map);
	}

	/**
	 * 获取报修记录
	 * repairId
	 */
	public static String getRepairRecord(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!getRepairRecord.action"), map);
	}

	/**
	 * 接单
	 * repairId
	 */
	public static String receiveRequest(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!receiveRequest.action"), map);
	}

	/**
	 * 可派单人员列表
	 * repairId
	 */
	public static String getCanDoWorkerList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!getCanDoWorkerList.action"), map);
	}

	/**
	 * 派单操作
	 * repairId，sendMessageFlag，workerId
	 */
	public static String saveSendRequest(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!saveSendRequest.action"), map);
	}
	/**
	 * 删除报修单
	 * repairId
	 */
	public static String repairRecordDelete(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!repairRecordDelete.action"), map);
	}

	/**
	 * 维修工提交反馈
	 */
	public static String saveWokerFeedBack(String baseUrl, List<File> files,Map<String, ParameterValue> loginMap,Map<String, ParameterValue> map,FileUpLoadCallBack callback) throws IOException {
		return commitWithFiles(getUrl(baseUrl + "/re/repairsRecordMobile!saveWokerFeedBack.action", loginMap),files, map,callback);
	}

	public static String saveWokerFeedBack(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!saveWokerFeedBack.action"), map);
	}

	/**
	 * 获取反馈项
	 */
	public static String getFeedbackContentList(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!getFeedbackContentList.action"), map);
	}

	/**
	 * 提交反馈
	 *   attitudeStr 服务态度	number
		 qualityStr	维修质量	number
		 repairFlag	是否修好	string
		 reportId	维修单Id	string
		 scoreStr	整体评价	number
		 speedStr	响应速度	number
		 suggestion	意见	string
		 technicalLevelStr	技术水平
	 */
	public static String saveFeedBack(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!saveFeedBack.action"), map);
	}

	/**
	 * 获取反馈项
	 * 	checkFlag	是否通过	string	1通过，2不通过
	 checkNote	审核意见	string
	 repairId	报修单Id
	 */
	public static String saveCostCheck(String baseUrl,Map<String, ParameterValue> map) throws IOException {
		return getUrlResponse(checkUrl(baseUrl + "/re/repairsRecordMobile!saveCostCheck.action"), map);
	}

	/**
	 * 获取消耗物品
	 */
	public static String getGoods(String baseUrl,Map<String, ParameterValue> map) {
		return getUrl(checkUrl(baseUrl + "/re/repairsRecordMobile!getGoods.action"), map);
	}

	/************************************ 数据接口end ************************************************************/

}
