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
import com.anda.moments.entity.Province;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengweiqiang on 16/5/12.
 */
public class ProvinceCityAdapter extends RecyclerView.Adapter<ProvinceCityAdapter.ProvinceViewHolder>{

    private Context mContext;
    private List<Province> mDatas;
    private int size = 0;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public ProvinceCityAdapter(Context context, List<Province> mDatas){
        this.mContext = context;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Province> datas){
        if(datas == null ){
            datas = new ArrayList<Province>();
        }
        mDatas = datas;
        size = datas.size();
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ProvinceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProvinceViewHolder viewHolder = new ProvinceViewHolder(mInflater.inflate(R.layout.activity_province_city_item,parent,false));
        return viewHolder;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ProvinceViewHolder holder, int position) {
        final Province bean = mDatas.get(position);
        String name = bean.getName();
        int type = bean.getType();

        if(type == 1){
            holder.mIvRightIn.setVisibility(View.GONE);
        }else{
            holder.mIvRightIn.setVisibility(View.VISIBLE);
        }

        if(!holder.itemView.hasOnClickListeners()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getPosition();
                    mOnItemClickListener.onItemClick(v,position);
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

        holder.mTvProvinceName.setText(name);

    }



    /**
     * 向指定位置添加元素
     * @param position
     * @param province
     */
    public void add(int position,Province province){
        if(position>size){
            position = size;
        }else if(position<0){
            position = 0;
        }

        if(mDatas == null){
            mDatas = new ArrayList<Province>();
        }
        mDatas.add(position,province);
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
    public Province remove(int position){
        if(position>size-1){
            return null;
        }
        size = mDatas.size();
        Province province = mDatas.remove(position);
        notifyItemRemoved(position);
        return province;
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
    class ProvinceViewHolder extends RecyclerView.ViewHolder {
        TextView mTvProvinceName;
        ImageView mIvRightIn;


        public ProvinceViewHolder(View convertView){
            super(convertView);
            mTvProvinceName = (TextView) convertView.findViewById(R.id.tv_province);
            mIvRightIn = (ImageView)convertView.findViewById(R.id.iv_right_in);

        }
    }

}
