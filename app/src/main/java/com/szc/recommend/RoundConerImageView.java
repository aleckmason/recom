package com.szc.recommend;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


/**
 * Created by Administrator on 2019/6/14.
 */
public class RoundConerImageView extends ImageView {
    float width, height;
    private Context mContext;
    public RoundConerImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public RoundConerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        init(context, attrs);
    }

    public RoundConerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int radia = (int) (12 * SystemUtils.getScale(mContext));
        if (width >= radia && height > radia) {
            Path path = new Path();
            //四个圆角
            path.moveTo(radia, 0);
            path.lineTo(width - radia, 0);
            path.quadTo(width, 0, width, radia);
            path.lineTo(width, height - radia);
            path.quadTo(width, height, width - radia, height);
            path.lineTo(radia, height);
            path.quadTo(0, height, 0, height - radia);
            path.lineTo(0, radia);
            path.quadTo(0, 0, radia, 0);
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
