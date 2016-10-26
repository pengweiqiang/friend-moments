package com.anda.moments.apdater;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.entity.NewMessage;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.StringUtils;
import com.anda.moments.utils.TextViewUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 新消息列表适配器
 */
public class NewMessageListAdapter extends BaseAdapter  {

	private LayoutInflater inflater;

	private Context context;

	private List<NewMessage> list;

	public NewMessageListAdapter(Context context, List<NewMessage> list) {
		this.context = context;
		if(list==null){
			list = new ArrayList<NewMessage>();
		}
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<NewMessage> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public NewMessage getItem(int position) {
		if(list!=null){
			return list.get(position);
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
			convertView = inflater.inflate(R.layout.items_new_message_list, null);
			holder = new ViewHolder();
			holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_user_head);
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.tvLine = convertView.findViewById(R.id.line);
			holder.tvContent = (LinearLayout) convertView.findViewById(R.id.content);
			holder.mTvCreateDate = (TextView)convertView.findViewById(R.id.tv_create_time);
			holder.mIvMessage= (ImageView)convertView.findViewById(R.id.iv_message_image);

			holder.mIvPraise = (ImageView)convertView.findViewById(R.id.iv_praise);

			holder.mTvComment = (TextView)convertView.findViewById(R.id.tv_comment_text);

			holder.mFlVideoMessage = (FrameLayout)convertView.findViewById(R.id.fl_video_view);
			holder.mIvVideoMessage = (ImageView)convertView.findViewById(R.id.thumbnailImageView);
			holder.mIvAudioMessage = (ImageView)convertView.findViewById(R.id.iv_message_audio);


			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.position = position;

		NewMessage newMessage = getItem(position);

		String msgType = newMessage.getMsgType();
		if("1".equals(msgType)) {//评论
			holder.mIvPraise.setVisibility(View.GONE);
		}else if("2".endsWith(msgType)){//点赞
			holder.mIvPraise.setVisibility(View.VISIBLE);
		}else{
			holder.mIvPraise.setVisibility(View.GONE);
		}

		String commentIcon = newMessage.getCommUserIcon();
		if(StringUtils.isEmpty(commentIcon)){
			commentIcon=String.valueOf(R.drawable.default_useravatar);
		}
		Picasso.with(context).load(commentIcon).placeholder(context.getResources().getDrawable(R.drawable.default_useravatar)).into(holder.ivHead);

		String commUserName = newMessage.getCommUserName();
		String commentText = newMessage.getCommentText();
		if(StringUtils.isEmpty(commUserName) || StringUtils.isEmpty(commentText)){
			holder.tvUserName.setText(commUserName+commentText);
		}else{
			holder.tvUserName.setText(TextViewUtils.getSpannableStringColor(commUserName+"回复"+commentText,commUserName.length(),commUserName.length()+2,"#F29c9F"));
		}

		holder.mTvCreateDate.setText(DateUtils.convert2String(newMessage.getCreateTime(),DateUtils.Y_M_D_H_M));

		showTypeMediaView(newMessage,holder);

		return convertView;
	}

	private void showTypeMediaView(NewMessage newMessage,ViewHolder viewHolder){
		String type = newMessage.getAttaType();//attaType-1-图片，2-音频，3-视频
		String attaIcon = newMessage.getAttaIcon();
		if(StringUtils.isEmpty(attaIcon)){
			viewHolder.mIvMessage.setVisibility(View.GONE);
		}else{
			viewHolder.mIvMessage.setVisibility(View.VISIBLE);
			Picasso.with(context).load(attaIcon).placeholder(new ColorDrawable(context.getResources().getColor(R.color.grey_line))).into(viewHolder.mIvMessage);
		}
		if("1".equals(type)){
			viewHolder.mFlVideoMessage.setVisibility(View.GONE);
			viewHolder.mIvAudioMessage.setVisibility(View.GONE);
			viewHolder.mIvMessage.setVisibility(View.VISIBLE);
			viewHolder.mTvComment.setVisibility(View.GONE);

		}else if("2".equals(type)){
			viewHolder.mFlVideoMessage.setVisibility(View.GONE);
			viewHolder.mIvAudioMessage.setVisibility(View.VISIBLE);
			viewHolder.mTvComment.setVisibility(View.GONE);
		}else if("3".equals(type)){
			viewHolder.mFlVideoMessage.setVisibility(View.VISIBLE);
			viewHolder.mIvAudioMessage.setVisibility(View.GONE);
			viewHolder.mTvComment.setVisibility(View.GONE);
			if(!StringUtils.isEmpty(attaIcon)){
				Picasso.with(context).load(attaIcon).placeholder(new ColorDrawable(context.getResources().getColor(R.color.grey_line))).into(viewHolder.mIvVideoMessage);
			}
		}else{
			viewHolder.mFlVideoMessage.setVisibility(View.GONE);
			viewHolder.mIvAudioMessage.setVisibility(View.GONE);
			viewHolder.mTvComment.setVisibility(View.VISIBLE);
			if(StringUtils.isEmpty(newMessage.getNewsText())){
				viewHolder.mTvComment.setVisibility(View.GONE);
			}else {
				viewHolder.mTvComment.setVisibility(View.VISIBLE);
				viewHolder.mTvComment.setText(newMessage.getNewsText());
			}
		}



	}

	class ViewHolder implements View.OnClickListener{
		ImageView ivHead;
		ImageView mIvMessage;//图片

		//音频
		ImageView mIvAudioMessage;

		//视频
		FrameLayout mFlVideoMessage;
		ImageView mIvVideoMessage;

		//文本
		TextView mTvComment;

		//点赞
		ImageView mIvPraise;


		TextView tvUserName;
		View tvLine;
		LinearLayout tvContent;
		TextView mTvCreateDate;

		public int position ;

		@Override
		public void onClick(View v) {


		}
	}



}
