package com.anda.moments.apdater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiMomentsUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.entity.CircleMessage;
import com.anda.moments.entity.CommentConfig;
import com.anda.moments.entity.CommentInfo;
import com.anda.moments.entity.CommentUser;
import com.anda.moments.entity.Image;
import com.anda.moments.entity.Images;
import com.anda.moments.entity.Media;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.ImagePagerActivity;
import com.anda.moments.ui.UserHomeActivity;
import com.anda.moments.ui.UserInfoActivity;
import com.anda.moments.ui.fragments.HomeFragment;
import com.anda.moments.utils.CommonHelper;
import com.anda.moments.utils.DateUtil;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.Log;
import com.anda.moments.utils.ScreenTools;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.CommentListView;
import com.anda.moments.views.CustomImageView;
import com.anda.moments.views.CustomSingleImageView;
import com.anda.moments.views.MultiImageView;
import com.anda.moments.views.NineGridlayout;
import com.anda.moments.views.audio.MediaManager;
import com.anda.moments.views.popup.ActionItem;
import com.anda.moments.views.popup.TitlePopup;
//import com.squareup.okhttp.Call;
import com.squareup.picasso.Picasso;
import com.yqritc.scalablevideoview.ScalableVideoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    private static final int ITEM_VIEW_TYPE_COUNT = 4;


    private Context context;
    private List<CircleMessage> datalist;

//    private int headWidth= 50;
    public HomeAdapter(Context context, List<CircleMessage> datalist) {
        this.context = context;
        this.datalist = datalist;
//        headWidth = DeviceInfo.dp2px(context,50);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.home_item_ninegridlayout, parent, false);
            ViewStub mediaViewStub = (ViewStub) convertView.findViewById(R.id.media_view_stub);
            switch (itemViewType){
                case ITEM_VIEW_TYPE_TEXT:
//                    mediaViewStub.setLayoutResource(R.layout.);
                    break;
                case ITEM_VIEW_TYPE_IMAGES://图片
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_images);
                    mediaViewStub.inflate();

                    viewHolder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
                    viewHolder.ivOne = (CustomSingleImageView) convertView.findViewById(R.id.iv_oneimage);
                    break;
                case ITEM_VIEW_TYPE_AUDIO://音频
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_audios);
                    mediaViewStub.inflate();

                    viewHolder.mViewAudio = convertView.findViewById(R.id.view_record);
                    viewHolder.mViewAnim = convertView.findViewById(R.id.voice_anim);
                    viewHolder.mTvAudioSecond = (TextView)convertView.findViewById(R.id.tv_audio_second);


                    break;
                case ITEM_VIEW_TYPE_VIDEO://视频
                    mediaViewStub.setLayoutResource(R.layout.home_view_stub_videos);
                    mediaViewStub.inflate();

                    viewHolder.mScalableVideoView = (ScalableVideoView)convertView.findViewById(R.id.video_view);
                    viewHolder.mPlayImageView = (ImageView)convertView.findViewById(R.id.playImageView);
                    viewHolder.mThumbnailImageView = (ImageView)convertView.findViewById(R.id.thumbnailImageView);
                    viewHolder.mProgressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

                    try {
                        viewHolder.mScalableVideoView.setDataSource("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;
            }



            viewHolder.mIvUserHead = (ImageView)convertView.findViewById(R.id.iv_user_head);
            viewHolder.mTvUserName = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.mTvContent = (TextView)convertView.findViewById(R.id.tv_content);
            viewHolder.mTvPublishTime = (TextView)convertView.findViewById(R.id.tv_create_time);

            viewHolder.mViewComment = convertView.findViewById(R.id.iv_comment);//评论弹出框

            viewHolder.digCommentBody = (LinearLayout) convertView.findViewById(R.id.digCommentBody);
            //评论列表
            viewHolder.commentListView = (CommentListView)convertView.findViewById(R.id.commentList);
            viewHolder.commentAdapter = new CommentAdapter(context);
            viewHolder.commentListView.setAdapter(viewHolder.commentAdapter);





            viewHolder.mViewComment.setOnClickListener(viewHolder);
            viewHolder.mIvUserHead.setOnClickListener(viewHolder);
            viewHolder.mTvUserName.setOnClickListener(viewHolder);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            resetViewHolder(viewHolder,position);
        }

        viewHolder.setPosition(position);


        //展示不同的类型
        showItemTypeData(itemViewType,circleMessage,viewHolder);


        //发表内容
        viewHolder.mTvContent.setText(StringUtils.ToDBC(circleMessage.getInfoText()));
        User publishUser = circleMessage.getPublishUser();
        if(publishUser!=null) {
            viewHolder.mTvUserName.setText(publishUser.getUserName());
            String headUrl = publishUser.getIcon();
//            Picasso.with(context).load(headUrl).resize(headWidth,headWidth).placeholder(R.drawable.default_useravatar).into(viewHolder.mIvUserHead);
            Picasso.with(context).load(headUrl).placeholder(R.drawable.default_useravatar).into(viewHolder.mIvUserHead);
        }
        //发表时间
        viewHolder.mTvPublishTime.setText(DateUtils.getTimestampString(circleMessage.getCreateTime()));


        //回复列表
        CommentInfo commentInfo = circleMessage.getCommentInfo();
        int commentNum = commentInfo.getCommentNum();
        if(commentNum>0) {
            final List<CommentUser> commentUsers = commentInfo.getCommentUsers();
            viewHolder.commentAdapter.setDatas(commentUsers);
            viewHolder.commentAdapter.notifyDataSetChanged();
            viewHolder.commentListView.setVisibility(View.VISIBLE);
            viewHolder.digCommentBody.setVisibility(View.VISIBLE);

            viewHolder.commentListView.setOnItemClick(new CommentListView.OnItemClickListener() {
                @Override
                public void onItemClick(int commentPosition) {
                    //当前的评论
                    CommentUser commentUser = commentUsers.get(commentPosition);

                    if(commentUser.getUserId().equals(circleMessage.getPublishUser().getUserId())){//自己评论自己的

                    }else{
                        CommentConfig commentConfig = new CommentConfig();
                        commentConfig.circlePosition = position;
                        commentConfig.commentPosition = commentPosition;
                        commentConfig.commentType = CommentConfig.Type.REPLY;
                        commentConfig.replyUser = commentUser;

                        homeFragment.updateEditTextBodyVisible(View.VISIBLE,commentConfig);
                    }

                }
            });
        }else{
            viewHolder.digCommentBody.setVisibility(View.GONE);
            viewHolder.commentListView.setVisibility(View.GONE);
        }


        return convertView;
    }

    private void showItemTypeData(int itemViewType, final CircleMessage circleMessage, final ViewHolder viewHolder){
        switch (itemViewType){
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
                break;
            case ITEM_VIEW_TYPE_AUDIO://语音

                viewHolder.mViewAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
           //playerVideo(datalist.get(position).getVideos().get(0).getPath(),viewHolder);
        }else{
            if(viewHolder.mThumbnailImageView!=null){
                viewHolder.mThumbnailImageView.setVisibility(View.VISIBLE);

            }
            if(viewHolder.mPlayImageView!=null){
                viewHolder.mPlayImageView.setVisibility(View.VISIBLE);
            }
//            if(viewHolder.mScalableVideoView!=null){
//                try {
//                    viewHolder.mScalableVideoView.setDataSource("");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }


    class ViewHolder implements View.OnClickListener{
        public View mViewComment;//萌化了
        //图片类型 start
        public NineGridlayout ivMore;//图片九宫格
        public CustomSingleImageView ivOne;//单张图片
        //图片类型 end

        //视频类型 start
        public ScalableVideoView mScalableVideoView;
        public ImageView mPlayImageView;//
        public ImageView mThumbnailImageView;//缩略图
        public ProgressBar mProgressBar;
        //视频类型 end

        //音频类型 start
        public View mViewAudio;//语音背景
        public View mViewAnim;//语音动画
        public TextView mTvAudioSecond;//时长

        //音频类型 end

        public ImageView mIvUserHead;//头像
        public TextView mTvUserName;//昵称
        public TextView mTvContent;//评论内容
        public TextView mTvPublishTime;//发表时间

        public LinearLayout digCommentBody;
        //评论列表控件
        public CommentListView commentListView;
        //评论列表适配器
        public CommentAdapter commentAdapter;

        private int position;

        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_comment://评论
                    popup(v,position);
                    break;
                case R.id.iv_user_head://头像
                    startUserInfoActivity(position);
                    break;
                case R.id.tv_user_name://昵称
                    startUserInfoActivity(position);
                    break;
            }
        }
    }

    //点击头像进入个人主页
    private void startUserInfoActivity(int position){
        Intent intent = new Intent(context,UserHomeActivity.class);
        User user = datalist.get(position).getPublishUser();
        user.setFlag(1);//已接受好友
        intent.putExtra("user",user);
        context.startActivity(intent);
    }

    private TitlePopup titlePopup;
    /**
     * 弹出评论,赞菜单加载
     */
    private void popup(View view,int position) {


        titlePopup = new TitlePopup(context, DeviceInfo.dp2px(context,265),DeviceInfo.dp2px(context,36));
        titlePopup.addAction(new ActionItem(context, "赞", R.drawable.circle_praise));
        titlePopup.addAction(new ActionItem(context, "萌化啦~",R.drawable.btn_comment_meng));
        titlePopup.addAction(new ActionItem(context, "评论",R.drawable.circle_comment));

        titlePopup.setItemOnClickListener(onItemOnClickListener);
        onItemOnClickListener.setParentPosition(position);

        titlePopup.setAnimationStyle(R.style.social_pop_anim);
        titlePopup.show(view);

    }

    public TitlePopup.OnItemOnClickListener onItemOnClickListener = new TitlePopup.OnItemOnClickListener(){

        private int positionParent;
        @Override
        public void onItemClick(ActionItem item, int position) {
            switch (position){
                case  0:
                    praise(positionParent);
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
    };


    /**
     * 点赞
     */
    private void praise(int position){
        User user = MyApplication.getInstance().getCurrentUser();
        if(user==null){
            ToastUtils.showToast(context,"请先登录");
            return;
        }
        String infoId = String.valueOf(datalist.get(position).getInfoId());
        ApiMomentsUtils.praise(context, infoId,user.getPhoneNum(), new HttpConnectionUtil.RequestCallback() {
            @Override
            public void execute(ParseModel parseModel) {
                if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
                    ToastUtils.showToast(context,parseModel.getInfo());
                }else{
                    ToastUtils.showToast(context,parseModel.getInfo());
                }
            }
        });
    }

    /**
     * 萌化啦
     * @param position
     */
    private void addLoveSth(int position){
        User user = MyApplication.getInstance().getCurrentUser();
        if(user==null){
            ToastUtils.showToast(context,"请先登录");
            return;
        }
        String infoId = String.valueOf(datalist.get(position).getInfoId());
        ApiMomentsUtils.addLoveSth(context,infoId,"1",user.getPhoneNum(),new HttpConnectionUtil.RequestCallback(){

            @Override
            public void execute(ParseModel parseModel) {
                if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
                    ToastUtils.showToast(context,parseModel.getInfo());
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
        viewHolder.mViewAnim.setBackgroundResource(R.drawable.anim_play_audio);
        if(animationDrawable!=null && animationDrawable.isRunning()){
            animationDrawable.selectDrawable(2);
            animationDrawable.stop();
        }
//		if(animation==null) {
        animationDrawable = (AnimationDrawable) viewHolder.mViewAnim.getBackground();
//		}

        animationDrawable.start();
        MediaManager.playSound(filePath,
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        animationDrawable.stop();
                        viewHolder.mViewAnim
                                .setBackgroundResource(R.drawable.icon_voice_anim_3);
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

}
