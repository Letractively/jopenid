package com.javaeedev.openid;

/**
 * Authentication information returned from OP.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class Authentication {

    private String identity;
    private String email;

    public String getIdentity() { return identity; }
    public void setIdentity(String identity) { this.identity = identity; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("Authentication [")
          .append("identity:").append(identity).append(", ")
          .append("email:").append(email)
          .append(']');
        return sb.toString();
    }
}
