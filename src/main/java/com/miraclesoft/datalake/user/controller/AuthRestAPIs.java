package com.miraclesoft.datalake.user.controller;

import com.miraclesoft.datalake.user.message.request.LoginForm;
import com.miraclesoft.datalake.user.message.request.SignUpForm;
import com.miraclesoft.datalake.user.message.response.JwtResponse;
import com.miraclesoft.datalake.user.message.response.ResponseMessage;
import com.miraclesoft.datalake.user.model.Role;
import com.miraclesoft.datalake.user.model.RoleName;
import com.miraclesoft.datalake.user.model.User;
import com.miraclesoft.datalake.user.repository.RoleRepository;
import com.miraclesoft.datalake.user.repository.UserRepository;
import com.miraclesoft.datalake.user.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtProvider jwtProvider;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		HttpHeaders httpHeaders= new HttpHeaders();
		

		String jwt = jwtProvider.generateJwtToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = userRepository.findByusername(loginRequest.getUsername());
		httpHeaders.add("Auth", "Bearer "+jwt);
		return ResponseEntity.ok().header("Auth", "Bearer "+jwt).body(new JwtResponse(jwt, userDetails.getUsername(), user.getFirstname(),
				user.getLastname(), userDetails.getAuthorities()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) throws AddressException {
		Boolean userexists = userRepository.findByusername(signUpRequest.getUsername()) == null ? false : true;
		if (userexists) {
			System.out.println("Fail -> Username is already taken");
			return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
					HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		User user = new User(signUpRequest.getFirstname(), signUpRequest.getLastname(), signUpRequest.getUsername(),
				signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getAlerts());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			switch (role) {
			case "ADMIN":
				Role adminRole = roleRepository.findByName(RoleName.ADMIN)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(adminRole);

				break;
			case "BUSINESS":
				Role pmRole = roleRepository.findByName(RoleName.BUSINESS)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(pmRole);

				break;
			default:
				Role userRole = roleRepository.findByName(RoleName.DEVELOPER)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(userRole);
			}
		});

		user.setRoles(roles);
		userRepository.save(user);

		if (signUpRequest.getAlerts().equals("true")) {
			final String username = "";
			final String password = "";
			String fromEmail = "kbourishetty@miraclesoft.com";

			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", "smtp.miraclesoft.com");
			properties.put("mail.smtp.port", "587");

			Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			MimeMessage msg = new MimeMessage(session);
			InternetAddress[] addresses = new InternetAddress[1];
			addresses[0] = new InternetAddress(signUpRequest.getEmail());
			try {
				msg.setFrom(new InternetAddress(fromEmail));
				msg.setRecipients(Message.RecipientType.TO, addresses);
				msg.setSubject("SAP DataLake Account created");
				msg.setText("An account has been created with a role" + "'" + signUpRequest.getRole() + "'"
						+ "credentials are as follows: \n" + "username: " + signUpRequest.getUsername() + "\npassword: "
						+ signUpRequest.getPassword()
						+ "\nPlease navigate to the link to login to your profile: https://sapdatalake.com/login ");
				Transport.send(msg);
				System.out.println("Sent message");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

		return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
	}
}
