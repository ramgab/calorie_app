package com.example.calorieapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Отображение уведомления
        NotificationHelper.showNotification(context, "Напоминание", "Не забудьте внести данные");
    }
}
