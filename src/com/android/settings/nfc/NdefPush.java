/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.nfc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import com.android.settings.R;

public class NdefPush extends Fragment
        implements CompoundButton.OnCheckedChangeListener {
    private View mView;
    private AnimationDrawable mAnimation;
    private ImageView mImageView;
    private NfcAdapter mNfcAdapter;
    private Switch mActionBarSwitch;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();

        mActionBarSwitch = new Switch(activity);

        if (activity instanceof PreferenceActivity) {
            PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
            if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                final int padding = activity.getResources().getDimensionPixelSize(
                        R.dimen.action_bar_switch_padding);
                mActionBarSwitch.setPadding(0, 0, padding, 0);
                activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                activity.getActionBar().setCustomView(mActionBarSwitch, new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | Gravity.RIGHT));
                activity.getActionBar().setTitle(R.string.ndef_push_settings_title);
            }
        }

        mActionBarSwitch.setOnCheckedChangeListener(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        mActionBarSwitch.setChecked(mNfcAdapter.isNdefPushEnabled());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ndefpush, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View view) {
        mActionBarSwitch.setOnCheckedChangeListener(this);
        mActionBarSwitch.setChecked(mNfcAdapter.isNdefPushEnabled());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImageView = (ImageView) getActivity().findViewById(R.id.ndef_push_image);
        mImageView.setBackgroundResource(R.drawable.ndef_push_anim);
        mAnimation = (AnimationDrawable) mImageView.getBackground();

    }

    @Override
    public void onPause() {
        super.onPause();
        mAnimation.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        // This is nasty: the animation can only be started once the fragment
        // is attached to the window, and there are no callbacks for that.
        mImageView.post(new Runnable() {
            public void run() {
                mAnimation.start();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean desiredState) {
        boolean success = false;
        mActionBarSwitch.setEnabled(false);
        if (desiredState) {
            success = mNfcAdapter.enableNdefPush();
        } else {
            success = mNfcAdapter.disableNdefPush();
        }
        if (success) {
            mActionBarSwitch.setChecked(desiredState);
        }
        mActionBarSwitch.setEnabled(true);
    }
}