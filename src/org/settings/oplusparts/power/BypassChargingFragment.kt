/*
 * Copyright (C) 2018-2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.settings.oplusparts.power

import android.os.Bundle
import androidx.preference.Preference
import com.android.settingslib.widget.SettingsBasePreferenceFragment
import com.android.settingslib.widget.SliderPreference
import org.settings.oplusparts.R

class BypassChargingFragment : SettingsBasePreferenceFragment(),
    Preference.OnPreferenceChangeListener {

    private lateinit var statusPref: Preference
    private lateinit var sliderPref: SliderPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.bypass_charging_settings, rootKey)

        statusPref = findPreference("bypass_charging_status")!!
        sliderPref = findPreference("bypass_charging_slider")!!

        sliderPref.min = 0
        sliderPref.max = 1
        sliderPref.sliderIncrement = 1
        sliderPref.setTickVisible(true)
        sliderPref.setHapticFeedbackMode(SliderPreference.HAPTIC_FEEDBACK_MODE_ON_TICKS)

        val currentMode = BypassChargingUtils.getMode(requireContext())
        sliderPref.value = currentMode
        sliderPref.onPreferenceChangeListener = this

        updateStatusText(currentMode)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if (preference.key == "bypass_charging_slider") {
            val mode = (newValue as Int).coerceIn(0, 1)
            val success = BypassChargingUtils.setMode(requireContext(), mode)
            if (success) {
                updateStatusText(mode)
            }
            return success
        }
        return false
    }

    private fun updateStatusText(mode: Int) {
        val (titleRes, descRes) = when (mode) {
            BypassChargingUtils.MODE_STANDARD ->
                Pair(R.string.bypass_charging_mode_1_title, R.string.bypass_charging_mode_1_desc)
            else ->
                Pair(R.string.bypass_charging_mode_0_title, R.string.bypass_charging_mode_0_desc)
        }
        statusPref.title = getString(titleRes)
        statusPref.summary = getString(descRes)
    }
}
