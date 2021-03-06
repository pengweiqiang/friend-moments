package com.anda.moments.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by Administrator on 2015/9/11.
 */
public class FileUtil {
    public final static String FILE_START_NAME = "VID_";
    public final static String VIDEO_EXTENSION = ".mp4";

    public static final String APP_SD_ROOT_DIR = "/com.anda.moments";
    public static final String MEDIA_FILE_DIR = APP_SD_ROOT_DIR + "/Media";
    public static final String PICTURE_FILE_DIR = APP_SD_ROOT_DIR+"/Picture";
    public static final String AUDIO_FILE_DIR = APP_SD_ROOT_DIR+"/Audio";

    public static final String DOWNLOAD_MEDIA_FILE_DIR = APP_SD_ROOT_DIR+"/download";

    public static final String DOWNLOAD_SAVE = "/moments";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * 判断SD卡是否挂载
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String saveMediaFirstPicture(Bitmap mBitmap,String bitName){
        File f = new File( Environment.getExternalStorageDirectory()+MEDIA_FILE_DIR+"/"+bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getPath();
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type){
        File file = getOutputMediaFile(type);
        if (null == file)
            return null;
        return Uri.fromFile(file);
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + MEDIA_FILE_DIR);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * 删除文件
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return file.delete();
        return true;
    }

    public static String createFilePath(String folder, String subfolder, String uniqueId) {
        File dir = new File(Environment.getExternalStorageDirectory(), folder);
        if (subfolder != null) {
            dir = new File(dir, subfolder);
        }
        dir.mkdirs();
        String fileName = FILE_START_NAME + uniqueId + VIDEO_EXTENSION;
        return new File(dir,fileName).getAbsolutePath();
    }

    public static void createPictureFile(){
        File dir = new File(Environment.getExternalStorageDirectory(),PICTURE_FILE_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
    }
    public static File createSavePicturePath(String imgPath){
        File dir = new File(Environment.getExternalStorageDirectory()+DOWNLOAD_SAVE);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File saveFile = new File(dir.getPath() + File.separator +
                imgPath);

        return saveFile;
    }
    public static String createFile(String filefloderPath){
        File dir = new File(Environment.getExternalStorageDirectory(),filefloderPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    private static int maxLength = 20;//缓存最大20个文件
    public static void deleteCache(){
        File dir = new File(Environment.getExternalStorageDirectory(),DOWNLOAD_MEDIA_FILE_DIR);
        if(dir.exists()){
            File files[] = dir.listFiles();
            int fileLengths = files.length;
            if(fileLengths>maxLength) {
                Arrays.sort(files, new CompratorByLastModified());
                for(int i=0;i<fileLengths-maxLength;i++){
                    files[i].delete();
                }
            }
        }
    }


    //根据文件修改时间进行比较的内部类
    static class CompratorByLastModified implements Comparator<File> {

        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0) {
                return 1;
            } else if (diff == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
