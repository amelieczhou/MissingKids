package com.github.jackrwj.myapplication;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class page5 extends AppCompatActivity implements SwipeFlingAdapterView.onFlingListener,
        SwipeFlingAdapterView.OnItemClickListener, View.OnClickListener {

    int [] headerIcons = {
            R.drawable.i1,
            R.drawable.i2,
            R.drawable.i3,
            R.drawable.i4,
            R.drawable.i5,
            R.drawable.i6
    };

    //    String [] names = {"张三","李四","王五","小明","小红","小花"};
//
//    String [] citys = {"北京", "上海", "广州", "深圳"};
//
//    String [] edus = {"大专", "本科", "硕士", "博士"};
//
//    String [] years = {"1年", "2年", "3年", "4年", "5年"};
    String[] names;
    String[] citys;
    String[] edus;
    String[] years;

    Random ran = new Random();

    private int cardWidth;
    private int cardHeight;

    private SwipeFlingAdapterView swipeView;
    private InnerAdapter adapter;
    public String result;
    public int len;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page5);

        sendHttpRequest();


    }

    private void initView() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        cardWidth = (int) (dm.widthPixels - (2 * 18 * density));
        cardHeight = (int) (dm.heightPixels - (338 * density));


        swipeView = (SwipeFlingAdapterView) findViewById(R.id.swipe_view);
        if (swipeView != null) {
            swipeView.setIsNeedSwipe(true);
            swipeView.setFlingListener(this);
            swipeView.setOnItemClickListener(this);

            adapter = new InnerAdapter();
            swipeView.setAdapter(adapter);
        }

        View v = findViewById(R.id.swipeLeft);
        if (v != null) {
            v.setOnClickListener(this);
        }
        v = findViewById(R.id.swipeRight);
        if (v != null) {
            v.setOnClickListener(this);
        }

    }


    @Override
    public void onItemClicked(MotionEvent event, View v, Object dataObject) {
        sendHttpRequest();
    }

    @Override
    public void removeFirstObjectInAdapter() {
        adapter.remove(0);
    }

    @Override
    public void onLeftCardExit(Object dataObject) {
    }

    @Override
    public void onRightCardExit(Object dataObject) {
    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {
        if (itemsInAdapter == 3) {
            loadData();
        }
    }

    @Override
    public void onScroll(float progress, float scrollXProgress) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swipeLeft:
                swipeView.swipeLeft();
                //swipeView.swipeLeft(250);
                break;
            case R.id.swipeRight:
                swipeView.swipeRight();
                //swipeView.swipeRight(250);
        }
    }

    public void sendHttpRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String path = "http://10.0.2.2:8000/api/getAllInfo";
                String path = "http://132.232.27.134/api/getAllInfo";
//
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");

                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    if(responseCode ==200){

                        InputStream in = connection.getInputStream();
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
                        Log.i("abc","no");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        //第一步，生成Json字符串格式的JSON对象
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray array = jsonObject.getJSONArray("data");
                        Boolean status = jsonObject.getBoolean("success");
                        len = array.length();
                        Log.i("abc",String.valueOf(len));
                        names = new String[len];
                        citys = new String[len];
                        edus = new String[len];
                        years = new String[len];

                        if(status){
                            for(int i=0;i<len;i++){
                                JSONObject o = array.getJSONObject(i);
                                int id = o.getInt("id");
                                String name = o.getString("name");
                                int age = o.getInt("age");
                                int sex = o.getInt("sex");
                                String parent_tel = o.getString("parent_tel");
                                names[i] = name;
                                citys[i] = String.valueOf(age) + "岁";
                                if(sex == 0){
                                    edus[i] = "女孩";
                                }else{
                                    edus[i] = "男孩";
                                }
                                years[i] = String.valueOf(parent_tel);
//                                pointList.add(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                            }
                        }else{
                            Looper.prepare();
                            Toast.makeText(page5.this,"未获取到数据",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }finally {

                        initView();
                        loadData();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData() {
        new AsyncTask<Void, Void, List<Talent>>() {
            @Override
            protected List<Talent> doInBackground(Void... params) {
                ArrayList<Talent> list = new ArrayList<>(len);
                Talent talent;
                for (int i = 0; i < len; i++) {
                    talent = new Talent();
//                    talent.headerIcon = headerIcons[i % headerIcons.length];
//                    talent.nickname = names[ran.nextInt(names.length-1)];
//                    talent.cityName = citys[ran.nextInt(citys.length-1)];
//                    talent.educationName = edus[ran.nextInt(edus.length-1)];
//                    talent.workYearName = years[ran.nextInt(years.length-1)];

//                    talent.headerIcon = headerIcons[i];
                    talent.nickname = names[i];
                    talent.cityName = citys[i];
                    talent.educationName = edus[i];
                    talent.workYearName = years[i];
                    list.add(talent);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<Talent> list) {
                super.onPostExecute(list);
                adapter.addAll(list);
            }
        }.execute();
    }


    private class InnerAdapter extends BaseAdapter {

        ArrayList<Talent> objs;

        public InnerAdapter() {
            objs = new ArrayList<>();
        }

        public void addAll(Collection<Talent> collection) {
            if (isEmpty()) {
                objs.addAll(collection);
                notifyDataSetChanged();
            } else {
                objs.addAll(collection);
            }
        }

        public void clear() {
            objs.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return objs.isEmpty();
        }

        public void remove(int index) {
            if (index > -1 && index < objs.size()) {
                objs.remove(index);
                notifyDataSetChanged();
            }
        }


        @Override
        public int getCount() {
            return objs.size();
        }

        @Override
        public Talent getItem(int position) {
            if(objs==null ||objs.size()==0) return null;
            return objs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // TODO: getView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Talent talent = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_new_item, parent, false);
                holder  = new ViewHolder();
                convertView.setTag(holder);
                convertView.getLayoutParams().width = cardWidth;
                holder.portraitView = (ImageView) convertView.findViewById(R.id.portrait);
                //holder.portraitView.getLayoutParams().width = cardWidth;
                holder.portraitView.getLayoutParams().height = cardHeight;
                holder.nameView = (TextView) convertView.findViewById(R.id.name);
                //parentView.getLayoutParams().width = cardWidth;
                //holder.jobView = (TextView) convertView.findViewById(R.id.job);
                //holder.companyView = (TextView) convertView.findViewById(R.id.company);
                holder.cityView = (TextView) convertView.findViewById(R.id.city);
                holder.eduView = (TextView) convertView.findViewById(R.id.education);
                holder.workView = (TextView) convertView.findViewById(R.id.work_year);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.portraitView.setImageResource(talent.headerIcon);

            holder.nameView.setText(String.format("%s", talent.nickname));
            //holder.jobView.setText(talent.jobName);

            final CharSequence no = "暂无";

            holder.cityView.setHint(no);
            holder.cityView.setText(talent.cityName);
            holder.cityView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.home01_icon_location,0,0);

            holder.eduView.setHint(no);
            holder.eduView.setText(talent.educationName);
            holder.eduView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.home01_icon_edu,0,0);

            holder.workView.setHint(no);
            holder.workView.setText(talent.workYearName);
            holder.workView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.home01_icon_work_year,0,0);


            return convertView;
        }

    }

    private static class ViewHolder {
        ImageView portraitView;
        TextView nameView;
        TextView cityView;
        TextView eduView;
        TextView workView;
        CheckedTextView collectView;

    }

    public static class Talent {
        public int headerIcon;
        public String nickname;
        public String cityName;
        public String educationName;
        public String workYearName;
    }

}
