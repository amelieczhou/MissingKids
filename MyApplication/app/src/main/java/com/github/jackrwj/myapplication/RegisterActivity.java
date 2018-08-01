package com.github.jackrwj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RegisterActivity extends Activity implements Validator.ValidationListener,View.OnClickListener {

    @NotEmpty(messageResId=R.string.name_hint)
    @Length(max=5, messageResId=R.string.name_length_hint)
    @Order(1)
    public EditText et_name;

    @Password(min =6, scheme = Password.Scheme.ANY,messageResId =R.string.pwd_hint)
    @Order(2)
    protected EditText et_pwd;

    @ConfirmPassword(messageResId =R.string.confirm_pwd_hint)
    @Order(3)
    public EditText et_confirm_pwd;

    @Pattern(regex = "^\\d{11}$",messageResId=R.string.tel_hint)
    @Order(4)
    public EditText et_tel;

    @Email
    @Order(5)

    protected EditText et_email;
    public Button btn_register;
    private Validator validator;
    private String text_name;
    private String text_pwd;
    private String text_tel;
    private String text_email;
    private String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        validator = new Validator(this);
        validator.setValidationListener(this);
        initView();

    }


    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_email = (EditText) findViewById(R.id.et_email);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
    }


    @Override
    public void onValidationSucceeded() {
        //TODO:验证成功之后的逻辑处理

        new Thread(new Runnable() {
            @Override
            public void run() {
                text_name = et_name.getText().toString();
                text_pwd = et_pwd.getText().toString();
                text_tel = et_tel.getText().toString();
                text_email = et_email.getText().toString();

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://10.0.2.2:8000/api/register?name=" + text_name + "&pwd=" + text_pwd + "&tel=" + text_tel + "&email=" + text_email + "&password=" + text_pwd);
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
                            Intent i = new Intent(RegisterActivity.this , MainActivity.class);
                            startActivity(i);
                        }else{
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
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
        Intent i = new Intent(RegisterActivity.this , MainActivity.class);
        startActivity(i);
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

    @Override
    public void onClick(View v) {
        //点击的时候验证：
        validator.validate();
    }
}
