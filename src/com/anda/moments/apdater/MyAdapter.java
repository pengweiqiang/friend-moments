package com.anda.moments.apdater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.entity.Images;
import com.anda.moments.entity.Infos;
import com.anda.moments.entity.Media;
import com.anda.moments.entity.User;
import com.anda.moments.ui.ImagePagerActivity;
import com.anda.moments.ui.UserInfoActivity;
import com.anda.moments.utils.CommonHelper;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.TextViewUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.CustomImageView;
import com.anda.moments.views.MultiMyImageView;
import com.anda.moments.views.NineGridlayout;
import com.anda.moments.views.popup.ActionItem;
import com.anda.moments.views.popup.TitlePopup;
import com.anda.universalimageloader.core.assist.ImageSize;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class MyAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_IMAGES = 0;
    private static final int ITEM_VIEW_TYPE_AUDIO = 1;
    private static final int ITEM_VIEW_TYPE_VIDEO = 2;
    private static final int ITEM_VIEW_TYPE_TEXT = 3;


    private static final int ITEM_VIEW_TYPE_COUNT = 4;

    private Context context;
    private List<Infos> datalist;

    public MyAdapter(Context context, List<Infos> datalist) {
        this.context = context;
        this.datalist = datalist;
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

        List<Media> videos = item.getVideos();
        List<Media> audios = item.getAudios();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.my_item_ninegridlayout, parent, false);
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
                    break;
                case ITEM_VIEW_TYPE_AUDIO://音频
                    viewHolder.mediaViewStub.setLayoutResource(R.layout.home_view_stub_my_audios);
                    viewHolder.mediaViewStub.inflate();

                    viewHolder.mViewAudio = convertView.findViewById(R.id.view_record);
                    viewHolder.mViewAnim = convertView.findViewById(R.id.voice_anim);
                    viewHolder.mTvAudioSecond = (TextView)convertView.findViewById(R.id.tv_audio_second);


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

        viewHolder.mTvContent.setText(StringUtils.ToDBC(infos.getInfoText()));
        String dateStr = DateUtils.getTimestampString2(infos.getCreateTime());
        if(dateStr.equals("今天")){
            viewHolder.mTvPublishTime.setText(dateStr);
        }else{
            if(!StringUtils.isEmpty(dateStr)){
                dateStr = dateStr.substring(0,2)+"\n"+dateStr.substring(2);
                viewHolder.mTvPublishTime.setText(TextViewUtils.getSpannableStringSizeAndColor(dateStr,2,dateStr.length(), 11,Color.parseColor("#434343")));
            }
        }


        return convertView;
    }


    private void showItemTypeData(int itemViewType, final Infos infos, final ViewHolder viewHolder){
        switch (itemViewType){
            case ITEM_VIEW_TYPE_TEXT:

                break;
            case ITEM_VIEW_TYPE_IMAGES:
                final List<Images> itemPicList = infos.getImages();
                if (itemPicList ==null || itemPicList.isEmpty()) {
                    viewHolder.multiMyImageView.setVisibility(View.GONE);
                } else {
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

                viewHolder.mViewAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(infos.getAudios()!=null && !infos.getAudios().isEmpty()) {
//                            downloadMedia(infos.getAudios().get(0).getPath(), ReqUrls.MEDIA_TYPE_AUDIO,viewHolder);
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

        //音频
        public View mViewAudio;//语音背景
        public View mViewAnim;//语音动画
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
            switch (v.getId()){

            }
        }
    }



}
