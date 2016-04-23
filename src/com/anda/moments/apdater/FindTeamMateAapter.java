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

public class FindTeamMateAapter extends BaseListAdapter {
	
	private Context mContext;
	private List<PartTimeJob> mData;
	private LayoutInflater mLayoutInflater;
	
	public FindTeamMateAapter(Context mContext,List<PartTimeJob> mData){
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
			convertView = mLayoutInflater.inflate(R.layout.find_teammate_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView)convertView.findViewById(R.id.imageView);
			viewHolder.mDescrption = (TextView)convertView.findViewById(R.id.descrption);
			viewHolder.mTitle = (TextView)convertView.findViewById(R.id.title);
			viewHolder.mTime = (TextView)convertView.findViewById(R.id.time);
			
					
			convertView.setTag(viewHolder);		
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		PartTimeJob partTimeJob = (PartTimeJob)getItem(position);
		viewHolder.mDescrption.setText(partTimeJob.getMoney());
		viewHolder.mTitle.setText(partTimeJob.getName()+" "+position);
		viewHolder.mTime.setText(partTimeJob.getTime());
		
		return convertView;
	}
	
	static class ViewHolder{
		public ImageView mImageView;
		public TextView mTitle;
		public TextView mTime;
		public TextView mDescrption;
	}
	
}
