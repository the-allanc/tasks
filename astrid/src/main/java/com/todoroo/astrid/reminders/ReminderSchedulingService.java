/**
 * Copyright (c) 2012 Todoroo Inc
 *
 * See the file "LICENSE" for the full license governing this code.
 */
package com.todoroo.astrid.reminders;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.todoroo.andlib.service.ContextManager;
import com.todoroo.andlib.utility.AndroidUtilities;
import com.todoroo.astrid.alarms.AlarmService;

import org.tasks.injection.InjectingService;
import org.tasks.scheduling.RefreshScheduler;

import javax.inject.Inject;

/**
 * Schedules reminders in the background to prevent ANR's
 *
 * @author Tim Su
 *
 */
public class ReminderSchedulingService extends InjectingService {

    @Inject RefreshScheduler refreshScheduler;

    /** Receive the alarm - start the synchronize service! */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContextManager.setContext(ReminderSchedulingService.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                delaySchedulingToPreventANRs();
                scheduleReminders();
                stopSelf();
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void scheduleReminders() {
        try {
            ReminderService.getInstance().scheduleAllAlarms();
            AlarmService.getInstance().scheduleAllAlarms();
            refreshScheduler.scheduleAllAlarms();

        } catch (Exception e) {
            Log.e("reminder-scheduling", "reminder-startup", e);
        }
    }

    private void delaySchedulingToPreventANRs() {
        AndroidUtilities.sleepDeep(5000L);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
