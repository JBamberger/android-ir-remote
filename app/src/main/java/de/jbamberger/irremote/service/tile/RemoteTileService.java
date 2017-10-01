package de.jbamberger.irremote.service.tile;

import android.service.quicksettings.TileService;

import de.jbamberger.irremote.service.ir.IRSenderService;

import static de.jbamberger.irremote.service.ir.Remotes.LED_REMOTE_44_KEY;


public class RemoteTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();

        IRSenderService.startActionSendIrCode(this, LED_REMOTE_44_KEY, "power"); //FIXME: use correct ir code
    }
}
