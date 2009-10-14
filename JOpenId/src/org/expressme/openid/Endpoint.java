package org.expressme.openid;

import java.text.SimpleDateFormat;

/**
 * Endpoint for OpenID Provider, and will be cached in memory for a certain time.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public final class Endpoint {

    private final String url;
    private final long expired;

    /**
     * Build Endpoint object by url and max age.
     * 
     * @param url URL of endpoint.
     * @param maxAgeInMilliSeconds Max age in milliseconds.
     */
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

    /**
     * Two Endpoints are equal and only equal if their urls are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o==this)
            return true;
        if (o instanceof Endpoint) {
            return ((Endpoint)o).url.equals(this.url);
        }
        return false;
    }

    /**
     * The hash code of Endpoint is equal to its url's hash code.
     */
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
