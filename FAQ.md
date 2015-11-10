# FAQ #

Here are some issues you may encounter when using JOpenID.

# How to specify realm #

The realm must start with `http://` or `https://`. For example:

```
manager.setRealm("http://www.example.com");
manager.setRealm("http://*.example.com");
```

The importance is that, the Identifier of an end user (returned by OP) is different if the realm changes. For example:

| **Realm** | **Identifier** |
|:----------|:---------------|
| http://www.example.com | http://www.google.com/accounts/o8/id?id=ABC |
| http://*.example.com | http://www.google.com/accounts/o8/id?id=XYZ |

If your web site has sub-domains such as `bbs.example.com`, `blog.example.com`, etc., we
strongly recommend you specify the realm as `http://*.example.com` to make all domains share one identifier.

# How to debug on local machine #

Assume that you have own the domain `example.com`, and to debug the web application you can simply add one line in your `hosts` file:

```
www.example.com 127.0.0.1
```

Then you can debug on local machine.

The file `hosts` is under `C:\Windows\System32\drivers\etc` in Windows, and `/etc/hosts` in Linux/UNIX.

# Error when using non-standard port #

You may failed to login by OpenID if your web application runs on non-standard port (e.g. 8080). Many OpenID providers only allow standard port (http 80 and https 443). So make sure your web application runs on 80 port.

# I saw some web sites allow users enter their emails or URLs to find the OP's sign on page #

Yes OpenID standards contains two parts: **discovery** and **authentication**. Discovery is the process where the RP uses email or URL to look up the address of OP.
It is complex, and may confuse the end users.

Instead, Web UI designer should give end users the chooses of their familiar OpenID accounts, such as Google account, Yahoo account, etc.

JOpenID only supports OpenID authentication because the discovery protocol is hard to use, and difficult to implement. You will find that JOpenID meets your 99.9% requirements.

# How can I add more OpenID Providers #

As we discussed before, JOpenID does not support discovery, which means you should provide a list of OPs to end users.
All OPs are list in `openid-providers.properties` file. If you want to add more OPs, try to find their URL with a little effort.
If you think JOpenID should contain more popular OPs, please add a comment with the OP's URL.