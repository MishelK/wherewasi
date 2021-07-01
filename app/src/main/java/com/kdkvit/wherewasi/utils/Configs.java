package com.kdkvit.wherewasi.utils;

/**
 * Appplication variable configuration file, controls key application service timing and criteria
 */
public final class Configs {
    public static final String APPLICATION_ID = "com.kdkvit.wherewasi";
    public static final String server_url = "https://wherewasi-be.herokuapp.com/";
    public static final int SIGNIFICANT_TIME = 30 * 60 * 1000;
    public static final int ADVERTISING_DELAY = 30 * 1000;
    public static final int SCANNING_DELAY = 60 * 1000;
    public static final int SENDING_DELAY = 10 * 1000;
    public static final int TIME_BETWEEN_CHECKING_LOCATIONS = 60 * 1000;
    public static final double KM_BETWEEN_LOCATIONS = 0.1;
    public static final int IDLE_DURATION = 15 * 60 * 1000;
    public static final int CONTACT_DURATION = 15 * 60 * 1000;
    public static final int TIME_BETWEEN_NEW_LOCATIONS = 15 * 60 * 1000;
    public static final int TIME_BETWEEN_LOCATION_AND_INTERACTION = 15 * 60 * 1000;
    public static final int TIME_STAYED = 15 * 60 * 1000;
}

