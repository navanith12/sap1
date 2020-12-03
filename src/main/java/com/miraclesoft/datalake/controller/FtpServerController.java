package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.FtpServer;
import com.miraclesoft.datalake.service.FtpServerService;

@RestController
@RequestMapping("/ftp")
public class FtpServerController {

	@Autowired
	private FtpServerService ftpServerService;

	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody FtpServer ftpServer) {
		return ftpServerService.add(ftpServer);
	}

	@GetMapping("/")
	public ResponseEntity<Object> list() {
		return ftpServerService.list();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getFtpServer(@PathVariable String id) {
		return ftpServerService.findFtpServer(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody FtpServer ftpServer, @PathVariable String id) {
		return ftpServerService.update(ftpServer, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable String id) {
		return ftpServerService.delete(id);
	}

}
