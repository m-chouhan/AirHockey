package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;

public interface NetworkInterface {
    void fromNetworkObj(SFSObject sfsObject);
    SFSObject toNetworkObj();
}
