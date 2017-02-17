package com.example.android.newdemowithnavgation.addtags.ui;

import android.content.Context;


import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.android.newdemowithnavgation.addtags.easytagdragview.EasyTipDragView;
import com.example.android.newdemowithnavgation.addtags.easytagdragview.bean.SimpleTitleTip;
import com.example.android.newdemowithnavgation.addtags.easytagdragview.bean.Tip;
import com.example.android.newdemowithnavgation.addtags.easytagdragview.widget.TipItemView;
import com.example.android.newdemowithnavgation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TagActivity extends AppCompatActivity {
    private EasyTipDragView easyTipDragView;
    private static String[] DefaultAddTips = {"Opinion", "Sports", "Soccer", "Tech", "Arts", "LifeStyle", "Fashion", "Business",
            "Travel", "Environment", "Science"};

    private static String[] dragTips = {"Home", "US", "Politics", "World"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        /* check savedDragTps have been changed or not */
        ArrayList<String> savedDragTags = checkedDragTags();
        if (savedDragTags.size() <= 0) {
            setDefaultTips();
        }

        easyTipDragView = (EasyTipDragView) findViewById(R.id.easy_tip_drag_view);
        //设置已包含的标签数据
        easyTipDragView.setAddData(getAddTips());
        //设置可以添加的标签数据
        easyTipDragView.setDragData(getDragTips());
        //在easyTipDragView处于非编辑模式下点击item的回调（编辑模式下点击item作用为删除item）
        easyTipDragView.setSelectedListener(new TipItemView.OnSelectedListener() {
            @Override
            public void onTileSelected(Tip entity, int position, View view) {

                //finish();
            }
        });
        //设置每次数据改变后的回调（例如每次拖拽排序了标签或者增删了标签都会回调
        easyTipDragView.setDataResultCallback(new EasyTipDragView.OnDataChangeResultCallback() {
            @Override
            public void onDataChangeResult(ArrayList<Tip> tips) {
                //Log.i("demo", tips.toString());

                ArrayList<String> defaultAddTags = new ArrayList<>();
                for (int i = 0; i < DefaultAddTips.length; i++) {
                    defaultAddTags.add(DefaultAddTips[i]);
                }

                SharedPreferences sharedPreferences = getSharedPreferences("dragTips", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.putInt("status_size", tips.size());

                for (int i = 0; i < tips.size(); i++) {
                    editor.putString("status" + i, tips.get(i).toString());
                    defaultAddTags.remove(tips.get(i).toString());
                }
                editor.commit();

                SharedPreferences sharedPreferences1 = getSharedPreferences("addTips", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.clear();
                for (String i : defaultAddTags) {
                    editor1.putString(i, i);
                }
                editor1.commit();

            }
        });

        //设置点击“确定”按钮后最终数据的回调
        easyTipDragView.setOnCompleteCallback(new EasyTipDragView.OnCompleteCallback() {
            @Override
            public void onComplete(ArrayList<Tip> tips) {
                // toast("最终数据：" + tips.toString());
                //   btn.setVisibility(View.VISIBLE);
//                ArrayList<String> defaultAddTags = new ArrayList<>();
//                for (int i = 0; i < DefaultAddTips.length; i++) {
//                    defaultAddTags.add(DefaultAddTips[i]);
//                }
//
//                SharedPreferences sharedPreferences = getSharedPreferences("dragTips", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.clear();
//                editor.putInt("status_size", tips.size());
//
//                for (int i = 0; i < tips.size(); i++) {
//                    editor.putString("status" + i, tips.get(i).toString());
//                    defaultAddTags.remove(tips.get(i).toString());
//                }
//                editor.commit();
//
//                SharedPreferences sharedPreferences1 = getSharedPreferences("addTips", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
//                editor1.clear();
//                for (String i : defaultAddTags) {
//                    editor1.putString(i, i);
//                }
//                editor1.commit();

                finish();
            }
        });
        easyTipDragView.open();
    }



    public void toast(String str) {
        Toast.makeText(TagActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void setDefaultTips() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("addTips", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putString("Opinion", "Opinion");
        editor1.putString("Sports", "Sports");
        editor1.putString("Soccer", "Soccer");
        editor1.putString("Tech", "Tech");
        editor1.putString("Arts", "Arts");
        editor1.putString("LifeStyle", "LifeStyle");
        editor1.putString("Fashion", "Fashion");
        editor1.putString("Business", "Business");
        editor1.putString("Travel", "Travel");
        editor1.putString("Environment", "Environment");
        editor1.putString("Science", "Science");
        editor1.commit();
    }

    public ArrayList<String> checkedDragTags() {
        SharedPreferences sharedPreferences = getSharedPreferences("dragTips", Context.MODE_PRIVATE);
        ArrayList<String> defaultDragTips = new ArrayList<>();
        int size = sharedPreferences.getInt("status_size", 0);
        for (int i = 0; i < size; i++) {
            defaultDragTips.add(sharedPreferences.getString("status" + i, null));
        }

        return defaultDragTips;

    }

    public List<Tip> getDragTips() {
        ArrayList<String> tempResult = checkedDragTags();
        List<Tip> result = new ArrayList<>();
        for (int i = 0; i < tempResult.size(); i++) {
            String temp = tempResult.get(i);
            SimpleTitleTip tip = new SimpleTitleTip();
            tip.setTip(temp);
            tip.setId(i);
            result.add(tip);
        }
        return result;
    }

    public ArrayList<String> checkedAddTags() {
        SharedPreferences sharedPreferences = getSharedPreferences("addTips", Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();
        ArrayList<String> defaultAddTips = new ArrayList<>();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String result = entry.getValue().toString();
            defaultAddTips.add(result);
        }
        return defaultAddTips;
    }


    public List<Tip> getAddTips() {
        ArrayList<String> tempResultDragTags = checkedDragTags();
        ArrayList<String> tempResult = checkedAddTags();
        List<Tip> result = new ArrayList<>();
        for (int i = 0; i < tempResult.size(); i++) {
            String temp = tempResult.get(i);
            SimpleTitleTip tip = new SimpleTitleTip();
            tip.setTip(temp);
            tip.setId(i + tempResultDragTags.size());
            result.add(tip);
        }
        return result;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//
//            if(easyTipDragView.isOpen()){
//                if(!easyTipDragView.onKeyBackDown()){
//
//                    //onBackPressed();
//                   return super.onOptionsItemSelected(item);
//                }
//                return true;
//            }
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //点击返回键
            case KeyEvent.KEYCODE_BACK:

                //判断easyTipDragView是否已经显示出来
                if (easyTipDragView.isOpen()) {
                    if (!easyTipDragView.onKeyBackDown()) {
                        //btn.setVisibility(View.VISIBLE);
                        //go back to previous version
                        return super.onKeyDown(keyCode, event);
                    }
                    return true;
                }
                //....自己的业务逻辑

                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}
