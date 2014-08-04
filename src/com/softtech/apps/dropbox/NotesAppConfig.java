package com.softtech.apps.dropbox;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

public final class NotesAppConfig {
    private NotesAppConfig() {}

    public static final String appKey = "te07kuf0gy45svr";
    public static final String appSecret = "x2wf1n4g8n4spak";

    public static DbxAccountManager getAccountManager(Context context)
    {
        return DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecret);
    }
}
