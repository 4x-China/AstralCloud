package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.myapplication.databinding.FragmentFirstBinding;

import java.io.FileOutputStream;
import java.io.IOException;
@SuppressLint({"MissingInflatedId", "LocalSuppress"})
public class FirstFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button button = findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
// 加载新的布局文件
                LayoutInflater inflater = getLayoutInflater();
                View newView = inflater.inflate(R.layout.content_main, null);
                Intent intent = new Intent(FirstFragment.this, MainActivity.class);
                startActivity(intent);
                // 替换当前的布局
                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                rootView.removeAllViews();
                rootView.addView(newView);
            }
        });
        TextView textView = findViewById(R.id.textView2);
        textView.setText(get());
        Toast.makeText(this,"change CloudReveIP",Toast.LENGTH_SHORT).show();
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
}