package com.jawnnypoo.openmeh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.util.ColorUtil;
import com.jawnnypoo.openmeh.util.MehPreferencesManager;
import com.jawnnypoo.openmeh.util.MehReminderManager;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Jawn on 4/23/2015.
 */
public class NotificationActivity extends BaseActivity {

    private static final String TIMEPICKER_TAG = "timepicker";

    private static final String EXTRA_THEME = "EXTRA_THEME";

    public static Intent newInstance(Context context) {
        return newInstance(context, null);
    }

    public static Intent newInstance(Context context, Theme theme) {
        Intent intent = new Intent(context, NotificationActivity.class);
        if (theme != null) {
            intent.putExtra(EXTRA_THEME, gson.toJson(theme));
        }
        return intent;
    }

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.toolbar_title)
    TextView toolbarTitle;
    @InjectView(R.id.notification_switch)
    SwitchCompat onOffSwitch;
    @InjectView(R.id.notification_switch_label)
    TextView onOffSwitchLabel;
    @InjectView(R.id.notification_time)
    TextView notifyTime;
    @InjectView(R.id.notification_time_label)
    TextView notifyTimeLabel;
    @InjectView(R.id.notification_sound)
    CheckBox soundCheck;
    @InjectView(R.id.notification_sound_label)
    TextView soundCheckLabel;
    @InjectView(R.id.notification_vibrate)
    CheckBox vibrateCheck;
    @InjectView(R.id.notification_vibrate_label)
    TextView vibrateCheckLabel;

    Calendar timeToAlert = Calendar.getInstance();
    static SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
    TimePickerDialog timePickerDialog;

    private final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
            timeToAlert.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeToAlert.set(Calendar.MINUTE, minute);
            notifyTime.setText(timeformat.format(timeToAlert.getTime()));
            MehPreferencesManager.setNotificationPreferenceHour(NotificationActivity.this, hourOfDay);
            MehPreferencesManager.setNotificationPreferenceMinute(NotificationActivity.this, minute);
            MehReminderManager.scheduleDailyReminder(NotificationActivity.this, hourOfDay, minute);
            //Recreate for next time, starting with the newly set time
            timePickerDialog.setStartTime(hourOfDay, minute);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.action_notifications);
        String themeJson = getIntent().getStringExtra(EXTRA_THEME);
        if (!TextUtils.isEmpty(themeJson)) {
            Theme theme = gson.fromJson(themeJson, Theme.class);
            applyTheme(theme);
        }
        timeToAlert.set(Calendar.HOUR_OF_DAY, MehPreferencesManager.getNotificationPreferenceHour(this));
        timeToAlert.set(Calendar.MINUTE, MehPreferencesManager.getNotificationPreferenceMinute(this));
        setupUi();
        timePickerDialog = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
        if (timePickerDialog == null) {
            timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, timeToAlert.get(Calendar.HOUR_OF_DAY), timeToAlert.get(Calendar.MINUTE), false, false);
        }
    }

    private void applyTheme(Theme theme) {
        //Tint widgets
        int accentColor = theme.getAccentColor();
        int foreGround = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
        ColorUtil.setTint(onOffSwitch, accentColor, foreGround);
        ColorUtil.setTint(soundCheck, accentColor, foreGround);
        ColorUtil.setTint(vibrateCheck, accentColor, foreGround);
        toolbarTitle.setTextColor(theme.getBackgroundColor());
        toolbar.setBackgroundColor(accentColor);
        toolbar.getNavigationIcon().setColorFilter(theme.getBackgroundColor(), PorterDuff.Mode.MULTIPLY);
        ColorUtil.setStatusBarAndNavBarColor(getWindow(), ColorUtil.getDarkerColor(accentColor));
        getWindow().getDecorView().setBackgroundColor(theme.getBackgroundColor());
        onOffSwitchLabel.setTextColor(foreGround);
        notifyTime.setTextColor(foreGround);
        notifyTimeLabel.setTextColor(foreGround);
        soundCheckLabel.setTextColor(foreGround);
        vibrateCheckLabel.setTextColor(foreGround);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.fade_out);
    }

    private void setupUi() {
        onOffSwitch.setChecked(MehPreferencesManager.getNotificationsPreference(this));
        soundCheck.setChecked(MehPreferencesManager.getNotificationSound(this));
        vibrateCheck.setChecked(MehPreferencesManager.getNotificationVibrate(this));
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationsPreference(NotificationActivity.this, isChecked);
                if (isChecked) {
                    MehReminderManager.restoreReminderPreference(NotificationActivity.this);
                } else {
                    MehReminderManager.cancelPendingReminders(NotificationActivity.this);
                }
            }
        });

        notifyTime.setText(timeformat.format(timeToAlert.getTime()));
        soundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationSound(NotificationActivity.this, isChecked);
            }
        });
        vibrateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationVibrate(NotificationActivity.this, isChecked);
            }
        });
    }

    @OnClick(R.id.notification_switch_root)
    void onOffClick(View view) {
        onOffSwitch.setChecked(!onOffSwitch.isChecked());
    }

    @OnClick(R.id.notification_time_root)
    void onTimeClick(View view) {
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

    }

    @OnClick(R.id.notification_sound_root)
    void onSoundClick(View view) {
        soundCheck.setChecked(!soundCheck.isChecked());
    }

    @OnClick(R.id.notification_vibrate_root)
    void onVibrateClick(View view) {
        vibrateCheck.setChecked(!vibrateCheck.isChecked());
    }
}
