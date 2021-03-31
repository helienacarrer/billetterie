package fr.heliena.billetterie.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	@GetMapping("/current")
	// return les infos du user actuellement authentifié
	public OAuth2User currentUser(@AuthenticationPrincipal OAuth2User principal) {
		return principal;
	}

}
