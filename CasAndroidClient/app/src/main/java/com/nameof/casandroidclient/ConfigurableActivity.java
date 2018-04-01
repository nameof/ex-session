package com.nameof.casandroidclient;/**
 * Created by Koma on 2018/4/1.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import com.nameof.casandroidclient.utils.HttpRequest;

import java.lang.reflect.Method;

/**
 * Create by ChengPan
 * 2018-04-01 17:26
 **/
public class ConfigurableActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        setIconEnable(menu,true);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 显示自定义菜单图标
     * @param menu
     * @param enable
     */
    private void setIconEnable(Menu menu, boolean enable)
    {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_serverUrl:
                final EditText et = new EditText(this);
                et.setText(HttpRequest.SERVER);
                new AlertDialog.Builder(this).setTitle("设置服务器地址")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HttpRequest.SERVER = et.getText().toString();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
