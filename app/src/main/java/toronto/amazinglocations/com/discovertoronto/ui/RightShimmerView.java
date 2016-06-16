/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import toronto.amazinglocations.com.discovertoronto.R;

public class RightShimmerView extends RelativeLayout {
    private Context mContext;
    private View mView;
    private RelativeLayout mRl;
    private Animation mAnim;

    public RightShimmerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mContext = context;
        
        LayoutInflater li = LayoutInflater.from(context);
        mView = li.inflate(R.layout.shimmer_view, this);
        mRl = (RelativeLayout) mView.findViewById(R.id.rl1);
    }
    
    public void startAnimation() {
        mAnim = AnimationUtils.loadAnimation(mContext, R.anim.shimmer_effect_right);
        mRl.startAnimation(mAnim);
    }

    public void stopAnimation() {
        mRl.clearAnimation();
        mAnim.cancel();
    }
}