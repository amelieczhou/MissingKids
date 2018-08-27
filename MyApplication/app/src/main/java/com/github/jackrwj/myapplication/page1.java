package com.github.jackrwj.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class page1 extends Activity implements View.OnClickListener ,Validator.ValidationListener{

    @NotEmpty(messageResId=R.string.name_hint)
    @Length(min=2,max=20, messageResId=R.string.name_length_hint)
    @Order(1)
    public TextInputEditText kids_name;

    @Pattern(regex = "^\\d{2}$",messageResId=R.string.age_hint)
    @Order(2)
    public TextInputEditText kids_age;

    @NotEmpty(messageResId=R.string.name_hint)
    @Length(min=2,max=15, messageResId=R.string.name_length_hint)
    @Order(3)
    public TextInputEditText parent;

    @Pattern(regex = "^\\d{8,11}$",messageResId=R.string.tel_hint)
    @Order(4)
    public TextInputEditText parent_tel;



    private Button btnDate, btnTime,btnNext;
    private  RadioGroup  radioGroup=null;
    private  RadioButton  radioButton_boy,radioButton_girl;
    public int y=0,M=0,d=0,h=0,m=0;
    private Validator validator;
    private String text_name;
    private String text_age;
    public int sex;
    private String text_parent;
    private String text_parent_tel;
    private String result;
    public String res;
    public boolean _pressed1 = false;
    public boolean _pressed2 = false;
    public boolean _pressed3 = false;
    public static String cookieString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1);
        validator = new Validator(this);
        validator.setValidationListener(this);
        initView();
//        Log.i("abc","asdd");
    }

    private void initView() {
        kids_name = (TextInputEditText) findViewById(R.id.kids_name);
        kids_age = (TextInputEditText) findViewById(R.id.kids_age);
        parent = (TextInputEditText) findViewById(R.id.parent);
        parent_tel = (TextInputEditText) findViewById(R.id.parent_tel);
        btnDate = (Button) findViewById(R.id.button);
        btnTime = (Button) findViewById(R.id.button2);
        btnNext = (Button) findViewById(R.id.button3);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup5);
        radioButton_boy=(RadioButton)findViewById(R.id.radioButton6);
        radioButton_girl=(RadioButton)findViewById(R.id.radioButton8);


        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(listen);
    }

    private RadioGroup.OnCheckedChangeListener listen=new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
             switch (group.getCheckedRadioButtonId()) {
                 case R.id.radioButton6:
                     sex = 1;
                     break;
                 case R.id.radioButton8:
                     sex = 0;
                     break;
             }
             _pressed3 = true;
         }
     };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                DatePickerDialog datePicker=new DatePickerDialog(page1.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        // TODO Auto-generated method stub
                        Toast.makeText(page1.this, year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日", Toast.LENGTH_SHORT).show();
                        y = year;
                        M = monthOfYear + 1;
                        d = dayOfMonth;
                    }
                }, 2018, 0, 1);
                datePicker.show();
                _pressed1 = true;
                break;

            case R.id.button2:
                TimePickerDialog time=new TimePickerDialog(page1.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        Toast.makeText(page1.this, hourOfDay+"时"+minute+"分", Toast.LENGTH_SHORT).show();
                        h = hourOfDay;
                        m = minute;
                    }
                }, 18, 25, true);
                time.show();
                _pressed2 = true;
                break;

            case R.id.button3:
                validator.validate();
//                Log.i("abc","asd");
        }
    }



    @Override
    public void onValidationSucceeded() {
        if(_pressed3 == false){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")//设置对话框的标题
                    .setMessage("请选择性别")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }
       else  if ((_pressed1 == false) || (_pressed2 == false)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")//设置对话框的标题
                    .setMessage("请选择日期和时间")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }


        else{
            sendHttpRequest();
        }
    }




    public void sendHttpRequest(){
        String  s= String.format("%04d-%02d-%02d %02d:%02d:%02d",y,M,d,h,m,0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        ts = ts/1000;
        res = String.valueOf(ts);

        new Thread(new Runnable() {
            @Override
            public void run() {
                text_name = kids_name.getText().toString();
                text_age = kids_age.getText().toString();
                text_parent = parent.getText().toString();
                text_parent_tel = parent_tel.getText().toString();

                String path = "http://132.232.27.134/api/create";
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");

                    //数据准备
                    String data = "name="+text_name+"&age="+text_age+"&sex="+sex+"&parent="+text_parent+"&parent_tel=" + text_parent_tel + "&missing_time=" + res;
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
                        InputStream in = connection.getInputStream();

                        cookieString=connection.getHeaderField("Set-Cookie");
                        cookieString = cookieString.substring(0, cookieString.indexOf(";"));


                        //下面对获取到的输入流进行读取
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
//                        Log.i("abc","请求失败");
                        Toast ts = Toast.makeText(page1.this,"post请求，请重试!", Toast.LENGTH_LONG);
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
                            Intent i = new Intent(page1.this , page2.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("cookie",cookieString);
//                            i.putExtras(bundle);
                             startActivity(i);
                            Looper.prepare();
                            Toast.makeText(page1.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(page1.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
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
