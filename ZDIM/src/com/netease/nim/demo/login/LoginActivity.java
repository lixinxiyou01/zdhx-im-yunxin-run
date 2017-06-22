package com.netease.nim.demo.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.contact.ContactHttpClient;
import com.netease.nim.demo.main.activity.MainActivity;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.Constant;
import zhwx.common.model.LoginErrorData;
import zhwx.common.model.LoginSucceedData;
import zhwx.common.model.Organization;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.JsonUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.CircleImageViewWithWhite;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.adapter.OrganizationSpinnerAdapter;


/**
 * 登录/注册界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class LoginActivity extends UI implements OnKeyListener {

    private Activity context;

    private HashMap<String, ParameterValue> map;

    private ECProgressDialog mPostingdialog;

    private Handler handler = new Handler();

    private String loginJson;

    private String tag = "LoginActivity";

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String KICK_OUT = "KICK_OUT";
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;

    private TextView rightTopBtn;  // ActionBar完成按钮
    private TextView switchModeBtn;  // 注册/登录切换按钮

//    private ClearableEditTextWithIcon loginAccountEdit;
//    private ClearableEditTextWithIcon loginPasswordEdit;

    private ClearableEditTextWithIcon registerAccountEdit;
    private ClearableEditTextWithIcon registerNickNameEdit;
    private ClearableEditTextWithIcon registerPasswordEdit;

    private View loginLayout;
    private View registerLayout;

    private ECAlertDialog buildAlert = null;

    private String ip;

    private List<Organization> organizations = new ArrayList<Organization>();

    private CircleImageViewWithWhite headImg;

    private Spinner organizationSp;

    private AbortableFuture<LoginInfo> loginRequest;
    private boolean registerMode = false; // 注册模式
    private boolean registerPanelInited = false; // 注册面板是否初始化

    /**newenwewenwewenwewenwewenw*/
    private EditText usernameET,passwordET;
    private Button loginBT;
    private ImageLoader imgLoader;

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login_new);
        imgLoader = new ImageLoader(context);
        initView();
        requestBasicPermission();
        onParseIntent();
//        initRightTopBtn();
        setupLoginPanel();
//        setupRegisterPanel();

        showCheck();//获取组织列表
    }

    private void initView() {
        organizationSp = (Spinner) findViewById(R.id.organizationSp);
        organizationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Organization organization = (Organization) parent.getAdapter()
                        .getItem(position);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
                preferences.edit().putString("organizationId", organization.getId()).putString("organizationName", organization.getName())
                        .putString("dataSourceName", organization.getDataSourceName())
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        usernameET = (EditText) findViewById(R.id.username);
        passwordET = (EditText) findViewById(R.id.password);
        loginBT = (Button) findViewById(R.id.loginBT);
        loginBT.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerMode) {
                    register();
                } else {
                    loginImServer();
                }
            }
        });
        headImg = (CircleImageViewWithWhite) findViewById(R.id.headImg);
        headImg.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                showConfigDcAddress();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StringUtil.isNotBlank(ECApplication.getInstance().getCurrentIMUser().getHeadPortraitUrl())) {
            // 设置头像
            imgLoader.DisplayImage(ECApplication.getInstance().getAddress()+ECApplication.getInstance().getCurrentIMUser().getHeadPortraitUrl(),headImg,false);
        } else {
            headImg.setImageResource(R.drawable.defult_head_img);

        }
    }

    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(LoginActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).request();
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

    private void onParseIntent() {
        if (getIntent().getBooleanExtra(KICK_OUT, false)) {
            int type = NIMClient.getService(AuthService.class).getKickedClientType();
            String client;
            switch (type) {
                case ClientType.Web:
                    client = "网页端";
                    break;
                case ClientType.Windows:
                    client = "电脑端";
                    break;
                case ClientType.REST:
                    client = "服务端";
                    break;
                default:
                    client = "移动端";
                    break;
            }
            EasyAlertDialogHelper.showOneButtonDiolag(LoginActivity.this, getString(R.string.kickout_notify),
                    String.format(getString(R.string.kickout_content), client), getString(R.string.ok), true, null);
        }
    }

    /**
     * 登录面板
     */
    private void setupLoginPanel() {
        String account = ECApplication.getInstance().getCurrentIMUser().getLoginName();
        usernameET.setText(account);

        String passWord = ECApplication.getInstance().getCurrentIMUser().getPassWord();
        passwordET.setText(passWord);
    }


    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 更新右上角按钮状态
//            if (!registerMode) {
//                // 登录模式
//                boolean isEnable = usernameET.getText().length() > 0
//                        && usernameET.getText().length() > 0;
//                updateRightTopBtn(LoginActivity.this, rightTopBtn, isEnable);
//            }
        }
    };

    private void updateRightTopBtn(Context context, TextView rightTopBtn, boolean isEnable) {
        rightTopBtn.setText(R.string.done);
        rightTopBtn.setBackgroundResource(R.drawable.g_white_btn_selector);
        rightTopBtn.setEnabled(isEnable);
        rightTopBtn.setTextColor(context.getResources().getColor(R.color.color_blue_0888ff));
        rightTopBtn.setPadding(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);
    }

    /**
     * ***************************************** 登录 **************************************
     */


    /** 登录IM服务器  */
    private void loginImServer() {
        final String loginName = usernameET.getEditableText().toString().toLowerCase();
        final String passWord =  passwordET.getEditableText().toString();
        DialogMaker.showProgressDialog(this, null, getString(R.string.loginingIMServer) + getString(R.string.app_name), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);
        map = new HashMap<String, ParameterValue>();
        map.put("terminal", new ParameterValue("android"));//android 0, ios 1, pc 2
        map.put("loginName", new ParameterValue(loginName));
        map.put("password", new ParameterValue(passWord));
        map.put("organizationId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getOrganizationId()));
        new ProgressThreadWrap(this, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    loginJson = UrlUtil.getUserWithLoginNameAndPassword(ECApplication.getInstance().getAddress(), map);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            System.out.println(loginJson);

                            if (loginJson.contains("errorMsg")) { // 后台登录错误
                                LoginErrorData led = JsonUtil.json2JavaPojo(loginJson, LoginErrorData.class);
                                ToastUtil.showMessage(led.getErrorMsg());
                                DialogMaker.dismissProgressDialog();
                            } else if (loginJson.contains("userData")) { // 后台登录成功
                                LoginSucceedData lsd = JsonUtil.json2JavaPojo(loginJson, LoginSucceedData.class);
                                Log.i(tag, "IM登录用户" + lsd.getUser().getName() + lsd.getUser().getId());
                                if (Constant.USER_ADMIN.equals(lsd.getUser().getKind())|| Constant.USER_OTHER.equals(lsd.getUser().getKind())) {
                                    ToastUtil.showMessage("此账号禁止登录");
                                    return;
                                }
                                lsd.getUser().setLoginName(loginName);
                                lsd.getUser().setPassWord(passWord);
                                ECApplication.getInstance().saveCurrentIMUser(lsd.getUser());
                                ECApplication.getInstance().saveToken(lsd.getToken());
                                loginChatServer();
                            } else {
                                ToastUtil.showMessage("无法解析登录返回数据,请检查网络");
                                DialogMaker.dismissProgressDialog();
                            }
                        }
                    }, 5);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("连接服务器失败");
                    DialogMaker.dismissProgressDialog();
                }
            }
        }).start();
    }

    private void loginChatServer() {
        DialogMaker.setMessage(getString(R.string.loginingIMServer));
        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        final String account = ECApplication.getInstance().getCurrentIMUser().getAccId();
        final String token = ECApplication.getInstance().getCurrentIMUser().getNeteaseToken();
        // 登录
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i(TAG, "login success");
                onLoginDone();
                DemoCache.setAccount(account);
                saveLoginInfo(account, token);

                // 初始化消息提醒
                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                // 初始化免打扰
                if (UserPreferences.getStatusConfig() == null) {
                    UserPreferences.setStatusConfig(DemoCache.getNotificationConfig());
                }
                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                // 构建缓存
                DataCacheManager.buildDataCacheAsync();

                // 进入主界面
                MainActivity.start(LoginActivity.this, new Intent().putExtra("startFlag",true));
                finish();
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    /**
     * 验证界面
     */
    public void showCheck() {
        if (ECApplication.getInstance().getAddress().length() == 0) {
            ECApplication.getInstance().saveAddress(Constant.SERVER_ADDRESS_DEFULT);
        }
        mPostingdialog = new ECProgressDialog(context, "连接服务器");
        mPostingdialog.show();
        new ProgressThreadWrap(this, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    String response = UrlUtil.getOrganizationJson(ECApplication.getInstance().getAddress());
                    if (response.contains("name")) {
                        organizations = JsonUtil.json2List(response,Organization.class);
                    } else {
                       updata();
                        ToastUtil.showMessage("网络异常,无法登录");
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (organizations.size() != 0) {
                                ShowSchoolInfo();
                                mPostingdialog.dismiss();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("服务器连接失败");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updata();
                            mPostingdialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 获取学校并加载列表
     */
    public void ShowSchoolInfo() {
        organizationSp.setAdapter(new OrganizationSpinnerAdapter(context,organizations));
        String id = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance()).getString("organizationId", "");
        for (int i = 0; i < organizations.size(); i++) {
            if (id.equals(organizations.get(i).getId())) {
                organizationSp.setSelection(i);
                break;
            }
        }
    }

    // 配置IP界面
    private void showConfigDcAddress() {
        buildAlert = ECAlertDialog.buildAlert(this,R.string.address_v3, null, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String address = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
                    if ("test".equals(address.toLowerCase())) {
                        ECApplication.getInstance().saveAddress(Constant.SERVER_ADDRESS_DEFULT_TEST);

                    } else if (StringUtil.isBlank(address)) {
                        ECApplication.getInstance().saveAddress(Constant.SERVER_ADDRESS_DEFULT);

                    } else if ("test1".equals(address.toLowerCase())) {
                        ECApplication.getInstance().saveAddress(Constant.SERVER_ADDRESS_DEFULT_TEST1);

                    } else {
                        ECApplication.getInstance().saveAddress(address);
                    }
                    ECApplication.getInstance().savePassWord("");
                    new ProgressThreadWrap(LoginActivity.this,new RunnableWrap() {
                        @Override
                        public void run() {
                            try {
                                String response = UrlUtil.getOrganizationJson(ECApplication.getInstance().getAddress());
                                organizations = JsonUtil.json2List(response,Organization.class);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ShowSchoolInfo();
                                        usernameET.setText("");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                showConfigDcAddress();
                                ToastUtil.showMessage("验证错误，请检查地址是否正确");
                            }
                        }
                    }).start();
                }
            });
            buildAlert.setTitle(getResources().getString(R.string.app_name) + "地址");
            buildAlert.setCanceledOnTouchOutside(false);
            buildAlert.setContentView(R.layout.config_dcaddress_dialog);
            String server = "";
            server = ECApplication.getInstance().getAddress();
            final EditText editText = (EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText));
            editText.setHint("请输入服务器地址");
            TextView delectTV = (TextView) buildAlert.getWindow().findViewById(R.id.delectTV);
            delectTV.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    editText.setText("");
                }
            });
            if (!server.equals("")) {
                editText.setText(server);
            }
            if(!buildAlert.isShowing()){
                if (context != null) {
                    buildAlert.show();
                    editText.setSelection(editText.getText().length());
                    editText.setSelectAllOnFocus(true);
                }
        }
    }

    public void updata() {
        PgyUpdateManager.register(LoginActivity.this,getString(R.string.file_provider),new UpdateManagerListener() {

            @Override
            public void onUpdateAvailable(final String result) {

                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);

                final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.pyger_update_dialog);
                TextView umeng_update_content = (TextView) window.findViewById(R.id.umeng_update_content);
                umeng_update_content.setText("v" + appBean.getVersionName() + "版本更新日志：\n" + (StringUtil.isBlank(appBean.getReleaseNote())?"无":appBean.getReleaseNote()));
                Button umeng_update_id_ok = (Button) window.findViewById(R.id.umeng_update_id_ok);
                umeng_update_id_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        startDownloadTask(LoginActivity.this,appBean.getDownloadURL());
                        alertDialog.dismiss();
                    }
                });
                Button umeng_update_id_cancel = (Button) window.findViewById(R.id.umeng_update_id_cancel);
                umeng_update_id_cancel.setOnClickListener(new OnClickListener() {

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


    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    /**
     * 关闭对话框
     */
    private void dismissPostingDialog() {
        if (mPostingdialog == null || !mPostingdialog.isShowing()) {
            return;
        }
        mPostingdialog.dismiss();
        mPostingdialog = null;
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
    private String tokenFromPassword(String password) {
        String appKey = readAppKey(this);
        boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey)
                || "fe416640c8e8a72734219e1847ad2547".equals(appKey);

        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ***************************************** 注册 **************************************
     */

    private void register() {
        if (!registerMode || !registerPanelInited) {
            return;
        }

        if (!checkRegisterContentValid()) {
            return;
        }

        if (!NetworkUtil.isNetAvailable(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        DialogMaker.showProgressDialog(this, getString(R.string.registering), false);

        // 注册流程
        final String account = registerAccountEdit.getText().toString();
        final String nickName = registerNickNameEdit.getText().toString();
        final String password = registerPasswordEdit.getText().toString();

        ContactHttpClient.getInstance().register(account, nickName, password, new ContactHttpClient.ContactHttpCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(LoginActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                usernameET.setText(account);
                passwordET.setText(password);

                registerAccountEdit.setText("");
                registerNickNameEdit.setText("");
                registerPasswordEdit.setText("");

                DialogMaker.dismissProgressDialog();
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                Toast.makeText(LoginActivity.this, getString(R.string.register_failed, code+"", errorMsg), Toast.LENGTH_SHORT)
                        .show();

                DialogMaker.dismissProgressDialog();
            }
        });
    }

    private boolean checkRegisterContentValid() {
        if (!registerMode || !registerPanelInited) {
            return false;
        }

        // 帐号检查
        String account = registerAccountEdit.getText().toString().trim();
        if (account.length() <= 0 || account.length() > 20) {
            Toast.makeText(this, R.string.register_account_tip, Toast.LENGTH_SHORT).show();

            return false;
        }

        // 昵称检查
        String nick = registerNickNameEdit.getText().toString().trim();
        if (nick.length() <= 0 || nick.length() > 10) {
            Toast.makeText(this, R.string.register_nick_name_tip, Toast.LENGTH_SHORT).show();

            return false;
        }

        // 密码检查
        String password = registerPasswordEdit.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, R.string.register_password_tip, Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    /**
     * ***************************************** 注册/登录切换 **************************************
     */
}
