package com.anda.moments.apdater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anda.moments.R;
import com.anda.moments.entity.CommentInfo;
import com.anda.moments.entity.CommentUser;
import com.anda.moments.views.CircleMovementMethod;
import com.anda.moments.views.CommentListView;
import com.anda.moments.views.spannable.NameClickListener;
import com.anda.moments.views.spannable.NameClickable;
import com.squareup.picasso.OkHttpDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiwei on 16/3/2.
 */
public class CommentAdapter {

    private Context mContext;
    private CommentListView mListview;
    private List<CommentUser> mDatas;

    public CommentAdapter(Context context){
        mContext = context;
        mDatas = new ArrayList<CommentUser>();
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

    private View getView(final int position){
        System.out.println("CommentAdapter getView-----------------------" + position);
        View convertView = View.inflate(mContext,
                R.layout.im_social_item_comment, null);
        TextView commentTv = (TextView) convertView.findViewById(R.id.commentTv);
        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(R.color.name_selector_color,
                R.color.name_selector_color);

        final CommentUser bean = mDatas.get(position);
        String name = bean.getUserName();
        String id = bean.getUserId();
//        String toReplyName = "回复的人";
//        if (bean.getToReplyUser() != null) {
//            toReplyName = bean.getToReplyUser().getName();
//        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, 0));

//        if (!TextUtils.isEmpty(toReplyName)) {

//            builder.append(" 回复 ");
//            builder.append(setClickableSpan(toReplyName, 1));
//        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = bean.getText();
        //SpannableString contentSpanText = new SpannableString(contentBodyStr);
        //contentSpanText.setSpan(new UnderlineSpan(), 0, contentSpanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(contentBodyStr);
        commentTv.setText(builder);

        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    mListview.getOnItemClickListener().onItemClick(position);
                }
            }
        });
        commentTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (circleMovementMethod.isPassToTv()) {
//                    mListview.getOnItemLongClickListener().onItemLongClick(position);
//                    return true;
//                }
                return false;
            }
        });


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
        for(int i=0; i<mDatas.size(); i++){
            final int index = i;
            View view = getView(index);
            if(view == null){
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            mListview.addView(view, index, layoutParams);
        }

    }

}
