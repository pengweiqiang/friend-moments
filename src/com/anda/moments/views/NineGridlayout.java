package com.anda.moments.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anda.moments.entity.Images;
import com.anda.moments.utils.ScreenTools;

import java.util.List;


/**
 * Created by Pan_ on 2015/2/2.
 */
public class NineGridlayout extends ViewGroup {

    /**
     * 图片之间的间隔
     */
    private int gap = 5;
    private int columns;//
    private int rows;//
    private List listData;
    private int totalWidth;

    public NineGridlayout(Context context) {
        super(context);
    }

    public NineGridlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ScreenTools screenTools=ScreenTools.instance(getContext());
        totalWidth=screenTools.getScreenWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    private void layoutChildrenView(){
        int childrenCount = listData.size();

        int singleWidth = 0;
        if(childrenCount==2||childrenCount == 4){
            singleWidth = (int) Math.ceil((totalWidth - gap * 1) / 2d);
        }
        else if(childrenCount == 3 || childrenCount ==5 ||childrenCount == 8|| childrenCount == 7 ||childrenCount == 6){
            showImagesLayout(childrenCount);
            return;
        }
        else {
            singleWidth = (int) Math.ceil((totalWidth - gap * (3 - 1)) / 3d);
        }
        int singleHeight = singleWidth;

        //根据子view数量确定高度
        LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        setLayoutParams(params);

        for (int i = 0; i < childrenCount; i++) {
            Images images = (Images)listData.get(i);
            CustomImageView childrenView = (CustomImageView) getChildAt(i);
            childrenView.setImageUrl(images.getImgPath());
            childrenView.setTag(i);

            int[] position = findPosition(i);
            int left = (singleWidth + gap) * position[1];//列
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;

            childrenView.layout(left, top, right, bottom);
        }

    }


    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }


    public void setImagesData(List<Images> lists) {
        if (lists == null || lists.isEmpty()) {
            return;
        }
        //初始化布局
        generateChildrenLayout(lists.size());
        //这里做一个重用view的处理
        if (listData == null) {
            int i = 0;
            while (i < lists.size()) {
                CustomImageView iv = generateImageView(i);
                addView(iv,generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldViewCount = listData.size();
            int newViewCount = lists.size();
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = 0; i < newViewCount - oldViewCount; i++) {
                    CustomImageView iv = generateImageView(i+oldViewCount);
                    addView(iv,generateDefaultLayoutParams());
                }
            }
        }
        listData = lists;
        layoutChildrenView();
    }


    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num	row	column
     * 1	   1	1
     * 2	   1	2
     * 3	   1	3
     * 4	   2	2
     * 5	   2	3
     * 6	   2	3
     * 7	   3	3
     * 8	   3	3
     * 9	   3	3
     *
     * @param length
     */
//    private void generateChildrenLayout(int length) {
//        if (length <= 3) {
//            rows = 1;
//            columns = length;
//        } else if (length <= 6) {
//            rows = 2;
//            columns = 3;
//            if (length == 4) {
//                columns = 2;
//            }
//        } else {
//            rows = 3;
//            columns = 3;
//        }
//    }

    private void generateChildrenLayout(int length) {
        if (length <= 2) {
            rows = 1;
            columns = length;
        }else if(length == 3){
            rows = 2;
            columns = 2;
        }
        else if (length <= 6) {
            rows = 2;
            columns = 3;
            if (length == 4) {
                columns = 2;
            }
        } else {
            rows = 3;
            columns = 3;
        }
    }

    private CustomImageView generateImageView(int position) {
        CustomImageView iv = new CustomImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setOnClickListener(mImageViewOnClickListener);
        iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
        iv.setTag(position);
        return iv;
    }


    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    // 图片点击事件
    private View.OnClickListener mImageViewOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(view, (Integer)view.getTag());
            }
        }
    };

    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }


    private void showImagesLayout(int childrenCount){
        if(childrenCount==3){
            int diff = 0;
            int singleWidthBottom = (int) Math.ceil((totalWidth - gap * (childrenCount - 2)) / (childrenCount-1));
            diff = totalWidth-gap-singleWidthBottom*2;
    //            int singleWidthTop = (int) Math.ceil((totalWidth - gap * 0) / (childrenCount-1));
            int singleWidthTop = singleWidthBottom+ singleWidthBottom*1/2;
            //根据子view数量确定高度
            LayoutParams params = getLayoutParams();
            params.height = singleWidthTop+singleWidthBottom + gap;
            setLayoutParams(params);

            for (int i = 0; i < childrenCount; i++) {
                Images images = (Images) listData.get(i);
                CustomImageView childrenView = (CustomImageView) getChildAt(i);
                childrenView.setImageUrl(images.getImgPath());
                childrenView.setTag(i);

                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                if (i == 0) {
                    left = 0;
                    top = 0;
                    right = totalWidth;
                    bottom = singleWidthTop;
                } else if (i == 1) {
                    left = 0;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthTop;
                } else if (i == 2) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom + diff;
                    bottom = top + singleWidthTop;
                }

                childrenView.layout(left, top, right, bottom);
            }
        }else if(childrenCount == 5){
            int diffTop = 0;
            int diffBottom = 0;
            int singleWidthTop = (int) Math.ceil((totalWidth - gap * 1) / 2);
            int singleWidthBottom = (int) Math.ceil((totalWidth - gap * 2) / 3);
            diffTop = totalWidth-gap-singleWidthTop*2;
            diffBottom = totalWidth-gap*2-singleWidthBottom*3;

            //根据子view数量确定高度
            LayoutParams params = getLayoutParams();
            params.height = singleWidthTop+singleWidthBottom + gap;
            setLayoutParams(params);

            for (int i = 0; i < childrenCount; i++) {
                Images images = (Images) listData.get(i);
                CustomImageView childrenView = (CustomImageView) getChildAt(i);
                childrenView.setImageUrl(images.getImgPath());
                childrenView.setTag(i);

                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                if (i == 0) {
                    left = 0;
                    top = 0;
                    right = singleWidthTop;
                    bottom = singleWidthTop;
                }else if(i==1){
                    left = gap+singleWidthTop;
                    top = 0;
                    right =left+singleWidthTop+diffTop;
                    bottom = singleWidthTop;
                }
                else if (i == 2) {
                    left = 0;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthBottom;
                } else if (i == 3) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom ;
                    bottom = top + singleWidthBottom;
                } else if (i == 4) {
                    left = (gap + singleWidthBottom )*2;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom +gap +singleWidthBottom+diffBottom;
                    bottom = top + singleWidthBottom;
                }

                childrenView.layout(left, top, right, bottom);
            }
        }

        else if(childrenCount == 6){
            int diffTop = 0;
            int diffBottom = 0;

            int singleWidthBottom = (int) Math.ceil((totalWidth - gap * 2) / 3);
            int singleWidthTop = totalWidth-gap-singleWidthBottom;
            diffTop = totalWidth-gap-singleWidthTop-singleWidthBottom;
            diffBottom = totalWidth-gap*2-singleWidthBottom*3;

            //根据子view数量确定高度
            LayoutParams params = getLayoutParams();
            params.height = singleWidthTop+singleWidthBottom + gap;
            setLayoutParams(params);

            for (int i = 0; i < childrenCount; i++) {
                Images images = (Images) listData.get(i);
                CustomImageView childrenView = (CustomImageView) getChildAt(i);
                childrenView.setImageUrl(images.getImgPath());
                childrenView.setTag(i);

                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                if (i == 0) {
                    left = 0;
                    top = 0;
                    right = singleWidthTop+diffTop;
                    bottom = singleWidthBottom*2+gap;
                }else if(i==1){
                    left = gap+singleWidthTop;
                    top = 0;
                    right =left+singleWidthBottom;
                    bottom = singleWidthBottom;
                }
                else if(i==2){
                    left = gap+singleWidthTop;
                    top = gap+singleWidthBottom;
                    right =left+singleWidthBottom;
                    bottom = top + singleWidthBottom;
                }
                else if (i == 3) {
                    left = 0;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthBottom;
                } else if (i == 4) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom ;
                    bottom = top + singleWidthBottom;
                } else if (i == 5) {
                    left = (gap + singleWidthBottom )*2;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom +gap +singleWidthBottom+diffBottom;
                    bottom = top + singleWidthBottom;
                }

                childrenView.layout(left, top, right, bottom);
            }
        }

        else if(childrenCount == 7){
            int diff = 0;
            int singleWidthBottom = (int) Math.ceil((totalWidth - gap * 2) / 3);
            diff = totalWidth-gap-singleWidthBottom*2;
            //            int singleWidthTop = (int) Math.ceil((totalWidth - gap * 0) / (childrenCount-1));
            int singleWidthTop = singleWidthBottom+ singleWidthBottom*3/4;
            //根据子view数量确定高度
            LayoutParams params = getLayoutParams();
            params.height = singleWidthTop+singleWidthBottom*2 + gap;
            setLayoutParams(params);

            for (int i = 0; i < childrenCount; i++) {
                Images images = (Images) listData.get(i);
                CustomImageView childrenView = (CustomImageView) getChildAt(i);
                childrenView.setImageUrl(images.getImgPath());
                childrenView.setTag(i);

                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                if (i == 0) {
                    left = 0;
                    top = 0;
                    right = totalWidth;
                    bottom = singleWidthTop;
                } else if (i == 1) {
                    left = 0;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthBottom;
                } else if (i == 2) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom ;
                    bottom = top + singleWidthBottom;
                } else if (i == 3) {
                    left = (gap + singleWidthBottom )*2;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom +gap +singleWidthBottom+diff;
                    bottom = top + singleWidthBottom;
                }else if (i == 4) {
                    left = 0;
                    top = gap + singleWidthTop+gap+singleWidthBottom;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthBottom +gap +singleWidthBottom;
                } else if (i == 5) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop+gap+singleWidthBottom;
                    right = left + singleWidthBottom ;
                    bottom = top + singleWidthBottom+gap +singleWidthBottom;
                } else if (i == 6) {
                    left = (gap + singleWidthBottom )*2;
                    top = gap + singleWidthTop+gap+singleWidthBottom;
                    right = left + singleWidthBottom +gap +singleWidthBottom+diff;
                    bottom = top + singleWidthBottom+gap +singleWidthBottom;
                }

                childrenView.layout(left, top, right, bottom);
            }
        }

        else if(childrenCount == 8){
            int diffTop = 0;
            int diffBottom = 0;
            int singleWidthTop = (int) Math.ceil((totalWidth - gap * 1) / 2);
            int singleWidthBottom = (int) Math.ceil((totalWidth - gap * 2) / 3);
            diffTop = totalWidth-gap-singleWidthTop*2;
            diffBottom = totalWidth-gap*2-singleWidthBottom*3;

            //根据子view数量确定高度
            LayoutParams params = getLayoutParams();
            params.height = singleWidthTop+singleWidthBottom*2 + gap;
            setLayoutParams(params);

            for (int i = 0; i < childrenCount; i++) {
                Images images = (Images) listData.get(i);
                CustomImageView childrenView = (CustomImageView) getChildAt(i);
                childrenView.setImageUrl(images.getImgPath());
                childrenView.setTag(i);

                int left = 0;
                int top = 0;
                int right = 0;
                int bottom = 0;
                if (i == 0) {
                    left = 0;
                    top = 0;
                    right = singleWidthTop;
                    bottom = singleWidthTop;
                }else if(i==1){
                    left = gap+singleWidthTop;
                    top = 0;
                    right =left+singleWidthTop+diffTop;
                    bottom = singleWidthTop;
                }
                else if (i == 2) {
                    left = 0;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthBottom;
                } else if (i == 3) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom ;
                    bottom = top + singleWidthBottom;
                } else if (i == 4) {
                    left = (gap + singleWidthBottom )*2;
                    top = gap + singleWidthTop;
                    right = left + singleWidthBottom +gap +singleWidthBottom+diffBottom;
                    bottom = top + singleWidthBottom;
                }else if (i == 5) {
                    left = 0;
                    top = gap + singleWidthTop+gap+singleWidthBottom;
                    right = left + singleWidthBottom;
                    bottom = top + singleWidthBottom +gap +singleWidthBottom;
                } else if (i == 6) {
                    left = gap + singleWidthBottom;
                    top = gap + singleWidthTop+gap+singleWidthBottom;
                    right = left + singleWidthBottom ;
                    bottom = top + singleWidthBottom+gap +singleWidthBottom;
                } else if (i == 7) {
                    left = (gap + singleWidthBottom )*2;
                    top = gap + singleWidthTop+gap+singleWidthBottom;
                    right = left + singleWidthBottom +gap +singleWidthBottom+diffBottom;
                    bottom = top + singleWidthBottom+gap +singleWidthBottom;
                }

                childrenView.layout(left, top, right, bottom);
            }
        }
    }


}
