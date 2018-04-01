package com.nameof.casandroidclient;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends ConfigurableActivity {

    private String username;

    private String password;

    private TextView showName;

    private ProgressDialog mProgressDialog;

    private static final int REQUEST_SCAN = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            HandleResult response = (HandleResult) msg.obj;
            Toast.makeText(MainActivity.this, response.getInfo(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showName = (TextView) findViewById(R.id.username);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");
        showName.setText("welcome " + username + " !");
        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), REQUEST_SCAN);
                } else {
                    Toast.makeText(MainActivity.this, "拒绝", Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理二维码
        if (requestCode == REQUEST_SCAN && resultCode == RESULT_OK) {
            sendQRCodeLoginRequest(data.getStringExtra("barCode"));
        }
    }

    private void sendQRCodeLoginRequest(String barCode) {
        if (mProgressDialog == null) {
            mProgressDialog  = CustomAlertDialogFactory.createProgressDialog(this,
                    "正在登录...", false);
        }
        mProgressDialog.show();
        final String result = barCode;
        new Thread(){
            @Override
            public void run() {
                HandleResult response = null;
                try {
                    JSONObject json = new JSONObject(result);
                    String token = json.getString("sessionid");
                    Map<String, String> cookies = new HashMap<>();
                    cookies.put("token", token);
                    response = HttpRequest.postHandleResult(HttpRequest.SERVER + "/app/processQRCodeLogin",
                            "username=" + username, cookies);
                } catch (JSONException e) {
                    response = HandleResult.error("不合法的二维码!");
                }catch (IOException e) {
                    response = HandleResult.error("请求失败!" + e);
                }
                Message message = handler.obtainMessage();
                message.obj = response;
                handler.sendMessageDelayed(message, 2000);
            }
        }.start();
    }
}
