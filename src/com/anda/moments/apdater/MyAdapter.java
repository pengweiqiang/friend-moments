package com.anda.moments.apdater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.entity.User;
import com.anda.moments.ui.ImagePagerActivity;
import com.anda.moments.ui.UserInfoActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.CustomImageView;
import com.anda.moments.views.NineGridlayout;
import com.anda.moments.views.popup.ActionItem;
import com.anda.moments.views.popup.TitlePopup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<List<String>> datalist;

    public MyAdapter(Context context, List<List<String>> datalist) {
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
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
       final List<String> itemList = datalist.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_item_ninegridlayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivMore = (NineGridlayout) convertView.findViewById(R.id.iv_ngrid_layout);
            viewHolder.ivOne = (CustomImageView) convertView.findViewById(R.id.iv_oneimage);
            viewHolder.mIvUserHead = (ImageView)convertView.findViewById(R.id.iv_user_head);
            viewHolder.mTvUserName = (TextView)convertView.findViewById(R.id.tv_user_name);

            viewHolder.mViewComment = convertView.findViewById(R.id.iv_comment);//评论
            viewHolder.mViewComment.setOnClickListener(viewHolder);
            viewHolder.mIvUserHead.setOnClickListener(viewHolder);
            viewHolder.mTvUserName.setOnClickListener(viewHolder);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setPosition(position);

        if (itemList.isEmpty() || itemList.isEmpty()) {
            viewHolder.ivMore.setVisibility(View.GONE);
            viewHolder.ivOne.setVisibility(View.GONE);
        } else if (itemList.size() == 1) {
            viewHolder.ivMore.setVisibility(View.GONE);
            viewHolder.ivOne.setVisibility(View.VISIBLE);

            handlerOneImage(viewHolder, itemList.get(0));
        } else {
            viewHolder.ivMore.setVisibility(View.VISIBLE);
            viewHolder.ivOne.setVisibility(View.GONE);

            viewHolder.ivMore.setImagesData(itemList);
            viewHolder.ivMore.setOnItemClickListener(new NineGridlayout.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ImagePagerActivity.imageSize = new int[]{view.getMeasuredWidth(), view.getMeasuredHeight()};
                    ImagePagerActivity.startImagePagerActivity(context, itemList, position);
                }
            });
//            viewHolder.ivMore.setOnClickListener();
        }

        return convertView;
    }

    private void handlerOneImage(ViewHolder viewHolder, final String image) {
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
        viewHolder.ivOne.setScaleType(ImageView.ScaleType.FIT_XY);
        viewHolder.ivOne.setImageUrl(image);
        viewHolder.ivOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 因为单张图片时，图片实际大小是自适应的，imageLoader缓存时是按测量尺寸缓存的
                ImagePagerActivity.imageSize = new int[]{v.getMeasuredWidth(), v.getMeasuredHeight()};
                List<String> urls = new ArrayList<String>();
                urls.add(image);
                ImagePagerActivity.startImagePagerActivity(context, urls, 0);
            }
        });


    }


    class ViewHolder implements View.OnClickListener{
        public View mViewComment;//萌化了
        public NineGridlayout ivMore;//图片九宫格
        public CustomImageView ivOne;//单张图片
        public ImageView mIvUserHead;//头像
        public TextView mTvUserName;//昵称

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

    private void startUserInfoActivity(int position){
        Intent intent = new Intent(context, UserInfoActivity.class);
        User user = new User();
        user.setPhoneNum("15652265843");
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
        titlePopup.addAction(new ActionItem(context, "评论",R.drawable.btn_comment_count));

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
                    ToastUtils.showToast(context,"点赞成功 "+positionParent);
                    break;
                case 1:
                    ToastUtils.showToast(context,"萌化了 "+positionParent);
                    break;
                case 2:
                    ToastUtils.showToast(context,"评论 "+positionParent);
                    break;
            }
        }

        @Override
        public void setParentPosition(int parentPosition) {
            this.positionParent = parentPosition;
        }
    };
}
