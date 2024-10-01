package com.example.astralcloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Sec extends AppCompatActivity {
    public static final String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        setContentView(R.layout.setting_set);
        Button Back2button = findViewById(R.id.button_backing);
        Back2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(Sec.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String cloud = sharedPreferences.getString("cloudreveip", "no");
                intent.putExtra("cloudreveip", cloud);
                editor.clear();
                editor.commit();
                startActivity(intent);
            }
        });
        Button xiazai = findViewById(R.id.xiazai);
        xiazai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String path = "%2fDownload%2f";
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:" + path);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");//想要展示的文件类型
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                startActivityForResult(intent, 0);


            }
        });
        secCreate();
    }
    public void secCreate() {
        Switch switch1 = findViewById(R.id.switch1);
        String beta = readFile2();
        if (beta.contains("true")) {
            switch1.setChecked(true);
        }else {
            switch1.setChecked(false);
        }
        switch1.setOnClickListener(v -> {
            if (switch1.isChecked()) {
                writeToFile2("beta=true");
                Toast.makeText(getApplicationContext(),"已开启Beta测试",Toast.LENGTH_SHORT).show();
            }else {
                writeToFile2("beta=false");
            }
        });
        Switch switch2 = findViewById(R.id.switch2);
        switch2.setOnClickListener(v -> {
            MainActivity.webView.clearCache(true);
            MainActivity.webView.clearHistory();
            Toast.makeText(getApplicationContext(),"已清除缓存",Toast.LENGTH_SHORT).show();
        });
    }
    public void writeToFile2(String data) {
        try {
            FileOutputStream fos = openFileOutput("beta.prop", MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Toast.makeText(this, "数据已写入文件" , Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "写入文件失败", Toast.LENGTH_SHORT).show();
        }
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
