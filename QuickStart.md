# Quick Start #

This demonstration shows how JOpenID works, and it let an end user sign on by their Google account.
Note that the Google account is an OpenID provider.

This sample class can be found [here](http://code.google.com/p/jopenid/source/browse/trunk/JOpenId/src/org/expressme/openid/Main.java).

Let's start with a `main()`:

```
public void main() throws Exception {
    // TODO: our OpenID sign on code here...
}
```

First, create an OpenIdManager instance and set your web site domain:

```
OpenIdManager manager = new OpenIdManager();
manager.setReturnTo("http://www.openid-example.com/openId");
manager.setRealm("http://*.openid-example.com");
```

The `setReturnTo` is the URL which handles the information returned from OP. Usually
it is a Servlet or Action of an MVC such as Struts.

The `setRealm` is the realm that the Identifier returned from OP contains.

Next, we lookup the Endpoint URL of Google:

```
Endpoint endpoint = manager.lookupEndpoint("Google");
System.out.println(endpoint);
```

The Endpoint URL tells us how to redirect the user to the Google sign on page.
You may see the Endpoint URL information like:

```
Endpoint [uri:https://www.google.com/accounts/o8/ud, expired:2009-10-22 03:09:48]
```

JOpenID will cache the Endpoint URL before the expired time.

Then we need to get the Association from the Endpoint URL of Google:

```
Association association = manager.lookupAssociation(endpoint);
System.out.println(association);
```

Association is something like a secure key shared by your web site and Google.
You may see the Association information like:

```
Association [session_type:no-encryption, assoc_type:HMAC-SHA1, assoc_handle:AOQobUe..., mac_key:wqgE0la..., expired:2009-10-21 21:51:47]
```

JOpenID will cache the Association before the expired time.

After the Association is setup between your web site and Google, we can redirect the end user to Google's sign on page:

```
String url = manager.getAuthenticationUrl(endpoint, association);
System.out.println("Copy the authentication URL in browser:\n" + url);
```

Here is a Java command program, so we copy the URL printed bellow to the address bar in browser:

```
https://www.google.com/accounts/o8/ud?openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns.ext1=http%3A%2F%2Fopenid.net%2Fsrv%2Fax%2F1.0&openid.ext1.mode=fetch_request&openid.ext1.type.email=http%3A%2F%2Fschema.openid.net%2Fcontact%2Femail&openid.ext1.required=email&openid.return_to=http%3A%2F%2Fwww.openid-example.com%2FopenId&openid.assoc_handle=AOQobUeshQjKifAdj2d7QML9uO8ZAiIfeo6rXc86LfDjovqtYzISdGw0&openid.realm=http%3A%2F%2F*.openid-example.com
```

The sign on URL is not short, so make sure the whole address is copied correctly. Then you can see the Google's sign on page:

![http://jopenid.googlecode.com/svn/wiki/sign-on-with-google.png](http://jopenid.googlecode.com/svn/wiki/sign-on-with-google.png)

Enter your email and password, then Google will ask you if allow sending your email to
web site `openid-example.com`:

![http://jopenid.googlecode.com/svn/wiki/approval.png](http://jopenid.googlecode.com/svn/wiki/approval.png)

If you press `Allow`, then Google will redirect you to `www.openid-example.com` with OpenID Identifier, encrypted association handler:

![http://jopenid.googlecode.com/svn/wiki/returning-url.png](http://jopenid.googlecode.com/svn/wiki/returning-url.png)

Of cause the URL is not exist because the domain `www.openid-example.com` is invalid host. In real world you need specify to your own domain.
But we can still continue to finish the sign on process:

```
System.out.println("After successfully sign on in browser, enter the URL of address bar in browser:");
String ret = readLine();
HttpServletRequest request = createRequest(ret);
Authentication authentication = manager.getAuthentication(request, association.getRawMacKey());
System.out.println(authentication);
System.out.println("Identity: " + authentication.getIdentity());
```

Copy the URL in address bar of browser, and paste into command window of our Java program, using `readLine()` to get the URL,
and call `manager.getAuthentication()` to get the Authentication of the signed on user.
You may see the Authentication information like:

```
Authentication [identity:https://www.google.com/accounts/o8/id?id=AItOawnOG_Y4Sl45ysLNTJkeDFCJyh5nNXY2Z2s, email:askxuefeng@gmail.com]
Identity: https://www.google.com/accounts/o8/id?id=AItOawnOG_Y4Sl45ysLNTJkeDFCJyh5nNXY2Z2s
```

Yes the `https://www.google.com/accounts/o8/id?id=AItOawnOG_Y4Sl45ysLNTJkeDFCJyh5nNXY2Z2s` is the identifier that proves an end user
has his/her Google account and successfully signed on. Your web site now can trust this user.

You may notice that the OpenID sign on process needs HttpServletRequest object, which means it must run on a Java web server.
In our command-line program, we created a mock HttpServletRequest object. See [how to use JOpenID in web application](DevGuide.md).