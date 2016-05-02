package com.anda.moments.apdater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.anda.moments.R;
import com.anda.moments.entity.User;
import com.anda.moments.ui.UserInfoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员列表适配器
 */
public class FriendsListAdapter extends BaseAdapter implements SectionIndexer {

	private LayoutInflater inflater;

	private Activity mActivity;

	private List<User> list;

	public FriendsListAdapter(Activity mActivity, List<User> list) {
		this.mActivity = mActivity;
		if(list==null){
			list = new ArrayList<User>();
		}
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<User> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public User getItem(int position) {
		if(list!=null){
			list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.items_friend_list, null);
			holder = new ViewHolder();
			holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_user_head);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			holder.tvLine = convertView.findViewById(R.id.line);
			holder.tvContent = (LinearLayout) convertView.findViewById(R.id.content);

			holder.tvContent.setOnClickListener(holder);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.position = position;
		final User dto = list.get(position);

		if (dto != null) {
			Picasso.with(mActivity).load(dto.getIcon()).placeholder(mActivity.getResources().getDrawable(R.drawable.default_useravatar)).into(holder.ivHead);

			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText("☆".equals(dto.getSortLetters()) ? dto.getSortLetters() + "(管理员)" : dto.getSortLetters());
				holder.tvLine.setVisibility(View.VISIBLE);
			} else {
				holder.tvLetter.setVisibility(View.GONE);
				holder.tvLine.setVisibility(View.GONE);
			}
			holder.tvUserName.setText(dto.getUserName());
		}
		return convertView;
	}

	class ViewHolder implements View.OnClickListener{
		ImageView ivHead;
		TextView tvLetter;
		TextView tvUserName;
		View tvLine;
		LinearLayout tvContent;

		public int position ;

		@Override
		public void onClick(View v) {
			User user = list.get(position);
			Intent intent = new Intent(mActivity, UserInfoActivity.class);
			intent.putExtra("user",user);
			mActivity.startActivity(intent);
		}
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
