package com.anda.moments.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anda.moments.R;
import com.anda.moments.commons.AppManager;
import com.anda.moments.entity.Images;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.FileUtil;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.publish.FileUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

//import com.anda.universalimageloader.core.DisplayImageOptions;
//import com.anda.universalimageloader.core.ImageLoader;
//import com.anda.universalimageloader.core.assist.FailReason;
//import com.anda.universalimageloader.core.assist.ImageScaleType;
//import com.anda.universalimageloader.core.assist.ImageSize;
//import com.anda.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.anda.universalimageloader.core.listener.SimpleImageLoadingListener;
//import com.anda.universalimageloader.utils.MemoryCacheUtils;

/**
 * Created by yiw on 2016/1/6.
 */
public class ImagePagerActivity extends BaseActivity {
    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
//    public static ImageSize imageSize;

    public static int [] imageSize;

    static CropSquareTransformation cropSquareTransformation;

    public static void startImagePagerActivity(Context context, List<Images> imgUrls, int position){
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putExtra(INTENT_IMGURLS, new ArrayList<Images>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_imagepager);
        super.onCreate(savedInstanceState);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        guideGroup = (LinearLayout) findViewById(R.id.guideGroup);

        int startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        ArrayList<Images> imgUrls = ( ArrayList<Images> )getIntent().getSerializableExtra(INTENT_IMGURLS);

        ImageAdapter mAdapter = new ImageAdapter(this);
        mAdapter.setDatas(imgUrls);
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0; i<guideViewList.size(); i++){
                    guideViewList.get(i).setSelected(i==position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos, imgUrls);
        if(imgUrls==null || imgUrls.size()<=1){
            guideGroup.setVisibility(View.GONE);
        }
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_UP:
//                        AppManager.getAppManager().finishActivity();
//                        break;
//                }
//                return false;
//            }
//        });
        cropSquareTransformation = new CropSquareTransformation();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initListener() {

    }

    private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<Images> imgUrls) {
        if(imgUrls!=null && imgUrls.size()>0){
            guideViewList.clear();
            for (int i=0; i<imgUrls.size(); i++){
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i==startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    private class ImageAdapter extends PagerAdapter {

        private List<Images> datas = new ArrayList<Images>();
        private LayoutInflater inflater;
//        private DisplayImageOptions options;
        private Context context;

        public void setDatas(List<Images> datas) {
            if(datas != null )
                this.datas = datas;
        }

        public ImageAdapter(Context context){
            this.context = context;
            this.inflater = LayoutInflater.from(context);
//            options = new DisplayImageOptions.Builder()
//                    .showImageForEmptyUri(R.drawable.ic_launcher)
//                    .showImageOnFail(R.drawable.ic_launcher)
//                    .resetViewBeforeLoading(true)
//                    .cacheOnDisc(true)
//                   // .cacheOnDisk(true)
//                    .imageScaleType(ImageScaleType.EXACTLY)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//                    .considerExifParams(true)
//                    .displayer(new FadeInBitmapDisplayer(300))
//                    .build();
        }

        @Override
        public int getCount() {
            if(datas == null) return 0;
            return datas.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if(view != null){
                final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);
                //预览imageView
                final ImageView smallImageView = new ImageView(context);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize[0], imageSize[1]);
                layoutParams.gravity = Gravity.CENTER;
                smallImageView.setLayoutParams(layoutParams);
                smallImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ((FrameLayout)view).addView(smallImageView);
                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout)view).addView(loading);

                final String imgurl = datas.get(position).getImgPath();
                loading.setVisibility(View.VISIBLE);
//                Picasso.with(context).setIndicatorsEnabled(true);
                Picasso.with(context).load(imgurl).transform(cropSquareTransformation)
                        .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.GONE);
                        smallImageView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                        Log.e("sd","error");
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        savePicture(imgurl);
                        return false;
                    }
                });
//                ImageLoader.getInstance().displayImage(imgurl, imageView, options, new SimpleImageLoadingListener(){
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//                        //获取内存中的缩略图
//                        String memoryCacheKey = MemoryCacheUtils.generateKey(imageUri, imageSize);
//                        Bitmap bmp = ImageLoader.getInstance().getMemoryCache().get(memoryCacheKey);
//                        if(bmp != null && !bmp.isRecycled()){
//                            smallImageView.setVisibility(View.VISIBLE);
//                            smallImageView.setImageBitmap(bmp);
//                        }
//                        loading.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        loading.setVisibility(View.GONE);
//                        smallImageView.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                        super.onLoadingFailed(imageUri, view, failReason);
//                    }
//                });

                container.addView(view, 0);

//                imageView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        AppManager.getAppManager().finishActivity();
//                        return false;
//                    }
//                });
                imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float v, float v1) {
                        AppManager.getAppManager().finishActivity();
                    }
                });

            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    /**
     * 自定义接口，实现图像缩小为原来的一半
     */
    public  class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {

            Bitmap result = FileUtils.ratio(source,DeviceInfo.getScreenWidth(ImagePagerActivity.this),DeviceInfo.getScreenHeight(ImagePagerActivity.this));
            if (result != source) {
                source.recycle();
            }

            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }

    private void savePicture(final String imgUrl){
        final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alertdialog);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("保存图片");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                downloadPicture(imgUrl);
                dlg.cancel();
            }
        });
        window.findViewById(R.id.ll_content2).setVisibility(View.GONE);

    }

    private void downloadPicture(String imgUrl){

        //Target
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                String imageName = System.currentTimeMillis() + ".png";

                File dcimFile = FileUtil
                        .createSavePicturePath(imageName);

                FileOutputStream ostream = null;
                try {
                    ostream = new FileOutputStream(dcimFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(ImagePagerActivity.this,"图片下载至:"+dcimFile,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        //Picasso下载
        Picasso.with(this).load(imgUrl).into(target);
    }



}
