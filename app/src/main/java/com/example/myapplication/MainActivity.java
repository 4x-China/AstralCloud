package com.example.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.*;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private Runnable hideButtonRunnable;
    private ValueCallback<Uri> valueCallback;
    private ValueCallback<Uri[]> valueCallbackArray;
    private Button button;
    private final int REQUEST_CODE = 0x1010;
    private Handler handler = new Handler();
    private final static int RESULT_CODE = 0x1011;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button2);
        hideButtonDelayed(5000);  // 3秒后隐藏按钮
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // 加载新的布局文件
                LayoutInflater inflater = getLayoutInflater();
                View newView = inflater.inflate(R.layout.content_main, null);
                Intent intent = new Intent(MainActivity.this, FirstFragment.class);
                startActivity(intent);
                // 替换当前的布局
                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                rootView.removeAllViews();
                rootView.addView(newView);
            }
        });

        webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);//设置可以使用localStorage
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);//设置webView自适应屏幕大小
        settings.setBuiltInZoomControls(true);//关闭zoom
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);//关闭zoom按钮
        settings.setRenderPriority(WebSettings.RenderPriority.LOW);
        settings.setBlockNetworkImage(true);
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
        });
        webView.setWebChromeClient(new WebChromeClient(){

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
                startActivity(i);
            }
        });


        // 开启调试模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        String ip = readFile();
        Log.d("地址",ip);
        if (ip.isEmpty()) {
            webView.loadUrl("http://4x.ink:5212/"); // 替换为你的URL
        }else{
            webView.loadUrl(ip);
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
            writeToFile("http://4x.ink:5212/");
            return "null";

        }
        return content.toString();
    }
    private void writeToFile(String data) {
        try {
            FileOutputStream fos = openFileOutput("serveripcloudreve.prop", MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(this, "数据已写入文件", Toast.LENGTH_SHORT).show();
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
}