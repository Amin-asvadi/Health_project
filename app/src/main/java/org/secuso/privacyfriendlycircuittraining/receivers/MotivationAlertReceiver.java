/**
 * This file is part of Privacy Friendly Circuit Trainer.
 * Privacy Friendly Circuit Trainer is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 * Privacy Friendly Circuit Trainer is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Privacy Friendly Interval Timer. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlycircuittraining.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.secuso.privacyfriendlycircuittraining.R;
import org.secuso.privacyfriendlycircuittraining.activities.MainActivity;
import org.secuso.privacyfriendlycircuittraining.helpers.NotificationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.secuso.privacyfriendlycircuittraining.activities.MotivationAlertTextsActivity.LOG_CLASS;

/**
 * Receives the motivation alert event and notifies the user.
 *
 * @author Tobias Neidig
 * @author Alexander Karakuz
 * @version 20170603
 * @license GNU/GPLv3 http://www.gnu.org/licenses/gpl-3.0.html
 */
public class MotivationAlertReceiver extends WakefulBroadcastReceiver {

    //General
    private Context context;
    public static final int NOTIFICATION_ID = 0;


    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if(NotificationHelper.isMotivationAlertEnabled(context)){
            motivate();
        }
    }

    private void motivate() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //Choose a motivation text
        Set<String> defaultStringSet = new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.pref_default_notification_motivation_alert_messages)));
        List<String> motivationTexts = new ArrayList<>(preferences.getStringSet(context.getString(R.string.pref_notification_motivation_alert_texts),  defaultStringSet));

        if (motivationTexts.size() == 0) {
            Log.e(LOG_CLASS, "Motivation texts are empty. Cannot notify the user.");

            //Reschedule alarm for tomorrow
            if(NotificationHelper.isMotivationAlertEnabled(context)){
                NotificationHelper.setMotivationAlert(context);
            }
            return;
        }

        Collections.shuffle(motivationTexts);
        String motivationText = motivationTexts.get(0);


        // Build the notification
        NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext())
                .setSmallIcon(R.drawable.ic_circletraining_logo_white_24dp)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(motivationText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1000, 1000);
        // Notify
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        //Reschedule alarm for tomorrow
        if(NotificationHelper.isMotivationAlertEnabled(context)){
            NotificationHelper.setMotivationAlert(context);
        }
    }
}