## 1. Configurable Extension Namespace Alias ##

Even during JOpenID`'`s intial release, support for OpenID Attribute Exchange is already available. The extension namespace alias used is hard-coded with a value of **_ext1_**. It was Ok to do this back then because Yahoo still doesn't support OpenID Attribute Exchange during those times. But now that Yahoo already does _(see this [article](http://developer.yahoo.net/blog/archives/2009/12/yahoo_openid_now_with_attribute_exchange.html))_, we did some test and found out Yahoo has it`'`s own extension namespace alias requirement. As an RP connecting to Yahoo for OpenID authentication you must use this specific alias or else you won`'`t be able to query any info aside from the Identity. That's why in this version of JOpenID we decided to add support for configurable extension namespace aliases to solve this issue.

### How to use this new feature ###

Using the `MainServlet` from [Developer's Guide](DevGuide.md) as our basis, we modify it`'`s `doGet` method to now look this way:
```
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String op = request.getParameter("op");
    if (op==null) {
        // check nonce:
        checkNonce(request.getParameter("openid.response_nonce"));
        // get authentication:
        HttpSession session = request.getSession();
        byte[] mac_key = (byte[]) session.getAttribute(ATTR_MAC);
        String alias = (String) session.getAttribute(ATTR_ALIAS);
        Authentication authentication = manager.getAuthentication(request, mac_key, alias);
        String identity = authentication.getIdentity();
        String email = authentication.getEmail();
        // TODO: create user if not exist in database:
        showAuthentication(response.getWriter(), identity, email);
    }
    else if ("Google".equals(op) || "Yahoo".equals(op)) {
        // redirect to Google/Yahoo sign on page:
        Endpoint endpoint = manager.lookupEndpoint(op);
        Association association = manager.lookupAssociation(endpoint);
        HttpSession session = request.getSession();
        session.setAttribute(ATTR_MAC, association.getRawMacKey());
        session.setAttribute(ATTR_ALIAS, endpoint.getAlias());
        String url = manager.getAuthenticationUrl(endpoint, association);
        response.sendRedirect(url);
    }
    else {
        throw new ServletException("Bad parameter op=" + op);
    }
}
```
Notice that we are now storing two attributes in session. One is the `ATTR_MAC` and the other one is `ATTR_ALIAS`. The `getAuthentication` method also has an added new parameter, the alias we stored in the session from the previous call.

## 2. Additional Attributes ##

On this version of JOpenID we also added new properties to the `Authentication` class. Aside from `email` you can now also get the `fullname`, `firstname`, `lastname`, `language`, and `gender` as long as they are supported by the chosen OP at runtime.

### How to use this new feature ###

Using again the `MainServlet` from [Developer's Guide](DevGuide.md) as our basis, we modify it`'`s `showAuthentication` method to now look this way:
```
void showAuthentication(PrintWriter pw, Authentication user) {
    pw.print("<html><body>");
    pw.print(" <h2>Hi "+user.getFullname()+"!</h2><p>Congratulations, you have successfully logged-in!</p>");
    pw.print("<p><b>Indentity:</b> "+user.getIdentity()+"<br>");
    pw.print("<b>Email:</b> "+user.getEmail()+"<br>");
    pw.print("<b>Gender:</b> "+user.getGender()+"<br>");
    pw.print("<b>Firstname:</b> "+user.getFirstname()+"<br>");
    pw.print("<b>Lastname:</b> "+user.getLastname()+"<br>");
    pw.print("<b>Language:</b> "+user.getLanguage()+"</p>");
    pw.print("</body></html>");
    pw.flush();
}
```
Do not forget to also modify the `MainServlet``'`s `doGet` method where a call to `showAuthentication` is used:
```
    .
    .
    Authentication authentication = manager.getAuthentication(request, mac_key, alias);
    // TODO: create user if not exist in database:
    showAuthentication(response.getWriter(), authentication);
    .
    .
```

## 3. Sample Web Application ##

Here`'`s a demo application running on Google App Engine. [jopenid-demo](http://jopenid-demo.appspot.com)