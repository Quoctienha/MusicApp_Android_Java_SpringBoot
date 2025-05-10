package com.example.musicapp.command;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NavigateToActivityCommand implements Command {
    private final Context context;
    private final Class<?> targetActivity;
    private final Bundle extras;
    private final int flags;

    public NavigateToActivityCommand(Context context, Class<?> targetActivity) {
        this(context, targetActivity, null, 0);  // Chỉ truyền context và targetActivity nếu không có extras và flags
    }

    public NavigateToActivityCommand(Context context, Class<?> targetActivity, Bundle extras) {
        this(context, targetActivity, extras, 0);  // Trường hợp có extras nhưng không có flags
    }

    public NavigateToActivityCommand(Context context, Class<?> targetActivity, int flags) {
        this(context, targetActivity, null, flags);  // Trường hợp chỉ có flags
    }

    public NavigateToActivityCommand(Context context, Class<?> targetActivity, Bundle extras, int flags) {
        this.context = context;
        this.targetActivity = targetActivity;
        this.extras = extras;
        this.flags = flags;
    }

    @Override
    public void execute() {
        Intent intent = new Intent(context, targetActivity);

        // Thêm extras nếu có
        if (extras != null) {
            intent.putExtras(extras);
        }

        // Thêm flags nếu có
        if (flags != 0) {
            intent.setFlags(flags);
        }

        context.startActivity(intent);
    }
}
