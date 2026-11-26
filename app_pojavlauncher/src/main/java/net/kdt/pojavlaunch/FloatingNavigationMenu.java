
package net.kdt.pojavlaunch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable roundedBackground(
            int color, int radius) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);

        HexagonMenuButton menuButton =
                new HexagonMenuButton(getContext());

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

    private class HexagonMenuButton extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path hexagon = new Path();

        HexagonMenuButton(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setClickable(true);
            setFocusable(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float w = getWidth();
            float h = getHeight();

            float inset = dp(5);

            hexagon.reset();
            hexagon.moveTo(w / 2f, inset);
            hexagon.lineTo(w - inset, h * 0.28f);
            hexagon.lineTo(w - inset, h * 0.72f);
            hexagon.lineTo(w / 2f, h - inset);
            hexagon.lineTo(inset, h * 0.72f);
            hexagon.lineTo(inset, h * 0.28f);
            hexagon.close();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(31, 25, 44));
            paint.setShadowLayer(
                    dp(9), 0, 0,
                    Color.rgb(126, 35, 245));
            canvas.drawPath(hexagon, paint);
            paint.clearShadowLayer();

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.rgb(170, 105, 255));
            canvas.drawPath(hexagon, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);

            float square = dp(7);
            float gap = dp(3);
            float centerX = w / 2f;
            float centerY = h / 2f;

            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    float left = centerX - square - gap / 2f
                            + col * (square + gap);
                    float top = centerY - square - gap / 2f
                            + row * (square + gap);

                    canvas.drawRoundRect(
                            new RectF(left, top,
                                    left + square, top + square),
                            dp(1.5f), dp(1.5f), paint);
                }
            }
        }
    }

    private void showMenu(View anchor) {

        LinearLayout panel = new LinearLayout(getContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(8), dp(8), dp(8), dp(8));

        GradientDrawable panelBackground =
                roundedBackground(
                        Color.rgb(25, 21, 36), 16);

        panelBackground.setStroke(
                dp(1), Color.rgb(126, 75, 190));

        panel.setBackground(panelBackground);
        panel.setElevation(dp(14));

        addMenuItem(panel, "Home",
                R.drawable.ic_menu_home, 0);

        addMenuItem(panel, "Versions",
                R.drawable.ic_edit_profile, 1);

        addMenuItem(panel, "Mods",
                R.drawable.ic_menu_install_jar, 2);

        addMenuItem(panel, "Settings",
                R.drawable.ic_menu_settings, 3);

        popupWindow = new PopupWindow(
                panel, dp(190), dp(224), true);

        popupWindow.setBackgroundDrawable(
                roundedBackground(Color.TRANSPARENT, 16));

        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(dp(14));

        popupWindow.showAsDropDown(
                anchor, -dp(134), -dp(228));
    }

    private void addMenuItem(
            LinearLayout panel,
            String title,
            int iconResource,
            int destination) {

        LinearLayout item = new LinearLayout(getContext());
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(dp(14), 0, dp(10), 0);

        item.setBackground(
                roundedBackground(
                        Color.TRANSPARENT, 10));

        item.setClickable(true);
        item.setFocusable(true);

        ImageView icon = new ImageView(getContext());
        icon.setImageResource(iconResource);
        icon.setColorFilter(
                Color.rgb(190, 145, 255));

        item.addView(icon, new LinearLayout.LayoutParams(
                dp(24), dp(24)));

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
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            navigate(destination);
        });
    }

    private void navigate(int destination) {

        if (!(getContext() instanceof FragmentActivity)) return;

        FragmentActivity activity =
                (FragmentActivity) getContext();

        Fragment current = activity
                .getSupportFragmentManager()
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
