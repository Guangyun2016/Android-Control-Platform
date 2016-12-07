package com.sirui.iotplatform.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.avos.avoscloud.okhttp.Call;
import com.avos.avoscloud.okhttp.Callback;
import com.avos.avoscloud.okhttp.OkHttpClient;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.Response;
import com.sirui.iotplatform.R;
import com.sirui.iotplatform.ui.activity.MainActivity;
import com.sirui.iotplatform.utils.AppDatas;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wx.android.common.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

    private static final String TAG = WXEntryActivity.class.getSimpleName();

    private EditText edtCount;
    private EditText edtPassWord;
    private Button btnLogin;
    private ImageButton wechatLogin;

    private static final int ACTION_GET_TOKEN = 1;
    private static final int ACTION_GET_CHECK = 2;
    private static final int ACTION_GET_INFO = 3;



    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private String code = null;
    private String access_token = null;
    private String openid = null;

    private String nickname = null;
    private int sex = 0;
    private String province = null;
    private String city = null;
    private String country = null;
    private String headimgurl = null;

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ACTION_GET_TOKEN:
                    getaccess_token();
                    break;
                case ACTION_GET_CHECK:
//                    check_ccess_token();
                    break;
                case ACTION_GET_INFO:
                    getuser_info();
                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, AppDatas.WX_AppId, false);
        // 将该app注册到微信
        api.registerApp(AppDatas.WX_AppId);
        /**加上这个即可*/
        api.handleIntent(getIntent(), this);

        sharedPreferences = this.getSharedPreferences("wxUserInfo", 0);
        editor = sharedPreferences.edit();

        initView();
    }

    private void initView() {

        edtCount = (EditText) findViewById(R.id.edt_count);
        edtPassWord = (EditText) findViewById(R.id.edt_pw);
        btnLogin = (Button) findViewById(R.id.btn_login);
        wechatLogin = (ImageButton) findViewById(R.id.login_wechat);


        edtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmpty(edtCount) == false && isEmpty(edtPassWord) == false) {
                    btnLogin.setBackgroundResource(R.drawable.btn_blue_selector);
                }
            }
        });

        edtPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmpty(edtCount) == false && isEmpty(edtPassWord) == false) {
                    btnLogin.setBackgroundResource(R.drawable.btn_blue_selector);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty(edtCount) && !isEmpty(edtPassWord) ) {
                    ToastUtils.showToast("登录");
                    // 跳转
                    startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                    WXEntryActivity.this.finish();
                } else {
                    ToastUtils.showToast("账号或者密码为空");
                }
            }
        });

        wechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (api == null) {
                    // 通过WXAPIFactory工厂，获取IWXAPI的实例
                    api = WXAPIFactory.createWXAPI(getApplicationContext(), AppDatas.WX_AppId, false);
                    // 将该app注册到微信
                    api.registerApp(AppDatas.WX_AppId);
                }

                if (!api.isWXAppInstalled()) {
                    //提醒用户没有按照微信
                    ToastUtils.showToast("使用该功能要先安装微信!");
                    return;
                }

                // send oauth request wechat login
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);

                ToastUtils.showToast("微信登录");
            }
        });




    }

    private boolean isEmpty(EditText et) {
        if (et.getText() == null || et.getText().toString().trim().equals("")) {
            return true;// 为空返回true
        }
        return false;// 非空返回false
    }


    public void goBackView(View view) {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();

    }

    public void registCount(View view) {
        ToastUtils.showToast("注册账号");
    }

    public void forgetPassword(View view) {
        ToastUtils.showToast("忘记密码");
    }


    /** wechat login加上这个即可
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq req) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     */
    @Override
    public void onResp(BaseResp resp) {

        int result = 0;
        switch (resp.errCode) {
            // 用户同意
            case BaseResp.ErrCode.ERR_OK:
                code = ((SendAuth.Resp) resp).code; //即为所需的code
                result = R.string.errcode_success;
                mHandler.sendEmptyMessage(ACTION_GET_TOKEN);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        ToastUtils.showToast(getResources().getString(result));
    }

    /**
     * 获得Token
     */
    private void getaccess_token() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + AppDatas.WX_AppId
                        + "&secret=" + AppDatas.WX_AppSecret
                        + "&code=" + code
                        + "&grant_type=authorization_code")
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String res = response.body().string();
//                ToastUtils.showToast(res);

                try {
                    JSONObject jb = new JSONObject(res.toString());
                    access_token = (String) jb.get("access_token");
                    openid = (String) jb.get("openid");
//                    mHandler.sendEmptyMessage(ACTION_GET_CHECK);
                    mHandler.sendEmptyMessage(ACTION_GET_INFO);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

/*                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edtCount.setText(access_token);
                        edtPassWord.setText(openid);
                    }
                });*/
            }
        });
    }

    /**
     * 获得user_info
     */
    private void getuser_info() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/userinfo?access_token="
                        + access_token +"&openid="+ openid)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String res = response.body().string();
//                ToastUtils.showToast(res);

                try {
                    JSONObject jb = new JSONObject(res.toString());
                    nickname = (String) jb.get("nickname");
                    sex = (Integer) jb.get("sex");
                    province = (String) jb.get("province");
                    city = (String) jb.get("city");
                    country = (String) jb.get("country");
                    headimgurl = (String) jb.get("headimgurl");

                    editor.putString("nickname", nickname);
                    editor.putInt("sex", sex);
                    editor.putString("province", province);
                    editor.putString("city", city);
                    editor.putString("country", country);
                    editor.putString("headimgurl", headimgurl);
                    editor.putString("signature", "这个人很懒，什么也没写");// 自己添加
                    editor.commit();
                    ToastUtils.showToast("SharedPreference保存成功");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        edtCount.setText(res);
//                        edtPassWord.setText("nickname:" + nickname +
//                                " sex:" + sex +
//                                " province:" + province +
//                                " city:" + city +
//                                " country:" + country +
//                                " headimgurl:" + headimgurl);

                        // 跳转
                        startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                        WXEntryActivity.this.finish();
                    }
                });
            }
        });
    }

    /**
     * 校验Token
     */
    private void check_ccess_token() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/auth?access_token="
                        + access_token +"&openid="+ openid)
                .build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String res = response.body().string();
//                ToastUtils.showToast(res);

            }
        });
    }



}
