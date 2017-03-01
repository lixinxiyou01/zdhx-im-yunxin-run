package com.netease.nim.demo.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.demo.main.activity.MultiportActivity;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.main.reminder.ReminderId;
import com.netease.nim.demo.main.reminder.ReminderItem;
import com.netease.nim.demo.main.reminder.ReminderManager;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.session.extension.GuessAttachment;
import com.netease.nim.demo.session.extension.RTSAttachment;
import com.netease.nim.demo.session.extension.SnapChatAttachment;
import com.netease.nim.demo.session.extension.StickerAttachment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.RecentContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zhwx.common.util.SharPreUtil;
import zhwx.common.util.ToastUtil;
import zhwx.ui.dcapp.noticecenter.NoticeCenterActivity;

/**
 * Created by zhoujianghua on 2015/8/17.
 */
public class SessionListFragment extends MainTabFragment {

    private View notifyBar;

    private TextView notifyBarText;

    // 同时在线的其他端的信息
    private List<OnlineClient> onlineClients;

    private View multiportBar;

    private RecentContactsFragment fragment;

    private View headView;

    public SessionListFragment() {
        this.setContainerId(MainTab.RECENT_CONTACTS.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        super.onDestroy();
    }

    @Override
    protected void onInit() {
        findViews();
        registerObservers(true);

        addRecentContactsFragment();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOtherClients(clientsObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    private void findViews() {
        notifyBar = getView().findViewById(R.id.status_notify_bar);
        notifyBarText = (TextView) getView().findViewById(R.id.status_desc_label);
        notifyBar.setVisibility(View.GONE);
        notifyBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击重连
                ToastUtil.showMessage("正在重连");
//                ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.intent_xlt_opendownload, null, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        LogoutHelper.logout();
//                    }
//                });
//                buildAlert.setMessage("是否注销并重新登录？");
//                buildAlert.setTitle("提示");
//                buildAlert.show();
            }
        });

        multiportBar = getView().findViewById(R.id.multiport_notify_bar);
        multiportBar.setVisibility(View.GONE);
        multiportBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiportActivity.startActivity(getActivity(), onlineClients);
            }
        });
    }

    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                kickOut(code);
            } else {
                if (code == StatusCode.NET_BROKEN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.net_broken);
                } else if (code == StatusCode.UNLOGIN) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_unlogin);
                } else if (code == StatusCode.CONNECTING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_connecting);
                } else if (code == StatusCode.LOGINING) {
                    notifyBar.setVisibility(View.VISIBLE);
                    notifyBarText.setText(R.string.nim_status_logining);
                } else {
                    notifyBar.setVisibility(View.GONE);
                }
            }
        }
    };

    Observer<List<OnlineClient>> clientsObserver = new Observer<List<OnlineClient>>() {
        @Override
        public void onEvent(List<OnlineClient> onlineClients) {
            SessionListFragment.this.onlineClients = onlineClients;
            if (onlineClients == null || onlineClients.size() == 0) {
                multiportBar.setVisibility(View.GONE);
            } else {
                multiportBar.setVisibility(View.VISIBLE);
                TextView status = (TextView) multiportBar.findViewById(R.id.multiport_desc_label);
                OnlineClient client = onlineClients.get(0);
                switch (client.getClientType()) {
                    case ClientType.Windows:
                        status.setText(getString(R.string.multiport_logging) + getString(R.string.computer_version));
                        break;
                    case ClientType.Web:
                        status.setText(getString(R.string.multiport_logging) + getString(R.string.web_version));
                        break;
                    case ClientType.iOS:
                    case ClientType.Android:
                        status.setText(getString(R.string.multiport_logging) + getString(R.string.mobile_version));
                        break;
                    default:
                        multiportBar.setVisibility(View.GONE);
                        break;
                }
            }
        }
    };

    private void kickOut(StatusCode code) {
        Preferences.saveUserToken("");

        if (code == StatusCode.PWD_ERROR) {
            LogUtil.e("Auth", "user password error");
            Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        } else {
            LogUtil.i("Auth", "Kicked!");
        }
        onLogout();
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听&清除状态
        LogoutHelper.logout();

        LoginActivity.start(getActivity(), true);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(RecentContactsFragment.adapter != null){
            RecentContactsFragment.adapter.getCallback().onRecentContactsLoaded();
        }
    }

    // 将最近联系人列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addRecentContactsFragment() {
        fragment = new RecentContactsFragment();
        fragment.setContainerId(R.id.messages_fragment);

        final UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (RecentContactsFragment) activity.addFragment(fragment);
        fragment.setCallback(new RecentContactsCallback() {
            @Override
            public void onRecentContactsLoaded() {
                //TODO 最近联系人列表加载完毕 添加通知
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if(fragment.listView.getHeaderViewsCount() != 0 && headView != null) {
                        fragment.listView.removeHeaderView(headView);
                    }
                    LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = layoutInflater.inflate(R.layout.nim_recent_contact_list_item,null);
                    headView = view;
                    HeadImageView img_head = (HeadImageView) view.findViewById(R.id.img_head);
                    img_head.setImageResource(R.drawable.icon_v3_message);

                    if ("1" .equals(SharPreUtil.getField("haveNew"))) {
                        ImageView avatar_prospect_iv = (ImageView) view.findViewById(R.id.avatar_prospect_iv);
                        avatar_prospect_iv.setVisibility(View.VISIBLE);
                        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
                        tv_message.setText(SharPreUtil.getField("noticeContent"));

                        ReminderItem item = ReminderManager.getInstance().getItems().get(ReminderId.SESSION);
                        item.setIndicator(true);
                        HomeFragment.onNoticeChange(item);
                    } else {
                        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
                        tv_message.setText("暂无新通知");
                        ReminderItem item = ReminderManager.getInstance().getItems().get(ReminderId.SESSION);
                        item.setIndicator(false);
                        HomeFragment.onNoticeChange(item);
                    }

                    TextView tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
                    tv_nickname.setText("消息中心");

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharPreUtil.saveField("haveNew","");
                            startActivity(new Intent(activity, NoticeCenterActivity.class));
                        }
                    });

                    fragment.listView.addHeaderView(view);
                }
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
                ReminderManager.getInstance().updateSessionUnreadNum(unreadCount);
            }

            @Override
            public void onItemClick(RecentContact recent) {
                // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
                switch (recent.getSessionType()) {
                    case P2P:
                        SessionHelper.startP2PSession(getActivity(), recent.getContactId());
                        break;
                    case Team:
                        SessionHelper.startTeamSession(getActivity(), recent.getContactId());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public String getDigestOfAttachment(MsgAttachment attachment) {
                // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
                // 当然，你也可以自定义一些内建消息的缩略语，例如图片，语音，音视频会话等，自定义的缩略语会被优先使用。
                if (attachment instanceof GuessAttachment) {
                    GuessAttachment guess = (GuessAttachment) attachment;
                    return guess.getValue().getDesc();
                } else if (attachment instanceof RTSAttachment) {
                    return "[白板]";
                } else if (attachment instanceof StickerAttachment) {
                    return "[贴图]";
                } else if (attachment instanceof SnapChatAttachment) {
                    return "[阅后即焚]";
                }

                return null;
            }

            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                String msgId = recent.getRecentMessageId();
                List<String> uuids = new ArrayList<>(1);
                uuids.add(msgId);
                List<IMMessage> msgs = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                if (msgs != null && !msgs.isEmpty()) {
                    IMMessage msg = msgs.get(0);
                    Map<String, Object> content = msg.getRemoteExtension();
                    if (content != null && !content.isEmpty()) {
                        return (String) content.get("content");
                    }
                }
                return null;
            }
        });
    }
}
