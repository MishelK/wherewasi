/*
 * Copyright (c) 2019. Alexis Ringot, Florian Taurer, Matthias Zeppelzauer.
 *
 * This file is part of SoniTalk Android SDK.
 *
 * SoniTalk Android SDK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SoniTalk Android SDK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SoniTalk Android SDK.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kdkvit.wherewasi.sonitalk.utils;

/**
 * Set of constants used e.g. in shared preferences, or as keys for the JSON configuration files.
 * Created by Otakusarea on 27.11.2018.
 */

public class ConfigConstants {
    /*
     * *** WARNING ***
     * The following String values are used as keys for the json configuration files.
     * Please do not modify, or if you do, keep everything in sync.
     */
//    public static final String FREQUENCY_ZERO = "frequency-zero"; // Lowest frequency used in Hz
//    public static final String BIT_PERIOD = "bit-period"; // Bit duration in ms
//    public static final String PAUSE_PERIOD = "pause-period"; // Pause duration in ms
//    public static final String NUMBER_OF_MESSAGE_BLOCKS = "number-of-message-blocks";
//    public static final String NUMBER_OF_FREQUENCIES = "number-of-frequencies";
//    public static final String SPACE_BETWEEN_FREQUENCIES = "space-between-frequencies"; // In Hz
    /*** ***/

    /* NOT used in this version.
    public static final String NUMBER_OF_MAX_CHARACTERS = "number-of-max-characters";
    public static final String NUMBER_OF_PARITY_BYTES = "number-of-parity-bytes";
    // Should these be moved to an app specific class (currently used from the MainActivity to store UI content in SharedPreferences) ?
    public static final String FREQUENCY_OFFSET_FOR_SPECTROGRAM = "frequency-offset-for-spectrogram"; //Used in Decoder demo MainActivity
    public static final String STEPFACTOR = "stepfactor"; //Used in Decoder demo MainActivity
    public static final String SILENT_MODE = "silent-mode"; //Used in Decoder demo MainActivity
    public static final String SIGNAL_TEXT = "signal-text"; //Used in Encoder demo MainActivity
    // Remove, always false
    public static final String IS_PAUSE_ACTIVE = "is-pause-active"; //Used in Encoder demo MainActivity
*/
    public static final byte[] GENERATOR_POLYNOM = new byte[] { 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1}; //CRC-17-CAN : 0x1685B
    public static final String CONTROL_FILLING_CHARACTER = "00011001";
    public static final String FREQUENCY_ZERO = "etprefFrequencyZero";
    public static final String BIT_PERIOD = "etprefBitperiod";
    public static final String PAUSE_PERIOD = "etprefPauseperiod";
    public static final String NUMBER_OF_FREQUENCIES = "lpprefNFrequencies";
    public static final String SPACE_BETWEEN_FREQUENCIES = "etprefFrequencyspace";
    public static final String NUMBER_OF_BYTES = "etprefNMaxBytes";
    public static final String LOUDNESS = "sbprefLoudness";
    public static final String PRESET = "setPresetPreferences";

    public static final String SETTING_FREQUENCY_ZERO_DEFAULT = "18000";
    public static final String SETTING_BIT_PERIOD_DEFAULT = "100";
    public static final String SETTING_PAUSE_PERIOD_DEFAULT = "0";
    public static final String SETTING_NUMBER_OF_FREQUENCIES_DEFAULT = "16";
    public static final String SETTING_SPACE_BETWEEN_FREQUENCIES_DEFAULT = "100";
    public static final String SETTING_NUMBER_OF_BYTES_DEFAULT = "18";
    public static final String SETTING_LOUDNESS_DEFAULT = "70";


    public static final int SETTING_BIT_PERIOD_LOWER_LIMIT = 30;
    public static final int SETTING_BIT_PERIOD_UPPER_LIMIT = 200;
    public static final int SETTING_PAUSE_PERIOD_LOWER_LIMIT = 0;
    public static final int SETTING_PAUSE_PERIOD_UPPER_LIMIT = 200;
    public static final int SETTING_FREQUENCY_ZERO_LOWER_LIMIT = 50;
    public static final int SETTING_FREQUENCY_ZERO_UPPER_LIMIT = 20000;
    public static final int SETTING_NUMBER_OF_BYTES_LOWER_LIMIT = 2;
    public static final int SETTING_NUMBER_OF_BYTES_UPPER_LIMIT = 30;
    public static final int SETTING_SPACE_BETWEEN_FREQUENCIES_LOWER_LIMIT = 50;
    public static final int SETTING_SPACE_BETWEEN_FREQUENCIES_UPPER_LIMIT = 200;
    public static final int SETTING_LOUDNESS_LOWER_LIMIT = 1;
    public static final int SETTING_LOUDNESS_UPPER_LIMIT = 100;

    public static final String PREFERENCE_RESET_PREFERENCES = "resetPreferences";
    public static final String PREFERENCE_PRESET_PREFERENCES = "setPresetPreferences";

    public static final String CURRENT_VOLUME = "current-volume";
    public static final int CURRENT_VOLUME_DEFAULT = 70;

    public static final String SEND_BUTTON_ENABLED = "send-button-enabled";
    public static final String LISTEN_BUTTON_ENABLED = "listen-button-enabled";
    public static final String STOP_LISTEN_BUTTON_ENABLED = "stop-listen-button-enabled";

    public static final String TEXT_TO_SEND = "text-to-send";
}
