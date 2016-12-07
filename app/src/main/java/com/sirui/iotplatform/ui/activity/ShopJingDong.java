package com.sirui.iotplatform.ui.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sirui.iotplatform.R;
import com.sirui.iotplatform.utils.AppDatas;

public class ShopJingDong extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jin_dong_web);

        webView = (WebView) findViewById(R.id.jd_webview);
        init_webview();


    }

    private void init_webview() {
        //  Auto-generated method stub
        // ----可以用打开本包内asset目录下的index.html文件的方法
        webView.loadUrl(AppDatas.SHOP_URL_JINGDONG);
        // 设置支持获取手势焦点(区别这里不用getSettings())用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点
        webView.requestFocusFromTouch();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                //  Auto-generated method stub
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                //  Auto-generated method stub
                super.onLoadResource(view, url);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                //  Auto-generated method stub (应用程序重新请求网页数据)
                super.onFormResubmission(view, dontResend, resend);
            }

            /**
             * shouldOverrideUrlLoading在WebView显示网页
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //  Auto-generated method stub(报告错误信息)
                // // 用javascript隐藏系统定义的404页面信息,这里显示
                // String data = "Page NO FOUND！";
                // view.loadUrl("javascript:document.body.innerHTML=\"" + data
                // + "\"");
                //                view.loadUrl(url_error);

                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                //  Auto-generated method stub（获取返回信息授权请求）
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //  Auto-generated method stub重写此方法可以让webview处理https请求
                super.onReceivedSslError(view, handler, error);
            }

            /**
             * 开始加载网页
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //  Auto-generated method stub
                // 这个事件就是开始载入页面调用的，通常我们可以在这设定一个loading的页面，告诉用户程序在等待网络响应。
                //                Log.e(TAG, "onPageStarted");
                super.onPageStarted(view, url, favicon);
                // rl_loading.setVisibility(View.VISIBLE); // 显示加载界面
            }

            /**
             * 加载网页完毕
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                //  Auto-generated method stub
                // 在页面加载结束时调用。同样道理，我们知道一个页面载入完成，于是我们可以关闭loading 条，切换程序动作
                // Toast.makeText(getApplicationContext(), "加载完毕",
                // Toast.LENGTH_SHORT).show();

                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // Set progress bar during loading
            // 设置网页加载的进度条
            public void onProgressChanged(WebView view, int newProgress) {

                // Toast.makeText(getApplicationContext(),
                // "setWebChromeClient!", Toast.LENGTH_SHORT).show();

//                // 设置加载完成后结束动画
//                if (newProgress == 100) {
//                    // refreshLayouhome.setRefreshing(false);//下拉刷新
//                    progressBar.setVisibility(View.INVISIBLE);// 加载条
//                } else {
//                    if (View.INVISIBLE == progressBar.getVisibility()) {
//                        progressBar.setVisibility(View.VISIBLE);
//                    }
//                    progressBar.setProgress(newProgress);
//                }
                super.onProgressChanged(view, newProgress);
            }

            /**
             * JavaScript弹出框
             */
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //  Auto-generated method stub
                // Log.e(TAG, "onJsAlert " + message);
                // Toast.makeText(getApplicationContext(), message,
                // Toast.LENGTH_SHORT).show();
                result.confirm();
                return true;
                // return super.onJsAlert(view, url, message, result);
            }

            /**
             *  JavaScript确认框
             */
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                //  Auto-generated method stub
                // Log.e(TAG, "onJsConfirm " + message);
                return super.onJsConfirm(view, url, message, result);
            }

            /**
             * JavaScript输入框
             */
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //  Auto-generated method stub
                // Log.e(TAG, "onJsPrompt " + url);
                return super.onJsPrompt(view, url, message, defaultValue,
                        result);
            }

        });

        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true); // 得到WebSettings对象，设置支持JavaScript的参数
        webSetting.setSupportZoom(true); // 设置可以支持缩放
        webSetting.setDefaultZoom(WebSettings.ZoomDensity.FAR); // 设置默认缩放方式尺寸是far
        webSetting.setBuiltInZoomControls(true); // 设置出现缩放工具

        // webSetting.setCacheMode(WebSettings.LOAD_DEFAULT); //设置缓存模式
        // ,根据cache-control决定是否从网络上取数据
        // webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE); //只从网络获取数据，不使用缓存
//		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        // //根据cache-control决定是否从网络上取数据
        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 如果内容已经存在cache则使用cache，即使是过去的历史记录。如果cache中不存在，从网络中获取！

        webSetting.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSetting.setDatabaseEnabled(true); // 开启 database storage API 功能
        // webSetting.setPluginsEnabled(true);//支持插件
        webSetting.setLoadWithOverviewMode(true);

        webSetting.setUseWideViewPort(true);// 将图片调整到适合webview的大小(之前是false导致不同手机网页显示不同)
        // 支持内容从新布局（这个有些手机尺寸不同，匹配有问题!!!）
        // webSetting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//把所有内容放到WebView组件等宽的一列中。这个是强制的，把网页都挤变形了
        // webSetting.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);//正常显示，没有渲染变化。

        webSetting.supportMultipleWindows();// 多窗口
        webSetting.setAllowFileAccess(true);// 设置可以访问文件
        webSetting.setLoadsImagesAutomatically(true);// 支持自动加载图片

        String cacheDirPath = getFilesDir().getAbsolutePath() + AppDatas.PATH_CACHE;
        webSetting.setDatabasePath(cacheDirPath); // 设置数据库缓存路径
        webSetting.setAppCachePath(cacheDirPath); // 设置 Application Caches 缓存目录
        webSetting.setAppCacheEnabled(true); // 开启 Application Caches 功能

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void goBackView(View view) {
        this.finish();
    }
}
