package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.TokenDTO;
import br.com.alura.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private TokenService tokenService;

	@PostMapping
	public ResponseEntity<?> autenticar(@RequestBody @Valid LoginForm loginForm) {

		UsernamePasswordAuthenticationToken userLogin = new UsernamePasswordAuthenticationToken(loginForm.getEmail(),
				loginForm.getSenha());
		
		try {
			
			Authentication authentication = authenticationManager.authenticate(userLogin);
			String token =  tokenService.gerarToken(authentication);
			
			TokenDTO tokenDTO = new TokenDTO(token, "Bearer");
			
			return ResponseEntity.ok(tokenDTO);
			
		}catch(AuthenticationException ex) {
		
			return ResponseEntity.badRequest().build();
		}


		

	}

}
