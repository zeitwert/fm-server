package io.zeitwert.server.test.adapter.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 'APP-ADMIN', 'Application Admin (Super User)'
// 'SAAS-USER', 'SaaS User'
// 'SAAS-ADMIN', 'SaaS Admin'
// 'COMMUNITY-USER', 'Community User'
// 'COMMUNITY-ADMIN', 'Community Admin'

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController("sessionTestController")
@RequestMapping("/api/test")
public class TestController {

	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	@GetMapping("/any")
	public String anyAccess() {
		return "Authorized Content.";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('APP-ADMIN') or hasRole('SAAS-USER') or hasRole('SAAS-ADMIN') or hasRole('COMMUNITY-USER') or hasRole('COMMUNITY-ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/app")
	@PreAuthorize("hasRole('SAAS-ADMIN')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('SAAS-ADMIN') or hasRole('COMMUNITY-ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}

}
