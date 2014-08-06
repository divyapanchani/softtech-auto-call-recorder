package com.softtech.apps.dropbox;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

public final class NotesAppConfig {
    private NotesAppConfig() {}

  public static final String appKey = "ohs82t8gt2f3snr";
    public static final String appSecret = "n2ae872ljnwk8we";
    public static DbxAccountManager getAccountManager(Context context)
    {
        return DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecret);
    }
}
