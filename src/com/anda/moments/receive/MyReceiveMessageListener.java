package com.anda.moments.receive;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.anda.moments.MyApplication;
import com.anda.moments.commons.AppManager;
import com.anda.moments.ui.chat.ConversationActivity;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * @author will on 16/8/1 17:27
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */
public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {


    @Override
    public boolean onReceived(Message message, int i) {
        playBeepSoundAndVibrate();
        return false;
    }

    public void playBeepSoundAndVibrate() {
        if(ConversationActivity.class.getName().equals( AppManager.getAppManager().currentActivity().getClass().getName())) {
//            Vibrator vibrator = (Vibrator)
//                    MyApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
//            //震动一次
//            vibrator.vibrate(1000);
            //第一个参数，指代一个震动的频率数组。每两个为一组，每组的第一个为等待时间，第二个为震动时间。
            //        比如  [2000,500,100,400],会先等待2000毫秒，震动500，再等待100，震动400
            //第二个参数，repest指代从 第几个索引（第一个数组参数） 的位置开始循环震动。
            //会一直保持循环，我们需要用 vibrator.cancel()主动终止
            //vibrator.vibrate(new long[]{300,500},0);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(MyApplication.getInstance(), notification);
            r.play();
        }
    }
}
