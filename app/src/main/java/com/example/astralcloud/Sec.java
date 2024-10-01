package com.example.astralcloud;

import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_set);
        Button Back2button = findViewById(R.id.button_backing);
        Back2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(Sec.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button xiazai = findViewById(R.id.xiazai);
        xiazai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(Sec.this, DownActi.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
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
