package zhwx.ui.dcapp.takecourse;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zhwx.common.model.ParameterValue;
import zhwx.common.util.StringUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.ui.dcapp.takecourse.listviewgroup.StickyListHeadersAdapter;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Elective;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Group;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemClickedListener;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemHeaderClickedListener;

/**
 * 欢迎加入QQ开发群讨论：88130145
 * 
 * @Description:
 * @author: zhuanggy
 * @see:
 * @since:
 * @copyright © 35.com
 * @Date:2013-10-14
 */
public class ChooseAdapter extends MailAdapter implements StickyListHeadersAdapter {

	private Elective elective;
	private List<Group> mMessageListGroup;

	private ItemClickedListener mItemClickedListener;
	private ItemHeaderClickedListener mItemHeaderClickedListener;

	private Activity context;

	private boolean mIsOpen;// 初始化View时分组是否展开
	
	public static final String[] STR_GRADE = { "优秀", "良好", "及格", "不及格" };

	public ChooseAdapter(Activity context, Elective elective, boolean isOpen) {

		this.context = context;
		this.elective = elective;
		this.mIsOpen = isOpen;
		initMessageList(elective);
	}

	private void initMessageList(Elective elective) {
		this.elective = elective;
		if (elective != null && elective.getEcElectiveGroupList().size() > 0) {
			getSectionIndicesAndGroupNames();
		}
	}

	@Override
	public int getCount() {
		int count = 0;
		for (int i = 0; i < elective.getEcElectiveGroupList().size(); i++) {
			count += elective.getEcElectiveGroupList().get(i).getEcActivityCourseList().size();

		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// i代表 第 几组
		// count 是总共数量
		// position是当前数量
		// (conut- position)
		int count = 0;
		for (int i = 0; i < elective.getEcElectiveGroupList().size(); i++) {
			count += elective.getEcElectiveGroupList().get(i).getEcActivityCourseList().size();

			if (position < count) {
				return elective.getEcElectiveGroupList().get(i).getEcActivityCourseList().get(elective.getEcElectiveGroupList().get(i).getEcActivityCourseList().size() - count + position);
			}
		}
		return 0;

	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public class ItemViewHolder {
		TextView textViewinfo;
		TextView textViewInfo;
		TextView teachernametv;
		TextView timetv;
		TextView scoretv;
		TextView hourTV;
		TextView reasonTV;
		TextView selectedNum;
		ImageView iconIV;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final Elective.EcActivityCourseList.EcActivityCourse schoolTermStudentCourse = (Elective.EcActivityCourseList.EcActivityCourse) getItem(position);
		ItemViewHolder holder;

		if (convertView == null) {
			holder = new ItemViewHolder();
			/**
			 * 校本选课 kind=0    显示内容：名称、任课教师、上课时间、学分、人数上限、已选人数
		     * 分班选课 kind=1  显示内容：名称、学分、已选人数
			 */
			if ("0".equals(schoolTermStudentCourse.getKind())) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_select_course_choos0, null);
				holder.textViewInfo = (TextView) convertView.findViewById(R.id.text_item_content_info);
				holder.textViewinfo = (TextView) convertView.findViewById(R.id.infotv);
				holder.teachernametv = (TextView) convertView.findViewById(R.id.teachernametv);
				holder.timetv = (TextView) convertView.findViewById(R.id.timetv);
				holder.hourTV = (TextView) convertView.findViewById(R.id.hourTV);
				holder.scoretv = (TextView) convertView.findViewById(R.id.scoretv);
				holder.reasonTV = (TextView) convertView.findViewById(R.id.reasonTV);
				holder.selectedNum = (TextView) convertView.findViewById(R.id.selectedNum);
				holder.iconIV = (ImageView) convertView.findViewById(R.id.iconIV);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_select_course_choos1, null);
				holder.textViewInfo = (TextView) convertView.findViewById(R.id.text_item_content_info);
				holder.textViewinfo = (TextView) convertView.findViewById(R.id.infotv);
				holder.scoretv = (TextView) convertView.findViewById(R.id.scoretv);
				holder.reasonTV = (TextView) convertView.findViewById(R.id.reasonTV);
				holder.selectedNum = (TextView) convertView.findViewById(R.id.selectedNum);
				holder.iconIV = (ImageView) convertView.findViewById(R.id.iconIV);
			}
			convertView.setTag(holder);
		} else {
			holder = (ItemViewHolder) convertView.getTag();
		}
		
		if ("0".equals(schoolTermStudentCourse.getKind())) {
			holder.textViewInfo.setText(schoolTermStudentCourse.getCourseDisplayName());
			holder.teachernametv.setText(StringUtil.isBlank(schoolTermStudentCourse.getTeacherName())?"":"【"+schoolTermStudentCourse.getTeacherName()+"】");
			holder.scoretv.setText("学分: " + schoolTermStudentCourse.getScoreStr());
			holder.selectedNum.setText("已报名：" + schoolTermStudentCourse.getSelectedNum() + "/" + (StringUtil.isBlank(schoolTermStudentCourse.getMaxCount())?"∞":schoolTermStudentCourse.getMaxCount()));
			holder.hourTV.setText("课时: " + schoolTermStudentCourse.getHourStr());
			holder.timetv.setText(schoolTermStudentCourse.getClassTimeOfWeekStr());
		} else {
			holder.textViewInfo.setText(schoolTermStudentCourse.getCourseDisplayName());
			holder.scoretv.setText("学分: " + schoolTermStudentCourse.getScoreStr());
			holder.selectedNum.setText("已报名：" + schoolTermStudentCourse.getSelectedNum() + "/" + (StringUtil.isBlank(schoolTermStudentCourse.getMaxCount())?"∞":schoolTermStudentCourse.getMaxCount()));
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mItemClickedListener != null) {
					mItemClickedListener.onItemClick(v, position);
				}
			}
		});

		holder.textViewinfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Elective.EcActivityCourseList.EcActivityCourse schoolTermStudentCourse = (Elective.EcActivityCourseList.EcActivityCourse) getItem(position);
				Map<String, ParameterValue> map = ECApplication.getInstance().getV3LoginMap();
				map.put("ecActivityCourseId", new ParameterValue(schoolTermStudentCourse.getId()));
				String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address()+"/ec/mobile/ecMobileTerminal!info.action", map);
				Intent intent = new Intent(context, CouseDetailActivity.class);
				intent.putExtra("webUrl", url);
				intent.putExtra("kind", CouseDetailActivity.KIND_SELECT);
				intent.putExtra("flag", schoolTermStudentCourse.getSelectedint());
				ChooseAcitivty.selectedPosition = position;
				context.startActivityForResult(intent, 100);
			}
		});
		holder.reasonTV.setVisibility(View.GONE);
		switch (schoolTermStudentCourse.getSelectedint()) {
			case -1:
				if ("1".equals(schoolTermStudentCourse.getSelected())) {
					holder.iconIV.setImageResource(0);
					holder.iconIV.setVisibility(View.GONE);
				} else {
					holder.iconIV.setImageResource(R.drawable.icon_check_course_full);
					holder.iconIV.setVisibility(View.VISIBLE);
				}
				break;
			case 0:
				holder.iconIV.setImageResource(0);
				holder.iconIV.setVisibility(View.GONE);
				break;
			case 1:
				holder.iconIV.setVisibility(View.VISIBLE);
				holder.iconIV.setImageResource(R.drawable.icon_check_course_selected);
				if ("1".equals(schoolTermStudentCourse.getVersioned())) {
					holder.reasonTV.setVisibility(View.VISIBLE);
					holder.reasonTV.setText(schoolTermStudentCourse.getCantSelectReason());
				}
				break;
			case -2:
				holder.iconIV.setVisibility(View.VISIBLE);
				holder.iconIV.setImageResource(R.drawable.icon_check_course_disable);
				holder.reasonTV.setVisibility(View.VISIBLE);
				holder.reasonTV.setText(schoolTermStudentCourse.getCantSelectReasonPreview());
				break;
		}
		if(schoolTermStudentCourse.getSelectedint() == -2) {
			holder.iconIV.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ECAlertDialog buildAlert = ECAlertDialog.buildPositiveAlert(context, R.string.action_settings, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
						}
					});
					buildAlert.setTitle("提示");
					buildAlert.setMessage(schoolTermStudentCourse.getCantSelectReason());
					buildAlert.show();
				}
			});
		}else{
			holder.iconIV.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mItemClickedListener != null) {
						mItemClickedListener.onItemClick(v, position);
					}
				}
			});
		}
		return convertView;
	}

	/**
	 * 分组
	 */
	private void getSectionIndicesAndGroupNames() {
		mMessageListGroup = new ArrayList<Group>();
		Group gp;
		int countGp = 0;
		String groupName = "";
		mMessageListGroup.add(new Group(groupName, true, 3, countGp));
		mMessageListGroup.add(new Group(groupName, true, 4, countGp));
		mMessageListGroup.add(new Group(groupName, true, 5, countGp));
	}

	@Override
	public View getHeaderView(final int position, View convertView, ViewGroup parent) {

		ItemHeaderViewHolder holder = null;
		if (convertView == null) {
			holder = new ItemHeaderViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.select_course_view_item_header, parent, false);
			holder.textViewGroupName = (TextView) convertView.findViewById(R.id.text_item_header_name);

			convertView.setTag(holder);
		} else {
			holder = (ItemHeaderViewHolder) convertView.getTag();
		}
		String nameString = elective.getEcElectiveGroupList().get(getGroupNum(position)).getGroupName();

		holder.textViewGroupName.setText(nameString);
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return getGroupNum(position);
	}

	/**
	 * 当点击header时，即GroupName，可以展开合起
	 * 
	 * @Description:
	 * @param headerId
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-10-14
	 */
	public void onListHeaderClicked(int position) {
		ChooseAdapter.this.notifyDataSetChanged();
	}
	public void setOnItemClickedListener(ItemClickedListener listener) {
		this.mItemClickedListener = listener;
	}

	public void setOnItemHeaderClickedListener(ItemHeaderClickedListener listener) {
		this.mItemHeaderClickedListener = listener;
	}

	public ItemHeaderClickedListener getmItemHeaderClickedListener() {
		return mItemHeaderClickedListener;
	}

	public class ItemHeaderViewHolder {

		TextView textViewGroupName;

	}

	public int getGroupNum(int position) {
		int count = 0;
		for (int i = 0; i < elective.getEcElectiveGroupList().size(); i++) {
			count += elective.getEcElectiveGroupList().get(i).getEcActivityCourseList().size();
			if (position < count) {
				return i;
			}
		}
		return 0;
	}
}
