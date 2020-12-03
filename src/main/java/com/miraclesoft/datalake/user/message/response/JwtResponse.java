package com.miraclesoft.datalake.user.message.response;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String firstname;
    private String lastname;
    private Collection<? extends GrantedAuthority> authorities;

//    public JwtResponse(String accessToken, String username, Collection<? extends GrantedAuthority> authorities) {
//        this.token = accessToken;
//        this.username = username;
//        this.authorities = authorities;
//    }

    public JwtResponse(String accessToken, String username, String firstname, String lastname,
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.token = accessToken;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.authorities = authorities;
	}

	public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}   
    
}

