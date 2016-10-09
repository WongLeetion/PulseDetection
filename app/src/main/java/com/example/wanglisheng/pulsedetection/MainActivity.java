package com.example.wanglisheng.pulsedetection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends Activity implements SurfaceHolder.Callback{
    private SurfaceView cameraPreview;
    private Camera camera = null;
    private SurfaceHolder holder;
    private String strCaptureFilePath = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera/";
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();


//    private SurfaceHolder.Callback cameraPreviewHolderCallback = new SurfaceHolder.Callback() {
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
//
//            startPreview();
//        }
//
//        @Override
//        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            stopPreview();
//
//        }
//    };


//    private String saveTempFile(byte[] bytes) {
//        try {
//            //File f = File.createTempFile("img", "");
//            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//            File f = new File(directory, "test.jpg");
//
//            FileOutputStream fos = new FileOutputStream(f);
//            fos.write(bytes);
//            fos.flush();
//            fos.close();
//
//
//            return f.getAbsolutePath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    private void startPreview() {            //预览拍完照的画面
//        camera = Camera.open();
//        try {
//            camera.setPreviewDisplay(cameraPreview.getHolder());
//            camera.setDisplayOrientation(90);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopPreview() {
//        camera.stopPreview();
//        camera.release();
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);



        //cameraPreview.getHolder().addCallback(cameraPreviewHolderCallback);
        holder = cameraPreview.getHolder();
        holder.addCallback(MainActivity.this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // TODO Auto-generated method stub
//				camera.autoFocus(mAutoFocusCallback);
        
        findViewById(R.id.btnTakePic).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
				/* 告动对焦后拍照 */
                camera.autoFocus(mAutoFocusCallback);
//                for (int i = 1; i <= 10; i++) {
//                    takePicture();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                System.out.println("完成照相功能！");
            }
        });

    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            camera.autoFocus(mAutoFocusCallback); //实现连续拍照
            super.handleMessage(msg);
        }
    };
    public void surfaceCreated(SurfaceHolder surfaceholder) {
        try {
			/* 打开相机， */
            System.out.println("打开照相功能！");
            camera = Camera.open();
            camera.setDisplayOrientation(90);//终于改好了!2016年10月9日
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
                               int h) {
		/* 相机初始化 */
        initCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        stopCamera();
        camera.release();
        camera = null;
    }

    /* 拍照的method */
    private void takePicture() {
        if (camera != null) {
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);

            System.out.println("this is takePicture()");

        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {

        System.out.println("intent=" + intent);
        System.out.println("requestCode=" + requestCode);

        super.startActivityForResult(intent, requestCode);

    }

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
			/* 按下快门瞬间会调用这里的程序 */
            System.out.println("this is onShtter");
        }
    };

    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
			/* 要处理raw data?写?否 */
            System.out.println("this is onPictureTaken");
        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) // 判断SD卡是否存在，并且可以可以读写
            {
                System.out.println("可以读写");
            } else {
                Toast.makeText(MainActivity.this, "SD卡不存在或写保护",
                        Toast.LENGTH_LONG).show();
            }

            try {

                Calendar c = Calendar.getInstance();
                String time = formatTimer(c.get(Calendar.YEAR)) + "-"
                        + formatTimer(c.get(Calendar.MONTH)) + "-"
                        + formatTimer(c.get(Calendar.DAY_OF_MONTH)) + " "
                        + formatTimer(c.get(Calendar.HOUR_OF_DAY)) + "."
                        + formatTimer(c.get(Calendar.MINUTE)) + "."
                        + formatTimer(c.get(Calendar.SECOND));
                System.out.println("现在时间：" + time + "  将此时间当作图片名存储");

				/* 取得相片 */
                Bitmap bm = BitmapFactory.decodeByteArray(_data, 0,
                        _data.length);
                System.out.println("-------xiangpian----"+bm);
                handler.sendEmptyMessage(0);
				/* 创建文件 */
                File myCaptureFile = new File(strCaptureFilePath, "" + time
                        + ".jpg");

                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(myCaptureFile));
				/* 采用压缩转档方法 */
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                System.out.println("this is pass");

				/* 调用flush()方法，更新BufferStream */
                bos.flush();

				/* 结束OutputStream */
                bos.close();

				/* 让相片显示3秒后圳重设相机 */
                // Thread.sleep(2000);
				/* 重新设定Camera */
                stopCamera();
                initCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    AlertDialog.Builder b;

    /**
     * 转换时间
     *
     * @param d
     * @return
     */
    public String formatTimer(int d) {
        return d > 10 ? "" + d : "0" + d;
    }

    /* 告定义class AutoFocusCallback */
    public final class AutoFocusCallback implements
            android.hardware.Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
			/* 对到焦点拍照 */
            takePicture();
        }
    };

    /* 相机初始化的method */
    private void initCamera() {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
				/*
				 * 设定相片大小为1024*768， 格式为JPG
				 */
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setPictureSize(0,0);
                camera.setParameters(parameters);
				/* 打开预览画面 */
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 停止相机的method */
    private void stopCamera() {
        if (camera != null) {
            try {
				/* 停止预览 */
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final static int ID_USER = 0;

    Runnable r = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            Message msg = new Message();
            msg.what = ID_USER;
            mHandler.sendMessage(msg);
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ID_USER:
                    System.out.println("tanchu s");
                    Toast.makeText(MainActivity.this, "已弹出", Toast.LENGTH_SHORT)
                            .show();

                    break;
            }
        };
    };
}
