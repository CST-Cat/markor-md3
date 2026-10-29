package net.gsantner.markor.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import net.gsantner.markor.R;
import net.gsantner.markor.model.AppSettings;
import net.gsantner.markor.util.MarkorContextUtils;
import net.gsantner.opoc.frontend.base.GsActivityBase;
import net.gsantner.opoc.frontend.base.GsFragmentBase;

public abstract class MarkorBaseActivity extends GsActivityBase<AppSettings, MarkorContextUtils> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _appSettings.applyAppTheme();
        if (_appSettings.isClaudeTheme()) {
            // Optional Claude color scheme: remap the MD3 color roles to
            // the DESIGN.md palette. Must run before any content view is
            // inflated (subclasses call setContentView after super).
            getTheme().applyStyle(R.style.ThemeOverlay_Markor_Claude, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
            getWindow().setExitTransition(null);
            if (_appSettings.isReduceMotionEnabled()) {
                // E-ink / reduced motion: kill the shared-element transitions
                // the theme declares as well, not just enter/exit.
                getWindow().setSharedElementEnterTransition(null);
                getWindow().setSharedElementExitTransition(null);
                getWindow().setReturnTransition(null);
                getWindow().setReenterTransition(null);
            }
        }
        _cu.setAppLanguage(this, _appSettings.getLanguage());
        if (_appSettings.isHideSystemStatusbar()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyActivityBarBackgroundColors();
    }

    protected boolean onReceiveKeyPress(GsFragmentBase fragment, int keyCode, KeyEvent event) {
        return fragment.onReceiveKeyPress(keyCode, event);
    }

    @Override
    public Integer getNewNavigationBarColor() {
        // MD3: the system navigation bar follows the tonal bar surface
        // instead of being forced black; the pure-black theme keeps black.
        if (_appSettings != null && _appSettings.getAppThemeName().contains("black")) {
            return Color.BLACK;
        }
        return _appSettings != null ? _appSettings.getConfiguredBarBackgroundColor() : null;
    }

    @Override
    public Integer getNewActivityBackgroundColor() {
        return _appSettings.getAppThemeName().contains("black") ? Color.BLACK : null;
    }

    public void applyActivityBarBackgroundColors() {
        final int backgroundColor = _appSettings.getConfiguredBarBackgroundColor();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(backgroundColor);
        }

        _cu.setStatusbarColor(this, backgroundColor);
    }

    @Override
    protected AppSettings createAppSettingsInstance() {
        return new AppSettings(this);
    }

    @Override
    protected MarkorContextUtils createContextUtilsInstance() {
        return new MarkorContextUtils(this);
    }

    @Override
    public Boolean isFlagSecure() {
        return _appSettings.isDisallowScreenshots();
    }
}
