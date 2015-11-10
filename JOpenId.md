<table><tr><td>
<h1>Overview</h1>

JOpenID is a very fast, lightweight Java client library for<br>
OpenID 2.0 specification. You can use JOpenID in your web site to<br>
enable users to sign on using their OpenID account without registration.<br>
<br>
See <a href='QuickStart.md'>Quick Start</a> to learn how JOpenID works.<br>
<br>
See <a href='DevGuide.md'>Developer's Guide</a> to learn how to use JOpenID in Java web applications.<br>
<br>
See <a href='ConfigExt.md'>Updates</a> to learn more about the new features included in the latest version of JOpenID.<br>
</td><td><wiki:gadget url="http://www.ohloh.net/p/317293/widgets/project_basic_stats.xml" height="220" border="1"/><br>
</td></tr></table>
# Terminology #

## OpenID ##

OpenID is an open, decentralized standard for authenticating users which can be used for access control, allowing users to log on to different services with the same digital identity where these services trust the authentication body.

## Identifier ##

An Identifier that was presented by the end user to your web site.

## OP ##

OP stands for OpenID Provider. An OP authentication server is which your web site relies for an assertion that
the end user owns an Identifier.

## RP ##

RP stands for Relying Party. It is your web site which want to use OpenID authentication.

## Alias ##

Alias refers to the extension namespace alias used by RP when requesting aditional information through OpenID Attribute Exchange.