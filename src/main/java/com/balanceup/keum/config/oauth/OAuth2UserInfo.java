package com.balanceup.keum.config.oauth;

public interface OAuth2UserInfo {

	String getProviderId();

	String getProvider();

	String getEmail();

	String getName();

}
