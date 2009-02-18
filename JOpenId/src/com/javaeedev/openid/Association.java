package com.javaeedev.openid;

import java.text.SimpleDateFormat;

/**
 * Assocation between RP and OP, and it will be cached in memory.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class Association {

    public static final String SESSION_TYPE_NO_ENCRYPTION = "no-encryption";
    public static final String ASSOC_TYPE_HMAC_SHA1 = "HMAC-SHA1";

    private String session_type;
    private String assoc_type;
    private String assoc_handle;
    private String mac_key;
    private byte[] raw_mac_key;
    private long expired;

    public String getSessionType() { return session_type; }
    public void setSessionType(String session_type) { this.session_type = session_type; }

    public String getAssociationType() { return assoc_type; }
    public void setAssociationType(String assoc_type) { this.assoc_type = assoc_type; }

    public String getAssociationHandle() { return assoc_handle; }
    public void setAssociationHandle(String assoc_handle) { this.assoc_handle = assoc_handle; }

    public String getMacKey() { return mac_key; }
    public void setMacKey(String mac_key) {
        this.mac_key = mac_key;
        this.raw_mac_key = Base64.decode(mac_key);
    }

    public byte[] getRawMacKey() {
        return raw_mac_key;
    }

    public void setMaxAge(long maxAgeInMilliseconds) {
        this.expired = System.currentTimeMillis() + maxAgeInMilliseconds;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expired;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("Association [")
          .append("session_type:").append(session_type).append(", ")
          .append("assoc_type:").append(assoc_type).append(", ")
          .append("assoc_handle:").append(assoc_handle).append(", ")
          .append("mac_key:").append(mac_key).append(", ")
          .append("expired:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expired))
          .append(']');
        return sb.toString();
    }
}
