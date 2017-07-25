package com.netease.nim.demo;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.netease.nim.demo.common.util.crash.AppCrashHandler;
import com.netease.nim.demo.common.util.sys.SystemUtil;
import com.netease.nim.demo.config.ExtraOptions;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.ContactHelper;
import com.netease.nim.demo.event.DemoOnlineStateContentProvider;
import com.netease.nim.demo.event.OnlineStateEventManager;
import com.netease.nim.demo.main.activity.WelcomeActivity;
import com.netease.nim.demo.session.NimDemoLocationProvider;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nim.uikit.custom.DefalutUserInfoProvider;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimStrings;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.pgyersdk.crash.PgyCrashManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import volley.Request;
import volley.RequestQueue;
import volley.toolbox.Volley;
import zhwx.Constant;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.User;
import zhwx.common.util.IMUtils;
import zhwx.common.util.SharPreUtil;
import zhwx.common.util.StringUtil;
import zhwx.common.util.UrlUtil;

public class ECApplication extends Application {

    private static ECApplication instance;

    private List<Activity> activityList = new ArrayList<Activity>();

    private static RequestQueue mVolleyQueue = null;

    /**
     * 单例，返回一个实例
     * @return
     */
    public static synchronized ECApplication getInstance() {
        if (instance == null) {
            LogUtil.w("ECApplication","[ECApplication] instance is null.");
        }
        return instance;
    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
    public void onCreate() {
        super.onCreate();
        instance = this;
        DemoCache.setContext(this);

        IMUtils.setContext(this);

        NIMClient.init(this, getLoginInfo(), getOptions());

        JPushInterface.setDebugMode(true);

        PgyCrashManager.register(this);

        JPushInterface.init(this);

        ExtraOptions.provide();

        // crash handler
        AppCrashHandler.getInstance(this);

        CCPAppManager.setContext(this);

        initImageLoader();

        if (inMainProcess()) {
            // init pinyin
            PinYin.init(this);

            PinYin.validate();

            // 初始化UIKit模块
            initUIKit();

            // 注册通知消息过滤器
            registerIMMessageFilter();

            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

            // 注册白板会话
            enableRTS();

            // 注册语言变化监听
            registerLocaleReceiver(true);

            OnlineStateEventManager.init();
        }
    }

    private LoginInfo getLoginInfo() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options);

        // 配置保存图片，文件，log等数据的目录
        options.sdkStorageRootPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // 用户信息提供者
        options.userInfoProvider = new DefalutUserInfoProvider(this);

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
//        options.sessionReadAck = true;

        // 云信私有化配置项
//        configServerAddress(options);

        return options;
    }

//    private void configServerAddress(final SDKOptions options) {
//        String appKey = PrivatizationConfig.getAppKey();
//        if (!TextUtils.isEmpty(appKey)) {
//            options.appKey = appKey;
//        }
//
//        ServerAddresses serverConfig = PrivatizationConfig.getServerAddresses();
//        if (serverConfig != null) {
//            options.serverConfig = serverConfig;
//        }
//    }

    private void initStatusBarNotificationConfig(SDKOptions options) {
        // load 应用的状态栏配置
        StatusBarNotificationConfig config = loadStatusBarNotificationConfig();

        // load 用户的 StatusBarNotificationConfig 设置项
        StatusBarNotificationConfig userConfig = UserPreferences.getStatusConfig();
        if (userConfig == null) {
            userConfig = config;
        } else {
            // 新增的 UserPreferences 存储项更新，兼容 3.4 及以前版本
            // 新增 notificationColor 存储，兼容3.6以前版本
            // APP默认 StatusBarNotificationConfig 配置修改后，使其生效
            userConfig.notificationEntrance = config.notificationEntrance;
//            userConfig.notificationFolded = config.notificationFolded;
//            userConfig.notificationColor = getResources().getColor(R.color.color_blue_3a9efb);
        }
        // 持久化生效
        UserPreferences.setStatusConfig(userConfig);
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = userConfig;
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private StatusBarNotificationConfig loadStatusBarNotificationConfig() {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = WelcomeActivity.class;
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
//        config.notificationColor = getResources().getColor(R.color.color_blue_3a9efb);
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        // save cache，留做切换账号备用
        DemoCache.setNotificationConfig(config);
        return config;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "zdhx_im_File/image");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .threadPoolSize(1)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .diskCacheFileNameGenerator(IMUtils.md5FileNameGenerator)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiscCache(cacheDir ,null ,IMUtils.md5FileNameGenerator))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // .writeDebugLogs() // Remove for release app
                .build();//开始构建
        ImageLoader.getInstance().init(config);
    }


    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     */
    private void registerIMMessageFilter() {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(new IMMessageFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (UserPreferences.getMsgIgnore() && message.getAttachment() != null) {
                    if (message.getAttachment() instanceof UpdateTeamAttachment) {
                        UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                        for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                            if (field.getKey() == TeamFieldEnum.ICON) {
                                return true;
                            }
                        }
                    } else  {
                        return true;
                    }
                }
                return false;
            }
        });
    }


    /**
     * 白板实时时会话配置与监听
     */
    private void enableRTS() {
        registerRTSIncomingObserver(true);
    }


    private void registerRTSIncomingObserver(boolean register) {
//        RTSManager.getInstance().observeIncomingSession(new Observer<RTSData>() {
//            @Override
//            public void onEvent(RTSData rtsData) {
//                RTSActivity.incomingSession(DemoCache.getContext(), rtsData, RTSActivity.FROM_BROADCAST_RECEIVER);
//            }
//        }, register);
    }

    private void registerLocaleReceiver(boolean register) {
        if (register) {
            updateLocale();
            IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(localeReceiver, filter);
        } else {
            unregisterReceiver(localeReceiver);
        }
    }

    private BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                updateLocale();
            }
        }
    };

    private void updateLocale() {
        NimStrings strings = new NimStrings();
        strings.status_bar_multi_messages_incoming = getString(R.string.nim_status_bar_multi_messages_incoming);
        strings.status_bar_image_message = getString(R.string.nim_status_bar_image_message);
        strings.status_bar_audio_message = getString(R.string.nim_status_bar_audio_message);
        strings.status_bar_custom_message = getString(R.string.nim_status_bar_custom_message);
        strings.status_bar_file_message = getString(R.string.nim_status_bar_file_message);
        strings.status_bar_location_message = getString(R.string.nim_status_bar_location_message);
        strings.status_bar_notification_message = getString(R.string.nim_status_bar_notification_message);
        strings.status_bar_ticker_text = getString(R.string.nim_status_bar_ticker_text);
        strings.status_bar_unsupported_message = getString(R.string.nim_status_bar_unsupported_message);
        strings.status_bar_video_message = getString(R.string.nim_status_bar_video_message);
        strings.status_bar_hidden_message_content = getString(R.string.nim_status_bar_hidden_msg_content);
        NIMClient.updateStrings(strings);
    }

    private void initUIKit() {
        // 初始化，需要传入用户信息提供者
        NimUIKit.init(this, infoProvider, contactProvider);

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
        NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // 会话窗口的定制初始化。
        SessionHelper.init();

        // 通讯录列表定制初始化
        ContactHelper.init();

        //在线状态提供者
        NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }

    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }

            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.drawable.avatar_def;
        }

        @Override
        public Bitmap getTeamIcon(String teamId) {
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            // 从内存缓存中查找群头像
            Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team != null) {
                Bitmap bm = ImageLoaderKit.getNotificationBitmapFromCache(team.getIcon());
                if (bm != null) {
                    return bm;
                }
            }

            // 默认图
            Drawable drawable = getResources().getDrawable(R.drawable.nim_avatar_group);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            UserInfo user = getUserInfo(account);
            return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user.getAvatar()) : null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                nick = NimUserInfoCache.getInstance().getAlias(account);
            } else if (sessionType == SessionTypeEnum.Team) {
                nick = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                if (TextUtils.isEmpty(nick)) {
                    nick = NimUserInfoCache.getInstance().getAlias(account);
                }
            }
            // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }

            return nick;
        }
    };

    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }

            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };

    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }
    };


    /********************************  经纶教育定义  ******************************/
    /**
     * IM登录信息
     * @return
     */
    public Map<String, ParameterValue> getLoginMap() {
        HashMap<String, ParameterValue> loginMap = new HashMap<String, ParameterValue>();
        loginMap.put("imToken", new ParameterValue(getCurrentIMUser().getToken()));
        loginMap.put("sys_Token", new ParameterValue(getCurrentIMUser().getToken()));
        String userName = getCurrentIMUser().getLoginName()+","+getCurrentIMUser().getOrganizationId();
        loginMap.put("sys_auto_authenticate", new ParameterValue("true"));
        loginMap.put("sys_username", new ParameterValue(userName));
        loginMap.put("sys_password", new ParameterValue(getCurrentIMUser().getPassWord()));
        return loginMap;
    }

    /**
     * V3登录信息
     * @return
     */
    public Map<String, ParameterValue> getV3LoginMap() {
        HashMap<String, ParameterValue> loginMap = new HashMap<String, ParameterValue>();
        loginMap.put("sys_auto_authenticate", new ParameterValue("true"));
        loginMap.put("sys_Token", new ParameterValue(getCurrentIMUser().getToken()));
        loginMap.put("dataSourceName", new ParameterValue(getCurrentIMUser().getDataSourceName()));
        loginMap.put("sys_username", new ParameterValue(getCurrentIMUser().getLoginName()));
        loginMap.put("sys_password", new ParameterValue(StringUtil.isBlank(getCurrentIMUser().getV3Pwd())?getCurrentIMUser().getPassWord():getCurrentIMUser().getV3Pwd()));
        return loginMap;
    }

//    /**
//     * V3登录信息
//     * @return
//     */
//    public Map<String, ParameterValue> getV3LoginMap() {
//    	HashMap<String, ParameterValue> loginMap = new HashMap<String, ParameterValue>();
//    	loginMap.put("sys_auto_authenticate", new ParameterValue("true"));
//    	loginMap.put("sys_username", new ParameterValue("jishuzhichi"));
//    	loginMap.put("sys_password", new ParameterValue("000000"));
//    	return loginMap;
//    }


    public String getV3Address(){
        return getCurrentIMUser().getV3Url();
//        return "http://192.168.1.121:10000/dc-repair";
    }

    /**
     * 存储当前登录用户信息
     * @param user
     */
    public void saveCurrentIMUser(User user){
        if (user != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
            preferences.edit().putString("Id", user.getId())
                    //lizheng加入
                    .putString("dataSourceName",user.getDataSourceName())

                    .putString("Name", user.getName())
                    .putString("V3Id", user.getV3Id())
                    .putString("v3PassWord", user.getV3Pwd())
                    .putString("SubToken", user.getSubToken())
                    .putString("TerminalId", user.getTerminalId())
                    .putString("VoipAccount", user.getVoipAccount())
                    .putString("VoipPwd", user.getVoipPwd())
                    .putString("LoginName", user.getLoginName())
                    .putString("MobileNum", user.getMobileNum())
                    .putString("Sex", user.getSex())
                    .putString("passWord", user.getPassWord())
                    .putString("headPortraitUrl", user.getHeadPortraitUrl())
                    .putString("kind", user.getKind())
                    .putString("v3Url", user.getV3Url())
                    .putString("appCode", user.getAppCode())
                    .putString("accId", user.getAccId())
                    .putString("neteaseToken", user.getNeteaseToken())
                    .putString("appCenterUrl", user.getAppCenterUrl())
                    .commit();
            SharPreUtil.saveObject(user.getId(),user);
        }
    }

    /**
     * 获取当前登录用户对象
     * @return
     */
    public User getCurrentIMUser(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        User user = new User();
        user.setId(preferences.getString("Id", ""));
        user.setV3Id(preferences.getString("V3Id", ""));
        user.setV3Pwd(preferences.getString("v3PassWord", ""));
        user.setName(preferences.getString("Name", ""));
        user.setSubToken(preferences.getString("SubToken", ""));
        user.setTerminalId(preferences.getString("TerminalId", ""));
        user.setVoipAccount(preferences.getString("VoipAccount", ""));
        user.setVoipPwd(preferences.getString("VoipPwd", ""));
        user.setLoginName(preferences.getString("LoginName", ""));
        user.setMobileNum(preferences.getString("MobileNum", ""));
        user.setSex(preferences.getString("Sex", ""));
        user.setPassWord(preferences.getString("passWord", ""));
        user.setHeadPortraitUrl(preferences.getString("headPortraitUrl", ""));
        user.setDataSourceName(preferences.getString("dataSourceName", ""));
        user.setOrganizationId(preferences.getString("organizationId", ""));
        user.setKind(preferences.getString("kind", ""));
        user.setAppCode(preferences.getString("appCode", ""));
        user.setV3Url(preferences.getString("v3Url", ""));
        user.setToken(preferences.getString("token", ""));
        user.setAccId(preferences.getString("accId", ""));
        user.setAppCenterUrl(preferences.getString("appCenterUrl", ""));
        user.setNeteaseToken(preferences.getString("neteaseToken", ""));
        return user;
    }

    public User getIMUser(String id) {
        User user = (User) SharPreUtil.readObjece(id);
        if (user != null) {
            return user;
        }
        return null;
    }

    public User getIMUser() {
        User user = (User) SharPreUtil.readObjece(getCurrentIMUser().getId());
        if (user != null) {
            return user;
        }
        return null;
    }

    /**
     * 存储经纶教育服务器地址
     * @param address
     */
    public void saveAddress(String address) {
        if (address.endsWith("/")) {
            address = address.substring(address.length()-1, address.length());
        }
        address = UrlUtil.checkUrl(address);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("address",address).commit();
    }

    public String getAddress(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        String address = preferences.getString("address", "");
        if (address.equals("http://117.78.48.224:9999/im")) {
            address = Constant.SERVER_ADDRESS_DEFULT;
        }
        return address;
    }

    /**
     * 存储密码
     * @param psw
     */
    public void savePassWord(String psw){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("passWord",psw).commit();
    }
    public String getPassWord(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("passWord", "");
    }

    /**
     * 存储V3密码
     * @param psw
     */
    public void saveV3PassWord(String psw){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("v3PassWord",psw).commit();
        SharPreUtil.saveObject(ECApplication.getInstance().getCurrentIMUser().getId(),ECApplication.getInstance().getCurrentIMUser());
    }

    public String getV3PassWord(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("v3PassWord", "");
    }

	public void clearUserData(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
		preferences.edit().clear().commit();
	}

    /**
     * 存储当前用户手机号
     */
    public void saveMobileNum(String mobileNum){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("MobileNum",mobileNum).commit();
    }

    /**
     * 登录token
     * @param token
     */
    public void saveToken(String token){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("token",token).commit();
    }
    public String getToken(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("token", "");
    }

    /**
     * 第一次打开应用提示标记
     */
    public void saveFlag(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("webAppFlag","1").commit();
    }
    public String getFlag(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("webAppFlag", "");
    }

    /**
     * 存储最后一条圈子的时间
     * @param time
     */
    public void saveLastSchoolCircleTime(String time){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("lastSchoolCircleTime",time).commit();
    }
    public String getLastSchoolCircleTime(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("lastSchoolCircleTime", "2030-01-01 00:00:00");
    }
    public void saveLastClassCircleTime(String time){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("lastClassCircleTime",time).commit();
    }
    public String getLastClassCircleTime(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("lastClassCircleTime", "2030-01-01 00:00:00");
    }

    /**
     * 存储评论&赞最后时间
     * @param time
     */
    public void saveLastSchoolCircleRecordTime(String time){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("lastSchoolCircleRecordTime",time).commit();
    }
    public String getLastSchoolCircleRecordTime(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("lastSchoolCircleRecordTime", "2030-01-01 00:00:00");
    }
    public void saveLastClassCircleRecordTime(String time){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        preferences.edit().putString("lastClassCircleRecordTime",time).commit();
    }
    public String getLastClassCircleRecordTime(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);
        return preferences.getString("lastClassCircleRecordTime", "2030-01-01 00:00:00");
    }


    //添加Activity到容器中
    public void addActivity(Activity activity){
        activityList.add(activity);
        System.out.println("activityList"+activityList.size());
    }

    //移除Activity到容器中
    public void removeActivity(Activity activity){
        activityList.remove(activity);
        System.out.println("activityList"+activityList.size());
    }

    // 遍历所有Activity并finish
    public void exit(){
        for (Activity activity : activityList){
            activity.finish();
        }
        System.exit(0);
    }


    public static RequestQueue getRequestQueue() {
        if (mVolleyQueue == null) {
            mVolleyQueue = Volley.newRequestQueue(getInstance());
        }
        return mVolleyQueue;
    }

    public static void addRequest(Request<?> req, String tag) {
        req.setTag(tag);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }
}
