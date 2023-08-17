package com.c4_soft.springaddons.security.oidc.starter.reactive.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.WebSession;

public class ReactiveMultiTenantOAuth2PrincipalSupport {
	private static final String OAUTH2_USERS_KEY = "com.c4-soft.spring-addons.oauth2.client.principal-by-issuer";

	public static Collection<Authentication> getAuthentications(WebSession session) {
		@SuppressWarnings("unchecked")
		final var authentications = (Map<String, Authentication>) session.getAttribute(OAUTH2_USERS_KEY);
		return authentications.entrySet().stream().map(Map.Entry::getValue).toList();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Authentication> getAuthenticationsByIssuer(WebSession session) {
		return Optional.ofNullable((Map<String, Authentication>) session.getAttribute(OAUTH2_USERS_KEY)).orElse(new HashMap<String, Authentication>());
	}

	public static Optional<Authentication> getAuthentication(WebSession session, String clientRegistrationId) {
		return Optional.ofNullable(getAuthenticationsByIssuer(session).get(clientRegistrationId));
	}

	public static synchronized void add(WebSession session, String clientRegistrationId, Authentication auth) {
		final var identities = getAuthenticationsByIssuer(session);
		identities.put(clientRegistrationId, auth);
		session.getAttributes().put(OAUTH2_USERS_KEY, identities);
	}

	public static synchronized void remove(WebSession session, String clientRegistrationId) {
		final var identities = getAuthenticationsByIssuer(session);
		identities.remove(clientRegistrationId);
		session.getAttributes().put(OAUTH2_USERS_KEY, identities);
	}
}