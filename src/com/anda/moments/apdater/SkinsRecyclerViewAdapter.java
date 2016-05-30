package com.anda.moments.apdater;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anda.moments.R;
import com.anda.moments.entity.Skins;
import com.anda.moments.utils.DeviceInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengweiqiang on 16/5/12.
 */
public class SkinsRecyclerViewAdapter extends RecyclerView.Adapter<SkinsRecyclerViewAdapter.SkinsViewHolder>{

    private Context mContext;
    private List<Skins> mDatas;
    private int size = 0;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    int width = 0;
    int height = 240;

    public SkinsRecyclerViewAdapter(Context context, List<Skins> mDatas){
        this.mContext = context;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
        width = DeviceInfo.getScreenWidth(context);
        height = (int)(width * 1.0 / 1080 * 480);
    }

    public void setDatas(List<Skins> datas){
        if(datas == null ){
            datas = new ArrayList<Skins>();
        }
        mDatas = datas;
        size = datas.size();
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public SkinsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SkinsViewHolder viewHolder = new SkinsViewHolder(mInflater.inflate(R.layout.skins_item,parent,false));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.mIvSkins.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        viewHolder.mIvSkins.setLayoutParams(layoutParams);
        return viewHolder;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final SkinsViewHolder holder, final int position) {
        final Skins bean = mDatas.get(position);
        String name = bean.getName();


        Picasso.with(mContext).load(bean.getSkinPath()).resize(width,height).centerCrop().placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder.mIvSkins);


        if(!holder.itemView.hasOnClickListeners()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v,position);
                    return true;
                }
            });
        }

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
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    class SkinsViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvSkins;

        public SkinsViewHolder(View convertView){
            super(convertView);
            mIvSkins = (ImageView)convertView.findViewById(R.id.iv_skin);


        }
    }

}
