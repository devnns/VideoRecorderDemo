package com.example.devnn.videorecorderdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;


public class MediaRecorderActivity extends Activity implements View.OnClickListener,Handler.Callback {
    private Button btnStart;
    private Button btnCancel;
    private MovieRecorderView movieRecorderView;
    private Handler handler = new Handler(this);
    private Status status= Status.NONE;

    private enum Status{
        RECORDING,
        NONE,
        RECORDED
    }
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_media_recorder);
        init();
        setData();
    }

    protected void init() {
        btnCancel= (Button) findViewById(R.id.movie_recorder_btn1);
        btnCancel.setOnClickListener(this);
        btnStart = (Button) findViewById(R.id.movie_recorder_btn2);
        btnStart.setOnClickListener(this);
        movieRecorderView = (MovieRecorderView) findViewById(R.id.movie_recorder_view);
        movieRecorderView.setHandler(handler);

    }

    protected void setData() {

    }



    @Override
    public void onClick(View v) {
        if (v.equals(this.btnStart)) {
            if(status== Status.NONE) {
                movieRecorderView.start();
            }else if(status== Status.RECORDING){
                movieRecorderView.stop();
                status= Status.RECORDED;
                btnStart.setText("确定");
                btnCancel.setText("重拍");
            }else if(status== Status.RECORDED){
                //ok
                status= Status.NONE;
                String videoPath=movieRecorderView.getMoviePath();
                Intent intent=new Intent();
                intent.putExtra("videoPath",videoPath);
                this.setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }else if(v.equals(this.btnCancel)){
            if(status== Status.NONE) {
                movieRecorderView.cancel();
                finish();
            }else if(status== Status.RECORDING){
                movieRecorderView.cancel();
                finish();
            }else if(status== Status.RECORDED){
                //重拍
                movieRecorderView.reRecord();

            }
        }

    }


    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {//已经开始录制
            if(status== Status.NONE) {
                status = Status.RECORDING;
                btnStart.setText("停止");
                btnCancel.setText("取消");
            }else if(status== Status.RECORDED){
                status = Status.RECORDING;
                btnStart.setText("停止");
                btnCancel.setText("取消");
            }
        } else if (msg.what == 2) {//时间已到
            status= Status.RECORDED;
            btnStart.setText("确定");
            btnCancel.setText("重拍");
        }else if(msg.what==3){
            finish();
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(status== Status.RECORDED||status== Status.RECORDED){
            movieRecorderView.cancel();
        }

    }
}
