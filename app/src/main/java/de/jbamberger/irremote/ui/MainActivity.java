package de.jbamberger.irremote.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import de.jbamberger.irremote.R;
import de.jbamberger.irremote.receiver.AutoStartReceiver;
import de.jbamberger.irremote.service.RemoteNotificationService;
import de.jbamberger.irremote.util.LEDRemoteUIInflater;


public class MainActivity extends AppCompatActivity {

    private Messenger mService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
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

        TableLayout layout = findViewById(R.id.main_layout);
        LEDRemoteUIInflater inf = new LEDRemoteUIInflater();
        inf.inflateRemoteControlUI(this, layout);
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

    private boolean isAutoStartEnabled() {
        ComponentName component = new ComponentName(this, AutoStartReceiver.class);

        int status = getPackageManager().getComponentEnabledSetting(component);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            try {
                return getPackageManager().getReceiverInfo(component, 0).isEnabled();
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        } else {
            return status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
    }

    private void toggleAutoStartReceiver(boolean enable) {
        ComponentName component = new ComponentName(this, AutoStartReceiver.class);
        if (enable) {
            getPackageManager().setComponentEnabledSetting(component,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            getPackageManager().setComponentEnabledSetting(component,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }
}
