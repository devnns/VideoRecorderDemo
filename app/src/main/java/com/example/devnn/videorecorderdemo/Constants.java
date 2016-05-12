package com.example.devnn.videorecorderdemo;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator + "devnn_videos";// 需要存储在SD卡上的文件的基路径
    public static String VIDEOS_DIR = ROOT_PATH + File.separator + "videos";// 视频存储目录

    static {
	File file1=new File(ROOT_PATH);
	File file2=new File(ROOT_PATH);
        if(!file1.exists()){
            file1.mkdirs();
        }
        if(!file2.exists()){
            file2.mkdirs();
        }

    }
}
