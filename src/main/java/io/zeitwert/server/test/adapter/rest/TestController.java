package io.zeitwert.server.test.adapter.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 'appAdmin', 'Application Admin'
// 'admin', 'Advisor or Community Admin'
// 'user', 'Advisor or Community User'
// 'super_user', 'Advisor or Community Super User (elevated privileges)'

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
	@PreAuthorize("hasRole('user')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/super_user")
	@PreAuthorize("hasRole('super_user')")
	public String superUserAccess() {
		return "SuperUser Content.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('admin')")
	public String adminAccess() {
		return "Admin Content.";
	}

	@GetMapping("/appAdmin")
	@PreAuthorize("hasRole('app_admin')")
	public String moderatorAccess() {
		return "Application Admin Content.";
	}

}
