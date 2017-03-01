package zhwx.ui.dcapp.assets.model;

import java.io.Serializable;
import java.util.List;

/**   
 * @Title: ApplyInfos.java 
 * @Package zhwx.ui.dcapp.assets.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月17日 下午5:56:26 
 */
public class ApplyInfos implements Serializable{
	
	private List<AssetsKind> assetKinds;
	private List<School> schools;
	private List<IdAndName> userDepartments;
	private IdAndName userDepartment;

	public List<AssetsKind> getAssetKinds() {
		return assetKinds;
	}

	public void setAssetKinds(List<AssetsKind> assetKinds) {
		this.assetKinds = assetKinds;
	}

	public List<School> getSchools() {
		return schools;
	}

	public void setSchools(List<School> schools) {
		this.schools = schools;
	}

	public List<IdAndName> getDepartments() {
		return userDepartments;
	}

	public void setDepartments(List<IdAndName> departments) {
		this.userDepartments = departments;
	}
	
	public IdAndName getUserDepartment() {
		return userDepartment;
	}

	public void setUserDepartment(IdAndName userDepartment) {
		this.userDepartment = userDepartment;
	}

	public class AssetsKind extends IdAndName {
		private String kindParentIName;
		private String kindParentId;
		public String getKindParentIName() {
			return kindParentIName;
		}
		public void setKindParentIName(String kindParentIName) {
			this.kindParentIName = kindParentIName;
		}
		public String getKindParentId() {
			return kindParentId;
		}
		public void setKindParentId(String kindParentId) {
			this.kindParentId = kindParentId;
		}
	}
	
	public class School extends IdAndName{
		
		private List<IdAndName> auditors;
		
		public List<IdAndName> getAuditors() {
			return auditors;
		}
		public void setAuditors(List<IdAndName> auditors) {
			this.auditors = auditors;
		}
	}
}
