package com.anda.moments.apdater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.LoginActivity;
import com.anda.moments.ui.UserInfoActivity;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.LoadingDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员列表适配器
 */
public class NewFriendsListAdapter extends BaseAdapter  {

	private LayoutInflater inflater;

	private Context context;

	private List<User> list;

	public NewFriendsListAdapter(Context context, List<User> list) {
		this.context = context;
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
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.items_new_friend_list, null);
			holder = new ViewHolder();
			holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_user_head);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.tvLine = convertView.findViewById(R.id.line);
			holder.tvContent = (LinearLayout) convertView.findViewById(R.id.content);
			holder.mTvStatus = (TextView)convertView.findViewById(R.id.tv_status);
			holder.mTvAccept = (TextView)convertView.findViewById(R.id.tv_accept_add_friend);
			holder.mTvRefuse = (TextView)convertView.findViewById(R.id.tv_refuse_add_friend);

			holder.tvContent.setOnClickListener(holder);
			holder.mTvAccept.setOnClickListener(holder);
			holder.mTvRefuse.setOnClickListener(holder);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.position = position;
		final User dto = list.get(position);

		if (dto != null) {
			Picasso.with(context).load(dto.getIcon()).placeholder(context.getResources().getDrawable(R.drawable.default_useravatar)).into(holder.ivHead);

			int flag = dto.getFlag();
			if(flag == 0){//flag—0表示已添加，flag-1表示接受好友请求，flag-2表示拒绝好友邀请,flag-4未添加
				holder.mTvAccept.setVisibility(View.VISIBLE);
				holder.mTvRefuse.setVisibility(View.VISIBLE);
				holder.mTvStatus.setVisibility(View.GONE);
				holder.mTvStatus.setText("已添加");
			}else if(flag == 1){
				holder.mTvAccept.setVisibility(View.GONE);
				holder.mTvRefuse.setVisibility(View.GONE);
				holder.mTvStatus.setVisibility(View.VISIBLE);
				holder.mTvStatus.setText("已接受");
			}else if(flag == 2){
				holder.mTvAccept.setVisibility(View.GONE);
				holder.mTvRefuse.setVisibility(View.GONE);
				holder.mTvStatus.setVisibility(View.VISIBLE);
				holder.mTvStatus.setText("已拒绝");
			}
			// 根据position获取分类的首字母的Char ascii值
//			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == 0) {
				holder.tvLine.setVisibility(View.GONE);
			} else {
				holder.tvLine.setVisibility(View.VISIBLE);
			}
			holder.tvUserName.setText(dto.getUserName());


		}
		return convertView;
	}

	class ViewHolder implements View.OnClickListener{
		ImageView ivHead;
		TextView tvUserName;
		View tvLine;
		LinearLayout tvContent;
		TextView mTvStatus;//添加好友状态
		TextView mTvAccept;//接受
		TextView mTvRefuse;//拒绝

		public int position ;

		@Override
		public void onClick(View v) {
			User user = list.get(position);
			switch (v.getId()){
				case R.id.tv_refuse_add_friend://拒绝
					addFriend(position,String.valueOf(user.getRelationId()),2);
					break;
				case R.id.tv_accept_add_friend://接受
					addFriend(position,String.valueOf(user.getRelationId()),1);
					break;
				case R.id.content:

					Intent intent = new Intent(context, UserInfoActivity.class);
					intent.putExtra("user",user);
					context.startActivity(intent);
					break;
			}

		}
	}


	LoadingDialog mLoadingDialog;
	/**
	 *
	 * @param position
	 * @param relId 好友关联id
	 * @param flag 1接受 2拒绝
     */
	private void addFriend(final int position, String relId,final int flag){
		User user = MyApplication.getInstance().getCurrentUser();
		if(user==null){
			Intent intent = new Intent(context, LoginActivity.class);
			context.startActivity(intent);
			return;
		}
		if(mLoadingDialog==null){
			mLoadingDialog = new LoadingDialog(context);

		}
		mLoadingDialog.show();
		ApiUserUtils.dealFriendsRequest(context, user.getPhoneNum(), relId,flag, new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					list.get(position).setFlag(flag);
					notifyDataSetChanged();
					ToastUtils.showToast(context,parseModel.getInfo());

				}else{
					ToastUtils.showToast(context,parseModel.getInfo());
				}
			}
		});
	}




}
