package com.anda.moments.utils;


import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	private static String oldMsg; 
    protected static Toast toast   = null; 
    private static long oneTime=0; 
    private static long twoTime=0; 

    public synchronized static void showToast(Context context, String s){     
       showToast(context, s, Toast.LENGTH_SHORT);
    } 
    public synchronized static void showToast(Context context,String text,int duration){
    	if(StringUtils.isEmpty(text)){
    		return;
    	}
    	 if(toast==null){  
             toast =Toast.makeText(context, text, duration); 
//             toast.setGravity(Gravity.CENTER, 0, 0);
             toast.show(); 
             oneTime=System.currentTimeMillis(); 
         }else{ 
             twoTime=System.currentTimeMillis(); 
             if(text.equals(oldMsg)){ 
                 if(twoTime-oneTime>duration){ 
//                 	toast.setGravity(Gravity.CENTER, 0, 0);
                 	toast.setDuration(duration);
                     toast.show(); 
                 } 
             }else{ 
                 oldMsg = text; 
                 toast.setText(text); 
                 toast.setDuration(duration);
//                 toast.setGravity(Gravity.CENTER, 0, 0);
                 toast.show(); 
             }        
         } 
         oneTime=twoTime; 
    }


    public static void showToast(Context context, int resId){    
        showToast(context, context.getString(resId)); 
    } 

}