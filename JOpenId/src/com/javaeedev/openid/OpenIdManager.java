package com.javaeedev.openid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;


/**
 * Open ID Manager for all open id operation.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class OpenIdManager {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private Map<String, Endpoint> endpointCache = new ConcurrentHashMap<String, Endpoint>();
    private Map<Endpoint, Association> associationCache = new ConcurrentHashMap<Endpoint, Association>();

    private int timeOut = 5000; // 5 seconds
    private String assocQuery = null;
    private String authQuery = null;
    private String returnTo = null;
    private String returnToUrlEncode = null;
    private String realm = null;

    public void setReturnTo(String returnTo) {
        try {
            this.returnToUrlEncode = Utils.urlEncode(returnTo);
        }
        catch(UnsupportedEncodingException e) {
            throw new OpenIdException(e);
        }
        this.returnTo = returnTo;
    }

    public void setRealm(String realm) {
        try {
            this.realm = Utils.urlEncode(realm);
        }
        catch(UnsupportedEncodingException e) {
            throw new OpenIdException(e);
        }
    }

    public void setTimeOut(int timeOutInMilliseconds) {
        this.timeOut = timeOutInMilliseconds;
    }

    public Authentication getAuthentication(HttpServletRequest request, byte[] key) {
        // verify:
        String identity = request.getParameter("openid.identity");
        if (identity==null)
            throw new OpenIdException("Missing 'openid.identity'.");
        if (request.getParameter("openid.invalidate_handle")!=null)
            throw new OpenIdException("Invalidate handle.");
        String sig = request.getParameter("openid.sig");
        if (sig==null)
            throw new OpenIdException("Missing 'openid.sig'.");
        String signed = request.getParameter("openid.signed");
        if (signed==null)
            throw new OpenIdException("Missing 'openid.signed'.");
        if (!returnTo.equals(request.getParameter("openid.return_to")))
            throw new OpenIdException("Bad 'openid.return_to'.");
        // check sig:
        String[] params = signed.split("[\\,]+");
        StringBuilder sb = new StringBuilder(1024);
        for (String param : params) {
            sb.append(param)
              .append(':');
            String value = request.getParameter("openid." + param);
            if (value!=null)
                sb.append(value);
            sb.append('\n');
        }
        String hmac = getHmacSha1(sb.toString(), key);
        if (!sig.equals(hmac))
            throw new OpenIdException("Verify signature failed.");
        // set auth:
        Authentication auth = new Authentication();
        auth.setIdentity(identity);
        auth.setEmail(request.getParameter("openid.ext1.value.email"));
        return auth;
    }

    String getHmacSha1(String data, byte[] key) {
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1_ALGORITHM);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
        }
        catch(NoSuchAlgorithmException e) {
            throw new OpenIdException(e);
        }
        catch(InvalidKeyException e) {
            throw new OpenIdException(e);
        }
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return Base64.encodeBytes(rawHmac);
    }

    public Endpoint lookupEndpoint(String url) {
        Endpoint endpoint = endpointCache.get(url);
        if (endpoint!=null && !endpoint.isExpired())
            return endpoint;
        endpoint = requestEndpoint(url);
        endpointCache.put(url, endpoint);
        return endpoint;
    }

    public Association lookupAssociation(Endpoint endpoint) {
        Association assoc = associationCache.get(endpoint);
        if (assoc!=null && !assoc.isExpired())
            return assoc;
        assoc = requestAssociation(endpoint);
        associationCache.put(endpoint, assoc);
        return assoc;
    }

    public String getAuthenticationUrl(Endpoint endpoint, Association association) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(endpoint.getUrl())
          .append('?')
          .append(getAuthQuery())
          .append("&openid.return_to=")
          .append(returnToUrlEncode)
          .append("&openid.assoc_handle=")
          .append(association.getAssociationHandle());
        if (realm!=null)
            sb.append("&openid.realm=").append(realm);
        return sb.toString();
    }

    Endpoint requestEndpoint(String url) {
        Map<String, Object> map = Utils.httpRequest(
                url,
                "GET",
                "application/xrds+xml",
                null,
                timeOut
        );
        try {
            String content = Utils.getContent(map);
            return new Endpoint(Utils.mid(content, "<URI>", "</URI>"), Utils.getMaxAge(map));
        }
        catch(UnsupportedEncodingException e) {
            throw new OpenIdException(e);
        }
    }

    Association requestAssociation(Endpoint endpoint) {
        Map<String, Object> map = Utils.httpRequest(
                endpoint.getUrl(),
                "POST",
                "*/*",
                getAssocQuery(),
                timeOut
        );
        String content = null;
        try {
            content = Utils.getContent(map);
        }
        catch(UnsupportedEncodingException e) {
            throw new OpenIdException(e);
        }
        Association assoc = new Association();
        try {
            BufferedReader r = new BufferedReader(new StringReader(content));
            for (;;) {
                String line = r.readLine();
                if (line==null)
                    break;
                line = line.trim();
                int pos = line.indexOf(':');
                if (pos!=(-1)) {
                    String key = line.substring(0, pos);
                    String value = line.substring(pos + 1);
                    if ("session_type".equals(key))
                        assoc.setSessionType(value);
                    else if ("assoc_type".equals(key))
                        assoc.setAssociationType(value);
                    else if ("assoc_handle".equals(key))
                        assoc.setAssociationHandle(value);
                    else if ("mac_key".equals(key))
                        assoc.setMacKey(value);
                    else if ("expires_in".equals(key)) {
                        long maxAge = Long.parseLong(value);
                        assoc.setMaxAge(maxAge * 900L); // 90%
                    }
                }
            }
        }
        catch(IOException e) {
            throw new RuntimeException("IOException is impossible!", e);
        }
        return assoc;
    }

    String getAuthQuery() {
        if (authQuery!=null)
            return authQuery;
        List<String> list = new ArrayList<String>();
        list.add("openid.ns=http://specs.openid.net/auth/2.0");
        list.add("openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select");
        list.add("openid.identity=http://specs.openid.net/auth/2.0/identifier_select");
        list.add("openid.mode=checkid_setup");
        list.add("openid.ns.ext1=http://openid.net/srv/ax/1.0");
        list.add("openid.ext1.mode=fetch_request");
        list.add("openid.ext1.type.email=http://schema.openid.net/contact/email");
        list.add("openid.ext1.required=email");
        String query = Utils.buildQuery(list);
        authQuery = query;
        return query;
    }

    String getAssocQuery() {
        if (assocQuery!=null)
            return assocQuery;
        List<String> list = new ArrayList<String>();
        list.add("openid.ns=http://specs.openid.net/auth/2.0");
        list.add("openid.mode=associate");
        list.add("openid.session_type=" + Association.SESSION_TYPE_NO_ENCRYPTION);
        list.add("openid.assoc_type=" + Association.ASSOC_TYPE_HMAC_SHA1);
        String query = Utils.buildQuery(list);
        assocQuery = query;
        return query;
    }
}
