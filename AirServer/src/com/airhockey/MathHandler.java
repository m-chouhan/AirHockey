package com.airhockey;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class MathHandler extends BaseClientRequestHandler {
    @Override
    public void handleClientRequest(User user, ISFSObject params) {
        int a = params.getInt("a");
        int b = params.getInt("b");

        ISFSObject returnVal = new SFSObject();
        returnVal.putInt("sum", a+b);
        getParentExtension().send("start", returnVal, user);
    }
}
