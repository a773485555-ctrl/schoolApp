package com.school.management.core.util

object Constants {
    const val BASE_URL = "http://10.0.2.2:5000/"
    const val DATABASE_NAME = "school_management_db"
    const val ENCRYPTED_PREFS_NAME = "school_secure_prefs"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val SYNC_WORK_NAME = "school_sync_work"
    const val SYNC_IMMEDIATE_WORK_NAME = "school_sync_immediate"
    const val SYNC_PERIODIC_INTERVAL_MINUTES = 15L
    const val MAX_SYNC_RETRIES = 3
    const val PAGE_SIZE = 20
    const val DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd"
    const val TIME_FORMAT_DISPLAY = "hh:mm a"
}
