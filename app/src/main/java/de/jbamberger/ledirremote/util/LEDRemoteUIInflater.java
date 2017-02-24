package de.jbamberger.ledirremote.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TableLayout;
import android.widget.TableRow;

import de.jbamberger.ledirremote.R;
import de.jbamberger.ledirremote.service.RemoteNotificationService;

/**
 * Created: 04.01.2016
 *
 * @author Jannik
 * @version 04.01.2016
 */
public class LEDRemoteUIInflater {

    private static final String TAG = "LEDRemoteUIInflater";
    private int gridHeight = 4;
    private int gridWidth = 4;


    public void generateLargeNotificationLayout(Context context, RemoteViews root) {
        String[] names = context.getResources().getStringArray(R.array.noti_value);
        int[] codes = context.getResources().getIntArray(R.array.noti_keys);
        int[] colors = context.getResources().getIntArray(R.array.noti_colors);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int columns = names.length / gridWidth;
        Log.d(TAG, "names: " + names.length);
        Log.d(TAG, "codes: " + names.length);
        //int columnHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 256 / columns, context.getResources().getDisplayMetrics());

        for (int i = 0; i < columns; i++) {
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.row_notification);

            for (int j = 0; j < gridWidth; j++) {
                int index = 0;
                switch (j) {
                    case 0:
                        index = i;
                        break;
                    case 1:
                        index = 5 + i;
                        break;
                    case 2:
                        index = 10 + i;
                        break;
                    case 3:
                        index = 15 + i;
                        break;
                }

                RemoteViews button = new RemoteViews(context.getPackageName(), R.layout.button_notification);
                Intent intent = new Intent();
                intent.setAction(context.getString(R.string.intentfilter_send_code));
                intent.putExtra(RemoteNotificationService.IR_CODE_NAME, codes[index]);
                button.setOnClickPendingIntent(R.id.button_notification, PendingIntent.getBroadcast(context, index + 5, intent, PendingIntent.FLAG_UPDATE_CURRENT));

                button.setInt(R.id.button_notification, "setColorFilter", colors[index]);
                row.addView(R.id.layout_notification_row, button);
            }
            root.addView(R.id.layout_notification_big_main, row);
        }
    }

    public void inflateRemoteControlUI(final Context context, ViewGroup container) {
        String[] names = context.getResources().getStringArray(R.array.noti_value);
        int[] codes = context.getResources().getIntArray(R.array.noti_keys);
        int[] colors = context.getResources().getIntArray(R.array.noti_colors);
        TypedArray images = context.getResources().obtainTypedArray(R.array.noti_images);
        inflateRemoteControlUI(context, container, names, codes, colors, images);
        images.recycle();
    }

    private void inflateRemoteControlUI(final Context context, ViewGroup container, String[] names, int[] codes, int[] colors, TypedArray images) {
        if (!(container instanceof TableLayout)) {
            throw new IllegalArgumentException("container must be of type TableLayout");
        }
        if(names.length != codes.length) {
            throw new IllegalArgumentException("names and codes must have equal length");
        }
        int columns = names.length / gridWidth;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < columns; i++) {
            TableRow row = new TableRow(context);
            container.addView(row);
            for (int j = 0; j < gridWidth; j++) {
                int index = 0;
                switch (j) {
                    case 0:
                        index = i;
                        break;
                    case 1:
                        index = 5 + i;
                        break;
                    case 2:
                        index = 10 + i;
                        break;
                    case 3:
                        index = 15 + i;
                        break;
                }
                final int code = codes[index];
                ImageButton button = (ImageButton) inflater.inflate(R.layout.button_notification, row, false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(context.getString(R.string.intentfilter_send_code));
                        intent.putExtra(RemoteNotificationService.IR_CODE_NAME, code);
                        context.sendBroadcast(intent);
                    }
                });
                button.setImageResource(images.getResourceId(index, R.drawable.ic_action_play));
                button.setColorFilter(colors[index]);
                row.addView(button);
            }
        }
    }
}
