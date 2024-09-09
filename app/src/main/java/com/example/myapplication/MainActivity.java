package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;



@SuppressLint({"MissingInflatedId", "LocalSuppress"})
public class MainActivity extends AppCompatActivity {
    public static WebView webView;
    private boolean outT = true;
    private int position = 0;
    private String cloudreveip;
    private Runnable hideButtonRunnable;
    private ValueCallback<Uri> valueCallback;
    private Button setbutton;
    private ValueCallback<Uri[]> valueCallbackArray;
    private boolean temp=true;
    private Button button;
    private final int REQUEST_CODE = 0x1010;
    private Handler handler = new Handler();
    private final static int RESULT_CODE = 0x1011;
    private Stack<String> history = new Stack<>();
    private View backButton;
    private boolean ifyes = true;
    private CircularProgressBar circularProgressBar;
    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 绑定按钮
         */
        setContentView(R.layout.activity_main);
        setbutton = findViewById(R.id.button_setting);
        button = findViewById(R.id.button2);
        backButton = findViewById(R.id.backbutton);
        hideButtonDelayed(3000);  // 3秒后隐藏按钮
        button.setVisibility(View.VISIBLE);

        setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(MainActivity.this, Sec.class);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // 加载新的布局文件
                LayoutInflater inflater = getLayoutInflater();
                View newView = inflater.inflate(R.layout.content_main, null);
                // 替换当前的布局
                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                rootView.removeAllViews();
                rootView.addView(newView);

                setContentView(R.layout.content_main);
                Button button4 = findViewById(R.id.button5);
                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        EditText serverip = findViewById(R.id.edit_text);
                        writeToFile(serverip.getText().toString());
                        Toast.makeText(getApplicationContext(),"change CloudReveIP 重启即可生效!",Toast.LENGTH_SHORT).show();
                    }
                });
                Button button7 = findViewById(R.id.button6);
                button7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                });
                TextView textView = findViewById(R.id.textView2);
                textView.setText(get());
                Toast.makeText(MainActivity.this,"change CloudReveIP",Toast.LENGTH_SHORT).show();
            }
        });
        // 设置按钮点击事件
        backButton.setOnClickListener(v -> {
            if (ifyes) {
                history.pop();
                webView.goBack();
                TextView error = findViewById(R.id.textViewError);
                error.setVisibility(View.GONE);
            } else {
                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        /**
         * 以上是按钮和页面部分
         * 下方是 webview 部分
         */
        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();

        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);//设置可以使用localStorage
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);//设置webView自适应屏幕大小
        settings.setBuiltInZoomControls(true);//关闭zoom
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);//关闭zoom按钮
        settings.setRenderPriority(WebSettings.RenderPriority.LOW);
        settings.setBlockNetworkImage(true);
        //反广告
        String userAgent = settings.getUserAgentString();
        userAgent += " NoAds";
        settings.setUserAgentString(userAgent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.LOAD_NORMAL);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setUseWideViewPort(true);
        } else {
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        /**
         * 基础浏览器功能实现
         */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 重定向到新的URL
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // 页面加载完成
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 页面开始加载
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (error.getErrorCode() == -6) {
                    Toast.makeText(MainActivity.this,"请检查网络权限,可能影响内容显示",Toast.LENGTH_SHORT).show();
                    outT = false;
                    circularProgressBar.setVisibility(View.GONE);
                    return;
                }
                if (!(error.getErrorCode() == -1)) {
                    Log.d("error", String.valueOf(error.getErrorCode()));
                    webView.setVisibility(View.GONE);
                    ifyes = false;
                    backButton.setVisibility(View.VISIBLE);
                    backButton.setEnabled(true);
                    try {
                        TextView textViewerror = findViewById(R.id.textViewError);
                        textViewerror.setVisibility(View.VISIBLE);
                        textViewerror.setText("页面错误! \n 访问的URL是 \n " + request.getUrl() + " \n如果网址存在则请检查网络或权限");
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this,"页面错误! \n 访问的URL是 \n " + request.getUrl() + " \n如果网址存在则请检查网络或权限",Toast.LENGTH_SHORT);
                    }


                    // 页面加载错误
                }
                outT = true;
            }
        });
        /**
         * 这里是为了图像显示正确
         */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        /**
         * 浏览器进度条
         */
        circularProgressBar = findViewById(R.id.circularProgressBar);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                circularProgressBar.setProgress(newProgress);
                if (outT ) {
                    if (newProgress == 100) {
                        circularProgressBar.setVisibility(View.GONE);
                    } else {

                        circularProgressBar.setVisibility(View.VISIBLE);
                    }
                }else {
                    circularProgressBar.setVisibility(View.GONE);
                }
                // 当进度达到100时，隐藏加载界面

                if (newProgress == 100) {
                    // 页面加载完成时更新按钮状态
                    backButton.setEnabled(webView.canGoBack());
                    history.push(webView.getUrl());

                    String r = webView.getUrl().replace(cloudreveip,"");
                    r = r.replace("home?path=%2F","").replace("/","");
                    if (r.equals("")) {
                        webView.clearHistory();
                    }
                    if (webView.canGoBack()) {
                        backButton.setVisibility(View.VISIBLE);
                    } else {
                        backButton.setVisibility(View.GONE);
                    }

                }
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (valueCallbackArray != null) {

                    valueCallbackArray.onReceiveValue(null);
                    valueCallbackArray = null;
                }
                valueCallbackArray = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (Exception e) {
                    valueCallbackArray = null;
                    return false;
                }
                return true;
            }
        });

        // 设置DownloadListener
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                Log.d("文件下载地址",url);
                startActivity(i);
            }
        });

        if (readFile2().equals("beta=true")) {

        }



        // 开启调试模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        String ip = readFile();
        cloudreveip = ip;
        Log.d("地址",ip);
        if (ip.equals("null")) {
            // 加载新的布局文件
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.content_main, null);
            // 替换当前的布局
            ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
            rootView.removeAllViews();
            rootView.addView(newView);

            setContentView(R.layout.content_main);
            TextView textView = findViewById(R.id.textView2);
            textView.setText(get());
            Toast.makeText(MainActivity.this,"change CloudReveIP",Toast.LENGTH_SHORT).show();
        }else{
            webView.loadUrl(ip);
        }

        // 检查是否有权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // 显示提示信息给用户
                Toast.makeText(this, "需要权限来显示画中画 ", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 引导用户到权限管理页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                Toast.makeText(this, "需要权限来显示画中画 ", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_CODE) {
                if (valueCallbackArray == null)
                    return;
                valueCallbackArray.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                valueCallbackArray = null;
            }
        } else if (requestCode == RESULT_CODE) {
            if (null == valueCallback)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            valueCallback.onReceiveValue(result);
            valueCallback = null;
        }
    }

    private String readFile() {
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("serveripcloudreve.prop");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            content.append(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "读取文件失败", Toast.LENGTH_SHORT).show();
            return "null";

        }
        return content.toString();

    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fos = openFileOutput("serveripcloudreve.prop", MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(this, "数据已写入文件" , Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "写入文件失败", Toast.LENGTH_SHORT).show();
        }
    }


    private void hideButtonDelayed(long delayMillis) {
        hideButtonRunnable = new Runnable() {
            @Override
            public void run() {
                button.animate().alpha(0).setDuration(2000).start();
                setbutton.animate().alpha(0).setDuration(2000).start();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                button.setEnabled(false);
                setbutton.setEnabled(false);
            }
        };
        handler.postDelayed(hideButtonRunnable, delayMillis);
    }
    private void cancelHideButtonTask() {
        if (hideButtonRunnable != null) {
            handler.removeCallbacks(hideButtonRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelHideButtonTask();  // 在Activity销毁时取消定时任务
    }

    private String get(){
        String content = "Privacy policy\n" +
                "Last modified on Jun 4, 2022\n" +
                "\n" +
                "Privacy Policy\n" +
                "By using our website or apps, you consent to our privacy policy by default.\n" +
                "\n" +
                "We do not collect any personal information, especially private information, from any files/binary released from official sources, including but not limited to Cloudreve Community Edition, Cloudreve Pro Edition and Cloudreve iOS Client, all of them will not collect anything from user.\n" +
                "\n" +
                "We can guarantee that no personal information is collected on any files released from official sources, but websites built by Cloudreve Community/Pro Edition and any secondary development sites are at the discretion of the administrator and are not subject to this provision.\n" +
                "\n" +
                "Policy Change Timeline\n" +
                "The privacy policy will follow the Cloudreve version published, we reserve the right to modify the privacy policy document any time.";
        return content;
    }
    public String readFile2() {
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("beta.prop");
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            content.append(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "读取文件失败", Toast.LENGTH_SHORT).show();
            return "null";

        }
        return content.toString();

    }
}