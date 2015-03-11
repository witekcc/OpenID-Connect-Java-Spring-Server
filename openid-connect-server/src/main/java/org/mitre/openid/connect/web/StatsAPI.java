/*******************************************************************************
 * Copyright 2015 The MITRE Corporation
 *   and the MIT Kerberos and Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.mitre.openid.connect.web;

import java.util.Map;

import org.mitre.openid.connect.service.StatsService;
import org.mitre.openid.connect.view.JsonEntityView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/stats")
public class StatsAPI {
	// Logger for this class
	private static final Logger logger = LoggerFactory.getLogger(StatsAPI.class);

	@Autowired
	private StatsService statsService;

	@Autowired
	private WebResponseExceptionTranslator providerExceptionHandler;

	@RequestMapping(value = "summary", produces = MediaType.APPLICATION_JSON_VALUE)
	public String statsSummary(ModelMap m) {

		Map<String, Integer> e = statsService.getSummaryStats();

		m.put("entity", e);

		return JsonEntityView.VIEWNAME;

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "byclientid", produces = MediaType.APPLICATION_JSON_VALUE)
	public String statsByClient(ModelMap m) {
		Map<Long, Integer> e = statsService.getByClientId();

		m.put("entity", e);

		return JsonEntityView.VIEWNAME;
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "byclientid/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String statsByClientId(@PathVariable("id") Long id, ModelMap m) {
		Integer e = statsService.getCountForClientId(id);

		m.put("entity", e);

		return JsonEntityView.VIEWNAME;
	}

	@ExceptionHandler(OAuth2Exception.class)
	public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
		logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
		return providerExceptionHandler.translate(e);
	}
}
