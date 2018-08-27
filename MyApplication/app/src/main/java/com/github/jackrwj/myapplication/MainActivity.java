package com.github.jackrwj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static java.net.Proxy.Type.HTTP;


public class MainActivity extends Activity implements Validator.ValidationListener,View.OnClickListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_email = findViewById(R.id.email_edit);
        et_pwd = findViewById(R.id.password_edit);
//        responseText = findViewById(R.id.response);
        btn_login = findViewById(R.id.login);
        btn_register = findViewById(R.id.register);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        validator = new Validator(this);
        validator.setValidationListener(this);


    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login){
            validator.validate();
        }else{
            Intent i = new Intent(MainActivity.this ,page1.class);
            startActivity(i);
        }
    }


    public void  sendHttpRequest(){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {

                text_email = et_email.getText().toString();
                text_pwd = et_pwd.getText().toString();
                String path = "http://132.232.27.134/api/login";
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");

                    //数据准备
                    String data = "email="+text_email+"&password="+text_pwd;
                    //至少要设置的两个请求头
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", data.length()+"");


                    //post的方式提交实际上是留的方式提交给服务器
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());

                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    if(responseCode ==200){
                        //请求成功
//                        InputStream is = connection.getInputStream();
                        InputStream in = connection.getInputStream();
//                    //下面对获取到的输入流进行读取
                        BufferedReader reader = null;
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null){
                            response.append(line);
                        }
                        result = response.toString();
                        Log.i("abc",result);
                    }else {
                        //请求失败
                        Log.i("abc","no");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //发送完成后的操作
                    try {
                        //第一步，生成Json字符串格式的JSON对象
                        JSONObject jsonObject = new JSONObject(result);
                        Boolean status = jsonObject.getBoolean("success");
                        if(status){
                            Intent i = new Intent(MainActivity.this , page1.class);
                            startActivity(i);
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
            }
        }).start();

    }


    @Override
    public void onValidationSucceeded() {
        sendHttpRequest();
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