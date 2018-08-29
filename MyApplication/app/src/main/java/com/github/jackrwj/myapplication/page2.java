package com.github.jackrwj.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.mobsandgeeks.saripaar.Validator;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class page2 extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Context mContext;
    private GridView gridView;
    private ArrayList<String> mPicList = new ArrayList<>(); //上传的图片凭证的数据源
    private GridViewAdapter mGridViewAddImgAdapter; //展示上传的图片的适配器
    protected Button btn_submit;
    protected EditText et_input;
    public String result;
    public String sessionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2);

        mContext = this;
        gridView = (GridView) findViewById(R.id.gridView);
        btn_submit = findViewById(R.id.button4);
        et_input = findViewById(R.id.et_input);
        btn_submit.setOnClickListener(this);
        initGridView();
    }

    //初始化展示上传图片的GridView
    private void initGridView() {
        mGridViewAddImgAdapter = new GridViewAdapter(mContext, mPicList);
        gridView.setAdapter(mGridViewAddImgAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {
                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过5张，才能点击
                    if (mPicList.size() == MainConstant.MAX_SELECT_PIC_NUM) {
                        //最多添加5张图片
                        viewPluImg(position);
                    } else {
                        //添加凭证图片
                        selectPic(MainConstant.MAX_SELECT_PIC_NUM - mPicList.size());
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        sendHttpRequest();
    }

    public void  sendHttpRequest(){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Intent intent = getIntent();
//                String cookie = intent.getStringExtra("cookie");
                String cookie = page1.cookieString;
//                Log.i("abc",cookie);
                String text = et_input.getText().toString();
                String path = "http://132.232.27.134/api/addDescAndPic";
                try {

                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");
                    //数据准备
                    String data = "description=" + text + "&picture=" + "44444";
                    //至少要设置的两个请求头
                    connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", data.length()+"");
                    connection.setRequestProperty("Cookie", cookie);


//                    Log.i("abc",data);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.getBytes());

                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    if(responseCode ==200){
                        //请求成功
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
                        Toast.makeText(page2.this,result,Toast.LENGTH_SHORT).show();
                    }else {
                        //请求失败
                        Toast.makeText(page2.this,"no",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //发送完成后的操作
                    try {
                        //第一步，生成Json字符串格式的JSON对象
//                        JSONObject jsonObject = new JSONObject(result);
//                        Boolean status = jsonObject.getBoolean("success");
//                        if(status){
                            Intent i = new Intent(page2.this , page3.class);
                            startActivity(i);
//                            Looper.prepare();
//                            Toast.makeText(MainActivity.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
//                            Looper.loop();
//                        }else{
//                            Looper.prepare();
//                            Toast.makeText(MainActivity.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
//                            Looper.loop();
//                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    //查看大图
    private void viewPluImg(int position) {
        Intent intent = new Intent(mContext, PlusImageActivity.class);
        intent.putStringArrayListExtra(MainConstant.IMG_LIST, mPicList);
        intent.putExtra(MainConstant.POSITION, position);
        startActivityForResult(intent, MainConstant.REQUEST_CODE_MAIN);
    }

    /**
     * 打开相册或者照相机选择凭证图片，最多5张
     *
     * @param maxTotal 最多选择的图片的数量
     */
    private void selectPic(int maxTotal) {
        PictureSelectorConfig.initMultiConfig(this, maxTotal);
    }

    // 处理选择的照片的地址
    private void refreshAdapter(List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                Log.i("abc", "path:---->" + compressPath);
                mPicList.add(compressPath); //把图片添加到将要上传的图片数组中
                mGridViewAddImgAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    refreshAdapter(PictureSelector.obtainMultipleResult(data));
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;
            }
        }
        if (requestCode == MainConstant.REQUEST_CODE_MAIN && resultCode == MainConstant.RESULT_CODE_VIEW_IMG) {
            //查看大图页面删除了图片
            ArrayList<String> toDeletePicList = data.getStringArrayListExtra(MainConstant.IMG_LIST); //要删除的图片的集合
            mPicList.clear();
            mPicList.addAll(toDeletePicList);
            mGridViewAddImgAdapter.notifyDataSetChanged();
        }
    }
}
