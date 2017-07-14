package org.snje.webnote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class MainActivity extends Activity {

    private WebView webView;
    private String[] listItems = new String[]{"网站登陆信息设定"};
    private String url;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        webView = (WebView) findViewById(R.id.webView);
        init();
    }

    private void init() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        webView.clearCache(true);
        webView.clearHistory();

        if (load_cfg()) {
            open_site();
        }
    }

    private boolean load_cfg() {
        try {
            String[] data = DataService.getSavedUserInfo(this);
            this.url = data[0];
            this.password = data[1];
            return true;
        } catch (Exception e) {
            this.url = "";
            this.password = "";
            return false;
        }
    }

    private void open_site() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        final String site_url = this.url;
        final String password = this.password;
        if (site_url.length() == 0 || password.length() == 0) {
            return;
        }
        webView.setWebViewClient(new WebViewClient() {
            private long last_post_time = 0;
            private boolean posted = false;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (BuildConfig.DEBUG) {
                    try {
                        String urlReal = URLDecoder.decode(url, "UTF-8");
                        Log.d("Main/onPageFinished", "Load Url :" + urlReal);
                    } catch (UnsupportedEncodingException e) {
                    }
                }

                super.onPageFinished(view, url);
                String login_url = site_url + "/system/login";
                if (url.equals(login_url) && !posted) {
                    posted = true;
                    long now = System.currentTimeMillis();
                    if (now - last_post_time > 5000) {
                        last_post_time = now;
                        webView.postUrl(login_url, ("pwd=" + password + "&app=1").getBytes());
                    } else {
                        Toast.makeText(MainActivity.this
                                , "检测到循环登陆，您的登陆信息可能存在问题，请更正后再试"
                                , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    posted = false;
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage(message)
                        .setPositiveButton("ok", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }
        });
        webView.loadUrl(site_url);
    }

    private void show_menu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("再次点击返回键退出程序");
        AlertDialog.Builder builder1 = builder.setItems(listItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, SiteSettingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                    }
                });
        final AlertDialog dlg = builder.create();
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ExitApplication.getInstance().exit();
            }
        });
        dlg.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        load_cfg();
        open_site();
    }

    //对物理按钮的监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                show_menu();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
