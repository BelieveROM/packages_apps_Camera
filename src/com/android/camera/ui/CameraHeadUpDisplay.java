/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.camera.ui;

import android.content.Context;

import com.android.camera.CameraSettings;
import com.android.camera.IconListPreference;
import com.android.camera.ListPreference;
import com.android.camera.PreferenceGroup;

public class CameraHeadUpDisplay extends HeadUpDisplay {

    private static final String TAG = "CamcoderHeadUpDisplay";

    private OtherSettingsIndicator mOtherSettings;
    private GpsIndicator mGpsIndicator;
    private ZoomIndicator mZoomIndicator;
    private Context mContext;

    public CameraHeadUpDisplay(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void initializeIndicatorBar(
            Context context, PreferenceGroup group) {
        super.initializeIndicatorBar(context, group);

        ListPreference prefs[] = getListPreferences(group,
                CameraSettings.KEY_FOCUS_MODE,
                CameraSettings.KEY_EXPOSURE,
                CameraSettings.KEY_SCENE_MODE,
                CameraSettings.KEY_PICTURE_SIZE,
                CameraSettings.KEY_JPEG_QUALITY,
                CameraSettings.KEY_COLOR_EFFECT,
                CameraSettings.KEY_METERING_MODE);

        mOtherSettings = new OtherSettingsIndicator(context, prefs);
        mOtherSettings.setOnRestorePreferencesClickedRunner(new Runnable() {
            public void run() {
                if (mListener != null) {
                    mListener.onRestorePreferencesClicked();
                }
            }
        });
        mIndicatorBar.addComponent(mOtherSettings);

        mGpsIndicator = new GpsIndicator(
                context, group, (IconListPreference)
                group.findPreference(CameraSettings.KEY_RECORD_LOCATION));
        mIndicatorBar.addComponent(mGpsIndicator);

        addIndicator(context, group, CameraSettings.KEY_WHITE_BALANCE);
        addIndicator(context, group, CameraSettings.KEY_FLASH_MODE);
    }

    public void setZoomListener(ZoomControllerListener listener) {
        // The rendering thread won't access listener variable, so we don't
        // need to do concurrency protection here
        mZoomIndicator.setZoomListener(listener);
    }

    public void setZoomIndex(int index) {
        GLRootView root = getGLRootView();
        if (root != null) {
            synchronized (root) {
                mZoomIndicator.setZoomIndex(index);
            }
        } else {
            mZoomIndicator.setZoomIndex(index);
        }
    }

    public void setGpsHasSignal(final boolean hasSignal) {
        GLRootView root = getGLRootView();
        if (root != null) {
            synchronized (root) {
                mGpsIndicator.setHasSignal(hasSignal);
            }
        } else {
            mGpsIndicator.setHasSignal(hasSignal);
        }
    }

    /**
     * Sets the zoom rations the camera driver provides. This methods must be
     * called before <code>setZoomListener()</code> and
     * <code>setZoomIndex()</code>
     */
    public void setZoomRatios(float[] zoomRatios) {
        GLRootView root = getGLRootView();
        if (root != null) {
            synchronized(root) {
                setZoomRatiosLocked(zoomRatios);
            }
        } else {
            setZoomRatiosLocked(zoomRatios);
        }
    }

    private void setZoomRatiosLocked(float[] zoomRatios) {
        if (mZoomIndicator == null) {
            mZoomIndicator = new ZoomIndicator(mContext);
            mIndicatorBar.addComponent(mZoomIndicator);
        }
        mZoomIndicator.setZoomRatios(zoomRatios);
    }
}
