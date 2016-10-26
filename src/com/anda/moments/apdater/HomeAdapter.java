package com.anda.moments.apdater;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anda.GlobalConfig;
import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.entity.Audio;
import com.anda.moments.entity.CircleMessage;
import com.anda.moments.entity.CommentConfig;
import com.anda.moments.entity.CommentInfo;
import com.anda.moments.entity.CommentUser;
import com.anda.moments.entity.Images;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.PraiseUser;
import com.anda.moments.entity.PraisedInfo;
import com.anda.moments.entity.User;
import com.anda.moments.entity.Video;
import com.anda.moments.ui.ImagePagerActivity;
import com.anda.moments.ui.VideoDetailActivity;
import com.anda.moments.ui.fragments.HomeFragment;
import com.anda.moments.ui.my.UserHomeActivity;
import com.anda.moments.utils.CommonHelper;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.CustomSingleImageView;
import com.anda.moments.views.FullLinearLayout;
import com.anda.moments.views.NineGridlayout;
import com.anda.moments.views.audio.MediaManager;
import com.anda.moments.views.popup.ActionItem;
import com.anda.moments.views.popup.TitlePopup;
import com.squareup.picasso.Picasso;
import com.yqritc.scalablevideoview.ScalableVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import sz.itguy.utils.FileUtil;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class HomeAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_IMAGES = 0;
    private static final int ITEM_VIEW_TYPE_AUDIO = 1;
    private static final int ITEM_VIEW_TYPE_VIDEO = 2;
    private static final int ITEM_VIEW_TYPE_TEXT = 3;
    private static final int ITEM_VIEW_TYPE_IMAGES_AUDIO = 4;//图片+语音


    private static final int ITEM_VIEW_TYPE_COUNT = 5;

    public int playingAudioIndex = -1;

    private Context context;
    private LayoutInflater layoutInflater;
    private List<CircleMessage> datalist;
    private User myUser ;//我的资料

    private int headWidth= 80;

    private int mMinItemWidth; //最小的item宽度
    private int mMaxItemWidth; //最大的item宽度
    public HomeAdapter(Context context, List<CircleMessage> datalist) {
        this.context = context;
        this.datalist = datalist;
        layoutInflater = LayoutInflater.from(context);
        headWidth = DeviceInfo.dp2px(context,70);
        myUser = MyApplication.getInstance().getCurrentUser();

        mMaxItemWidth = (int) (DeviceInfo.getScreenWidth(context) * 0.75f);
        mMinItemWidth = (int) (DeviceInfo.getScreenWidth(context) * 0.15f);
    }

    private HomeFragment homeFragment;
    public void setFragment(HomeFragment homeFragment){
        this.homeFragment = homeFragment;
    }

    public List<CircleMessage> getDatas() {
        return datalist;
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = ITEM_VIEW_TYPE_TEXT;
        CircleMessage item = getItem(position);

        List<Video> videos = item.getVideos();
        List<Audio> audios = item.getAudios();
        List<Images> images = item.getImages();

//        if((images!=null && !images.isEmpty()) && (audios!=null && !audios.isEmpty())){//图片+语音
//          itemType = ITEM_VIEW_TYPE_IMAGES_AUDIO;
//        } else
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
    public int getCount() {
        if(datalist==null){
            return 0;
        }
        return datalist.size();
    }

    @Override
    public CircleMessage getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        final CircleMessage circleMessage = datalist.get(position);

        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.home_item_ninegridlayout, parent, false);
            ViewStub mediaViewStub = (ViewStub) convertView.findViewById(R.id.media_view_stub);
            switch (itemViewType){
                case ITEM_VIEW_TYPE_TEXT:
//                    mediaViewStub.setLayoutResource(R.layout.);
                    break;
                case ITEM_VIEW_TYPE_IMAGES_AUDIO://图片+语音
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_voices_images);
                    mediaViewStub.inflate();

                    viewHolder.ivOne = (CustomSingleImageView) convertView.findViewById(R.id.iv_oneimage);
                    viewHolder.mPlayVoice = (ImageView)convertView.findViewById(R.id.iv_play_voice);
                    break;
                case ITEM_VIEW_TYPE_IMAGES://图片
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_images);
                    mediaViewStub.inflate();

                    viewHolder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
                    viewHolder.ivOne = (CustomSingleImageView) convertView.findViewById(R.id.iv_oneimage);
                    viewHolder.mIvPlayVoiceImages = (ImageView)convertView.findViewById(R.id.iv_play_voice_images);
                    break;
                case ITEM_VIEW_TYPE_AUDIO://音频
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_audios);
                    mediaViewStub.inflate();

                    viewHolder.mViewAudio = convertView.findViewById(R.id.view_record);
                    viewHolder.mIvAudio = (ImageView)convertView.findViewById(R.id.iv_audio);
                    viewHolder.mViewAnim = convertView.findViewById(R.id.voice_anim);
                    viewHolder.mViewAnim.setBackgroundResource(R.drawable.anim_play_audio);
                    viewHolder.animationDrawable = (AnimationDrawable) viewHolder.mViewAnim.getBackground();
                    viewHolder.mTvAudioSecond = (TextView)convertView.findViewById(R.id.tv_audio_second);




                    break;
                case ITEM_VIEW_TYPE_VIDEO://视频
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_videos);
                    mediaViewStub.inflate();

                    viewHolder.mScalableVideoView = (ScalableVideoView)convertView.findViewById(R.id.video_view);
                    viewHolder.mPlayImageView = (ImageView)convertView.findViewById(R.id.playImageView);
                    viewHolder.mThumbnailImageView = (ImageView)convertView.findViewById(R.id.thumbnailImageView);
                    viewHolder.mProgressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

//                    try {
//                        viewHolder.mScalableVideoView.setDataSource("");
//                    } catch (IOException e) {
//                       // e.printStackTrace();
//                    }

                    viewHolder.mScalableVideoView.setOnClickListener(viewHolder);

                    break;
            }



            viewHolder.mIvUserHead = (ImageView)convertView.findViewById(R.id.iv_user_head);
            viewHolder.mTvUserName = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.mTvContent = (TextView)convertView.findViewById(R.id.tv_content);
            viewHolder.mTvPublishTime = (TextView)convertView.findViewById(R.id.tv_create_time);

            viewHolder.mViewComment = convertView.findViewById(R.id.iv_comment);//评论弹出框
            viewHolder.mTvDelete = (TextView) convertView.findViewById(R.id.tv_delete);//删除动态

            viewHolder.digCommentBody = convertView.findViewById(R.id.digCommentBody);
            //评论列表
//            viewHolder.commentListView = (CommentListView)convertView.findViewById(R.id.commentList);
//            viewHolder.mTvCommentCount = (TextView)convertView.findViewById(R.id.tv_comment_count);
//            viewHolder.commentAdapter = new CommentAdapter(context);
//            viewHolder.commentListView.setAdapter(viewHolder.commentAdapter);
//            viewHolder.commentListView.setOnItemClick(viewHolder);
            viewHolder.commentListView = (RecyclerView) convertView.findViewById(R.id.commentList);
            viewHolder.mTvCommentCount = (TextView)convertView.findViewById(R.id.tv_comment_count);
            viewHolder.commentAdapter = new CommentRecyclerViewAdapter(context,null);
            viewHolder.commentAdapter.setOnItemClickListener(viewHolder);

            viewHolder.mViewPraiseCommentLine  = convertView.findViewById(R.id.line_praise_comment);

            //点赞列表
            viewHolder.praiseListView = (RecyclerView) convertView.findViewById(R.id.praiseList);
            viewHolder.mTvPraiseCount = (TextView)convertView.findViewById(R.id.tv_praise_count);
            viewHolder.praiseRecyclerViewAdapter = new PraiseRecyclerViewAdapter(context,null);
            viewHolder.praiseRecyclerViewAdapter.setOnItemClickListener(viewHolder);
            //设置固定大小
            viewHolder.praiseListView.setHasFixedSize(true);
            //创建线性布局
            LinearLayoutManager mLayoutManagerPraise = new LinearLayoutManager(context);
            //垂直方向
            mLayoutManagerPraise.setOrientation(OrientationHelper.HORIZONTAL);
            //给RecyclerView设置布局管理器
            viewHolder.praiseListView.setLayoutManager(mLayoutManagerPraise);
            viewHolder.praiseListView.setAdapter(viewHolder.praiseRecyclerViewAdapter);


            //设置固定大小
            viewHolder.commentListView.setHasFixedSize(true);
            //创建线性布局
            FullLinearLayout mLayoutManagerComment = new FullLinearLayout(context,1);
//            //垂直方向
            mLayoutManagerComment.setOrientation(OrientationHelper.VERTICAL);
//            //给RecyclerView设置布局管理器
            viewHolder.commentListView.setLayoutManager(mLayoutManagerComment);

//            final FullyLinearLayoutManager manager = new FullyLinearLayoutManager(context);
//            manager.setOrientation(OrientationHelper.VERTICAL);
//            manager.setSmoothScrollbarEnabled(true);
//            viewHolder.commentListView.setLayoutManager(new FullLinearLayout(context,1));
            viewHolder.commentListView.setAdapter(viewHolder.commentAdapter);



            viewHolder.mViewComment.setOnClickListener(viewHolder);//评论
            viewHolder.mIvUserHead.setOnClickListener(viewHolder);
            viewHolder.mTvUserName.setOnClickListener(viewHolder);
            viewHolder.mTvDelete.setOnClickListener(viewHolder);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            resetViewHolder(viewHolder,position);
        }

        viewHolder.setPosition(position);




        //展示不同的类型
        showItemTypeData(itemViewType,circleMessage,viewHolder);


        try {
            String text  = URLDecoder.decode(URLDecoder.decode(circleMessage.getInfoText(), "UTF-8"),"UTF-8");
            //发表内容
            viewHolder.mTvContent.setText(StringUtils.ToDBC(text));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        User publishUser = circleMessage.getPublishUser();
        if(publishUser!=null) {
            viewHolder.mTvUserName.setText(publishUser.getUserName());
            String headUrl = publishUser.getIcon();
            Picasso.with(context).load(headUrl).resize(headWidth,headWidth).centerCrop().placeholder(R.drawable.default_useravatar).into(viewHolder.mIvUserHead);
//            Picasso.with(context).load(headUrl).placeholder(R.drawable.default_useravatar).into(viewHolder.mIvUserHead);
        }
        //发表时间
        viewHolder.mTvPublishTime.setText(DateUtils.getTimestampString(circleMessage.getCreateTime()));

        //如果是我的，显示删除动态按钮
        if(publishUser!=null && publishUser.getId() == myUser.getId()){
            viewHolder.mTvDelete.setVisibility(View.VISIBLE);
        }else{
            viewHolder.mTvDelete.setVisibility(View.GONE);
        }


        //点赞列表
        PraisedInfo praisedInfo = circleMessage.getPraisedInfo();
        int praiseInfoCount = praisedInfo.getPraiseNum();

        //回复列表
        CommentInfo commentInfo = circleMessage.getCommentInfo();
        int commentNum = commentInfo.getTotalNum();
        if(commentNum>0 || praiseInfoCount > 0) {
            viewHolder.digCommentBody.setVisibility(View.VISIBLE);
            //评论列表不为空
            if(commentNum>0) {
                viewHolder.mTvCommentCount.setText(String.valueOf(commentNum));
                final List<CommentUser> commentUsers = commentInfo.getCommentUsers();
                viewHolder.commentAdapter.setDatas(commentUsers);
//            viewHolder.commentAdapter.notifyDataSetChanged();
                calRecycleViewHeight(viewHolder.commentListView, commentNum);
                viewHolder.mTvCommentCount.setVisibility(View.VISIBLE);
                viewHolder.commentListView.setVisibility(View.VISIBLE);
                viewHolder.digCommentBody.setVisibility(View.VISIBLE);
                viewHolder.mViewPraiseCommentLine.setVisibility(View.VISIBLE);
            }else{
                viewHolder.mTvCommentCount.setVisibility(View.GONE);
                viewHolder.commentListView.setVisibility(View.GONE);
                viewHolder.mViewPraiseCommentLine.setVisibility(View.GONE);
            }
            //点赞列表不为空
            if(praiseInfoCount>0){
                viewHolder.mTvPraiseCount.setText(String.valueOf(praiseInfoCount));
                final List<PraiseUser> praiseUsers = praisedInfo.getPraiseUsers();
                viewHolder.praiseRecyclerViewAdapter.setDatas(praiseUsers);
//            viewHolder.praiseRecyclerViewAdapter.notifyDataSetChanged();
                viewHolder.praiseListView.setVisibility(View.VISIBLE);
                viewHolder.mTvPraiseCount.setVisibility(View.VISIBLE);
            }else{
                viewHolder.praiseListView.setVisibility(View.GONE);
                viewHolder.mTvPraiseCount.setVisibility(View.GONE);
                viewHolder.mViewPraiseCommentLine.setVisibility(View.GONE);
            }

        }else{
            viewHolder.digCommentBody.setVisibility(View.GONE);
            viewHolder.commentListView.setVisibility(View.GONE);
        }

        return convertView;
    }

//    /**
//     * 获取评论列表
//     * @param mDatas
//     * @return
//     */
//    private List<CommentUser> getCommentUsers(List<CommentUser> mDatas){
//        List<CommentUser> commentUserList = new ArrayList<CommentUser>();
//        commentUserList.addAll(mDatas);
//        int beforeSize = mDatas.size();
//        Log.e("1111111",+beforeSize+"  ");
//        for(int i = 0 ;i<beforeSize;i++){
//            List<CommentUser> commentUsers = getCommentUsers(mDatas.get(i));
//            commentUserList.addAll(commentUsers);
//        }
//
//        return mDatas;
//    }
//
//    private List<CommentUser> getCommentUsers(CommentUser commentUser){
//        List<CommentUser> commentUserList = new ArrayList<CommentUser>();
//        if(commentUser!=null && commentUser.getSubCommUsers()!=null){
//            List<CommentUser> subCommentUsers = commentUser.getSubCommUsers();
//            for(int i = 0;i<subCommentUsers.size();i++){
//                CommentUser subCommentUser = subCommentUsers.get(i);
//                subCommentUser.setReplyText(subCommentUser.getUserName()+"<font color=\"#F29c9F\">回复</font>"+commentUser.getUserName()+"：");
//                commentUserList.add(subCommentUser);
//            }
////            commentUserList.addAll(subCommentUsers);
//            for(CommentUser subCommentUser:subCommentUsers){
//                getCommentUsers(subCommentUser);
//            }
//        }
//        return commentUserList;
//    }

    private void calRecycleViewHeight(RecyclerView recyclerView,int size){
        ViewGroup.LayoutParams mParams = recyclerView.getLayoutParams();
        mParams.height = DeviceInfo.dp2px(context,42) * size+DeviceInfo.dp2px(context,1)+1;
        mParams.width = DeviceInfo.getScreenWidth(context);
        recyclerView.setLayoutParams(mParams);
    }

    private void showItemTypeData(int itemViewType, final CircleMessage circleMessage, final ViewHolder viewHolder){
        switch (itemViewType){
            case ITEM_VIEW_TYPE_IMAGES_AUDIO:
                //图片展示  start
                final List<Images> imagesLists = circleMessage.getImages();

                handlerOneImage(viewHolder, imagesLists.get(0));

                viewHolder.ivOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePagerActivity.imageSize = new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
                        ImagePagerActivity.startImagePagerActivity(context, imagesLists, 0);
                    }
                });

                break;
            case ITEM_VIEW_TYPE_TEXT:

                break;
            case ITEM_VIEW_TYPE_IMAGES:
                //图片展示  start
                final List<Images> imagesList = circleMessage.getImages();
                if (imagesList==null || imagesList.isEmpty()) {
                    viewHolder.ivMore.setVisibility(View.GONE);
                    viewHolder.ivOne.setVisibility(View.GONE);
                } else if (imagesList.size() == 1) {
                    viewHolder.ivMore.setVisibility(View.GONE);
                    viewHolder.ivOne.setVisibility(View.VISIBLE);

                    handlerOneImage(viewHolder, imagesList.get(0));
                } else {
                    viewHolder.ivMore.setVisibility(View.VISIBLE);
                    viewHolder.ivOne.setVisibility(View.GONE);

                    viewHolder.ivMore.setImagesData(imagesList);
                    viewHolder.ivMore.setOnItemClickListener(new NineGridlayout.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            ImagePagerActivity.imageSize = new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
                            ImagePagerActivity.startImagePagerActivity(context, imagesList, position);
                        }
                    });
//            viewHolder.ivMore.setOnClickListener();
                }
                //图片展示  end

                //播放图片中的语音 start
                final List<Audio> audiosImages = circleMessage.getAudios();
                if(audiosImages!=null && !audiosImages.isEmpty()){
                    viewHolder.mIvPlayVoiceImages.setVisibility(View.VISIBLE);
                    viewHolder.mIvPlayVoiceImages.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(circleMessage.getAudios()!=null && !circleMessage.getAudios().isEmpty()) {
                                downloadMedia(audiosImages.get(0).getPath(), ReqUrls.MEDIA_TYPE_AUDIO,viewHolder);
                            }
                        }
                    });
                }else{
                    viewHolder.mIvPlayVoiceImages.setVisibility(View.GONE);
                }
                //播放图片中的语音 end

                break;
            case ITEM_VIEW_TYPE_AUDIO://语音
                List<Audio> audios = circleMessage.getAudios();
                if(audios!=null && !audios.isEmpty()) {
                    String audioTime = StringUtils.isEmpty(audios.get(0).getAudioTime())?"0":audios.get(0).getAudioTime();
                    viewHolder.mTvAudioSecond.setText(audioTime+"''");
                    float audioLength = 0;
                    try{
                        audioLength = Float.valueOf(audioTime);
                        if(audioLength<15){
                            audioLength = 15;
                        }else if(audioLength>=15&&audioLength<30){
                            audioLength = 30;
                        }else if(audioLength>=30 && audioLength<45){
                            audioLength = 45;
                        }else if(audioLength>=45){
                            audioLength = 60;
                        }
                        RelativeLayout.LayoutParams paramsAudio = (RelativeLayout.LayoutParams) viewHolder.mIvAudio.getLayoutParams();
                        int width = (int) (mMinItemWidth + (mMaxItemWidth / 50f)* audioLength);
                        if(width>mMaxItemWidth){
                            width = mMaxItemWidth;
                        }
                        paramsAudio.width = width;
                        paramsAudio.setMargins(DeviceInfo.dp2px(context, 11), 0, 0, 0);
//  audioLength = (20-audioLength)*40;
////                        if(audioLength<DeviceInfo.getScreenWidth(context)-DeviceInfo.dp2px(context,70)){
////                            paramsAudio.width=DeviceInfo.dp2px(context,audioLength*40);
//                        if(audioLength>0&&audioLength<DeviceInfo.getScreenWidth(context)-DeviceInfo.dp2px(context,70)) {
////                        int marginRight = DeviceInfo.getScreenWidth(context)-DeviceInfo.dp2px(context,70)-(int)audioLength*10;
//                            paramsAudio.setMargins(DeviceInfo.dp2px(context, 11), 0, (int) audioLength, 0);
//                        }else{
//                            paramsAudio.setMargins(DeviceInfo.dp2px(context, 11), 0, DeviceInfo.dp2px(context,70), 0);
//                        }
//
//                        viewHolder.mIvAudio.setLayoutParams(paramsAudio);
////                            paramsAudio.height = DeviceInfo.dp2px(context,50);
////                            viewHolder.mViewAudio.setLayoutParams(paramsAudio);
////                        }
                    }catch (Exception e){
                        audioLength = 20;
                        RelativeLayout.LayoutParams paramsAudio = (RelativeLayout.LayoutParams) viewHolder.mIvAudio.getLayoutParams();
                        paramsAudio.width = (int) (mMinItemWidth + (mMaxItemWidth / 40f)* audioLength);
                        paramsAudio.setMargins(DeviceInfo.dp2px(context, 11), 0, 0, 0);
                    }

                }

//                if(animationDrawable!=null && animationDrawable.isRunning()){
//                    animationDrawable.stop();
//                    viewHolder.mViewAnim
//                            .setBackgroundResource(R.drawable.icon_voice_anim_3);
//                }
                if(viewHolder.position == playingAudioIndex){
                    startAnimAudio(viewHolder);
                }else{
                    stopAnimAudio(viewHolder);
                }
                viewHolder.mIvAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopCurrentAnimAudio();
                        if(viewHolder.position == playingAudioIndex){//正在播放的再次点击暂停音频
                            MediaManager.pause();
                            playingAudioIndex = -1;
//                            stopAnimAudio(viewHolder);
                            return;
                        }
                        if(circleMessage.getAudios()!=null && !circleMessage.getAudios().isEmpty()) {
                            downloadMedia(circleMessage.getAudios().get(0).getPath(), ReqUrls.MEDIA_TYPE_AUDIO,viewHolder);
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
                viewHolder.mPlayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(circleMessage.getVideos()!=null && !circleMessage.getVideos().isEmpty()) {
//                            playerVideo(circleMessage.getVideos().get(0).getPath(),viewHolder);
                            downloadMedia(circleMessage.getVideos().get(0).getPath(), ReqUrls.MEDIA_TYPE_VIDEO,viewHolder);
                        }
                    }
                });

                Video video = circleMessage.getVideos().get(0);
                String url = video.getPath();
                String downLoadPath =  FileUtil.createFile(FileUtil.DOWNLOAD_MEDIA_FILE_DIR);
                String fileName = url.substring(url.lastIndexOf("/")+1);
                Picasso.with(context).load(video.getIcon()).error(new ColorDrawable(Color.BLACK)).into(viewHolder.mThumbnailImageView);
//                viewHolder.mThumbnailImageView.setImageBitmap(getVideoThumbnail(downLoadPath+"/"+fileName));
                try {
                    viewHolder.mScalableVideoView.setDataSource(url);
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                break;
        }
    }

    private void handlerOneImage(ViewHolder viewHolder, final Images image) {
        int totalWidth;
        int imageWidth;
        int imageHeight;
//        ScreenTools screentools = ScreenTools.instance(context);
//        totalWidth = screentools.getScreenWidth() - screentools.dip2px(100);
//        imageWidth = screentools.dip2px(image.getWidth());
//        imageWidth = totalWidth;
//        imageHeight = screentools.dip2px(image.getHeight());
//        if (image.getWidth() <= image.getHeight()) {
//            if (imageHeight > totalWidth) {
//                imageHeight = totalWidth;
//                imageWidth = (imageHeight * image.getWidth()) / image.getHeight();
//            }
//        } else {
//            if (imageWidth > totalWidth) {
//                imageWidth = totalWidth;
//                imageHeight = (imageWidth * image.getHeight()) / image.getWidth();
//            }
//        }
//        ViewGroup.LayoutParams layoutparams = viewHolder.ivOne.getLayoutParams();
//        layoutparams.height = imageHeight;
//        layoutparams.width = imageWidth;
//        viewHolder.ivOne.setLayoutParams(layoutparams);
        viewHolder.ivOne.setClickable(true);
//        viewHolder.ivOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.ivOne.setImageUrl(image.getImgPath());
        viewHolder.ivOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 因为单张图片时，图片实际大小是自适应的，imageLoader缓存时是按测量尺寸缓存的
                ImagePagerActivity.imageSize = new int[]{v.getMeasuredWidth(), v.getMeasuredHeight()};
                List<Images> urls = new ArrayList<Images>();
                urls.add(image);
                ImagePagerActivity.startImagePagerActivity(context, urls, 0);
            }
        });


    }

    private void resetViewHolder(ViewHolder viewHolder,int position){

        if(datalist.get(position).isPlay()){
            if(viewHolder.mThumbnailImageView!=null){
                viewHolder.mThumbnailImageView.setVisibility(View.GONE);

            }
            if(viewHolder.mPlayImageView!=null){
                viewHolder.mPlayImageView.setVisibility(View.GONE);
            }
            if(viewHolder.mPlayImageView!=null){
                viewHolder.mPlayImageView.setVisibility(View.VISIBLE);
            }
           //playerVideo(datalist.get(position).getVideos().get(0).getPath(),viewHolder);
        }else{
            if(viewHolder.mScalableVideoView!=null){
                try {
                    viewHolder.mScalableVideoView.setDataSource(datalist.get(position).getVideos().get(0).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(viewHolder.mThumbnailImageView!=null){
                viewHolder.mThumbnailImageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(datalist.get(position).getVideos().get(0).getIcon()).error(new ColorDrawable(Color.BLACK)).into(viewHolder.mThumbnailImageView);

            }
            if(viewHolder.mPlayImageView!=null){
                viewHolder.mPlayImageView.setVisibility(View.VISIBLE);
            }


        }
    }


    public class ViewHolder implements View.OnClickListener,CommentRecyclerViewAdapter.OnItemClickListener,PraiseRecyclerViewAdapter.OnItemClickListener {
        public View mViewComment;//萌化了
        //图片类型 start
        public NineGridlayout ivMore;//图片九宫格
        public CustomSingleImageView ivOne;//单张图片
        public ImageView mIvPlayVoiceImages;//图片中的语音
        //图片类型 end

        //视频类型 start
        public ScalableVideoView mScalableVideoView;
        public ImageView mPlayImageView;//
        public ImageView mThumbnailImageView;//缩略图
        public ProgressBar mProgressBar;
        //视频类型 end

        //音频类型 start
        public View mViewAudio;//语音背景
        public ImageView mIvAudio;
        public View mViewAnim;//语音动画
        public AnimationDrawable animationDrawable;
        public TextView mTvAudioSecond;//时长

        //音频类型 end

        //图片+语音 start
        public ImageView mPlayVoice;
        //图片+语音 end

        public ImageView mIvUserHead;//头像
        public TextView mTvUserName;//昵称
        public TextView mTvContent;//评论内容
        public TextView mTvPublishTime;//发表时间

        public TextView mTvDelete;//删除动态

        public View digCommentBody;
        //评论列表控件
//        public CommentListView commentListView;
        public RecyclerView commentListView;
        public TextView mTvCommentCount;//评论总数
        //评论列表适配器
//        public CommentAdapter commentAdapter;

        public CommentRecyclerViewAdapter commentAdapter;
        public View mViewPraiseCommentLine;

        //点赞列表
        public RecyclerView praiseListView;
        public TextView mTvPraiseCount;
        public PraiseRecyclerViewAdapter praiseRecyclerViewAdapter;



        private int position;

        public void setPosition(int position){
            this.position = position;
        }

        public int getPosition(){
            return position;
        }
        @Override
        public void onClick(View v) {
            if(CommonHelper.isFastClick()){
                return;
            }
            switch (v.getId()){
                case R.id.tv_delete://删除动态
                    showDeleteCircleWindow(position);
                    break;
                case R.id.iv_comment://评论
                    popup(v,position,this);
                    break;
                case R.id.iv_user_head://头像
                    startUserInfoActivity(datalist.get(position).getPublishUser());
                    break;
                case R.id.tv_user_name://昵称
                    startUserInfoActivity(datalist.get(position).getPublishUser());
                    break;
                case R.id.video_view://点击进入视频详情
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra(VideoDetailActivity.KEY_FILE_PATH,getItem(position).getVideos().get(0).getPath());
                    intent.putExtra("firstPicture","");
                    context.startActivity(intent);
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int commentPosition) {
            CircleMessage circleMessage = getItem(position);
            //当前的评论
//            List<CommentUser> commentUsers = circleMessage.getCommentInfo().getCommentUsers();
//            CommentUser commentUser = getCommentUsers(commentUsers).get(commentPosition);
            CommentUser commentUser = circleMessage.getCommentInfo().getCommentUsers().get(commentPosition);

//            if(commentUser.getUserId().equals(circleMessage.getPublishUser().getUserId())){//自己评论自己的
//
//            }else{
            if(circleMessage.getPublishUser().getUserId().equals(commentUser.getUserId()) //回复楼主
                    || commentUser.getUserId().equals(MyApplication.getInstance().getCurrentUser().getUserId())){//回复自己
                CommentConfig commentConfig = new CommentConfig();
                commentConfig.circlePosition = position;
                commentConfig.commentPosition = commentPosition;
                commentConfig.commentType = CommentConfig.Type.PUBLIC;
                commentConfig.replyUser = commentUser;
                homeFragment.updateEditTextBodyVisible(View.VISIBLE,commentConfig);
            }else {//回复其他人
                CommentConfig commentConfig = new CommentConfig();
                commentConfig.circlePosition = position;
                commentConfig.commentPosition = commentPosition;
                commentConfig.commentType = CommentConfig.Type.REPLY;
                commentConfig.replyUser = commentUser;

                homeFragment.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
            }
//            }
        }

        @Override
        public void onItemLongClick(View view, int commentPosition) {
            CircleMessage circleMessage = getItem(position);
            //当前的评论
            CommentUser commentUser = circleMessage.getCommentInfo().getCommentUsers().get(commentPosition);
            User user = MyApplication.getInstance().getCurrentUser();
            if(commentUser.getPhoneNum().equals(user.getPhoneNum()) && commentUser.getType()!=2) {//自己评论的 并且不能删除萌化了操作
                showDeleteWindow(position, commentPosition, this);
            }

        }

        @Override
        public void onItemPraiseClick(View view, int praisePosition) {
            PraiseUser praiseUser = datalist.get(position).getPraisedInfo().getPraiseUsers().get(praisePosition);
            User user = new User();
            user.setIcon(praiseUser.getIcon());
            user.setUserId(praiseUser.getUserId());
            user.setUserName(praiseUser.getUserName());
            user.setPhoneNum(praiseUser.getPhoneNum());
            startUserInfoActivity(user);
        }

        @Override
        public void onItemPraiseLongClick(View view, int position) {

        }

//        @Override
//        public void onItemClick(int commentPosition) {
//            CircleMessage circleMessage = getItem(position);
//            //当前的评论
//            CommentUser commentUser = circleMessage.getCommentInfo().getCommentUsers().get(commentPosition);
//
//            if(commentUser.getUserId().equals(circleMessage.getPublishUser().getUserId())){//自己评论自己的
//
//            }else{
//                CommentConfig commentConfig = new CommentConfig();
//                commentConfig.circlePosition = position;
//                commentConfig.commentPosition = commentPosition;
//                commentConfig.commentType = CommentConfig.Type.REPLY;
//                commentConfig.replyUser = commentUser;
//
//                homeFragment.updateEditTextBodyVisible(View.VISIBLE,commentConfig);
//            }
//        }
    }

    //进入个人主页
    private void startUserInfoActivity(User user){
        Intent intent = new Intent(context,UserHomeActivity.class);
//        User user = datalist.get(position).getPublishUser();
        user.setFlag(1);//已接受好友
        intent.putExtra("user",user);
        context.startActivity(intent);
    }

    private TitlePopup titlePopup;
    /**
     * 弹出评论,赞菜单加载
     */
    private void popup(View view,int position,ViewHolder viewHolder) {


        titlePopup = new TitlePopup(context, DeviceInfo.dp2px(context,265),DeviceInfo.dp2px(context,36));
        PraisedInfo praisedInfo = getItem(position).getPraisedInfo();
//        boolean isPraise = false;//自己是否赞过
        String praiseText = "赞";
        int praiseIndex = 0;//当前点赞位置
        if(praisedInfo.getPraiseNum()>0 ){
            List<PraiseUser> praiseUsers = praisedInfo.getPraiseUsers();
            User user = MyApplication.getInstance().getCurrentUser();
            for (int i = 0;i<praiseUsers.size();i++) {
                if(praiseUsers.get(i).getPhoneNum().equals(user.getPhoneNum())){
//                    isPraise = true;
                    praiseIndex = i;
                    praiseText = "取消";
                    break;
                }
            }
        }
        titlePopup.addAction(new ActionItem(context, praiseText, R.drawable.circle_praise,praiseIndex));
        titlePopup.addAction(new ActionItem(context, "萌化啦~",R.drawable.btn_comment_meng,praiseIndex));
        titlePopup.addAction(new ActionItem(context, "评论",R.drawable.circle_comment,praiseIndex));

        titlePopup.setItemOnClickListener(onItemOnClickListener);
        onItemOnClickListener.setParentPosition(position);
        onItemOnClickListener.setViewHolder(viewHolder);

        titlePopup.setAnimationStyle(R.style.social_pop_anim);
        titlePopup.show(view);

    }

    public TitlePopup.OnItemOnClickListener onItemOnClickListener = new TitlePopup.OnItemOnClickListener(){

        private int positionParent;//当前评论id
        public ViewHolder viewHolder;
        @Override
        public void onItemClick(ActionItem item, int position) {
            switch (position){
                case  0:
                    if(item.mTitle.equals("赞")) {
                        praise(positionParent,item.position,viewHolder);
                    }else{//取消赞
                        cancelPraise(positionParent,item.position,viewHolder);
                    }
                    break;
                case 1:
                    addLoveSth(positionParent);
                    break;
                case 2://直接回复楼主的评论
//                    String content = "评论"+positionParent;
//                    ToastUtils.showToast(context,"评论 "+positionParent);
//                    addComment(positionParent,content);
                    CommentConfig commentConfig = new CommentConfig();
                    commentConfig.circlePosition = positionParent;
                    commentConfig.commentType = CommentConfig.Type.PUBLIC;
                    homeFragment.updateEditTextBodyVisible(View.VISIBLE,commentConfig);
                    break;
            }
        }

        @Override
        public void setParentPosition(int parentPosition) {
            this.positionParent = parentPosition;
        }

        @Override
        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }


    };

    private void showDeleteCircleWindow(final int circlePosition){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 设置图标
//        builder.setTitle("确定删除？");

//        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        }).setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
        final AlertDialog alertDialog = builder.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_delete);
        window.findViewById(R.id.umeng_update_id_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        window.findViewById(R.id.umeng_update_id_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCircleMessage(circlePosition);
                alertDialog.cancel();
            }
        });

    }
    /**
     * 删除动态
     * @param circlePosition
     */
    private void deleteCircleMessage(final int circlePosition){
        String infoId = String.valueOf(datalist.get(circlePosition).getInfoId());
        ApiMomentsUtils.deleteCircleMessage(context, infoId, myUser.getPhoneNum(), new HttpConnectionUtil.RequestCallback() {
            @Override
            public void execute(ParseModel parseModel) {
                if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
                    datalist.remove(circlePosition);
                    notifyDataSetChanged();
                }else{
                    ToastUtils.showToast(context,parseModel.getInfo());
                }
            }
        });
    }

    /**
     * 点赞
     */
    private void praise(final int circlePosition, final int praisePosition, final ViewHolder viewHolder){
        final User user = MyApplication.getInstance().getCurrentUser();
        if(user==null){
            ToastUtils.showToast(context,"请先登录");
            return;
        }
        String infoId = String.valueOf(datalist.get(circlePosition).getInfoId());
        String ownerPhoneNum = datalist.get(circlePosition).getPublishUser().getPhoneNum();
        ApiMomentsUtils.praise(context, infoId,user.getPhoneNum(),ownerPhoneNum, new HttpConnectionUtil.RequestCallback() {
            @Override
            public void execute(ParseModel parseModel) {
                if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
//                    ToastUtils.showToast(context,parseModel.getInfo());
                   notifyPraiseData(1,viewHolder,circlePosition,praisePosition);
                }else{
                    ToastUtils.showToast(context,parseModel.getInfo());
                }
            }
        });
    }

    /**
     * 取消点赞
     * @param circlePostion 当前列表位置
     * @param praisePosition 取消赞位置
     * @param viewHolder
     */
    private void cancelPraise(final int circlePostion,final int praisePosition,final ViewHolder viewHolder){
        User user = MyApplication.getInstance().getCurrentUser();
        if(user==null){
            ToastUtils.showToast(context,"请先登录");
            return;
        }
        String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_CANCEL_PRAISE;
        String infoId = String.valueOf(datalist.get(circlePostion).getInfoId());
        OkHttpUtils//
                .get()//
                .addHeader("JSESSIONID",GlobalConfig.JSESSION_ID)
                .addParams("infoId",infoId)
                .addParams("phoneNum",user.getPhoneNum())
                .url(url)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtils.showToast(context,"取消点赞失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {
                                notifyPraiseData(0, viewHolder, circlePostion, praisePosition);
                            }else{
                                ToastUtils.showToast(context,jsonObject.getString("info"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(context,"取消点赞失败,稍后再试");
                        }

                    }
                });
    }

    /**
     * 弹出删除评论对话框
     * @param circlePosition
     * @param commentPosition
     * @param viewHolder
     */
    private void showDeleteWindow(final int circlePosition,final int commentPosition,final ViewHolder viewHolder){
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alertdialog);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("删除");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                deleteComment(circlePosition,commentPosition,viewHolder);
                dlg.cancel();
            }
        });
        window.findViewById(R.id.ll_content2).setVisibility(View.GONE);


    }
    /**
     * 删除评论
     * @param circlePostion 动态位置
     * @param commentPosition 评论位置
     * @param viewHolder
     */
    private void deleteComment(final int circlePostion,final int commentPosition,final ViewHolder viewHolder){
        User user = MyApplication.getInstance().getCurrentUser();
        if(user==null){
            ToastUtils.showToast(context,"请先登录");
            return;
        }
        String url = ReqUrls.DEFAULT_REQ_HOST_IP + ReqUrls.REQUEST_DELETE_COMMENT;
        CircleMessage circleMessage = getItem(circlePostion);
        final CommentInfo commentInfo = circleMessage.getCommentInfo();
        final String commentId = String.valueOf(commentInfo.getCommentUsers().get(commentPosition).getCommentId());
        OkHttpUtils//
                .get()//
                .addHeader("JSESSIONID",GlobalConfig.JSESSION_ID)
                .addParams("commentId",commentId)
                .addParams("phoneNum",user.getPhoneNum())
                .url(url)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtils.showToast(context,"删除评论失败");
                    }

                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if(ApiConstants.RESULT_SUCCESS.equals(jsonObject.getString("retFlag"))) {
                                commentInfo.getCommentUsers().remove(commentPosition);
                                commentInfo.setTotalNum(commentInfo.getTotalNum()-1<0?0:commentInfo.getTotalNum()-1);
                                notifyDataSetChanged();
                            }else{
                                ToastUtils.showToast(context,jsonObject.getString("info"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(context,"删除评论失败");
                        }

                    }
                });
    }


    /**
     * //刷新点赞列表
     * @param type 0 取消赞  1点赞
     * @param viewholder
     * @param circlePosition  当前列表位置
     */
    private void notifyPraiseData(int type,ViewHolder viewholder,int circlePosition,int praisePosition){

        CircleMessage circleMessage = getItem(circlePosition);

        PraisedInfo praisedInfo = circleMessage.getPraisedInfo();
        int count = praisedInfo.getPraiseNum();
        if(type == 0){//取消赞
            if(count >0){
                count --;
            }
            praisedInfo.getPraiseUsers().remove(praisePosition);
//            viewholder.praiseRecyclerViewAdapter.remove(praisePosition);
        }else{//点赞
            count ++;
            User user = MyApplication.getInstance().getCurrentUser();
            PraiseUser praiseUser = new PraiseUser();
            praiseUser.setIcon(user.getIcon());
            praiseUser.setPhoneNum(user.getPhoneNum());
            praiseUser.setUserName(user.getUserName());
            praisedInfo.getPraiseUsers().add(praiseUser);
//            viewholder.praiseRecyclerViewAdapter.add(0,praiseUser);
        }
        praisedInfo.setPraiseNum(count);
        viewholder.mTvCommentCount.setText(String.valueOf(count));
        notifyDataSetChanged();
//        homeFragment.updateView(circlePosition,""+count);

    }



    /**
     * 萌化啦
     * @param position
     */
    private void addLoveSth(final int position){
        final User user = MyApplication.getInstance().getCurrentUser();
        if(user==null){
            ToastUtils.showToast(context,"请先登录");
            return;
        }
        String infoId = String.valueOf(datalist.get(position).getInfoId());
        String ownerPhoneNum = datalist.get(position).getPublishUser().getPhoneNum();
        ApiMomentsUtils.addLoveSth(context,infoId,"2",user.getPhoneNum(),ownerPhoneNum,new HttpConnectionUtil.RequestCallback(){

            @Override
            public void execute(ParseModel parseModel) {
                if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
                    String info = parseModel.getInfo();
                    if(!"不能重复萌化".equals(info)) {

                        CommentUser commentUser = new CommentUser();
                        commentUser.setUserName(user.getUserName());
                        commentUser.setText("萌化了~");
                        commentUser.setIcon(user.getIcon());
                        commentUser.setPhoneNum(user.getPhoneNum());
                        commentUser.setType(2);
                        commentUser.setUserId(user.getUserId());
                        commentUser.setPublishTime(System.currentTimeMillis());

                        CommentInfo commentInfo = getItem(position).getCommentInfo();
                        commentInfo.getCommentUsers().add(commentUser);
                        commentInfo.setTotalNum(commentInfo.getTotalNum() + 1);
                        notifyDataSetChanged();
                    }else{
                        ToastUtils.showToast(context,parseModel.getInfo());
                    }
                }else{
                    ToastUtils.showToast(context,parseModel.getInfo());
                }
            }
        });
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
//                .execute(new com.anda.moments.callBack.FileCallBack(downLoadPath+"/", fileName,url) {
//                    @Override
//                    public void inProgress(float progress, long total) {
//                        if(type == ReqUrls.MEDIA_TYPE_VIDEO) {
//                            int progressInt = (int) (100 * progress);
//                            viewHolder.mProgressBar.setProgress(progressInt);
//                            Log.e(TAG,progressInt+"  total "+total);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String filePath) {
//                        switch (type){
//                            case  ReqUrls.MEDIA_TYPE_AUDIO://音频
//                                playAudioRecord(filePath,viewHolder);
//                                break;
//                            case  ReqUrls.MEDIA_TYPE_VIDEO://视频
//                                playerVideo(filePath,viewHolder);
//                                break;
//                        }
//                    }
//                });
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
        if(viewHolder.animationDrawable!=null) {
//        viewHolder.mViewAnim.setBackgroundResource(R.drawable.anim_play_audio);
            if (viewHolder.animationDrawable != null && viewHolder.animationDrawable.isRunning()) {
                viewHolder.animationDrawable.selectDrawable(2);
                viewHolder.animationDrawable.stop();
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
//                        viewHolder.mViewAnim
//                                .setBackgroundResource(R.drawable.icon_voice_anim_3);
                        }
                    });
        }else{
            MediaManager.playSound(filePath,
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playingAudioIndex = -1;//音频播放停止
                        }
                    });
        }
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

    private void playerVideo(String filePath,final  ViewHolder viewHolder){

        try {
            viewHolder.mScalableVideoView.setDataSource(filePath);
            viewHolder.mScalableVideoView.setVolume(0,0);
            viewHolder.mScalableVideoView.setLooping(true);
            viewHolder.mScalableVideoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    datalist.get(viewHolder.position).setPlay(true);
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

}
