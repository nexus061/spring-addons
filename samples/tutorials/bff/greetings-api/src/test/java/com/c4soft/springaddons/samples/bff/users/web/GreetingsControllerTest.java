package com.c4soft.springaddons.samples.bff.users.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithAnonymousUser;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.c4_soft.springaddons.security.oauth2.test.annotations.parameterized.ParameterizedAuthentication;
import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.c4_soft.springaddons.security.oauth2.test.webmvc.MockMvcSupport;

@WebMvcTest(controllers = GreetingsController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
class GreetingsControllerTest {

	@Autowired
	MockMvcSupport api;

	@Autowired
	WithJwt.AuthenticationFactory authFactory;

	@Test
	@WithAnonymousUser
	void givenRequestIsAnonymous_whenGetPublicGreeting_thenUnauthorized() throws Exception {
		api.get("/greetings/public").andExpect(status().isUnauthorized());
	}

	@Test
	@WithAnonymousUser
	void givenRequestIsAnonymous_whenGetNiceGreeting_thenUnauthorized() throws Exception {
		api.get("/greetings/nice").andExpect(status().isUnauthorized());
	}

	@ParameterizedTest
	@MethodSource("allIdentities")
	void givenUserIsAuthenticated_whenGetPublicGreeting_thenOk(@ParameterizedAuthentication Authentication auth) throws Exception {
		api.get("/greetings/public").andExpect(status().isOk()).andExpect(
				jsonPath("$.message").value(
						"Hi %s! You are authenticated by %s and granted with: %s.".formatted(
								auth.getName(),
								((JwtAuthenticationToken) auth).getTokenAttributes().get(JwtClaimNames.ISS),
								auth.getAuthorities())));
	}

	@Test
	@WithJwt("ch4mp.json")
	void givenUserIsCh4mp_whenGetNiceGreeting_thenOk() throws Exception {
		api.get("/greetings/nice").andExpect(status().isOk()).andExpect(
				jsonPath("$.message")
						.value("Dear ch4mp! You are authenticated by https://oidc.c4-soft.com/auth/realms/spring-addons and granted with: [NICE, AUTHOR]."));
	}

	@Test
	@WithJwt("tonton-pirate.json")
	void givenUserIsTontonPirate_whenGetNiceGreeting_thenForbidden() throws Exception {
		api.get("/greetings/nice").andExpect(status().isForbidden());
	}

	private Stream<AbstractAuthenticationToken> allIdentities() {
		final var authentications = authFactory.authenticationsFrom("ch4mp.json", "tonton-pirate.json").toList();
		return authentications.stream();
	}
}
