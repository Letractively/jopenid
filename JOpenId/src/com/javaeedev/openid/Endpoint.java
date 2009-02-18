package com.javaeedev.openid;

import java.text.SimpleDateFormat;

/**
 * Endpoint for OP, and it will be cached in memory.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public final class Endpoint {

    private final String url;
    private final long expired;

    public Endpoint(String url, long maxAgeInMilliSeconds) {
        if (url==null)
            throw new NullPointerException("Url is null.");
        this.url = url;
        this.expired = System.currentTimeMillis() + maxAgeInMilliSeconds;
    }

    public String getUrl() {
        return url;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expired;
    }

    @Override
    public boolean equals(Object o) {
        if (o==this)
            return true;
        if (o instanceof Endpoint) {
            return ((Endpoint)o).url.equals(this.url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("Endpoint [uri:")
          .append(url)
          .append(", expired:")
          .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expired))
          .append(']');
        return sb.toString();
    }
}
