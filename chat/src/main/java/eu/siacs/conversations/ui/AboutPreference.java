package eu.siacs.conversations.ui;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

import com.yourbestigor.chat.BuildConfig;
import com.yourbestigor.chat.R;

public class AboutPreference extends Preference {
    public AboutPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setSummaryAndTitle(context);
    }

    public AboutPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setSummaryAndTitle(context);
    }

    private void setSummaryAndTitle(final Context context) {
        setSummary(String.format("%s %s", BuildConfig.APP_NAME, "1.0"));
        setTitle(context.getString(R.string.title_activity_about_x, BuildConfig.APP_NAME));
    }

    @Override
    protected void onClick() {
        super.onClick();
        final Intent intent = new Intent(getContext(), AboutActivity.class);
        getContext().startActivity(intent);
    }
}

