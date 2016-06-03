package com.anda.moments.apdater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.entity.Audio;
import com.anda.moments.entity.Images;
import com.anda.moments.entity.Infos;
import com.anda.moments.entity.Video;
import com.anda.moments.ui.ImagePagerActivity;
import com.anda.moments.ui.VideoDetailActivity;
import com.anda.moments.utils.CommonHelper;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.TextViewUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.MultiMyImageView;
import com.anda.moments.views.audio.MediaManager;
import com.squareup.picasso.Picasso;
import com.yqritc.scalablevideoview.ScalableVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import sz.itguy.utils.FileUtil;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class MyAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_IMAGES = 0;
    private static final int ITEM_VIEW_TYPE_AUDIO = 1;
    private static final int ITEM_VIEW_TYPE_VIDEO = 2;
    private static final int ITEM_VIEW_TYPE_TEXT = 3;

    public int playingAudioIndex = -1;

    private static final int ITEM_VIEW_TYPE_COUNT = 4;

    private Context context;
    private List<Infos> datalist;
    private LayoutInflater layoutInflater;

    public MyAdapter(Context context, List<Infos> datalist) {
        this.context = context;
        this.datalist = datalist;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(datalist==null){
            return 0;
        }
        return datalist.size();
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = ITEM_VIEW_TYPE_TEXT;
        Infos item = getItem(position);

        List<Video> videos = item.getVideos();
        List<Audio> audios = item.getAudios();
        List<Images> images = item.getImages();

        if (images!=null && !images.isEmpty()) {//图片
            itemType = ITEM_VIEW_TYPE_IMAGES;
        } else if (audios!=null && !audios.isEmpty()) {//音频
            itemType = ITEM_VIEW_TYPE_AUDIO;
        } else if(videos!=null && !videos.isEmpty()){//视频
            itemType = ITEM_VIEW_TYPE_VIDEO;
        }else{
            itemType = ITEM_VIEW_TYPE_TEXT;
        }
        return itemType;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_COUNT;
    }


    @Override
    public Infos getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Infos infos = datalist.get(position);
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.my_item_ninegridlayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mediaViewStub = (ViewStub) convertView.findViewById(R.id.media_view_stub);
            switch (itemViewType){
                case ITEM_VIEW_TYPE_TEXT:
                    viewHolder.mediaViewStub.setLayoutResource(R.layout.my_view_stub_my_text);
                    viewHolder.mediaViewStub.inflate();
                    break;
                case ITEM_VIEW_TYPE_IMAGES://图片
                    viewHolder.mediaViewStub.setLayoutResource(R.layout.home_view_stub_my_images);
                    viewHolder.mediaViewStub.inflate();

                    viewHolder.multiMyImageView = (MultiMyImageView) convertView.findViewById(R.id.iv_multi_image);
                    viewHolder.mTvImagesCount = (TextView)convertView.findViewById(R.id.tv_images_count);
                    break;
                case ITEM_VIEW_TYPE_AUDIO://音频
                    viewHolder.mediaViewStub.setLayoutResource(R.layout.home_view_stub_my_audios);
                    viewHolder.mediaViewStub.inflate();

                    viewHolder.mViewAudio = convertView.findViewById(R.id.view_record);
                    viewHolder.mViewAnim = convertView.findViewById(R.id.voice_anim);
                    viewHolder.mViewAnim.setBackgroundResource(R.drawable.anim_play_audio);
                    viewHolder.animationDrawable = (AnimationDrawable) viewHolder.mViewAnim.getBackground();
                    viewHolder.mTvAudioSecond = (TextView)convertView.findViewById(R.id.tv_audio_second);

//                    viewHolder.mViewAudio.setOnClickListener(viewHolder);

                    break;
                case ITEM_VIEW_TYPE_VIDEO://视频
                    viewHolder.mediaViewStub.setLayoutResource(R.layout.home_view_stub_my_videos);
                    viewHolder.mediaViewStub.inflate();

//                    viewHolder.mScalableVideoView = (ScalableVideoView)convertView.findViewById(R.id.video_view);
                    viewHolder.mPlayImageView = (ImageView)convertView.findViewById(R.id.playImageView);
                    viewHolder.mThumbnailImageView = (ImageView)convertView.findViewById(R.id.thumbnailImageView);
                    viewHolder.mProgressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

//                    try {
//                        viewHolder.mScalableVideoView.setDataSource("");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    viewHolder.mScalableVideoView.setOnClickListener(viewHolder);
                    viewHolder.mPlayImageView.setOnClickListener(viewHolder);


                    break;
            }


            viewHolder.multiMyImageView = (MultiMyImageView) convertView.findViewById(R.id.iv_multi_image);
            viewHolder.mTvPublishTime = (TextView) convertView.findViewById(R.id.tv_publish_time);
            viewHolder.mTvContent = (TextView)convertView.findViewById(R.id.tv_content);
            viewHolder.mLineTop = convertView.findViewById(R.id.line_top);



//            viewHolder.mViewComment.setOnClickListener(viewHolder);
//            viewHolder.mIvUserHead.setOnClickListener(viewHolder);
//            viewHolder.mTvUserName.setOnClickListener(viewHolder);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setPosition(position);

        if(position ==0){
            viewHolder.mLineTop.setVisibility(View.GONE);
        }else{
            viewHolder.mLineTop.setVisibility(View.VISIBLE);
        }

        showItemTypeData(itemViewType,infos,viewHolder);


        String dateStr = DateUtils.getTimestampString2(infos.getCreateTime());
        infos.setDateTitle(dateStr);

        String section = getSectionForPosition(position);
        if(position == getPositionForSection(section)){
            if(position != 0) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.mediaViewStub.getLayoutParams();
                layoutParams.setMargins(0, DeviceInfo.dp2px(context, 10), 0, 0);
                viewHolder.mediaViewStub.setLayoutParams(layoutParams);
            }else{
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.mediaViewStub.getLayoutParams();
                params.setMargins(0,0,0,0);
                viewHolder.mediaViewStub.setLayoutParams(params);
                viewHolder.mTvPublishTime.setVisibility(View.GONE);
            }
            viewHolder.mTvPublishTime.setVisibility(View.VISIBLE);
            if(dateStr.equals("今天")){
                viewHolder.mTvPublishTime.setText(dateStr);
            }else{
                if(!StringUtils.isEmpty(dateStr)){
                    dateStr = dateStr.substring(0,2)+"\n"+dateStr.substring(2);
                    viewHolder.mTvPublishTime.setText(TextViewUtils.
                            getSpannableStringSizeAndColor(dateStr,2,dateStr.length(), 11,Color.parseColor("#434343")));
                }
            }
        }else{
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.mediaViewStub.getLayoutParams();
            params.setMargins(0,0,0,0);
            viewHolder.mediaViewStub.setLayoutParams(params);
            viewHolder.mTvPublishTime.setVisibility(View.GONE);
        }

        viewHolder.mTvContent.setText(StringUtils.ToDBC(infos.getInfoText()));




        return convertView;
    }

    /**
     */
    public String getSectionForPosition(int position) {
        return datalist.get(position).getDateTitle();
    }

    /**
     */
    public int getPositionForSection(String section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = datalist.get(i).getDateTitle();
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }


    private void showItemTypeData(int itemViewType, final Infos infos, final ViewHolder viewHolder){
        switch (itemViewType){
            case ITEM_VIEW_TYPE_TEXT:

                break;
            case ITEM_VIEW_TYPE_IMAGES:
                final List<Images> itemPicList = infos.getImages();
                if (itemPicList ==null || itemPicList.isEmpty()) {
                    viewHolder.multiMyImageView.setVisibility(View.GONE);
                    viewHolder.mTvImagesCount.setVisibility(View.GONE);
                } else {
                    int imageSize = itemPicList.size();
                    if(imageSize>1){
                        viewHolder.mTvImagesCount.setVisibility(View.VISIBLE);
                        viewHolder.mTvImagesCount.setText("共"+itemPicList.size()+"张");
                    }else{
                        viewHolder.mTvImagesCount.setVisibility(View.GONE);
                    }

                    viewHolder.multiMyImageView.setList(itemPicList);
                    viewHolder.multiMyImageView.setVisibility(View.VISIBLE);
                    viewHolder.multiMyImageView.setOnItemClickListener(new MultiMyImageView.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // 因为单张图片时，图片实际大小是自适应的，imageLoader缓存时是按测量尺寸缓存的
                            ImagePagerActivity.imageSize = new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
                            ImagePagerActivity.startImagePagerActivity(context, itemPicList, position);
                        }
                    });
                }
                break;
            case ITEM_VIEW_TYPE_AUDIO://语音
                Audio audio = infos.getAudios().get(0);
                viewHolder.mTvAudioSecond.setText(audio.getAudioTime()+"''");
                if(viewHolder.position == playingAudioIndex){
                    startAnimAudio(viewHolder);
                }else{
                    stopAnimAudio(viewHolder);
                }
                viewHolder.mViewAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopCurrentAnimAudio();
                        if(viewHolder.position == playingAudioIndex){//正在播放的再次点击暂停音频
                            MediaManager.pause();
                            playingAudioIndex = -1;
//                            stopAnimAudio();
                            return;
                        }
                        if(infos.getAudios()!=null && !infos.getAudios().isEmpty()) {
                            downloadMedia(infos.getAudios().get(0).getPath(), ReqUrls.MEDIA_TYPE_AUDIO,viewHolder);
                        }
                    }
                });

                break;
            case ITEM_VIEW_TYPE_VIDEO://视频
//                viewHolder.mIvPlay.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(circleMessage.getVideos()!=null && !circleMessage.getVideos().isEmpty()) {
//                            downloadMedia(circleMessage.getVideos().get(0).getPath());
//                        }
//                    }
//                });
//                viewHolder.mPlayImageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(circleMessage.getVideos()!=null && !circleMessage.getVideos().isEmpty()) {
////                            playerVideo(circleMessage.getVideos().get(0).getPath(),viewHolder);
//                            downloadMedia(circleMessage.getVideos().get(0).getPath(), ReqUrls.MEDIA_TYPE_VIDEO,viewHolder);
//                        }
//                    }
//                });

                Video video = infos.getVideos().get(0);
                String url = video.getPath();
                String downLoadPath =  FileUtil.createFile(FileUtil.DOWNLOAD_MEDIA_FILE_DIR);
                String fileName = url.substring(url.lastIndexOf("/")+1);
//                viewHolder.mThumbnailImageView.setImageBitmap(getVideoThumbnail(downLoadPath+"/"+fileName));
                Picasso.with(context).load(video.getIcon()).error(new ColorDrawable(Color.BLACK)).into(viewHolder.mThumbnailImageView);

                break;
        }
    }



    class ViewHolder implements View.OnClickListener{
        public TextView mTvPublishTime;//发布时间
        public TextView mTvContent;//发布内容
        public View mLineTop;//顶端的线
        public ViewStub mediaViewStub;

        //图片
        public MultiMyImageView multiMyImageView;//图片四宫格
        public TextView mTvImagesCount;

        //音频
        public View mViewAudio;//语音背景
        public View mViewAnim;//语音动画
        public AnimationDrawable animationDrawable;
        public TextView mTvAudioSecond;//时长

        //视频
        public ScalableVideoView mScalableVideoView;
        public ImageView mPlayImageView;//
        public ImageView mThumbnailImageView;//缩略图
        public ProgressBar mProgressBar;

        private int position;

        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Infos infos = datalist.get(position);
            switch (v.getId()){
                case R.id.view_record://音频播放
                    if(infos.getAudios()!=null && !infos.getAudios().isEmpty()) {
                        downloadMedia(infos.getAudios().get(0).getPath(), ReqUrls.MEDIA_TYPE_AUDIO,this);
                    }
                    break;
                case R.id.playImageView://视频播放
                    if(infos.getVideos()!=null && !infos.getVideos().isEmpty()){
                        Intent intent = new Intent(context, VideoDetailActivity.class);
                        intent.putExtra(VideoDetailActivity.KEY_FILE_PATH,getItem(position).getVideos().get(0).getPath());
                        intent.putExtra("firstPicture","");
                        context.startActivity(intent);
                    }
                    break;
            }
        }
    }


    private String TAG = "HomeAdapter";
    public void downloadMedia(String url,final int type,final ViewHolder viewHolder){
        if(CommonHelper.isFastClick()){
            return;
        }
        String downLoadPath =  FileUtil.createFile(FileUtil.DOWNLOAD_MEDIA_FILE_DIR);
        String fileName = url.substring(url.lastIndexOf("/")+1);

        if(new File(downLoadPath,fileName).exists()){//是否下载过
            String filePath = downLoadPath+"/"+fileName;
            switch (type){
                case  ReqUrls.MEDIA_TYPE_AUDIO://音频
                    playAudioRecord(filePath,viewHolder);
                    break;
                case  ReqUrls.MEDIA_TYPE_VIDEO://视频
                    playerVideo(filePath,viewHolder);
                    break;
            }
            return;
        }

        OkHttpUtils//
                .get()//
//                .tag(this)
                .url(url)//
                .build()//
                .execute(new FileCallBack(downLoadPath+"/", fileName) {
                    @Override
                    public void inProgress(float progress, long total) {
                        if(type == ReqUrls.MEDIA_TYPE_VIDEO) {
                            int progressInt = (int) (100 * progress);
                            viewHolder.mProgressBar.setProgress(progressInt);
                            Log.e(TAG,progressInt+"  total "+total);
                        }

                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(File file) {
                        if(file.exists()){
                            switch (type){
                                case  ReqUrls.MEDIA_TYPE_AUDIO://音频
                                    playAudioRecord(file.getPath(),viewHolder);
                                    break;
                                case  ReqUrls.MEDIA_TYPE_VIDEO://视频
                                    playerVideo(file.getPath(),viewHolder);
                                    break;
                            }
                        }
                        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });

    }


    /**
     * 播放录音
     */
    AnimationDrawable animationDrawable;
    private void playAudioRecord(String filePath,final ViewHolder viewHolder){
//        viewHolder.mViewAnim.setBackgroundResource(R.drawable.anim_play_audio);
        if(viewHolder.animationDrawable!=null && viewHolder.animationDrawable.isRunning()){
            animationDrawable.selectDrawable(2);
            animationDrawable.stop();
        }
//		if(animation==null) {
//        animationDrawable = (AnimationDrawable) viewHolder.mViewAnim.getBackground();
//		}
        playingAudioIndex = viewHolder.position;
        animationDrawable = viewHolder.animationDrawable;
        viewHolder.animationDrawable.start();
        MediaManager.playSound(filePath,
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewHolder.animationDrawable.stop();
                        playingAudioIndex = -1;//音频播放停止
                        viewHolder.animationDrawable.selectDrawable(2);
                    }
                });
    }

    private void playerVideo(String filePath,final  ViewHolder viewHolder){

        try {
            viewHolder.mScalableVideoView.setDataSource(filePath);
            viewHolder.mScalableVideoView.setVolume(0,0);
            viewHolder.mScalableVideoView.setLooping(true);
            viewHolder.mScalableVideoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    datalist.get(viewHolder.position).setPlay(true);
                    viewHolder.mPlayImageView.setVisibility(View.GONE);
                    viewHolder.mThumbnailImageView.setVisibility(View.GONE);
                    viewHolder.mScalableVideoView.start();
                }
            });


        } catch (IOException e) {
            android.util.Log.e(TAG, e.getLocalizedMessage());
            ToastUtils.showToast(context, "播放视频异常");
        }


    }

    public Bitmap getVideoThumbnail(String filePath) {
        if(!new File(filePath).exists()){
            return null;
        }
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(TimeUnit.SECONDS.toMicros(1));
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void stopAnimAudio(ViewHolder viewHolder){
        if(viewHolder.animationDrawable!=null){
            viewHolder.animationDrawable.selectDrawable(2);
            viewHolder.animationDrawable.stop();
        }
    }
    public void stopCurrentAnimAudio(){
        if(animationDrawable!=null){
            animationDrawable.selectDrawable(2);
            animationDrawable.stop();
        }
    }
    public void startAnimAudio(ViewHolder viewHolder){
        if(viewHolder.animationDrawable!=null) {
            viewHolder.animationDrawable.start();
        }
    }



}
