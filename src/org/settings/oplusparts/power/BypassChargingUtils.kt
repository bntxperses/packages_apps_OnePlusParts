/*
 * Copyright (C) 2018-2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.settings.oplusparts.power

import android.content.Context
import androidx.preference.PreferenceManager
import org.settings.oplusparts.BYPASS_CHARGING_NODE
import org.settings.oplusparts.FileUtils
import org.settings.oplusparts.PREF_BYPASS_CHARGING_MODE

object BypassChargingUtils {

    const val MODE_NORMAL = 0
    const val MODE_STANDARD = 1
    const val MODE_GAMING = 2

    fun isSupported(): Boolean = FileUtils.fileExists(BYPASS_CHARGING_NODE)

    fun getMode(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(PREF_BYPASS_CHARGING_MODE, MODE_NORMAL)
    }

    fun getHardwareMode(): Int {
        val value = FileUtils.readOneLine(BYPASS_CHARGING_NODE)?.trim() ?: return MODE_NORMAL
        // Kernel "1" = Normal (0), Kernel "0" = Bypass (1)
        return if (value == "1") MODE_NORMAL else MODE_STANDARD
    }

    fun setMode(context: Context, mode: Int): Boolean {
        val targetMode = mode.coerceIn(MODE_NORMAL, MODE_STANDARD)
        
        // UI 0 = Kernel 1, UI 1 = Kernel 0
        val kernelValue = if (targetMode == MODE_NORMAL) "1" else "0"
        
        val success = FileUtils.writeLine(BYPASS_CHARGING_NODE, kernelValue)
        if (success) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_BYPASS_CHARGING_MODE, targetMode)
                .apply()
        }
        return success
    }

    fun restoreOnBoot(context: Context) {
        if (!isSupported()) return
        val mode = getMode(context)
        if (mode != MODE_NORMAL) {
            FileUtils.writeLine(BYPASS_CHARGING_NODE, mode.toString())
        }
    }
}
