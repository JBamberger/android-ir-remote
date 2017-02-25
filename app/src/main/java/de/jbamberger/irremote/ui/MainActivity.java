package de.jbamberger.irremote.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import de.jbamberger.irremote.R;
import de.jbamberger.irremote.service.RemoteNotificationService;
import de.jbamberger.irremote.util.LEDRemoteUIInflater;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";


    Messenger mService;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent startService = new Intent(this, RemoteNotificationService.class);
        startService(startService);
        bindService(startService, serviceConnection, BIND_AUTO_CREATE);

        TableLayout layout = (TableLayout) findViewById(R.id.main_layout);
        LEDRemoteUIInflater inf = new LEDRemoteUIInflater();
        inf.inflateRemoteControlUI(this, layout);


    }

    public void send(View v) {
        ControlNotification.notify(this, "examplestring", 666);
        if (mService != null) {
            Message msg = new Message();
            msg.what = RemoteNotificationService.COM_SEND_CODE;
            msg.arg1 = RemoteNotificationService.IR_CODE_POWER;
            try {
                mService.send(msg);


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
