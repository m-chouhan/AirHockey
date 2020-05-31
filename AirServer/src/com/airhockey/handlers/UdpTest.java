package com.airhockey.handlers;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class UdpTest extends BaseClientRequestHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject isfsObject) {
        trace("udp request received :)");
    }
}
