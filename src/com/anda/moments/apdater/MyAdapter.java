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
import com.anda.moments.entity.Images;
import com.anda.moments.entity.Infos;
import com.anda.moments.entity.User;
import com.anda.moments.ui.ImagePagerActivity;
import com.anda.moments.ui.UserInfoActivity;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.CustomImageView;
import com.anda.moments.views.MultiMyImageView;
import com.anda.moments.views.NineGridlayout;
import com.anda.moments.views.popup.ActionItem;
import com.anda.moments.views.popup.TitlePopup;
import com.anda.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class MyAdapter extends BaseAdapter {
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
        Infos infos = datalist.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_item_ninegridlayout, parent, false);
            viewHolder = new ViewHolder();
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
        viewHolder.mTvContent.setText(StringUtils.ToDBC(infos.getInfoText()));
        viewHolder.mTvPublishTime.setText(DateUtils.getTimestampString2(infos.getCreateTime()));

        return convertView;
    }




    class ViewHolder implements View.OnClickListener{
        public MultiMyImageView multiMyImageView;//图片四宫格
        public TextView mTvPublishTime;//发布时间
        public TextView mTvContent;//发布内容
        public View mLineTop;//顶端的线

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
