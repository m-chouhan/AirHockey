package com.airhockey;

import com.smartfoxserver.v2.extensions.SFSExtension;

public class AirHockeyExtension extends SFSExtension {

    @Override
    public void init() {
        trace("mahendra in init");
        this.addRequestHandler("ready", MathHandler.class);
    }
}
