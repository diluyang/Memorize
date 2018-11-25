package com.test2.memorize;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.test2.memorize.Database.MyDB;


public class EditActivity extends BaseActivity implements View.OnClickListener{

    MyDB myDB;
    private Button btnSave;
    private Button btnBack;
    private TextView editTime;
    private EditText editTitle;
    private EditText editBody;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_linear_layout);
        init();
        if (editTime.getText().length()==0)
            editTime.setText(getNowTime());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title;
            String body;
            String createDate;
            title = editTitle.getText().toString();
            body = editBody.getText().toString();
            createDate = editTime.getText().toString();
            //当返回按键被按下
            if (!isShowIng()){
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate);
                    clearDialog();
                } else {
                    intentStart();
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        String title;
        String body;
        String createDate;
        title = editTitle.getText().toString();
        body = editBody.getText().toString();
        createDate = editTime.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
                if (saveFunction(title,body,createDate)){
                    intentStart();
                }
                break;
            case R.id.button_back:
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate);
                    clearDialog();
                } else {
                    intentStart();
                }
                break;
            default:
                break;
        }
    }

    /*
     * 初始化函数
     */
    void init(){
        myDB = new MyDB(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_body);
        editTime = findViewById(R.id.edit_title_time);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    /*
     * 返回主界面
     */
    void intentStart(){
        Intent intent = new Intent(EditActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /*
     * 保存函数
     */
    boolean saveFunction(String title,String body,String createDate){

        SQLiteDatabase db;
        ContentValues values;

        boolean flag = true;
        if ("".equals(title)){
            Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length()>10){
            Toast.makeText(this,"标题过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if ("".equals(createDate)){
            Toast.makeText(this,"时间格式错误",Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if(flag){
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(MyDB.TITLE,title);
            values.put(MyDB.BODY,body);
            values.put(MyDB.TIME,createDate);
            db.insert(MyDB.TABLE_NAME,null,values);
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }

    /*
     * 弹窗函数
     * @param title
     * @param body
     * @param createDate
     */
    void showDialog(final String title, final String body, final String createDate){
        dialog = new AlertDialog.Builder(EditActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveFunction(title, body, createDate);
                        intentStart();
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intentStart();
                    }
                });
        dialog.show();
    }

    void clearDialog(){
        dialog = null;
    }

    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }

    /*
     * 得到当前时间
     * @return
     */
    String getNowTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
