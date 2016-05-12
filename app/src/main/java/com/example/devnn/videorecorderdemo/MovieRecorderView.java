package com.example.devnn.videorecorderdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频录制控件
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener {
    private String TAG = this.getClass().getSimpleName();
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器

    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mVecordFile = null;// 文件
    private Context context;
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        this.context=context;
        isOpenCamera = true;
        mRecordMaxTime = 60;

        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mSurfaceView.getLayoutParams().width=context.getResources().getDisplayMetrics().widthPixels;
        mSurfaceView.getLayoutParams().height = (context.getResources().getDisplayMetrics().widthPixels )* 480 / 320;
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public String getMoviePath(){
       if( mVecordFile!=null){
           return mVecordFile.getAbsolutePath();
       }else{
           return null;
       }
    }


    /**
     * @author liuyinjun
     * @date 2015-2-5
     */
    private class CustomCallBack implements Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
                initCamera();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }

    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     * @author lip
     * @date 2015-3-16
     */
    private void initCamera() {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;

        setCameraParams();
        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.unlock();
    }

    /**
     * 设置摄像头为竖屏
     *
     * @author lip
     * @date 2015-3-16
     */
    private void setCameraParams() {
        if (mCamera != null) {
            Parameters params = mCamera.getParameters();
            List<Camera.Size> supportVideoSizes = params.getSupportedVideoSizes();
            List<Camera.Size> supportPreviewSizes = params.getSupportedPreviewSizes();
            Log.d(TAG, "support video sizes:");
            for (Camera.Size size : supportVideoSizes) {
                Log.d(TAG, "w:" + size.width + ",h:" + size.height);
            }
            Log.d(TAG, "support preview sizes:");
            for (Camera.Size size : supportPreviewSizes) {
                Log.i(TAG, "w:" + size.width + ",h:" + size.height);
            }
            try {
                params.setPreviewSize(480, 320);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                params.setPreviewSize(320, 480);
            }

//            params.set(Parameters.SCENE_MODE_LANDSCAPE);
            params.set("orientation", "portrait");
//            params.set("orientation", "landscape");
            mCamera.setParameters(params);
        }
    }
    /**
     * 初始化
     *
     * @throws IOException
     * @author lip
     * @date 2015-3-16
     */
    private void initRecord() throws IOException {
//        mSurfaceView.getLayoutParams().height = 50;

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null) {
//            mCamera.setDisplayOrientation(90);
            mMediaRecorder.setCamera(mCamera);
//            mMediaRecorder.setc
        }
        mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        mMediaRecorder.setOnErrorListener(this);

        mMediaRecorder.setVideoSource(VideoSource.CAMERA);//视频源
        mMediaRecorder.setAudioSource(AudioSource.MIC);// 音频源

        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);//视频输出格式
        mMediaRecorder.setVideoSize(480,320);// 设置分辨率

        mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024);
        mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);// 音频格式
        mMediaRecorder.setVideoEncoder(VideoEncoder.H264);// 视频录制格式


//        mMediaRecorder.setVideoFrameRate(20);// 这个我把它去掉了，感觉没什么用

        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
            if(handler!=null){
                handler.sendEmptyMessage(1);
            }
//            mMediaRecorder.get
        } catch (IllegalStateException e) {
            if(handler!=null){
                handler.sendEmptyMessage(3);
            }
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 释放摄像头资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private void createRecordDir() {
        String moviePath= Constants.VIDEOS_DIR+File.separator+"_and_"+new Date().getTime()+".mp4";
        mVecordFile=new File(moviePath);
    }



    /**
     * 开始录制视频
     *
     * @author liuyinjun
     * @date 2015-2-5
     * //     * @param fileName
     * //     *            视频储存位置
     */
    public void reRecord(){
        if(mVecordFile!=null&&mVecordFile.exists()){
            mVecordFile.delete();
        }
        initCamera();
        start();
    }

    public void cancel(){
        stop();
        if(mVecordFile!=null&&mVecordFile.exists()){
            mVecordFile.delete();
        }
    }


    public void start() {
//        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
            {
                initCamera();
            }
            initRecord();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if(handler!=null) {
                            handler.sendEmptyMessage(2);
                        }
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止拍摄
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void stop() {
//        if(handler!=null){
//            handler.sendEmptyMessage(2);
//        }
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void stopRecord() {
        mProgressBar.setProgress(0);
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }


    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}