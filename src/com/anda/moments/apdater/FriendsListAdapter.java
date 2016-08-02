package com.anda.moments.apdater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.anda.moments.MyApplication;
import com.anda.moments.R;
import com.anda.moments.api.ApiUserUtils;
import com.anda.moments.api.constant.ApiConstants;
import com.anda.moments.entity.ParseModel;
import com.anda.moments.entity.User;
import com.anda.moments.ui.my.UserInfoActivity;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.HttpConnectionUtil;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.ToastUtils;
import com.anda.moments.views.LoadingDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 成员列表适配器
 */
public class FriendsListAdapter extends BaseAdapter implements SectionIndexer {

	private LayoutInflater inflater;

	private Activity mActivity;

	private List<User> list;

	int headWidth = 80;
	public FriendsListAdapter(Activity mActivity, List<User> list) {
		this.mActivity = mActivity;
		if(list==null){
			list = new ArrayList<User>();
		}
		this.list = list;
		headWidth = DeviceInfo.dp2px(mActivity,70);
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
			holder.tvBottomLine = convertView.findViewById(R.id.line_bottom);
			holder.layoutParams = (LinearLayout.LayoutParams)holder.tvBottomLine.getLayoutParams();
			holder.tvContent = (LinearLayout) convertView.findViewById(R.id.content);

			holder.tvContent.setOnClickListener(holder);
			holder.ivHead.setOnClickListener(holder);
			holder.tvContent.setOnLongClickListener(holder);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.position = position;
		final User dto = list.get(position);

		if (dto != null) {
			Picasso.with(mActivity).load(dto.getIcon()).resize(headWidth,headWidth).centerCrop().placeholder(mActivity.getResources().getDrawable(R.drawable.default_useravatar)).into(holder.ivHead);

			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText("#".equals(dto.getSortLetters()) ? dto.getSortLetters() + "" : dto.getSortLetters());
				holder.tvLine.setVisibility(View.VISIBLE);
			} else {
				holder.tvLetter.setVisibility(View.GONE);
				holder.tvLine.setVisibility(View.GONE);
			}
			String descTag = dto.getDescTag();
			StringBuffer sbUserName = new StringBuffer();
			String userName = dto.getUserName();
			if(StringUtils.isEmpty(userName)){
				userName = "";
			}
			if(!StringUtils.isEmpty(descTag)) {
				sbUserName.append(descTag);
			}else{
				sbUserName.append(userName);
			}
			holder.tvUserName.setText(sbUserName.toString());

			if(position == list.size()-1){
				holder.layoutParams.setMargins(0,0,0,0);
			}else{
				holder.layoutParams.setMargins(DeviceInfo.dp2px(mActivity,10),0,0,0);
			}
			holder.tvBottomLine.setLayoutParams(holder.layoutParams);

		}
		return convertView;
	}

	class ViewHolder implements View.OnClickListener,View.OnLongClickListener{
		ImageView ivHead;
		TextView tvLetter;
		TextView tvUserName;
		View tvLine;
		LinearLayout tvContent;
		View tvBottomLine;
		LinearLayout.LayoutParams layoutParams;

		public int position ;

		@Override
		public void onClick(View v) {
			User user = list.get(position);
			if(v.getId()==R.id.iv_user_head){
				Intent intent = new Intent(mActivity, UserInfoActivity.class);
				intent.putExtra("user", user);
				mActivity.startActivity(intent);
			}else {

				String userName = StringUtils.isEmpty(user.getDescTag())?user.getUserName():user.getDescTag();
				//刷新用户信息头像
				Uri headUri = Uri.parse(StringUtils.isEmpty(user.getIcon())?"":user.getIcon());
				RongContext.getInstance().getUserInfoCache().put(user.getPhoneNum(),new UserInfo(user.getPhoneNum(),userName, headUri));

				RongIM.getInstance().startConversation(mActivity, Conversation.ConversationType.PRIVATE, String.valueOf(user.getPhoneNum()), userName);

			}
		}

		@Override
		public boolean onLongClick(View v) {
			User user = list.get(position);
			showDeleteWindow(position,user.getRelationId()+"");
			return false;
		}
	}

	/**
	 * 弹出删除对话框
	 * @param position
	 * @param relationId
     */
	private void showDeleteWindow(final int position, final String relationId){
		final AlertDialog dlg = new AlertDialog.Builder(mActivity).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.alertdialog);
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("删除好友");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				deleteItem(position,relationId);
				dlg.cancel();
			}
		});
		window.findViewById(R.id.ll_content2).setVisibility(View.GONE);


	}

	private void deleteItem(final int position, String relationId){
		User user = MyApplication.getInstance().getCurrentUser();
		if(user==null){
			return;
		}
		final LoadingDialog mLoadingDialog = new LoadingDialog(mActivity);

		mLoadingDialog.show();
		ApiUserUtils.deleteFriend(mActivity, relationId,user.getPhoneNum(), new HttpConnectionUtil.RequestCallback() {
			@Override
			public void execute(ParseModel parseModel) {
				mLoadingDialog.cancel();
				if(ApiConstants.RESULT_SUCCESS.equals(parseModel.getRetFlag())){
					list.remove(position);
					notifyDataSetChanged();
				}else{
					ToastUtils.showToast(mActivity, StringUtils.isEmpty(parseModel.getInfo())?"删除失败":parseModel.getInfo());
				}
			}
		});
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
