package com.test2.memorize;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.app.AlertDialog;


import java.util.ArrayList;
import java.util.List;
import com.test2.memorize.Database.MyDB;
import com.test2.memorize.Entity.Record;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    MyDB myDB;
    private ListView myListView;
    private EditText editText;
    private Button createButton;
    private Button selectButton;
    private MyBaseAdapter myBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //初始化控件
    private void init(){
        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(this);
        selectButton = findViewById(R.id.selectButton);
        selectButton.setOnClickListener(this);
        myListView = findViewById(R.id.list_view);
        editText = findViewById(R.id.select_title);

        List<Record> recordList = new ArrayList<>();
        myDB = new MyDB(this);
        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = db.query(MyDB.TABLE_NAME,null,
                null,null,null,
                null,"create_time DESC");
        if(cursor.moveToFirst()){
            Record record;
            while (!cursor.isAfterLast()){
                record = new Record();
                record.setId(
                        Integer.valueOf(cursor.getString(cursor.getColumnIndex(MyDB.ID))));
                record.setTitleName(
                        cursor.getString(cursor.getColumnIndex(MyDB.TITLE))
                );
                record.setTextBody(
                        cursor.getString(cursor.getColumnIndex(MyDB.BODY))
                );
                record.setCreateTime(
                        cursor.getString(cursor.getColumnIndex(MyDB.TIME)));
                recordList.add(record);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // 创建一个Adapter的实例
        myBaseAdapter = new MyBaseAdapter(this,recordList,R.layout.list_item);
        myListView.setAdapter(myBaseAdapter);
        // 设置点击监听
        myListView.setOnItemClickListener(this);
        myListView.setOnItemLongClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createButton:
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                break;
            case R.id.selectButton:
                String title = editText.getText().toString();
                SQLiteDatabase db = myDB.getWritableDatabase();
                Cursor cursor = db.rawQuery("select * from record where title_name like ? or text_body like ?",new String[]{"%"+title+"%","%"+title+"%"});
                //Cursor cursor=db.query("T_Message",new String[]{"id","date","title","content"},"id=?",new String[]{id_query},null,null,null);
                List<Record> list =new ArrayList<Record>();
                Record record;
                while(cursor.moveToNext()){
                    record = new Record();
                    record.setId(
                            Integer.valueOf(cursor.getString(cursor.getColumnIndex(MyDB.ID))));
                    record.setTitleName(
                            cursor.getString(cursor.getColumnIndex(MyDB.TITLE))
                    );
                    record.setTextBody(
                            cursor.getString(cursor.getColumnIndex(MyDB.BODY))
                    );
                    record.setCreateTime(
                            cursor.getString(cursor.getColumnIndex(MyDB.TIME)));
                    list.add(record);
                }
                cursor.close();
                db.close();
                // 创建一个Adapter的实例
                myBaseAdapter = new MyBaseAdapter(this,list,R.layout.list_item);
                myListView.setAdapter(myBaseAdapter);
                // 设置点击监听
                myListView.setOnItemClickListener(this);
                myListView.setOnItemLongClickListener(this);
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this,AmendActivity.class);
        Record record = (Record) myListView.getItemAtPosition(position);
        intent.putExtra(MyDB.TITLE,record.getTitleName().trim());
        intent.putExtra(MyDB.BODY,record.getTextBody().trim());
        intent.putExtra(MyDB.TIME,record.getCreateTime().trim());
        intent.putExtra(MyDB.ID,record.getId().toString().trim());
        this.startActivity(intent);
        MainActivity.this.finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Record record = (Record) myListView.getItemAtPosition(position);
        showDialog(record,position);
        return true;
    }

    void showDialog(final Record record,final int position){

        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("是否删除？");
        String textBody = record.getTextBody();
        dialog.setMessage(
                textBody.length()>150?textBody.substring(0,150)+"...":textBody);
        dialog.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = myDB.getWritableDatabase();
                        db.delete(MyDB.TABLE_NAME,
                                MyDB.ID+"=?",
                                new String[]{String.valueOf(record.getId())});
                        db.close();
                        myBaseAdapter.removeItem(position);
                        myListView.post(new Runnable() {
                            @Override
                            public void run() {
                                myBaseAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }



    /**
     * ListView展示的适配器类
     */
    class MyBaseAdapter extends BaseAdapter{
        private List<Record> recordList;//数据集合
        private Context context;
        private int layoutId;

        public MyBaseAdapter(Context context,List<Record> recordList,int layoutId){
            this.context = context;
            this.recordList = recordList;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            if (recordList!=null&&recordList.size()>0)
                return recordList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if (recordList!=null&&recordList.size()>0)
                return recordList.get(position);
            else
                return null;
        }

        public void removeItem(int position){
            this.recordList.remove(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.list_item, parent,
                        false);
                viewHolder  = new ViewHolder();
                viewHolder.titleView = convertView.findViewById(R.id.list_item_title);
                viewHolder.bodyView = convertView.findViewById(R.id.list_item_body);
                viewHolder.timeView = convertView.findViewById(R.id.list_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String tile = recordList.get(position).getTitleName();
            viewHolder.titleView.setText((position+1)+"."+(tile.length()>7?tile.substring(0,7)+"...":tile));
//            viewHolder.titleView.setText(tile);
            String body = recordList.get(position).getTextBody();
            viewHolder.bodyView.setText(body.length()>13?body.substring(0,12)+"...":body);
//            viewHolder.bodyView.setText(body);
            viewHolder.timeView.setText(recordList.get(position).getCreateTime());
            return convertView;
        }
    }

    /**
     * ListView里的组件包装类
     */
    class ViewHolder{
        TextView titleView;
        TextView bodyView;
        TextView timeView;
    }

}
