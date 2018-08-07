package com.crfsdi.whm.model;

import lombok.Data;

@Data
public class WechatAuthInfo {
    private String openId;
    private String sessionKey;
}
