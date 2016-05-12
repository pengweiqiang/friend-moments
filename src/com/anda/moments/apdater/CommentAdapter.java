package com.anda.moments.apdater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.entity.CommentInfo;
import com.anda.moments.entity.CommentUser;
import com.anda.moments.utils.DateUtils;
import com.anda.moments.utils.DeviceInfo;
import com.anda.moments.utils.Log;
import com.anda.moments.views.CircleMovementMethod;
import com.anda.moments.views.CommentListView;
import com.anda.moments.views.spannable.NameClickListener;
import com.anda.moments.views.spannable.NameClickable;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiwei on 16/3/2.
 */
public class CommentAdapter {

    private Context mContext;
    private CommentListView mListview;
    private List<CommentUser> mDatas;
    private int size = 0;
//    private View convertView ;

    int headWidth = 40;
    public CommentAdapter(Context context){
        mContext = context;
        mDatas = new ArrayList<CommentUser>();
        headWidth = DeviceInfo.dp2px(context,70);

    }

    public CommentAdapter(Context context, List<CommentUser> datas){
        mContext = context;
        setDatas(datas);

    }

    public void bindListView(CommentListView listView){
        if(listView == null){
            throw new IllegalArgumentException("CommentListView is null....");
        }
        mListview = listView;
    }

    public void setDatas(List<CommentUser> datas){
        if(datas == null ){
            datas = new ArrayList<CommentUser>();
        }
        mDatas = datas;
        size = datas.size();
    }

    public List<CommentUser> getDatas(){
        return mDatas;
    }

    public int getCount(){
        if(mDatas == null){
            return 0;
        }
        return mDatas.size();
    }

    public CommentUser getItem(int position){
        if(mDatas == null){
            return null;
        }
        if(position < mDatas.size()){
            return mDatas.get(position);
        }else{
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    static class ViewHolder{
        TextView commentTv;
        ImageView mIvHead;
        TextView mTvCommentUserName;
        TextView mTvCommentTime;
    }

    private View getView(final int position){
//        ViewHolder viewHolder = null;
        // 开始计时
        long startTime = System.nanoTime();
        System.out.println("CommentAdapter getView-----------------------" + position);
//        if(convertView==null){
//            viewHolder = new ViewHolder();
            View convertView = View.inflate(mContext,
                    R.layout.im_social_item_comment, null);
            System.out.println("CommentAdapter initView++++++++++++++" );

            TextView commentTv = (TextView) convertView.findViewById(R.id.commentTv);
            ImageView mIvHead = (ImageView)convertView.findViewById(R.id.iv_comment_user_head);
            TextView mTvCommentUserName = (TextView)convertView.findViewById(R.id.tv_comment_user_name);
            TextView mTvCommentTime = (TextView)convertView.findViewById(R.id.tv_comment_time);

//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder) convertView.getTag();
//        }

        if(position == size-1){
            convertView.findViewById(R.id.line).setVisibility(View.GONE);
        }

//        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(R.color.name_selector_color,
//                R.color.name_selector_color);

        final CommentUser bean = mDatas.get(position);
        String name = bean.getUserName();
        String id = bean.getUserId();

        Picasso.with(mContext).load(bean.getIcon()).placeholder(R.drawable.default_useravatar).resize(headWidth,headWidth).centerCrop().into(mIvHead);
        mTvCommentUserName.setText(name);
        commentTv.setText(bean.getText());
        mTvCommentTime.setText(DateUtils.getTimestampString(bean.getPublishTime()));

//        String toReplyName = "回复的人";
//        if (bean.getToReplyUser() != null) {
//            toReplyName = bean.getToReplyUser().getName();
//        }

//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(setClickableSpan(name, 0));

//        if (!TextUtils.isEmpty(toReplyName)) {

//            builder.append(" 回复 ");
//            builder.append(setClickableSpan(toReplyName, 1));
//        }
//        builder.append(": ");
        //转换表情字符
//        String contentBodyStr = bean.getText();
        //SpannableString contentSpanText = new SpannableString(contentBodyStr);
        //contentSpanText.setSpan(new UnderlineSpan(), 0, contentSpanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.append(contentBodyStr);
//        commentTv.setText(builder);

//        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (circleMovementMethod.isPassToTv()) {
                    mListview.getOnItemClickListener().onItemClick(position);
//                }
            }
        });
//        commentTv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (circleMovementMethod.isPassToTv()) {
//                    mListview.getOnItemLongClickListener().onItemLongClick(position);
//                    return true;
//                }
//                return false;
//            }
//        });
        // 停止计时
        long endTime = System.nanoTime();
        // 计算耗时
        long val = (endTime - startTime) / 1000L;
        Log.e("Test", "---------Position CommentAdapter: " + position + ":" + val);

        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new NameClickable(new NameClickListener(
                        subjectSpanText, ""), position), 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public void notifyDataSetChanged(){
        if(mListview == null){
            throw new NullPointerException("listview is null, please bindListView first...");
        }
        mListview.removeAllViews();
        if(mDatas == null || mDatas.size() == 0){
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(int i=0; i<size; i++){
            View view = getView(i);
//            ViewGroup p = (ViewGroup) view.getParent();
//            if (p != null) {
//                p.removeAllViewsInLayout();
//            }

            if(i != size-1){
                layoutParams.setMargins(0,0,0,DeviceInfo.dp2px(mContext,5));
            }
//            else{
//                layoutParams.setMargins(0,0,0,0);
//            }

            mListview.addView(view, i, layoutParams);
        }

    }

}
