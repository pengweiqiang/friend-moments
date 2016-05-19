package com.anda.moments.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.sea_monster.cache.DiskLruCache;

import java.io.File;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pengweiqiang on 16/5/18.
 */
public class DiskLruCacheUtils {

    /**
     * 获取DiskLruCache的缓存文件夹
     * 注意第二个参数dataType
     * DiskLruCache用一个String类型的唯一值对不同类型的数据进行区分.
     * 比如bitmap,object等文件夹.其中在bitmap文件夹中缓存了图片.
     *
     * 缓存数据的存放位置为:
     * /sdcard/Android/data//cache
     * 如果SD卡不存在时缓存存放位置为:
     * /data/data//cache
     *
     */
    public static File getDiskLruCacheDir(Context context, String dataType) {
        String dirPath;
        File cacheFile = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable()) {
            dirPath=context.getExternalCacheDir().getPath();
        } else {
            dirPath=context.getCacheDir().getPath();
        }
        cacheFile=new File(dirPath+File.separator+dataType);
        return cacheFile;
    }

    /**
     * 获取APP当前版本号
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context){
        int versionCode=1;
        try {
            String packageName=context.getPackageName();
            PackageManager packageManager=context.getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(packageName, 0);
            versionCode=packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    /**
     * 将字符串用MD5编码.
     * 比如在改示例中将url进行MD5编码
     */
    public static String getStringByMD5(String string) {
        String md5String = null;
        try {
            // Create MD5 Hash
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes());
            byte messageDigestByteArray[] = messageDigest.digest();
            if (messageDigestByteArray == null || messageDigestByteArray.length == 0) {
                return md5String;
            }

            // Create hexadecimal String
            StringBuffer hexadecimalStringBuffer = new StringBuffer();
            int length = messageDigestByteArray.length;
            for (int i = 0; i < length; i++) {
                hexadecimalStringBuffer.append(Integer.toHexString(0xFF & messageDigestByteArray[i]));
            }
            md5String = hexadecimalStringBuffer.toString();
            return md5String;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5String;
    }


    // 将数据写入DiskLruCache
//    public static void saveDataToDiskLruCache(String url) {
//
//                try {
//                    //第一步:获取将要缓存的图片的对应唯一key值.
//                    String key = getStringByMD5(url);
//                    //第二步:获取DiskLruCache的Editor
//                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
//                    if (editor!=null) {
//                        //第三步:从Editor中获取OutputStream
//                        OutputStream outputStream=editor.newOutputStream(0);
//                        //第四步:下载网络图片且保存至DiskLruCache图片缓存中
//                        boolean isSuccessfull=getBitmapFromNetWorkAndSaveToDiskLruCache(url, outputStream);
//                        if (isSuccessfull) {
//                            editor.commit();
//                        }else{
//                            editor.abort();
//                        }
//                        mDiskLruCache.flush();
//                    }
//                } catch (Exception e) {
//
//                }
//
//    }

}
