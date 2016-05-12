package com.example.devnn.videorecorderdemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * create by devnn
 * Email:devnn@devnn.com
 */
public class MainActivity extends AppCompatActivity {
    private String TAG=this.getClass().getSimpleName();
    private LinearLayout picturesContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    private void init(){
        picturesContainer = (LinearLayout) findViewById(R.id.picture_container);
    }
    /**
     * 自定义录像机
     * @param view
     */
    public void record1(View view) {
        Intent intent = new Intent(this, MediaRecorderActivity.class);
        startActivityForResult(intent, 4);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            String videoPath = data.getStringExtra("videoPath");
            Log.i(TAG, "video path:" + videoPath);
            addVideoCoverFromLocal(videoPath);//系统录像机
        }
    }
    private void addVideoCoverFromLocal(final String videoPath) {
        if (picturesContainer.getVisibility() == View.GONE) {
            picturesContainer.setVisibility(View.VISIBLE);
        }
        Bitmap thumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        Bitmap squareBitmap = BitmapUtil.getSquareBitmap(thumbBitmap);
        ImageView imageView=new ImageView(this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin=15;
        layoutParams.leftMargin=15;
        layoutParams.rightMargin=15;
        layoutParams.bottomMargin=15;

        imageView.setImageBitmap(squareBitmap);
        picturesContainer.addView(imageView);
        imageView.setLayoutParams(layoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(videoPath), "video/mp4");
                try {
                    MainActivity.this.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"没有安装视频播放器",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


    }
}
