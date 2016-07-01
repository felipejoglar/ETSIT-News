/*
 * Copyright (C) 2016 Felipe Joglar Santos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fjoglar.etsitnews.view.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fjoglar.etsitnews.R;
import com.fjoglar.etsitnews.presenter.SettingsPresenter;
import com.fjoglar.etsitnews.presenter.contracts.SettingsContract;
import com.fjoglar.etsitnews.sync.EtsitSyncAdapter;
import com.fjoglar.etsitnews.view.navigation.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsActivity extends AppCompatActivity
        implements SettingsContract.View, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ETSIT_URL = "http://www.tel.uva.es";
    private static final String AITCYL_URL = "http://www.aitcyl.es";

    private SettingsContract.Presenter mSettingsPresenter;
    private Context mContext;
    int mSelectedIndex;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.notification_switch) SwitchCompat notificationSwitch;
    @BindView(R.id.sync_frequency_period) TextView syncFrequencyPeriod;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        unbinder = ButterKnife.bind(this);
        mContext = this;

        initializeActivity();
    }

    // Registers a shared preference change listener that gets notified when preferences change.
    @Override
    protected void onResume() {
        mSettingsPresenter.start();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    // Unregisters a shared preference change listener.
    @Override
    protected void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    // This gets called after the preference is changed, which is important because we
    // modify our synchronization period here.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sync_frequency_key))) {
            EtsitSyncAdapter.configurePeriodicSync(this,
                    EtsitSyncAdapter.SYNC_INTERVAL,
                    EtsitSyncAdapter.SYNC_FLEXTIME);
        }
        mSettingsPresenter.getCurrentSettings();
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mSettingsPresenter = presenter;
    }

    @Override
    public void setupSettings(boolean isNotificationEnabled, String syncFrequencyPeriod, int index) {
        notificationSwitch.setChecked(isNotificationEnabled);
        this.syncFrequencyPeriod.setText(syncFrequencyPeriod);
        mSelectedIndex = index;
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @OnClick(R.id.notification)
    void notificationSettingClicked() {
        notificationSwitch.setChecked(!notificationSwitch.isChecked());
        mSettingsPresenter.updateNotificationSettings(notificationSwitch.isChecked());
    }

    @OnClick(R.id.notification_switch)
    void notificationSwitchSettingClicked() {
        mSettingsPresenter.updateNotificationSettings(notificationSwitch.isChecked());
    }

    @OnClick(R.id.sync_frequency)
    void showFrequencySelectionDialog() {
        createFrequencySelectionDialog().show();
    }

    @OnClick(R.id.etsit_web)
    void goToEtsitWeb() {
        Navigator.getInstance().openUrl(getContext(), ETSIT_URL);
    }

    @OnClick(R.id.aitcyl_web)
    void goToAitcylWeb() {
        Navigator.getInstance().openUrl(getContext(), AITCYL_URL);
    }

    private void initializeActivity() {
        mSettingsPresenter = new SettingsPresenter(this);
        setUpToolbar();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(R.string.settings_activity_title);
    }

    private AlertDialog createFrequencySelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.SettingsDialog);
        builder.setTitle(R.string.pref_sync_frequency_label);

        String[] items = getResources().getStringArray(R.array.pref_sync_frequency_options);
        builder.setSingleChoiceItems(items, mSelectedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSettingsPresenter.updateSyncFrequencySettings(which);
                        dialog.dismiss();
                    }
                });


        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private Context getContext() {
        return mContext;
    }

}