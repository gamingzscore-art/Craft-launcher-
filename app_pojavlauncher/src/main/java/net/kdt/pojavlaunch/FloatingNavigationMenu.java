
package net.kdt.pojavlaunch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import net.kdt.pojavlaunch.fragments.MainMenuFragment;
import net.kdt.pojavlaunch.fragments.ProfileTypeSelectFragment;
import net.kdt.pojavlaunch.fragments.SearchModFragment;
import net.kdt.pojavlaunch.prefs.screens.LauncherPreferenceFragment;

public class FloatingNavigationMenu extends FrameLayout {

    private PopupWindow popupWindow;

    public FloatingNavigationMenu(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatingNavigationMenu(@NonNull Context context,
                                  @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable roundedBackground(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);

        TextView menuButton = new TextView(getContext());
        menuButton.setText("▦");
        menuButton.setTextSize(30);
        menuButton.setTextColor(Color.WHITE);
        menuButton.setGravity(Gravity.CENTER);
        menuButton.setBackground(roundedBackground(
                Color.rgb(126, 35, 245), 50));
        menuButton.setElevation(dp(8));
        menuButton.setClickable(true);
        menuButton.setFocusable(true);

        addView(menuButton, new FrameLayout.LayoutParams(
                dp(56), dp(56), Gravity.CENTER));

        menuButton.setOnClickListener(v -> {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                showMenu(menuButton);
            }
        });
    }

    private void showMenu(View anchor) {
        LinearLayout panel = new LinearLayout(getContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(8), dp(8), dp(8), dp(8));
        panel.setBackground(roundedBackground(
                Color.rgb(25, 22, 42), 18));
        panel.setElevation(dp(12));

        addMenuItem(panel, "Home", R.drawable.ic_menu_home, 0);
        addMenuItem(panel, "Versions", R.drawable.ic_edit_profile, 1);
        addMenuItem(panel, "Mods", R.drawable.ic_menu_install_jar, 2);
        addMenuItem(panel, "Settings", R.drawable.ic_menu_settings, 3);

        popupWindow = new PopupWindow(
                panel, dp(190), dp(216), true);

        popupWindow.setBackgroundDrawable(
                roundedBackground(Color.TRANSPARENT, 18));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(dp(12));

        popupWindow.showAsDropDown(
                anchor, -dp(134), -dp(228));
    }

    private void addMenuItem(LinearLayout panel, String title,
                             int iconResource, int destination) {
        LinearLayout item = new LinearLayout(getContext());
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(dp(14), 0, dp(10), 0);
        item.setBackground(roundedBackground(
                Color.TRANSPARENT, 10));
        item.setClickable(true);
        item.setFocusable(true);

        ImageView icon = new ImageView(getContext());
        icon.setImageResource(iconResource);
        icon.setColorFilter(Color.WHITE);

        item.addView(icon, new LinearLayout.LayoutParams(
                dp(25), dp(25)));

        TextView label = new TextView(getContext());
        label.setText(title);
        label.setTextColor(Color.WHITE);
        label.setTextSize(15);
        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        0, LayoutParams.WRAP_CONTENT, 1);
        labelParams.leftMargin = dp(16);
        item.addView(label, labelParams);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, dp(48));
        params.bottomMargin = dp(4);
        panel.addView(item, params);

        item.setOnClickListener(v -> {
            if (popupWindow != null) popupWindow.dismiss();
            navigate(destination);
        });
    }

    private void navigate(int destination) {
        if (!(getContext() instanceof FragmentActivity)) return;

        FragmentActivity activity =
                (FragmentActivity) getContext();

        Fragment current = activity.getSupportFragmentManager()
                .findFragmentById(R.id.container_fragment);

        switch (destination) {
            case 0:
                if (!(current instanceof MainMenuFragment)) {
                    Tools.backToMainMenu(activity);
                }
                break;

            case 1:
                if (!(current instanceof ProfileTypeSelectFragment)) {
                    Tools.swapFragment(activity,
                            ProfileTypeSelectFragment.class,
                            ProfileTypeSelectFragment.TAG, null);
                }
                break;

            case 2:
                if (!(current instanceof SearchModFragment)) {
                    Tools.swapFragment(activity,
                            SearchModFragment.class,
                            SearchModFragment.TAG, null);
                }
                break;

            case 3:
                if (!(current instanceof LauncherPreferenceFragment)) {
                    Tools.swapFragment(activity,
                            LauncherPreferenceFragment.class,
                            LauncherActivity.SETTING_FRAGMENT_TAG, null);
                }
                break;
        }
    }
                      }
