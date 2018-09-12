package com.github.jackrwj.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity implements Validator.ValidationListener,View.OnClickListener {
    @Email(messageResId =R.string.email_hint)
    @Order(1)
    protected EditText et_email;
    @Password(min =6, scheme = Password.Scheme.ANY,messageResId =R.string.pwd_hint)
    @Order(2)

    protected EditText et_pwd;
    private Validator validator;
    protected Button btn_login;
    protected Button btn_register;
    protected String text_email;
    protected String text_pwd;
    public String result;
    public static String cookieString;
    private AMapLocationClient locationClientContinue = null;
    public AMapLocation pub_location;
    private int continueCount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,"rndcE1GaQtHwdCyVgadFlOKU");
        continueCount = 0;
        et_email = findViewById(R.id.email_edit);
        et_pwd = findViewById(R.id.password_edit);
        btn_login = findViewById(R.id.login);
        btn_register = findViewById(R.id.register);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
        requestPower();
    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提示")//设置对话框的标题
                        .setMessage("无读写权限将程序将无法正常运行，请点击确定并授权")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestWrite();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestWrite();
                            }
                        }).create();
                dialog.show();
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提示")//设置对话框的标题
                        .setMessage("无位置权限将程序将无法正常运行，请点击确定并授权")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocation();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocation();
                            }
                        }).create();
                dialog.show();
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
            }
        }
    }

    public void requestLocation(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
    }
    public void requestWrite(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login){
            validator.validate();
        }else{
            Intent i = new Intent(MainActivity.this ,RegisterActivity.class);
            startActivity(i);
        }
    }

    OkHttpClient client = new OkHttpClient();
    private void upload() {
        String path = "http://132.232.27.134/api/login";
//        String path = "http://localhost:8000/api/login";
        text_email = et_email.getText().toString();
        text_pwd = et_pwd.getText().toString();
        RequestBody muiltipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", text_email)
                .addFormDataPart("password", text_pwd)
                .build();
        final Request request = new Request.Builder()
                .url(path)
                .post(muiltipartBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) { System.out.println("上传失败"); }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                result = response.body().string();
                System.out.println("上传返回：\n" + result);

                if(response.code() == 200){
                    cookieString=response.header("Set-Cookie");
                    cookieString = cookieString.substring(0, cookieString.indexOf(";"));
                    try {
                        //第一步，生成Json字符串格式的JSON对象
                        JSONObject jsonObject = new JSONObject(result);
                        Boolean status = jsonObject.getBoolean("success");
                        if(status){
                            System.out.print(result);
                            startContinueLocation();
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("fail");
                }
            }
        });
    }

    public void sendHttpRequest2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cookie = MainActivity.cookieString;
//                String path = "http://10.0.2.2:8000/api/position";
                String path = "http://132.232.27.134/api/position";
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");

                    String data = "time=" + continueCount + "&latitude=" + pub_location.getLatitude() + "&longitude=" + pub_location.getLongitude();

                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", data.length() + "");
                    connection.setRequestProperty("Cookie", cookie);

                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());

                    //获得结果码
                    int responseCode = connection.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 启动连续客户端定位
     */
    void startContinueLocation() {
        if (null == locationClientContinue) {
            locationClientContinue = new AMapLocationClient(this.getApplicationContext());
        }
        try {
            //使用连续的定位方式  默认连续
            AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
            // 地址信息
            locationClientOption.setNeedAddress(true);
            locationClientContinue.setLocationOption(locationClientOption);
            locationClientContinue.setLocationListener(locationContinueListener);
            locationClientContinue.startLocation();
        } finally {
            Intent i = new Intent(MainActivity.this,page1.class);
            startActivity(i);
        }
    }

    /**
     * 连续客户端的定位监听
     */
    AMapLocationListener locationContinueListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            continueCount++;
//            long callBackTime = System.currentTimeMillis();
//            StringBuffer sb = new StringBuffer();
//            sb.append("持续定位完成 " + continueCount +  "\n");
//
//            sb.append("回调时间: " + Utils.formatUTC(callBackTime, null) + "\n");
//            if(null == location){
//                sb.append("定位失败：location is null!!!!!!!");
//            } else {
//                sb.append(Utils.getLocationStr(location));
//            }
//            tvResultContinue.setText(sb.toString());
            pub_location = location;
            sendHttpRequest2();
        }
    };

    @Override
    public void onValidationSucceeded() {
        upload();
    }
    /***
     * 验证失败的处理
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}

//TODO: 内存读取权限，图片上传，切后台不定位