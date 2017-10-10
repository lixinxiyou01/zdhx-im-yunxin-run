package com.netease.nim.demo.main.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.demo.main.fragment.CircleFragment;
import com.netease.nim.demo.main.fragment.HomeFragment;
import com.netease.nim.demo.main.reminder.ReminderId;
import com.netease.nim.demo.main.reminder.ReminderItem;
import com.netease.nim.demo.main.reminder.ReminderManager;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.team.TeamCreateHelper;
import com.netease.nim.demo.team.activity.AdvancedTeamSearchActivity;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import zhwx.common.model.MomentRecordCount;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.ui.contact.AddContactActivity;

/**
 * 主界面
 * <p/>
 * Created by huangjun on 2015/3/25.
 */
public class MainActivity extends UI {

    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int REQUEST_CODE_NORMAL = 1;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;

    private HomeFragment mainFragment;

    private HashMap<String, ParameterValue> map2;

    private HashMap<String, ParameterValue> saveLoginMap;

    public static List<MomentRecordCount> counts;

    private ImageLoader mImageLoader;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    // 注销
    public static void logout(Context context, boolean quit) {
        Intent extra = new Intent();
        extra.putExtra(EXTRA_APP_QUIT, quit);
        start(context, extra);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        mImageLoader = new ImageLoader(this);
        requestBasicPermission();

        onParseIntent();

        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
            }
        });

        Log.i(TAG, "sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        }

        onInit();

        updata(); //蒲公英更新

        setPushUser();
    }

    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(MainActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                        )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess(){
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed(){
//        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }

    private void onInit() {
        // 加载主页面
        showMainFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }

    @Override
    public void onBackPressed() {
        if (mainFragment != null) {
            if (mainFragment.onBackPressed()) {
                return;
            } else {
                moveTaskToBack(true);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.create_normal_team:
                editGroupName(REQUEST_CODE_NORMAL);
                break;
            case R.id.create_regular_team:
                editGroupName(REQUEST_CODE_ADVANCED);
                break;
            case R.id.search_advanced_team:
                AdvancedTeamSearchActivity.start(MainActivity.this);
                break;
            case R.id.add_buddy:
                startActivity(new Intent(MainActivity.this, AddContactActivity.class));
//                AddFriendActivity.start(MainActivity.this);
                break;
            case R.id.search_btn:
                GlobalSearchActivity.start(MainActivity.this);
                break;
            case R.id.sao:
                startActivity(new Intent(MainActivity.this, CaptureActivity.class).putExtra("moduleCode","login"));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onParseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
                default:
                    break;
            }
        } else if (intent.hasExtra(EXTRA_APP_QUIT)) {
            onLogout();
            return;
        }
//        else if (intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
//            if (AVChatProfile.getInstance().isAVChatting()) {
//                Intent localIntent = new Intent();
//                localIntent.setClass(this, AVChatActivity.class);
//                startActivity(localIntent);
//            }
//        }
        else if (intent.hasExtra(com.netease.nim.demo.main.model.Extras.EXTRA_JUMP_P2P)) {
            Intent data = intent.getParcelableExtra(com.netease.nim.demo.main.model.Extras.EXTRA_DATA);
            String account = data.getStringExtra(com.netease.nim.demo.main.model.Extras.EXTRA_ACCOUNT);
            if (!TextUtils.isEmpty(account)) {
                SessionHelper.startP2PSession(this, account);
            }
        } else if (intent.hasExtra("startFlag")) {
            //TODO 登录统计
            saveLoginToServer();
        }
    }


    /**
     * 提交登录状态到服务器
     */
    public void saveLoginToServer(){
        System.out.println("提交登录状态到服务器");
        saveLoginMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
        saveLoginMap.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
        saveLoginMap.put("terminal", new ParameterValue("0"));
        new ProgressThreadWrap(MainActivity.this, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    UrlUtil.saveOrUpdate(ECApplication.getInstance().getAddress(), saveLoginMap).trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showMainFragment() {
        if (mainFragment == null && !isDestroyedCompatible()) {
            mainFragment = new HomeFragment();
            switchFragmentContent(mainFragment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createNormalTeam(MainActivity.this, groupName,selected, false, null);
                } else {
                    Toast.makeText(MainActivity.this, "请选择至少一个联系人！", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_ADVANCED) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                TeamCreateHelper.createAdvancedTeam(MainActivity.this, selected,groupName);
            }
        }

    }

    /**
     * 检查更新
     */
    public void updata() {
        PgyUpdateManager.register(MainActivity.this,getString(R.string.file_provider),new UpdateManagerListener() {

            @Override
            public void onUpdateAvailable(final String result) {
                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);

                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.pyger_update_dialog);
                TextView umeng_update_content = (TextView) window.findViewById(R.id.umeng_update_content);
                umeng_update_content.setText("v" + appBean.getVersionName() + "版本更新日志：\n" + (StringUtil.isBlank(appBean.getReleaseNote())?"无":appBean.getReleaseNote()));
                Button umeng_update_id_ok = (Button) window.findViewById(R.id.umeng_update_id_ok);
                umeng_update_id_ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        startDownloadTask(MainActivity.this,appBean.getDownloadURL());
                        alertDialog.dismiss();
                    }
                });
                Button umeng_update_id_cancel = (Button) window.findViewById(R.id.umeng_update_id_cancel);
                umeng_update_id_cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        alertDialog.dismiss();
                    }
                });
            }

            @Override
            public void onNoUpdateAvailable() {

            }
        });}

    private ECAlertDialog buildAlert = null;

    private String groupName = "";
    /**
     * 创建群组名称
     */
    private void editGroupName(final int groupType) {
        buildAlert = ECAlertDialog.buildAlert(this,R.string.address_v3, null, new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                groupName = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
                ContactSelectActivity.Option option = TeamHelper.getCreateContactSelectOption(null, 50);
                NimUIKit.startContactSelect(MainActivity.this, option, groupType);
            }
        });
        buildAlert.setTitle((groupType == REQUEST_CODE_NORMAL? "讨论组":"群组") + "名称");
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.setContentView(R.layout.config_dcaddress_dialog);
        final EditText editText = (EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText));
        TextView delectTV = (TextView) buildAlert.getWindow().findViewById(R.id.delectTV);
        delectTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        if(!buildAlert.isShowing()){
            buildAlert.show();
            editText.setSelection(editText.getText().length());
            editText.setSelectAllOnFocus(true);
        }
    }

    private HashMap<String, ParameterValue> map;

    private Handler handler = new Handler();

    public void setPushUser(){
        final String regId = JPushInterface.getRegistrationID(this);

        if(regId == null){
            ToastUtil.showMessage("推送服务绑定失败");
            return;
        }
        map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
        map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
        map.put("mobileKind", new ParameterValue("1"));
        map.put("mobileFlag", new ParameterValue(regId+";"+regId));
        new ProgressThreadWrap(this, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    final String flag = UrlUtil.setUserMobileFlag(ECApplication.getInstance().getAddress(), map).trim();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (flag.contains("ok")) {
                                System.out.println("推送绑定成功：" + regId);
                            } else {
                                System.out.println("推送绑定失败:" + flag);
                            }
                        }
                    }, 5);
                } catch (IOException e) {
                    System.out.println("推送绑定失败");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNewMomentRecordCount();
    }


    /**
     * 主页各种通知
     * personTime, classTime, personRecordTime, classRecordTime
     */
    public void getNewMomentRecordCount(){
        System.out.println("主页各种通知");
        map2 = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
        map2.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
        map2.put("personTime", new ParameterValue(ECApplication.getInstance().getLastSchoolCircleTime()));
        map2.put("classTime", new ParameterValue(ECApplication.getInstance().getLastClassCircleTime()));
        map2.put("personRecordTime", new ParameterValue(ECApplication.getInstance().getLastSchoolCircleRecordTime()));
        map2.put("classRecordTime", new ParameterValue(ECApplication.getInstance().getLastClassCircleRecordTime()));
        new ProgressThreadWrap(MainActivity.this, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    final String json = UrlUtil.getNewMomentRecordCount(ECApplication.getInstance().getAddress(), map2).trim();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            refreshMomentRceord(json);
                        }
                    }, 5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void refreshMomentRceord(String json){
        System.out.println(json);
        int sMomentCount = 0;
        int cMomentCount = 0;
        int sRceordCount = 0;
        int cRceordCount = 0;
        if(!json.contains("<html>")){
            try {
                counts = new Gson().fromJson(json, new TypeToken<List<MomentRecordCount>>(){}.getType());
                sMomentCount = Integer.parseInt(counts.get(0).getCount());
                cMomentCount = Integer.parseInt(counts.get(1).getCount());
                sRceordCount = Integer.parseInt(counts.get(2).getCount());
                cRceordCount = Integer.parseInt(counts.get(3).getCount());

                if((sRceordCount + cRceordCount) > 0){//有回复的时候显示数字
                    ReminderItem item = ReminderManager.getInstance().getItems().get(ReminderId.CIRCLE);
                    item.setIndicator(true);
                    HomeFragment.onNoticeChange(item);
                }else{
                    if((sMomentCount + cMomentCount) > 0){//没有回复有新动态的时候显示红点
                        ReminderItem item = ReminderManager.getInstance().getItems().get(ReminderId.CIRCLE);
                        item.setIndicator(true);
                        HomeFragment.onNoticeChange(item);
                    }else{//什么也没有隐藏
                        ReminderItem item = ReminderManager.getInstance().getItems().get(ReminderId.CIRCLE);
                        item.setIndicator(false);
                        HomeFragment.onNoticeChange(item);
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //校友圈 收到新回复提示
            if (CircleFragment.unReadCircleCountTV1 != null) {
                if(sRceordCount > 0){
                    CircleFragment.unReadCircleCountTV1.setBackgroundResource(R.drawable.red_circle);
                    CircleFragment.unReadCircleCountTV1.setVisibility(View.VISIBLE);
                    CircleFragment.unReadCircleCountTV1.setText(sRceordCount + "");
                }else{
                    CircleFragment.unReadCircleCountTV1.setVisibility(View.INVISIBLE);
                }
            }

            //校友圈  新动态提示
            if (CircleFragment.sMomentLay != null) {
                if(sMomentCount > 0){
                    CircleFragment.sMomentLay.setVisibility(View.VISIBLE);
                    mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + counts.get(0).getHeadUrl(), CircleFragment.sMomentIV,
                            false);
                }else{
                    CircleFragment.sMomentLay.setVisibility(View.INVISIBLE);
                }
            }

            //班级墙报  收到新回复提示
            if (CircleFragment.unReadCircleCountTV2!=null) {
                if(cRceordCount > 0){
                    CircleFragment.unReadCircleCountTV2.setVisibility(View.VISIBLE);
                    CircleFragment.unReadCircleCountTV2.setText(cRceordCount + "");
                }else{
                    CircleFragment.unReadCircleCountTV2.setVisibility(View.INVISIBLE);
                }
            }

            //班级墙报  新动态提示
            if (CircleFragment.cMomentLay != null) {
                if(cMomentCount > 0){
                    CircleFragment.cMomentLay.setVisibility(View.VISIBLE);
                    mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + counts.get(1).getHeadUrl(), CircleFragment.cMomentIV,false);
                }else{
                    CircleFragment.cMomentLay.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听
        LogoutHelper.logout();

        // 启动登录
        LoginActivity.start(this);
        finish();
    }


}
