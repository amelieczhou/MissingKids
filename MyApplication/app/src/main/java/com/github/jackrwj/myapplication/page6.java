package com.github.jackrwj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class page6 extends AppCompatActivity {

    public String result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page6);
        news("http://10.0.2.2:8000/api/news");
    }

    OkHttpClient client = new OkHttpClient();
    private void news(String url) {
        String cookie = page1.cookieString;
        // 创建一个RequestBody，文件的类型是image/png
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
//        String text = et_input.getText().toString();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("source", file.getName(),requestBody)
//                .addFormDataPart("description",text)
                .build();
        Request request = new Request.Builder()
                .addHeader("Cookie",cookie)
                .url(url)
                .post(multipartBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                result = response.body().string();
                System.out.println("上传返回：\n" + result);
                if(response.code() == 200){
                    try {
                        //第一步，生成Json字符串格式的JSON对象
                        JSONObject jsonObject = new JSONObject(result);
                        Boolean status = jsonObject.getBoolean("success");
                        if(status){
                            Intent i = new Intent(page2.this ,page3.class);
                            startActivity(i);
                            Looper.prepare();
                            Toast.makeText(page2.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else{
                            Looper.prepare();
                            Toast.makeText(page2.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Looper.prepare();
                    Toast.makeText(page2.this,"上传失败，请检查网络",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

}
