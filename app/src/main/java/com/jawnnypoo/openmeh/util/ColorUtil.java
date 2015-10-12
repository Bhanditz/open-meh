package com.jawnnypoo.openmeh.util;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;

import com.jawnnypoo.openmeh.R;

import java.util.ArrayList;

/**
 * Created by John on 4/20/2015.
 */
public class ColorUtil {

    private static float[] hsv = new float[3];
    public static ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public static LinearInterpolator linearInterpolator = new LinearInterpolator();
    public static AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();

    public static int getDarkerColor(int color) {
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public static void setStatusBarAndNavBarColor(Window window, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }

    public static void animateStatusBarAndNavBarColors(Window window, int endColor, int duration) {
        if (Build.VERSION.SDK_INT >= 21) {
            statusBar(window, window.getStatusBarColor(), endColor, duration);
            navigationBar(window, window.getNavigationBarColor(), endColor, duration);
        }
    }

    public static ColorStateList createColorStateList(int color) {
        return ColorStateList.valueOf(color);
    }

    public static ColorStateList createColorStateList(int color, int pressed) {
        return new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{}
        }, new int[]{
                pressed,
                color
        });
    }

    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static int getBackgroundColor(View v) {
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable) {
            return ((ColorDrawable) background).getColor();
        } else {
            return Color.TRANSPARENT;
        }
    }

    public static Animator backgroundColor(View v, int endColor, int duration) {
        ObjectAnimator oa = ObjectAnimator.ofObject(v, "backgroundColor", argbEvaluator,
                getBackgroundColor(v), endColor);
        oa.setDuration(duration);
        oa.setInterpolator(linearInterpolator);
        oa.start();
        return oa;
    }

    public static Animator statusBar(Window window, int startColor, int endColor, int duration) {
        ObjectAnimator oa = ObjectAnimator.ofObject(window, "statusBarColor", argbEvaluator,
                startColor, endColor);
        oa.setDuration(duration);
        oa.setInterpolator(linearInterpolator);
        oa.start();
        return oa;
    }

    public static Animator navigationBar(Window window, int startColor, int endColor, int duration) {
        ObjectAnimator oa = ObjectAnimator.ofObject(window, "navigationBarColor", argbEvaluator,
                startColor, endColor);
        oa.setDuration(duration);
        oa.setInterpolator(linearInterpolator);
        oa.start();
        return oa;
    }

    public static void setTint(CheckBox box, int color, int unpressedColor) {
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        }, new int[]{
                unpressedColor,
                color
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            box.setButtonTintList(sl);
        } else {
            Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(box.getContext(), R.drawable.abc_btn_check_material));
            DrawableCompat.setTintList(drawable, sl);
            box.setButtonDrawable(drawable);
        }
    }

    public static void setTint(SwitchCompat switchCompat, int color, int unpressedColor) {
        ColorStateList sl = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        }, new int[]{
                unpressedColor,
                color
        });
        DrawableCompat.setTintList(switchCompat.getThumbDrawable(), sl);
    }

    public static void setMenuItemsColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable icon = menu.getItem(i).getIcon();
            if (icon != null) {
                icon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    /**
     * Sets the color of the overflow menu item. You most likely need to use a {@link android.view.ViewTreeObserver.OnGlobalLayoutListener} with this to make it work
     * @param activity activity
     * @param color color to set the overflow icon to
     */
    public static void setOverflowColor(final Activity activity, int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getWindow().getDecorView().findViewsWithText(outViews, overflowDescription,
                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        if (outViews.isEmpty()) {
            return;
        }
        TintImageView overflow=(TintImageView) outViews.get(0);
        overflow.setColorFilter(color);
    }

}
