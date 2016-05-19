//package com.anda.moments.callBack;
//
//import com.anda.moments.MyApplication;
//import com.anda.moments.utils.DiskLruCacheUtils;
//import com.sea_monster.cache.DiskLruCache;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.Callback;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import okhttp3.Response;
//
///**
// * Created by pengweiqiang on 16/5/18.
// */
//public abstract class FileCallBack extends Callback<String>
//{
//    private String url;
//    /**
//     * 目标文件存储的文件夹路径
//     */
//    private String destFileDir;
//    /**
//     * 目标文件存储的文件名
//     */
//    private String destFileName;
//
//    private DiskLruCache diskLruCache;
//
//    public abstract void inProgress(float progress,long total);
//
//    public FileCallBack(String destFileDir, String destFileName,String url)
//    {
//        this.destFileDir = destFileDir;
//        this.destFileName = destFileName;
//        this.url = url;
////        diskLruCache = MyApplication.getDiskLruCache();
//    }
//
//
//    @Override
//    public String parseNetworkResponse(Response response) throws Exception
//    {
////        return saveFile(response);
//        return saveFileDiskCache(response);
//    }
//
//    public String saveFileDiskCache(Response response) throws IOException{
//        InputStream is = null;
//        byte[] buf = new byte[2048];
//        int len = 0;
//        String key = DiskLruCacheUtils.getStringByMD5(url);
//        OutputStream fos = null;
//        try
//        {
//            DiskLruCache.Editor editor = diskLruCache.edit(key);
//            if(editor!=null){
//                fos = editor.newOutputStream(0);
//            }
//            is = response.body().byteStream();
//            final long total = response.body().contentLength();
//            long sum = 0;
//
////            File dir = new File(destFileDir);
////            if (!dir.exists())
////            {
////                dir.mkdirs();
////            }
//            while ((len = is.read(buf)) != -1)
//            {
//                sum += len;
//                fos.write(buf, 0, len);
//                final long finalSum = sum;
//                OkHttpUtils.getInstance().getDelivery().post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//
//                        inProgress(finalSum * 1.0f / total,total);
//                    }
//                });
//            }
//            fos.flush();
//            editor.commit();
//
//            return diskLruCache.getDirectory()+"/"+key;
//
//        } finally
//        {
//            try
//            {
//                if (is != null) is.close();
//            } catch (IOException e)
//            {
//            }
//            try
//            {
//                if (fos != null) fos.close();
//            } catch (IOException e)
//            {
//            }
//            try{
//                if(diskLruCache!=null){
//                    diskLruCache.flush();
//                }
//            }catch (IOException e){
//            }
//
//        }
//    }
//
//    public File saveFile(Response response) throws IOException
//    {
//        InputStream is = null;
//        byte[] buf = new byte[2048];
//        int len = 0;
//        FileOutputStream fos = null;
//        try
//        {
//            is = response.body().byteStream();
//            final long total = response.body().contentLength();
//            long sum = 0;
//
//            File dir = new File(destFileDir);
//            if (!dir.exists())
//            {
//                dir.mkdirs();
//            }
//            File file = new File(dir, destFileName);
//            fos = new FileOutputStream(file);
//            while ((len = is.read(buf)) != -1)
//            {
//                sum += len;
//                fos.write(buf, 0, len);
//                final long finalSum = sum;
//                OkHttpUtils.getInstance().getDelivery().post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//
//                        inProgress(finalSum * 1.0f / total,total);
//                    }
//                });
//            }
//            fos.flush();
//
//            return file;
//
//        } finally
//        {
//            try
//            {
//                if (is != null) is.close();
//            } catch (IOException e)
//            {
//            }
//            try
//            {
//                if (fos != null) fos.close();
//            } catch (IOException e)
//            {
//            }
//
//        }
//    }
//
//
//}
