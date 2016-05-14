package com.anda.moments.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.entity.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengweiqiang on 16/5/12.
 */
public class CitySelectAdapter extends RecyclerView.Adapter<CitySelectAdapter.ProvinceViewHolder>{

    private Context mContext;
    private List<Province.Sub> mDatas;
    private int size = 0;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public CitySelectAdapter(Context context, List<Province.Sub> mDatas){
        this.mContext = context;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Province.Sub> datas){
        if(datas == null ){
            datas = new ArrayList<Province.Sub>();
        }
        mDatas = datas;
        size = datas.size();
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ProvinceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProvinceViewHolder viewHolder = new ProvinceViewHolder(mInflater.inflate(R.layout.activity_city_item,parent,false));
        return viewHolder;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ProvinceViewHolder holder, int position) {
        final Province.Sub bean = mDatas.get(position);
        String name = bean.getName();

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
        holder.mTvCityName.setText(name);

    }



    /**
     * 向指定位置添加元素
     * @param position
     * @param city
     */
    public void add(int position,Province.Sub city){
        if(position>size){
            position = size;
        }else if(position<0){
            position = 0;
        }

        if(mDatas == null){
            mDatas = new ArrayList<Province.Sub>();
        }
        mDatas.add(position,city);
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
    public Province.Sub remove(int position){
        if(position>size-1){
            return null;
        }
        size = mDatas.size();
        Province.Sub city = mDatas.remove(position);
        notifyItemRemoved(position);
        return city;
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
        TextView mTvCityName;


        public ProvinceViewHolder(View convertView){
            super(convertView);
            mTvCityName = (TextView) convertView.findViewById(R.id.tv_city);


        }
    }

}
