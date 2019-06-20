/*
 * Copyright 2019 Jérôme Wacongne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.c4soft.springaddons.sample.authorization.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	ClientDetailsService clientDetailsService;

	@Override
	public void configure(HttpSecurity security) throws Exception {
		// @formatter:off
		security
			.userDetailsService(new ClientDetailsUserDetailsService(clientDetailsService))
			.requestMatchers()
				.mvcMatchers("/.well-known/jwks.json", "/introspect").and()
				.authorizeRequests()
					.mvcMatchers("/.well-known/jwks.json").permitAll()
					.mvcMatchers("/introspect").hasAuthority("INTROSPECTION_CLIENT")
					.anyRequest().authenticated().and()
			.httpBasic().and()
			.cors().and()
			.csrf()
				.ignoringRequestMatchers(request -> "/introspect".equals(request.getRequestURI()))
				.csrfTokenRepository(new CookieCsrfTokenRepository());
		// @formatter:on
	}

	@Bean
	public CorsConfiguration corsConfiguration() {
		final CorsConfiguration cors = new CorsConfiguration();
		cors.setAllowedOrigins(Arrays.asList("https://localhost:8090", "https://localhost:8443"));
		return cors;
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		//@formatter:off
		return new InMemoryUserDetailsManager(
				org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
					.username("user")
					.password("password")
					.authorities("ROLE_USER")
					.build(),
				org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
					.username("admin")
					.password("password")
					.authorities("ROLE_USER", "showcase:AUTHORIZED_PERSONEL")
					.build(),
				org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
					.username("jpa")
					.password("password")
					.authorities(Collections.emptySet())
					.build(),
				org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
					.username("actuator")
					.password("secret")
					.authorities("ACTUATOR")
					.build());
		// @formatter:on
	}

}