package com.netease.nim.demo.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.ui.viewpager.FadeInOutPageTransformer;
import com.netease.nim.demo.common.ui.viewpager.PagerSlidingTabStrip;
import com.netease.nim.demo.main.activity.SettingsActivity;
import com.netease.nim.demo.main.adapter.MainTabPagerAdapter;
import com.netease.nim.demo.main.helper.SystemMessageUnreadManager;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.main.reminder.ReminderId;
import com.netease.nim.demo.main.reminder.ReminderItem;
import com.netease.nim.demo.main.reminder.ReminderManager;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.drop.DropCover;
import com.netease.nim.uikit.common.ui.drop.DropFake;
import com.netease.nim.uikit.common.ui.drop.DropManager;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

import zhwx.common.view.CustomViewPager;

/**
 * 云信主界面（导航页）
 */
public class HomeFragment extends TFragment implements View.OnClickListener,OnPageChangeListener, ReminderManager.UnreadNumChangedCallback {

    public static PagerSlidingTabStrip tabs;

    private CustomViewPager pager;

    private int scrollState;

    private MainTabPagerAdapter adapter;

    private View rootView;

    private Button[] mTabs;

    private int index;
    // 当前fragment的index
    private int currentTabIndex;

    private DropFake unReadCountTV,unReadSysNoticeCountTV,unReadCircleCountTV;

    public HomeFragment() {
        setContainerId(R.id.welcome_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setToolBar(R.id.toolbar, R.string.app_name, R.drawable.actionbar_dark_logo);
        setTitle(ECApplication.getInstance().getCurrentIMUser().getName());
        getToolBar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        findViews();
        setupPager();
        setupTabs();
        registerMsgUnreadInfoObserver(true);
        registerSystemMessageObservers(true);
        requestSystemMessageUnreadCount();
        initUnreadCover();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
        adapter.onPageScrolled(position);
    }

    @Override
    public void onPageSelected(int position) {
        tabs.onPageSelected(position);

        selectPage(position);

        enableMsgNotification(false);
        onTabClicked(mTabs[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        tabs.onPageScrollStateChanged(state);
        scrollState = state;
        selectPage(pager.getCurrentItem());
    }

    private void selectPage(int page) {
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
            adapter.onPageSelected(pager.getCurrentItem());
        }
    }

    public void switchTab(int tabIndex, String params) {
        pager.setCurrentItem(tabIndex);
    }

    /**
     * 查找页面控件
     */
    private void findViews() {
        tabs = findView(R.id.tabs);
        //修改tab选中颜色
        tabs.setIndicatorColorResource(R.color.main_bg);
        tabs.setCheckedTextColorResource(R.color.main_bg);
        pager = findView(R.id.main_tab_pager);
//        pager.setScanScroll(false);
        mTabs = new Button[4];
        mTabs[0] = (Button) findView(R.id.btn_conversation);
        mTabs[0].setOnClickListener(this);
        mTabs[1] = (Button) findView(R.id.btn_app);
        mTabs[1].setOnClickListener(this);
        mTabs[2] = (Button) findView(R.id.btn_address_list);
        mTabs[2].setOnClickListener(this);
        mTabs[3] = (Button) findView(R.id.btn_circle);
        mTabs[3].setOnClickListener(this);
        mTabs[0].setSelected(true);
        unReadCountTV = findView(R.id.unReadCountTV);
    }

    /**
     * button点击事件
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_app:
                index = 1;
                break;
            case R.id.btn_address_list:
                index = 2;
                break;
            case R.id.btn_circle:
                index = 3;
                break;
        }
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
        pager.setCurrentItem(currentTabIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mTabs != null && mTabs.length != 0) {
//            onTabClicked(mTabs[0]);
//        }
        enableMsgNotification(false);
        //quitOtherActivities();
    }

    @Override
    public void onPause() {
        super.onPause();
        enableMsgNotification(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerMsgUnreadInfoObserver(false);
        registerSystemMessageObservers(false);
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {
        onTabClicked(v);
    }



    /**
     * 设置viewPager
     */
    private void setupPager() {
        adapter = new MainTabPagerAdapter(getFragmentManager(), getActivity(), pager);
        pager.setOffscreenPageLimit(adapter.getCacheCount());
        // page swtich animation
        pager.setPageTransformer(true, new FadeInOutPageTransformer());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
    }

    /**
     * 设置tab条目
     */
    private void setupTabs() {
        tabs.setOnCustomTabListener(new PagerSlidingTabStrip.OnCustomTabListener() {
            @Override
            public int getTabLayoutResId(int position) {
                return R.layout.tab_layout_main;
            }

            @Override
            public boolean screenAdaptation() {
                return true;
            }
        });
        tabs.setViewPager(pager);
        tabs.setOnTabClickListener(adapter);
        tabs.setOnTabDoubleTapListener(adapter);
    }

    private void enableMsgNotification(boolean enable) {
        boolean msg = (pager.getCurrentItem() != MainTab.RECENT_CONTACTS.tabIndex);
        if (enable | msg) {
            /**
             * 设置最近联系人的消息为已读
             *
             * @param account,    聊天对象帐号，或者以下两个值：
             *                    {@link #MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
             *                    {@link #MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
             */
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }

    /**
     * 注册未读消息数量观察者
     */
    private void registerMsgUnreadInfoObserver(boolean register) {
        if (register) {
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this);
        }
    }

    /**
     * 未读消息数量观察者实现
     */
    @Override
    public void onUnreadNumChanged(ReminderItem item) {
        MainTab tab = MainTab.fromReminderId(item.getId());
        if (tab != null) {
            tabs.updateTab(tab.tabIndex, item);
        }
//        unReadCountTV.setVisibility(item.unread() > 0?View.VISIBLE:View.INVISIBLE);
//        unReadCountTV.setText(String.valueOf(ReminderSettings.unreadMessageShowRule(item.unread())));
    }

    public static void onNoticeChange(ReminderItem item){
        MainTab tab = MainTab.fromReminderId(item.getId());
        if (tab != null) {
            tabs.updateTab(tab.tabIndex, item);
        }
    }

    /**
     * 注册/注销系统消息未读数变化
     *
     * @param register
     */
    private void registerSystemMessageObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver,
                register);
    }

    private Observer<Integer> sysMsgUnreadCountChangedObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer unreadCount) {
            SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
            ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
        }
    };

    /**
     * 查询系统消息未读数
     */
    private void requestSystemMessageUnreadCount() {
        int unread = NIMClient.getService(SystemMessageService.class).querySystemMessageUnreadCountBlock();
        SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unread);
        ReminderManager.getInstance().updateContactUnreadNum(unread);
    }

    /**
     * 初始化未读红点动画
     * TODO 执行清空未读操作
     */
    private void initUnreadCover() {
        DropManager.getInstance().init(getContext(), (DropCover) findView(R.id.unread_cover), new DropCover.IDropCompletedListener() {
            @Override
            public void onCompleted(Object id, boolean explosive) {
                if (id == null || !explosive) {
                    return;
                }

                if (id instanceof RecentContact) {
                    RecentContact r = (RecentContact) id;
                    NIMClient.getService(MsgService.class).clearUnreadCount(r.getContactId(), r.getSessionType());
                    LogUtil.i("HomeFragment", "clearUnreadCount, sessionId=" + r.getContactId());
                } else if (id instanceof String) {
                    if (((String) id).contentEquals(String.valueOf(ReminderId.SESSION))) {
                        List<RecentContact> recentContacts = NIMClient.getService(MsgService.class).queryRecentContactsBlock();
                        for (RecentContact r : recentContacts) {
                            if (r.getUnreadCount() > 0) {
                                NIMClient.getService(MsgService.class).clearUnreadCount(r.getContactId(), r.getSessionType());
                            }
                        }
                        LogUtil.i("HomeFragment", "clearAllUnreadCount");
                    } else if (((String) id).contentEquals(String.valueOf(ReminderId.CONTACT))) {
                        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
                        LogUtil.i("HomeFragment", "clearAllSystemUnreadCount");
                    }
                }
            }
        });
    }
}