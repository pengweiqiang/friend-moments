package com.anda.moments.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.entity.CommentUser;
import com.anda.moments.entity.PraiseUser;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengweiqiang on 16/5/12.
 */
public class PraiseRecyclerViewAdapter extends RecyclerView.Adapter<PraiseRecyclerViewAdapter.PraiseViewHolder>{

    private Context mContext;
    private List<PraiseUser> mDatas;
    private int size = 0;
    private LayoutInflater mInflater;
    int headWidth = 40;
    private OnItemClickListener mOnItemClickListener;

    public PraiseRecyclerViewAdapter(Context context, List<PraiseUser> mDatas){
        this.mContext = context;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
        headWidth = DeviceInfo.dp2px(context,70);
    }

    public void setDatas(List<PraiseUser> datas){
        if(datas == null ){
            datas = new ArrayList<PraiseUser>();
        }
        mDatas = datas;
        size = datas.size();
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public PraiseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PraiseViewHolder viewHolder = new PraiseViewHolder(mInflater.inflate(R.layout.parise_item,parent,false));
        return viewHolder;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final PraiseViewHolder holder, int position) {
        long startTime = System.nanoTime();
        final PraiseUser bean = mDatas.get(position);

        Picasso.with(mContext).load(bean.getIcon()).placeholder(R.drawable.default_useravatar).
                resize(headWidth,headWidth).centerCrop().into(holder.mIvHead);

        if(!holder.itemView.hasOnClickListeners()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getPosition();
                    mOnItemClickListener.onItemPraiseClick(v,position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemPraiseLongClick(v,position);
                    return true;
                }
            });
        }

        // 停止计时
        long endTime = System.nanoTime();
        // 计算耗时
        long val = (endTime - startTime) / 1000L;
        Log.e("Test", "---------Position PraiseRecyclerViewAdapter: " + position + ":" + val);
    }



    /**
     * 向指定位置添加元素
     * @param position
     * @param praiseUser
     */
    public void add(int position,PraiseUser praiseUser){
        if(position>size){
            position = size;
        }else if(position<0){
            position = 0;
        }
        if(mDatas == null){
            mDatas = new ArrayList<PraiseUser>();
        }
//        mDatas.add(position,praiseUser);
        size = mDatas.size();
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        notifyItemInserted(position);
    }

    /**
     * 移除指定位置元素
     * @param position
     * @return
     */
    public PraiseUser remove(int position){
        if(position>size-1){
            return null;
        }
//        PraiseUser praiseUser = mDatas.remove(position);
        size = mDatas.size();
        notifyItemRemoved(position);
        return null;
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        if(mDatas == null){
            return 0;
        }
        return size;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 处理item的点击事件和长按事件
     */
    interface  OnItemClickListener{
        public void onItemPraiseClick(View view, int position);
        public void onItemPraiseLongClick(View view, int position);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    class PraiseViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvHead;


        public PraiseViewHolder(View convertView){
            super(convertView);
            mIvHead = (ImageView)convertView.findViewById(R.id.iv_comment_user_head);
        }
    }

}
