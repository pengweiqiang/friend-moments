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
import com.anda.moments.views.CommentListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengweiqiang on 16/5/12.
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder>{

    private Context mContext;
    private List<CommentUser> mDatas;
    private int size = 0;
    private LayoutInflater mInflater;
    int headWidth = 40;
    private OnItemClickListener mOnItemClickListener;

    public CommentRecyclerViewAdapter(Context context,List<CommentUser> mDatas){
        this.mContext = context;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
        headWidth = DeviceInfo.dp2px(context,70);
    }

    public void setDatas(List<CommentUser> datas){
        if(datas == null ){
            datas = new ArrayList<CommentUser>();
        }
        mDatas = datas;
        size = datas.size();
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommentViewHolder viewHolder = new CommentViewHolder(mInflater.inflate(R.layout.im_social_item_comment,parent,false));
        return viewHolder;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        long startTime = System.nanoTime();
        final CommentUser bean = mDatas.get(position);
        String name = bean.getUserName();

        Picasso.with(mContext).load(bean.getIcon()).placeholder(R.drawable.default_useravatar).resize(headWidth,headWidth).centerCrop().into(holder.mIvHead);
        holder.mTvCommentUserName.setText(name);
        holder.commentTv.setText(bean.getText());
        holder.mTvCommentTime.setText(DateUtils.getTimestampString(bean.getPublishTime()));

        if(position == size-1){
            holder.mViewLine.setVisibility(View.GONE);
        }else{
            holder.mViewLine.setVisibility(View.VISIBLE);
        }

        if(!holder.itemView.hasOnClickListeners()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int position = holder.getPosition();
//                    mOnItemClickListener.onItemClick(v,position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemLongClick(v,position);
                    return true;
                }
            });
        }

        // 停止计时
        long endTime = System.nanoTime();
        // 计算耗时
        long val = (endTime - startTime) / 1000L;
        Log.e("Test", "---------Position CommentAdapter: " + position + ":" + val);
    }



    /**
     * 向指定位置添加元素
     * @param position
     * @param commentUser
     */
    public void add(int position,CommentUser commentUser){
        if(position>size){
            position = size;
        }else if(position<0){
            position = 0;
        }

        if(mDatas == null){
            mDatas = new ArrayList<CommentUser>();
        }
//        mDatas.add(position,praiseUser);
        size = mDatas.size();

//        mDatas.add(position,commentUser);
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
    public CommentUser remove(int position){
        if(position>size-1){
            return null;
        }
        size = mDatas.size();
//        CommentUser commentUser = mDatas.remove(position);
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
    public interface  OnItemClickListener{
        public void onItemClick(View view,int position);
        public void onItemLongClick(View view,int position);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTv;
        ImageView mIvHead;
        TextView mTvCommentUserName;
        TextView mTvCommentTime;
        View mViewLine;

        public CommentViewHolder(View convertView){
            super(convertView);
            commentTv = (TextView) convertView.findViewById(R.id.commentTv);
            mIvHead = (ImageView)convertView.findViewById(R.id.iv_comment_user_head);
            mTvCommentUserName = (TextView)convertView.findViewById(R.id.tv_comment_user_name);
            mTvCommentTime = (TextView)convertView.findViewById(R.id.tv_comment_time);
            mViewLine = convertView.findViewById(R.id.line);

        }
    }

}
