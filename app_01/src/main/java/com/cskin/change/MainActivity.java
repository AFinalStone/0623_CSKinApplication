package com.cskin.change;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.resources.skinplugin.SkinManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        System.out.println("newBase==_attachBaseContext" + getBaseContext().getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("newBase==_onCreate" + getBaseContext().getClass());
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
        findViewById(R.id.btn_change_skin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File apkPluginFile = new File(Environment.getExternalStorageDirectory() + "/app_plugin.apk");
                if (apkPluginFile.exists()) {
                    String apkPluginResPath = apkPluginFile.getAbsolutePath();
                    SkinManager.getInstance().loadSkinResource(apkPluginResPath);
                    SkinManager.getInstance().changeActivitySkin(MainActivity.this);
                }
            }
        });
        findViewById(R.id.btn_back_defalut_skin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinManager.getInstance().loadSkinResource("");
                SkinManager.getInstance().changeActivitySkin(MainActivity.this);
            }
        });
        findViewById(R.id.btn_show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .create();
                View view = getLayoutInflater().inflate(R.layout.alert_dialog_main, null, false);
                alertDialog.setView(view);
                alertDialog.show();
            }
        });
        TextView textView = new TextView(this);
        textView.setText(R.string.text_hello);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}