package com.airhockey.entities;

import com.smartfoxserver.v2.entities.data.SFSObject;

public interface SFSInterface {
    void fromSfs(SFSObject sfsObject);
    SFSObject toSfs();
}
