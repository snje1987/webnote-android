package org.snje.webnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_setting);
        ExitApplication.getInstance().addActivity(this);
        init();
    }

    private void init() {
        this.load_cfg();
        Button saveBtn = (Button) findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                EditText urlEdit = (EditText) findViewById(R.id.siteurlEdit);
                EditText pwdEdit = (EditText) findViewById(R.id.passwordEdit);

                String url = urlEdit.getText().toString();
                while(url.endsWith("/")){
                    url = url.substring(0,url.length() - 1);
                }
                Pattern pattern = Pattern.compile("https?://(\\w+\\.)+\\w+(:\\d+)?(/[/.\\w-]*)?");
                Matcher matcher = pattern.matcher(url);
                if (!matcher.matches()) {
                    Toast.makeText(SiteSettingActivity.this, "URL格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                DataService.saveUserInfo(SiteSettingActivity.this,
                        url,
                        pwdEdit.getText().toString());
                Intent intent = new Intent(SiteSettingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

        });
    }

    private void load_cfg() {
        try {
            String[] data = DataService.getSavedUserInfo(this);
            EditText urlEdit = (EditText) findViewById(R.id.siteurlEdit);
            EditText pwdEdit = (EditText) findViewById(R.id.passwordEdit);
            urlEdit.setText(data[0].toCharArray(), 0, data[0].length());
            pwdEdit.setText(data[1].toCharArray(), 0, data[1].length());
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        load_cfg();
    }

}
