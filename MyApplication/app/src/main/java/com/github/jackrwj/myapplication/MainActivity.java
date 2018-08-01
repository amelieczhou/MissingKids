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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends Activity implements Validator.ValidationListener,View.OnClickListener {
    @Email
    @Order(1)
    protected EditText et_email;
    @Password(min =6, scheme = Password.Scheme.ANY,messageResId =R.string.pwd_hint)
    @Order(2)

    protected EditText et_pwd;
    protected TextView responseText;
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
        responseText = findViewById(R.id.response);
        btn_login = findViewById(R.id.login);
        btn_register = findViewById(R.id.register);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login){
            validator.validate();
        }else{
            Intent i = new Intent(MainActivity.this , RegisterActivity.class);
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
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://10.0.2.2:8000/api/login?email=" + text_email + "&password=" + text_pwd);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    result = response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                    //发送完成后的操作
                    try {
                        //第一步，生成Json字符串格式的JSON对象
                        JSONObject jsonObject = new JSONObject(result);
                        Boolean status = jsonObject.getBoolean("status");
                        if(status){
                            System.out.println("true");
                            Intent i = new Intent(MainActivity.this , page1.class);
                            startActivity(i);
                        }else{
                            System.out.println("false");
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @Override
    public void onValidationSucceeded() {
        //TODO:验证成功之后的逻辑处理
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

//    //打印错误信息
//    private void showResponse(final String response){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //在这里进行UI操作，将结果显示到界面上
//                responseText.setTextColor(Color.RED);
//                responseText.setText(response);
//            }
//        });
//    }


}