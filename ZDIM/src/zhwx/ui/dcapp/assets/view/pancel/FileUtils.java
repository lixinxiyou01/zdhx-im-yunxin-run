package zhwx.ui.dcapp.assets.view.pancel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Environment;

public class FileUtils {
//	private static String teachName = EBookSSOConnector.getInstance().getName();
	/**
	 * 存储图片到指定目录
	* @param bitmap
	 */
	public static void savePic(Bitmap bitmap,String path,String filename) {
			
			generateDir();
	
			if(path != null && !"".equals(path)){
			
			File file = new File(path, filename);

			FileOutputStream out;
		
			try {
				out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out)) {
					out.flush();
					out.close();
				}
			} catch (FileNotFoundException e) {
			
			} catch (IOException e) {
			
			}
		}
	}

	// 获取指定目录下的所有图片
	public static List<String> getImgFromSDcard(String path) {
		List<String> fileList = new ArrayList<String>();
		if(path != null && !"".equals(path)){
		File file = new File(path);
		File[] files = file.listFiles();

		if(files != null){
			
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					String filename = files[i].getName();
					// 获取bmp,jpg,png格式的图片
					if (filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".bmp")|| filename.endsWith(".JPG")|| filename.endsWith(".gif")) {
						String filePath = files[i].getAbsolutePath();
						fileList.add(filePath);
					}
				} else if (files[i].isDirectory()) {
					path = files[i].getAbsolutePath();
					getImgFromSDcard(path);
				}
			}
		}
		}
		return fileList;
	}
	
	/**
	 * 生成指定目录--原图
	 */
	public static String generateDirForOrg(String coursename){
		String path = "";
		if(coursename != null && !"".equals(coursename)){
			
			path = getSDCardPath() +"/"+coursename;
		File	file = new File(path);
			if (!file.exists())
				file.mkdir();
			path = getSDCardPath()  + "/"+coursename+"/org";
			file = new File(path);
			if (!file.exists())
				file.mkdir();
			
		}
			return path;
	}
	
	/**
	 * 生成指定目录 -- 收题
	 * 原图
	 */
	public static void generateDir(){
			String path = "";
		
			 path = getSDCardPath() + "/receive";
			File file = new File(path);
			if (!file.exists())
				file.mkdir();
			
			path = getSDCardPath() + "/receive/org";
			file = new File(path);
				if (!file.exists())
					file.mkdir();

			path = getSDCardPath()  + "/receive/thrumb";
			file = new File(path);
			if (!file.exists())
				file.mkdir();
			
//			path = getSDCardPath()  + "/answer";
//			file = new File(path);
//			if (!file.exists())
//				file.mkdir();
		
			
	}
	/**
	 * 生成指定目录--缩略图
	 */
	public static String generateDirForThumb(String coursename){
		String path = "";
		if(coursename != null && !"".equals(coursename)){
//			 path = getSDCardPath() + "/"+teachName;
//				File file = new File(path);
//				if (!file.exists())
//					file.mkdir();
				path =  getSDCardPath()+"/"+coursename;
				File file = new File(path);
				
				if (!file.exists())
					file.mkdir();
			
				path = getSDCardPath()  +"/"+coursename+"/thumb";
				file = new File(path);
				if (!file.exists())
					file.mkdir();
		}
	
		
			return path;
	}
	/**
	 * 获取SDCard的目录路径功能
	 */
	public static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
//		String path ="/mnt/sdcard";
		return sdcardDir.getPath();
	}
	/**
	 * 获取原图文件夹的路径
	 */
	public static String getOrgPath(String coursename) {
		String path="";
		if(coursename != null && !"".equals(coursename)){
			 path = getSDCardPath()  + "/"+coursename+"/org";
			
		}
		return path;
	}
	
	/**
	 * 获取指定文件的路径
	 */
	public static String getFilePath(String coursename,String filename) {
		String path="";
		if(coursename != null && !"".equals(coursename)){
			 path = getSDCardPath()  +"/"+coursename+"/org/"+filename;
			
		}
		return path;
	}
	/**
	 * 获取缩略图文件夹的路径
	 */
	public static String getThumbPath(String coursename) {
		String path="";
		if(coursename != null && !"".equals(coursename)){
			 path = getSDCardPath()  +"/"+coursename+"/thumb";
			
		}
			return path;
	}
	/**
	 * 根据当前课堂名字，在下课的时候删除本节课发送的题目文件夹
	 * @param   fileName    待删除的文件名  
	 * @return 文件删除成功返回true,否则返回false  
	 * 删除文件，可以是单个文件或文件夹  
	 * 
	 */
	     public static boolean delete(String fileName){   
	         File file = new File(fileName);   
	         if(!file.exists()){   
	             // 路径不存在 
	             return false;   
	         }else{   
	             // 路径存在并且是文件
	             if(file.isFile()){   

	                 return deleteFile(fileName);   
	             // 路径存在并且是目录
	             }else{   
	                 return deleteDirectory(fileName);   
	             }   
	         }   
	     }   

	     /**  
	      * 删除单个文件  
	      * @param   fileName    被删除文件的文件名  
	      * @return 单个文件删除成功返回true,否则返回false  
	      */  
	     public static boolean deleteFile(String fileName){   
	         File file = new File(fileName);   
	         if(file.isFile() && file.exists()){   
	             file.delete();   
	             System.out.println("删除单个文件"+fileName+"成功！");   
	             return true;   
	         }else{   
	             System.out.println("删除单个文件"+fileName+"失败！");   
	             return false;   
	         }   
	     }   

	     /**  
	      * 删除目录（文件夹）以及目录下的文件  
	      * @param   dir 被删除目录的文件路径  
	      * @return  目录删除成功返回true,否则返回false  
	      */  
	     public static boolean deleteDirectory(String dir){   
	         //如果dir不以文件分隔符结尾，自动添加文件分隔符   
	         if(!dir.endsWith(File.separator)){   
	             dir = dir+File.separator;   
	         }   
	         File dirFile = new File(dir);   
	         //如果dir对应的文件不存在，或者不是一个目录，则退出   
	         if(!dirFile.exists() || !dirFile.isDirectory()){   
	             System.out.println("删除目录失败"+dir+"目录不存在！");   
	             return false;   
	         }   
	         boolean flag = true;   
	         //删除文件夹下的所有文件(包括子目录)   
	         File[] files = dirFile.listFiles();   
	         for(int i=0;i<files.length;i++){   
	             //删除子文件   
	             if(files[i].isFile()){   
	                 flag = deleteFile(files[i].getAbsolutePath());   
	                 if(!flag){   
	                     break;   
	                 }   
	             }   
	             //删除子目录   
	             else{   
	                 flag = deleteDirectory(files[i].getAbsolutePath());   
	                 if(!flag){   
	                     break;   
	                 }   
	             }   
	         }   

	         if(!flag){   
	             System.out.println("删除目录失败");   
	             return false;   
	         }   

	         //删除当前目录   
	         if(dirFile.delete()){   
	             System.out.println("删除目录"+dir+"成功！");   
	             return true;   
	         }else{   
	             System.out.println("删除目录"+dir+"失败！");   
	             return false;   
	         }   
	     

	 }  
	/**
	 * 获得以课堂名字命名的文件夹路经
	* @author hefeng
	* @version 创建时间：2013-10-23 上午9:14:54
	* @param coursename
	* @param filename
	* @return
	 */
	public static String getPath(String coursename) {
		
		String path="";
		
		if(coursename != null && !"".equals(coursename)){
			 path = getSDCardPath()  + "/"+coursename;
			
		}
		return path;
	}
	/**
	 * 获得教师发题文件夹路径
	* @author hefeng
	* @version 创建时间：2013-12-6 下午1:47:38
	* @return
	 */
		public static String getPathForDel(String coursename) {
		
		String path="";
		
		if(coursename != null && !"".equals(coursename)){
			 path = getSDCardPath()  +"/"+coursename;
		}
			
		return path;
	}
	/**
	 * 接收题后 ---- 图片   原图 保存的目录
	 */
	public static String getPathOfTest(){
		String path = getSDCardPath()+"/receive/org";
		return path;
	}
	/**
	 * 接收题后 ---- 图片   缩略图 保存的目录
	 */
	public static String getPathOfTestThrumb(){
		String path = getSDCardPath()+"/receive/thrumb";
		return path;
	}
	/**
	 * 手写题答案 ---- 图片    保存的目录
	 */
	public static String getAnswerOfWrite(){
		String path = getSDCardPath()+"/answer";
		return path;
	}
}
