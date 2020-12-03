package com.miraclesoft.datalake.controller;
//
//package com.miraclesoft.datalake.mongo.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.miraclesoft.datalake.mongo.service.UserService;
//import com.miraclesoft.datalake.mongo.model.User;
//
//@RestController
//@RequestMapping("/users")
//public class UserController {
//
//	@Autowired
//	private UserService userService;
//
//	@PostMapping("/")
//	public User add(@RequestBody User user) {
//		return userService.add(user);
//	}
//
//	@GetMapping("/")
//	public List<User> list() {
//		return userService.list();
//	}
//
//	@GetMapping("/{username}")
//	public User getuser(@PathVariable String username) {
//		return userService.findUser(username);
//	}
//
//	@PutMapping("/{id}")
//	public User update(@RequestBody User localFile, @PathVariable String id) {
//		return userService.update(localFile, id);
//	}
//
//	@DeleteMapping("/{id}")
//	public String delete(@PathVariable String id) {
//		return userService.delete(id);
//	}
//
//}
