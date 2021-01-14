package com.codestown.examinoo.helper;

import com.codestown.examinoo.R;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.internal.ScrimInsetsFrameLayout;
import com.google.android.material.navigation.NavigationView;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.os.Build;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.lang.reflect.Field;

public class ArcNavigationView extends NavigationView {

    private static int THRESHOLD;

    private ArcViewSettings settings;
    private int height = 0;
    private int width = 0;
    private Path clipPath, arcPath;

    public ArcNavigationView(Context context) {
        super(context);
        init(context, null);
    }

    public ArcNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        settings = new ArcViewSettings(context, attrs);
        settings.setElevation(ViewCompat.getElevation(this));

        /**
         * If hardware acceleration is on (default from API 14), clipPath worked correctly
         * from API 18.
         *
         * So we will disable hardware Acceleration if API < 18
         *
         * https://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
         * Section #Unsupported Drawing Operations
         */
        setBackgroundColor(Color.TRANSPARENT);
     //   setInsetsColor(Color.TRANSPARENT);
        THRESHOLD = Math.round(ArcViewSettings.dpToPx(getContext(), 14)); //some threshold for child views while remeasuring them
    }

    private void setInsetsColor(int color) {
        try {
            Field insetForegroundField = ScrimInsetsFrameLayout.class.getDeclaredField("mInsetForeground");
            insetForegroundField.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(color);
            insetForegroundField.set(this, colorDrawable);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RtlHardcoded")
    private Path createClipPath() {
        final Path path = new Path();
        arcPath = new Path();

        float arcWidth = settings.getArcWidth();
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) getLayoutParams();
        if (settings.isCropInside()) {
            if (layoutParams.gravity == Gravity.START ) {
                arcPath.moveTo(width, 0);
                arcPath.quadTo(width - arcWidth, height / 2,
                        width, height);
                arcPath.close();

                path.moveTo(0, 0);
                path.lineTo(width, 0);
                path.quadTo(width - arcWidth, height / 2,
                        width, height);
                path.lineTo(0, height);
                path.close();
            } else if (layoutParams.gravity == Gravity.END ) {
                arcPath.moveTo(0, 0);
                arcPath.quadTo(arcWidth, height / 2,
                        0, height);
                arcPath.close();

                path.moveTo(width, 0);
                path.lineTo(0, 0);
                path.quadTo(arcWidth, height / 2,
                        0, height);
                path.lineTo(width, height);
                path.close();
            }
        } else {
            if (layoutParams.gravity == Gravity.START ) {
                arcPath.moveTo(width - arcWidth / 2, 0);
                arcPath.quadTo(width + arcWidth / 2, height / 2,
                        width - arcWidth / 2, height);
                arcPath.close();

                path.moveTo(0, 0);
                path.lineTo(width - arcWidth / 2, 0);
                path.quadTo(width + arcWidth / 2, height / 2,
                        width - arcWidth / 2, height);
                path.lineTo(0, height);
                path.close();
            } else if (layoutParams.gravity == Gravity.END ) {
                arcPath.moveTo(arcWidth / 2, 0);
                arcPath.quadTo(-arcWidth / 2, height / 2,
                        arcWidth / 2, height);
                arcPath.close();

                path.moveTo(width, 0);
                path.lineTo(arcWidth / 2, 0);
                path.quadTo(-arcWidth / 2, height / 2,
                        arcWidth / 2, height);
                path.lineTo(width, height);
                path.close();
            }
        }
        return path;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            calculateLayoutAndChildren();
        }
    }


    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (child instanceof NavigationMenuView) {
            child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        } else {
            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        }
    }

    private void calculateLayoutAndChildren() {
        if (settings == null) {
            return;
        }
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        if (width > 0 && height > 0) {
            clipPath = createClipPath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewCompat.setElevation(this, settings.getElevation());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setOutlineProvider(new ViewOutlineProvider() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void getOutline(View view, Outline outline) {
                            if (clipPath.isConvex()) {
                                outline.setConvexPath(clipPath);
                            }
                        }
                    });
                }
            }

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View v = getChildAt(i);

                if (v instanceof NavigationMenuView) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        v.setBackground(settings.getBackgroundDrawable());
                    } else {
                        v.setBackgroundDrawable(settings.getBackgroundDrawable());
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        ViewCompat.setElevation(v, settings.getElevation());
                    }
                    //TODO: adjusting child views to new width in their rightmost/leftmost points related to path
//                    adjustChildViews((ViewGroup) v);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SuppressLint("RtlHardcoded")
    private void adjustChildViews(ViewGroup container) {
        final int containerChildCount = container.getChildCount();
        PathMeasure pathMeasure = new PathMeasure(arcPath, false);
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) getLayoutParams();

        for (int i = 0; i < containerChildCount; i++) {
            View child = container.getChildAt(i);
            if (child instanceof ViewGroup) {
                adjustChildViews((ViewGroup) child);
            } else {
                float pathCenterPointForItem[] = {0f, 0f};
                Rect location = locateView(child);
                int halfHeight = location.height() / 2;

                pathMeasure.getPosTan(location.top + halfHeight, pathCenterPointForItem, null);
                if (layoutParams.gravity == Gravity.END ) {
                    int centerPathPoint = getMeasuredWidth() - Math.round(pathCenterPointForItem[0]);
                    if (child.getMeasuredWidth() > centerPathPoint) {
                        child.measure(MeasureSpec.makeMeasureSpec(centerPathPoint - THRESHOLD,
                                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                                child.getMeasuredHeight(), MeasureSpec.EXACTLY));
                        child.layout(centerPathPoint + THRESHOLD, child.getTop(), child.getRight(), child.getBottom());
                    }
                } else if (layoutParams.gravity == Gravity.START ) {
                    if (child.getMeasuredWidth() > pathCenterPointForItem[0]) {
                        child.measure(MeasureSpec.makeMeasureSpec((Math.round(pathCenterPointForItem[0]) - THRESHOLD),
                                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                                child.getMeasuredHeight(), MeasureSpec.EXACTLY));
                        child.layout(child.getLeft(), child.getTop(), (Math.round(pathCenterPointForItem[0]) - THRESHOLD), child.getBottom());
                    }
                }

                //set text ellipsize to end to prevent it from overlapping edge
                if (child instanceof TextView) {
                    ((TextView) child).setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        }
    }

    private Rect locateView(View view) {
        Rect loc = new Rect();
        int[] location = new int[2];
        if (view == null) {
            return loc;
        }
        view.getLocationOnScreen(location);

        loc.left = location[0];
        loc.top = location[1];
        loc.right = loc.left + view.getWidth();
        loc.bottom = loc.top + view.getHeight();
        return loc;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        canvas.clipPath(clipPath);
        ;
        super.dispatchDraw(canvas);

        canvas.restore();
    }

    public static class ArcViewSettings {
        public final static int CROP_INSIDE = 0;
        public final static int CROP_OUTSIDE = 1;
        private boolean cropInside = true;
        private float arcWidth;
        private float elevation;
        private Drawable backgroundDrawable = new ColorDrawable(Color.WHITE); //default background color of navigation view

        public static float dpToPx(Context context, int dp) {
            Resources r = context.getResources();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        }

        public ArcViewSettings(Context context, AttributeSet attrs) {
            TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ArcDrawer, 0, 0);
            arcWidth = styledAttributes.getDimension(R.styleable.ArcDrawer_arc_width, dpToPx(context, 10));

            final int cropDirection = styledAttributes.getInt(R.styleable.ArcDrawer_arc_cropDirection, CROP_INSIDE);
            cropInside = (cropDirection == CROP_INSIDE);

            int[] attrsArray = new int[]{
                    android.R.attr.background,
                    android.R.attr.layout_gravity,
            };

            TypedArray androidAttrs = context.obtainStyledAttributes(attrs, attrsArray);
            backgroundDrawable = androidAttrs.getDrawable(0);

            androidAttrs.recycle();
            styledAttributes.recycle();
        }

        public float getElevation() {
            return elevation;
        }

        public void setElevation(float elevation) {
            this.elevation = elevation;
        }

        public boolean isCropInside() {
            return cropInside;
        }

        public float getArcWidth() {
            return arcWidth;
        }

        public Drawable getBackgroundDrawable() {
            return backgroundDrawable;
        }
    }
}
