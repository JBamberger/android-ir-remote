package de.jbamberger.irremote.service.tile;

import android.service.quicksettings.TileService;

import de.jbamberger.irremote.service.IRSenderService;

public class RemoteTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();

        IRSenderService.startActionSendIrcode(this, IRSenderService.LED_REMOTE_44_KEY, "power"); //FIXME: use correct ir code
    }
}
