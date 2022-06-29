package io.zeitwert.server.test.adapter.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 'appAdmin', 'Application Admin (Super User)'
// 'admin', 'Advisor or Account Admin'
// 'user', 'Advisor or Account User'

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

	@GetMapping("/admin")
	@PreAuthorize("hasRole('admin')")
	public String adminAccess() {
		return "Admin Board.";
	}

	@GetMapping("/appAdmin")
	@PreAuthorize("hasRole('appAdmin')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

}
