package de.jbamberger.irremote.service.tile;

import android.service.quicksettings.TileService;

import de.jbamberger.irremote.service.IRSenderService;

public class RemoteTileService extends TileService {
    private static final String TAG = "RemoteTileService";

    @Override
    public void onClick() {
        super.onClick();

        IRSenderService.startActionSendIrcode(this, 0, "power"); //FIXME: use correct ir code
    }
}
