package de.jbapps.ledirremote.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;

import de.jbapps.ledirremote.R;
import de.jbapps.ledirremote.RemoteNotificationService;

/**
 * Created: 04.01.2016
 *
 * @author Jannik
 * @version 04.01.2016
 */
public class NotificationInflater {

    private static final String TAG = "NotificationInflater";
    private int gridHeight = 4;
    private int gridWidth = 4;


    public void generateLayout(Context context, RemoteViews root) {
        String[] names = context.getResources().getStringArray(R.array.noti_value);
        int[] codes = context.getResources().getIntArray(R.array.noti_keys);
        int[] colors = context.getResources().getIntArray(R.array.noti_colors);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int columns = names.length / gridWidth;
        Log.d(TAG, "names: " + names.length);
        Log.d(TAG, "codes: " + names.length);
        //int columnHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 256 / columns, context.getResources().getDisplayMetrics());

        for (int i = 0; i < columns; i++) {
            //Log.d(TAG, "i: " + i);
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.row_notification);

            for (int j = 0; j < gridWidth; j++) {
                int index = 0;
                switch (j) {
                    case 0: index = i; break;
                    case 1: index = 5 + i; break;
                    case 2: index = 10 + i; break;
                    case 3: index = 15 + i; break;
                }
                //Log.d(TAG, "j: " + j);
                RemoteViews button = new RemoteViews(context.getPackageName(), R.layout.button_notification);
                Intent intent = new Intent();
                intent.setAction(context.getString(R.string.intentfilter_send_code));
                intent.putExtra(RemoteNotificationService.IR_CODE_NAME, codes[index]);
                button.setOnClickPendingIntent(R.id.button_notification, PendingIntent.getBroadcast(context, index + 5, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                //2button.setInt(R.id.button_notification, "setImageResource", R.drawable.ic_action_power);



                button.setInt(R.id.button_notification, "setColorFilter", colors[index]);
                row.addView(R.id.layout_notification_row, button);
            }
            root.addView(R.id.layout_notification_big_main, row);
        }
    }
}
