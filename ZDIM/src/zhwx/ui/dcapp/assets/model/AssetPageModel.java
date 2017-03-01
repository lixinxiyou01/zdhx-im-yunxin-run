package zhwx.ui.dcapp.assets.model;
import java.util.List;

/**
 * Created by LX on 2016/8/29.
 */

public class AssetPageModel {

    private List<AssetKindsBean> assetKinds;
    private List<LocationsBean> locations;
    private List<StatusCode> status;
    
    public List<StatusCode> getStatus() {
		return status;
	}

	public void setStatus(List<StatusCode> status) {
		this.status = status;
	}

	public List<AssetKindsBean> getAssetKinds() {
        return assetKinds;
    }

    public void setAssetKinds(List<AssetKindsBean> assetKinds) {
        this.assetKinds = assetKinds;
    }

    public List<LocationsBean> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationsBean> locations) {
        this.locations = locations;
    }

    public static class AssetKindsBean extends IdAndName {

        private List<AssetBrandsBean> assetBrands;
        private String kindParentId;
        private String kindParentIName;

		public String getKindParentId() {
			return kindParentId;
		}

		public void setKindParentId(String kindParentId) {
			this.kindParentId = kindParentId;
		}

		public String getKindParentIName() {
			return kindParentIName;
		}

		public void setKindParentIName(String kindParentIName) {
			this.kindParentIName = kindParentIName;
		}

		public List<AssetBrandsBean> getAssetBrands() {
            return assetBrands;
        }

        public void setAssetBrands(List<AssetBrandsBean> assetBrands) {
            this.assetBrands = assetBrands;
        }

        public static class AssetBrandsBean extends IdAndName {

            private List<IdAndName> assetPatterns;

            public List<IdAndName> getAssetPatterns() {
                return assetPatterns;
            }

            public void setAssetPatterns(List<IdAndName> assetPatterns) {
                this.assetPatterns = assetPatterns;
            }
        }
    }

    public static class LocationsBean extends IdAndName {

        private List<IdAndName> classrooms;

        public List<IdAndName> getClassrooms() {
            return classrooms;
        }

        public void setClassrooms(List<IdAndName> classrooms) {
            this.classrooms = classrooms;
        }
    }
    
    public static class StatusCode {
    	private String name;
    	private String code;
    	private boolean isSelected;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public boolean isSelected() {
			return isSelected;
		}
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}
    }
}
