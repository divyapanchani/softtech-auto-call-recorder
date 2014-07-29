package com.softtech.apps.dropbox;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

public final class NotesAppConfig {
    private NotesAppConfig() {}

    public static final String appKey = "8zovt5anppc5xm4";
    public static final String appSecret = "zhvqogfcrw42mrd";

    public static DbxAccountManager getAccountManager(Context context)
    {
        return DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecret);
    }
}
