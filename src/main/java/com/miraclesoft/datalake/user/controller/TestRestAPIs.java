package com.miraclesoft.datalake.user.controller;

import com.miraclesoft.datalake.user.message.response.ResponseMessage;
import com.miraclesoft.datalake.user.model.User;
import com.miraclesoft.datalake.user.repository.RoleRepository;
import com.miraclesoft.datalake.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.util.List;
import java.util.Properties;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TestRestAPIs {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/api/test/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return ">>> User Contents!";
    }

    @GetMapping("/api/test/userinfo")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<User> getUserInfo(){
        return userRepository.findAll();
    }

    @GetMapping("/api/email/{username}")
    public User getEmail(@PathVariable String username){
        return userRepository.getUserEmail(username);
    }

    @DeleteMapping("/api/test/delete/user/{id}")
    public String deleteUser(@PathVariable Long id){
        User a = userRepository.getOne(id);
        userRepository.delete(a);
        return ("deleted");
    }

    @GetMapping("/api/test/getinfo/{id}")
    public User getInfo(@PathVariable Long id){
        User a = userRepository.getOne(id);
        return a;
    }

    @GetMapping("/api/user/{role}")
    public List<User> getData(@PathVariable String role){
        if(role.equals("BUSINESS")){
            return userRepository.getBusiness();
        }else if(role.equals("USER")){
            return userRepository.getUser();
        }else{
            return userRepository.getAdmin();
        }

    }

    @PatchMapping("/api/test/resetPassword/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @Valid @RequestBody User pass) throws AddressException {
        User user1 = userRepository.getOne(id);
        System.out.println("reset password : " + pass.getPassword());
        user1.setPassword(encoder.encode(pass.getPassword()));
        userRepository.save(user1);
        if(pass.getAlerts().equals("true")){
            final String username = "";
            final String password = "";
            String fromEmail = "";


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
            addresses[0] = new InternetAddress(pass.getEmail());
            try {
                msg.setFrom(new InternetAddress(fromEmail));
                msg.setRecipients(Message.RecipientType.TO, addresses);
                msg.setSubject("SAP DataLake Account Password Reset");
                msg.setText("Password Updated successfully. The new credentials are as follows: \n" + "username: "+ pass.getUsername() + "\npassword: " + pass.getPassword()
                        +"\nPlease navigate to the link to login to your profile: https://sapdatalake.com/login ");
                Transport.send(msg);
                System.out.println("Sent message");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(new ResponseMessage("Password Updated successfully!"), HttpStatus.OK);
    }

    @GetMapping("/api/test/business")
    @PreAuthorize("hasRole('BUSINESS') or hasRole('ADMIN')")
    public String projectManagementAccess() {
        return ">>> Project Management Board";
    }

    @GetMapping("/api/test/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return ">>> Admin Contents";
    }
}

