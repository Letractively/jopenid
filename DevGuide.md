# Developer's Guide #

JOpenID is used in web application to enable users sign on by OpenID standards.

This demonstration shows how to use JOpenID in a Servlet to handle user's sign on request.

This sample Servlet can be found [here](http://code.google.com/p/jopenid/source/browse/trunk/src/test/java/org/expressme/openid/MainServlet.java).

```
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String op = request.getParameter("op");
    if (op==null) {
        // check nonce:
        checkNonce(request.getParameter("openid.response_nonce"));
        // get authentication:
        byte[] mac_key = (byte[]) request.getSession().getAttribute(ATTR_MAC);
        String alias = (String) request.getSession().getAttribute(ATTR_ALIAS);
        Authentication authentication = manager.getAuthentication(request, mac_key, alias);
        String identity = authentication.getIdentity();
        String email = authentication.getEmail();
        // TODO: create user if not exist in database:
        showAuthentication(response.getWriter(), identity, email);
    }
    else if ("Google".equals(op)) {
        // redirect to Google sign on page:
        Endpoint endpoint = manager.lookupEndpoint("Google");
        Association association = manager.lookupAssociation(endpoint);
        request.getSession().setAttribute(ATTR_MAC, association.getRawMacKey());
        request.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
        String url = manager.getAuthenticationUrl(endpoint, association);
        response.sendRedirect(url);
    }
    else {
        throw new ServletException("Bad parameter op=" + op);
    }
}
```

We assume that the `MainServlet` above has been mapped to `/openid` in our web application:

If user requests URL `/openid?op=Google`, then the MainServlet will redirect user to the Google's sign on page, and store the mac key of current association into HttpSession for later use.

After sign on, the user will be redirected back to the MainServlet with URL `http://your-domain/openid?parameters....`.

To prevent the replay-attack, OpenID standards use a random String called `nonce` which is different every time when user sign on.
And it is our responsibility to check if the `nonce` is valid.

The nonce String can be got by `request.getParameter("openid.response_nonce")` like this:

```
2009-10-21T02:11:39Zrhco-EsNzi8FtQ
```

It contains two parts: the time "2009-10-21T02:11:39" which time zone is GMT+00:00 and the random String "Zrhco-EsNzi8FtQ".

Web application must check the diff of nonce time and system time is less than 1 hour, and the nonce String is not exist in database.
Then store the nonce into database in case of preventing the hacker using the same URL to sign on.

A cron job which cleans the expired nonce in database may needed.

If the nonce is valid, the Authentication object can be got from request with a pre-stored mac key.
Use `getIdentity()` to get the identifier of user. And email can be got by `getEmail()`, but OP may not returning user's email, which means you may get a `null` value of email.

# How to specify the OP #

When you build the redirect URL for end user to sign on, the Endpoint is lookup by the name of OP:

```
Endpoint endpoint = manager.lookupEndpoint("Google");
```

Actually JOpenID will load all names of OP as well as its Endpoint URL in `openid-providers.properties`:

```
Google = https://www.google.com/accounts/o8/id
Yahoo = http://open.login.yahooapis.com/openid20/www.yahoo.com/xrds
```

If you want to add more OPs, just create your own `openid-providers.properties` and place it under `/WEB-INF/classes/`.

# How to design UI for sign on #

You may list all OPs in sign on page to let users choose their OpenID accounts. For example:

```
<p>Please sign on from:<p>
  <p><a href="/openid?op=Google">Google Account</a></p>
  <p><a href="/openid?op=Yahoo">Yahoo Account</a></p>
```

# How to build JOpenID #

JOpenID uses Maven 2 since 1.08. You need install [Maven 2.2](http://maven.apache.org/) first, and run following command to build:

```
C:\workspace\JOpenID> mvn package
```

The target jar and packaged source jar can be found in 'target' directory.