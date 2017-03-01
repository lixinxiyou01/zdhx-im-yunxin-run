package zhwx.ui.dcapp.takecourse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.util.StringUtil;
import zhwx.ui.dcapp.takecourse.listviewgroup.StickyListHeadersAdapter;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Elective;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Group;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.MyEletiveCourse;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemClickedListener;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemHeaderClickedListener;


/**
 * @Date:2013-10-14
 */
public class MailAdapter extends BaseAdapter implements
		StickyListHeadersAdapter {

	private MyEletiveCourse myEletiveCourse;
	private List<Group> mMessageListGroup;

	private ItemClickedListener mItemClickedListener;
	private ItemHeaderClickedListener mItemHeaderClickedListener;

	private Context context;

	private boolean mIsOpen;// 初始化View时分组是否展开

	public static final String[] STR_GRADE = { "优秀", "良好", "及格", "不及格" };

	public MailAdapter() {

	}

	public MailAdapter(Context context, Elective elective, boolean isOpen) {

	}

	public MailAdapter(Context context, MyEletiveCourse myEletiveCourse,
			boolean isOpen) {
		this.context = context;
		this.myEletiveCourse = myEletiveCourse;
		this.mIsOpen = isOpen;
		initMessageList(myEletiveCourse);
	}

	private void initMessageList(MyEletiveCourse myEletiveCourse) {
		this.myEletiveCourse = myEletiveCourse;
		if (myEletiveCourse != null
				&& myEletiveCourse.getSchoolTermInfoList().size() > 0) {
			getSectionIndicesAndGroupNames();
		}
	}

	@Override
	public int getCount() {
		int count = 0;
		for (int i = 0; i < myEletiveCourse.getSchoolTermInfoList().size(); i++) {
			count += myEletiveCourse
					.getSchoolTermStudentCourseMap()
					.get(myEletiveCourse.getSchoolTermInfoList().get(i).getId())
					.size();

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
		for (int i = 0; i < myEletiveCourse.getSchoolTermInfoList().size(); i++) {
			count += myEletiveCourse
					.getSchoolTermStudentCourseMap()
					.get(myEletiveCourse.getSchoolTermInfoList().get(i).getId())
					.size();

			if (position < count) {
				return myEletiveCourse
						.getSchoolTermStudentCourseMap()
						.get(myEletiveCourse.getSchoolTermInfoList().get(i)
								.getId())
						.get(myEletiveCourse
								.getSchoolTermStudentCourseMap()
								.get(myEletiveCourse.getSchoolTermInfoList()
										.get(i).getId()).size()
								- count + position);

			}

		}
		return 0;

	}

	@Override
	public long getItemId(int arg0) {
		System.out.println("id" + arg0);
		return arg0;
	}

	public class ItemViewHolder {
		TextView textViewinfo;
		TextView textViewInfo;
		TextView teachernametv;
		TextView timetv;
		TextView scoretv;
		TextView gradetv;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ItemViewHolder holder;

		if (convertView == null) {
			holder = new ItemViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_selectedcourse, null);
			holder.textViewInfo = (TextView) convertView
					.findViewById(R.id.text_item_content_info);
			holder.teachernametv = (TextView) convertView.findViewById(R.id.teachernametv);
			holder.timetv = (TextView) convertView.findViewById(R.id.timetv);
			holder.scoretv = (TextView) convertView.findViewById(R.id.scoretv);
			holder.gradetv = (TextView) convertView.findViewById(R.id.gradetv);
			convertView.setTag(holder);
		} else {
			holder = (ItemViewHolder) convertView.getTag();
		}
		MyEletiveCourse.schoolTermStudentCourseMap schoolTermStudentCourse =  (MyEletiveCourse.schoolTermStudentCourseMap) getItem(position);
		holder.textViewInfo.setText(schoolTermStudentCourse.getCourseDisplayName());
		holder.teachernametv.setText(StringUtil.isBlank(schoolTermStudentCourse.getTeacherName())?"":"【"+schoolTermStudentCourse.getTeacherName()+"】");
		holder.scoretv.setText("科目学分: " + schoolTermStudentCourse.getScoreStr());
		try {
			holder.gradetv.setText("综合评价: " + STR_GRADE[Integer.parseInt(schoolTermStudentCourse.getEvaluate())]);
		} catch (Exception e) {
			e.printStackTrace();
			holder.gradetv.setText("");
		}

		holder.timetv.setText(schoolTermStudentCourse.getClassTimeOfWeekStr());

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mItemClickedListener != null) {
					mItemClickedListener.onItemClick(v, position);
				}

			}
		});

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
	public View getHeaderView(final int position, View convertView,
			ViewGroup parent) {

		ItemHeaderViewHolder holder = null;
		if (convertView == null) {
			holder = new ItemHeaderViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.select_course_view_item_header, parent, false);
			holder.textViewGroupName = (TextView) convertView
					.findViewById(R.id.text_item_header_name);

			convertView.setTag(holder);
		} else {
			holder = (ItemHeaderViewHolder) convertView.getTag();
		}
		String nameString = myEletiveCourse.getSchoolTermInfoList()
				.get(getGroupNum(position)).getFullName()
				+ "     总学分："
				+ myEletiveCourse.getSchoolTermSumScoreMap().get(
						myEletiveCourse.getSchoolTermInfoList()
								.get(getGroupNum(position)).getId());
		holder.textViewGroupName.setText(nameString);
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		// return mMessageList.get(position).getGroupId();
		System.out.println(getGroupNum(position));
		return getGroupNum(position);
	}

	/**
	 * 当点击header时，即GroupName，可以展开合起
	 * 
	 * @Description:
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-10-14
	 */
	public void onListHeaderClicked(int position) {
		// Group gp =
		// mMessageListGroup.get(mMessageList.get(position).getGroupId());
		// gp.setShown(!gp.isShown());

		MailAdapter.this.notifyDataSetChanged();
	}

	// ///////////////////////////////////////
	public void setOnItemClickedListener(ItemClickedListener listener) {
		this.mItemClickedListener = listener;
	}

	public void setOnItemHeaderClickedListener(
			ItemHeaderClickedListener listener) {
		this.mItemHeaderClickedListener = listener;
	}

	public ItemHeaderClickedListener getmItemHeaderClickedListener() {
		return mItemHeaderClickedListener;
	}

	// /////////////////////////////////////
	public class ItemHeaderViewHolder {

		TextView textViewGroupName;
		// ImageView imgArrow;
		// TextView textViewGroupCount;

	}

	public int getGroupNum(int position) {
		int count = 0;
		for (int i = 0; i < myEletiveCourse.getSchoolTermInfoList().size(); i++) {
			count += myEletiveCourse
					.getSchoolTermStudentCourseMap()
					.get(myEletiveCourse.getSchoolTermInfoList().get(i).getId())
					.size();

			if (position < count) {
				return i;
			}

		}
		return 0;
	}

}
