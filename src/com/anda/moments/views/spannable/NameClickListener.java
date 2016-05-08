package com.anda.moments.views.spannable;

import android.text.SpannableString;

import com.anda.moments.MyApplication;
import com.anda.moments.utils.ToastUtils;

/**
 *
 * @ClassName: NameClickListener
 * @Description: 点赞和评论中人名的点击事件
 *
 */
public class NameClickListener implements ISpanClick {
    private SpannableString userName;
    private String userId;

    public NameClickListener(SpannableString name, String userid) {
        this.userName = name;
        this.userId = userid;
    }

    @Override
    public void onClick(int position) {
        ToastUtils.showToast(MyApplication.getInstance(),userName + " &id = " + userId);
    }
}
