package de.jbamberger.irremote;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private Map<String, RemoteParser.RemoteDefinition> remotes;
    private Executor exec;
    private TableLayout remoteLayout;
    private RemoteParser.IrDef commands;
    private boolean initMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remoteLayout = findViewById(R.id.main_layout);

        exec = Executors.newSingleThreadExecutor();
        exec.execute(this::loadRemotes);

    }

    public void runIR(String command) {
        final int frequency = commands.getFrequency();
        final int[] code = commands.getCodeMap().get(command);
        exec.execute(() -> {
            ConsumerIrManager mIRManager =
                    (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
            if (mIRManager == null || !mIRManager.hasIrEmitter()) {
                Timber.d("An error occurred while transmitting.");
                return;
            }
            ConsumerIrManager.CarrierFrequencyRange[] range = mIRManager.getCarrierFrequencies();
            for (ConsumerIrManager.CarrierFrequencyRange carrierFrequencyRange : range) {
                if (carrierFrequencyRange.getMinFrequency() <= frequency
                        && frequency <= carrierFrequencyRange.getMaxFrequency()) {
                    mIRManager.transmit(frequency, code);
                    return;
                }
            }
            Timber.d("The required frequency range is not supported.");

        });
//        String command = (String) v.getTag();
//        IRSenderService.startActionSendIrCode(this, Remotes.CEIL_REMOTE, command);
    }

    private void onRemotesReady(Map<String, RemoteParser.RemoteDefinition> remotes) {
        this.remotes = remotes;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (remotes != null) {
            menu.clear();
            remotes.keySet().forEach(menu::add);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (remotes != null) {
            RemoteParser.RemoteDefinition definition = remotes.get(item.getTitle().toString());
            initUi(definition);
        }


        return super.onOptionsItemSelected(item);
    }

    private void onRemotesLoadingFailed() {
        Toast.makeText(this, "Could not load remotes", Toast.LENGTH_LONG).show();
    }

    private void initUi(RemoteParser.RemoteDefinition def) {
        remoteLayout.removeAllViews();
        initMenu = true;
        if (def == null) {
            return;
        }
        this.commands = def.getCommandDefs();

        for (RemoteParser.Control[] row : def.getLayout().getControls()) {
            TableRow tr = new TableRow(this);
            for (RemoteParser.Control control : row) {
                Button b = new Button(this);
                if (control == null) {
                    b.setVisibility(View.INVISIBLE);
                } else {
                    b.setOnTouchListener(new View.OnTouchListener() {
                        private Handler handler;

                        @Override public boolean onTouch(View v, MotionEvent event) {
                            switch(event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    if (handler != null) return true;
                                    handler = new Handler();
                                    handler.post(action);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    if (handler == null) return true;
                                    handler.removeCallbacks(action);
                                    handler = null;
                                    break;
                            }
                            return false;
                        }

                        private Runnable action = new Runnable() {
                            @Override public void run() {
                                exec.execute(() -> runIR(control.getCommand()));
                                handler.postDelayed(this, 300);
                            }
                        };
                    });
                    b.setTag(control.getCommand());
                    b.setText(control.getName());
                }
                tr.addView(b);
            }
            remoteLayout.addView(tr);
        }
    }

    private void loadRemotes() {
        try (InputStream is = getResources().openRawResource(R.raw.remotes)) {
            final String s = Utils.readString(is);
            final RemoteParser p = new RemoteParser();
            final Map<String, RemoteParser.RemoteDefinition> remotes = p.parse(s);
            runOnUiThread(() -> onRemotesReady(remotes));
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(this::onRemotesLoadingFailed);
        }
    }
}
