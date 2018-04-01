package com.nameof.casandroidclient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.nameof.casandroidclient.component.CustomAlertDialogFactory;
import com.nameof.casandroidclient.response.HandleResult;
import com.nameof.casandroidclient.utils.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;
import static com.nameof.casandroidclient.R.id.username;

public class LoginActivity extends ConfigurableActivity {

    private EditText userName;

    private String name;

    private EditText password;

    private String passwd;

    private Button login;

    private ProgressDialog mProgressDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            HandleResult response = (HandleResult) msg.obj;
            Toast.makeText(LoginActivity.this, response.getInfo(), Toast.LENGTH_LONG).show();
            if (response.isState()) {
                loginSuccess(response);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = userName.getText().toString().trim();
                passwd = password.getText().toString().trim();
                if (!"".equals(name) && !"".equals(passwd)) {
                    sendLoginRequest();
                } else {
                    Toast.makeText(LoginActivity.this, "用户名和密码不能为空!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginSuccess(HandleResult result) {
        HttpRequest.JWT = result.getString("accessToken");//保存授权TOKEN

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", name);
        bundle.putString("password", name);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void sendLoginRequest() {
        if (mProgressDialog == null) {
            mProgressDialog  = CustomAlertDialogFactory.createProgressDialog(LoginActivity.this,
                    "正在登录...", false);
        }
        mProgressDialog.show();
        new Thread(){
            @Override
            public void run() {
                HandleResult response = null;
                try {
                    response = HttpRequest.postHandleResult(HttpRequest.SERVER + "/app/login",
                            "name=" + name + "&passwd=" + passwd, null);
                } catch (Exception e) {
                    response = HandleResult.error("请求失败!" + e);
                }
                Message message = handler.obtainMessage();
                message.obj = response;
                handler.sendMessageDelayed(message, 500);
            }
        }.start();
    }

}
