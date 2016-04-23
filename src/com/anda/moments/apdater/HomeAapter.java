package com.anda.moments.apdater;

import java.util.List;

import com.anda.moments.R;
import com.anda.moments.entity.PartTimeJob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeAapter extends BaseListAdapter {
	
	private Context mContext;
	private List<PartTimeJob> mData;
	private LayoutInflater mLayoutInflater;
	
	public HomeAapter(Context mContext,List<PartTimeJob> mData){
		this.mContext = mContext;
		this.mData = mData;
		mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public void setBusyState(boolean b) {
		super.setBusyState(b);
	}

	@Override
	public int getCount() {
		if(mData == null){
			return 0;
		}
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		if(mData==null){
			return null;
		}
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.home_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView)convertView.findViewById(R.id.imageView);
			viewHolder.mDistance = (TextView)convertView.findViewById(R.id.distance);
			viewHolder.mPartTimeName = (TextView)convertView.findViewById(R.id.partTimeName);
			viewHolder.mMoney = (TextView)convertView.findViewById(R.id.money);
			viewHolder.mTime = (TextView)convertView.findViewById(R.id.time);
			
					
			convertView.setTag(viewHolder);		
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		PartTimeJob partTimeJob = (PartTimeJob)getItem(position);
		viewHolder.mDistance.setText(partTimeJob.getDistance());
		viewHolder.mMoney.setText(partTimeJob.getMoney());
		viewHolder.mPartTimeName.setText(partTimeJob.getName()+" "+position);
		viewHolder.mTime.setText(partTimeJob.getTime());
		
		return convertView;
	}
	
	static class ViewHolder{
		public ImageView mImageView;
		public TextView mPartTimeName;
		public TextView mDistance;
		public TextView mTime;
		public TextView mMoney;
	}
	
}
