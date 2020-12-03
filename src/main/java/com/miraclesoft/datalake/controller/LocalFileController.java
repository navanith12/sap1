
package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.LocalFile;
import com.miraclesoft.datalake.service.LocalFileService;

@RestController
@RequestMapping("/local")
public class LocalFileController {

	@Autowired
	private LocalFileService localFileService;

	@PostMapping("/")
	public LocalFile add(@RequestBody LocalFile localFile) {
		return localFileService.add(localFile);
	}

	@GetMapping("/")
	public List<LocalFile> list() {
		return localFileService.list();
	}

	@GetMapping("/{id}")
	public LocalFile getLocalFile(@PathVariable String id) {
		return localFileService.findLocalFile(id);
	}

	@PutMapping("/{id}")
	public LocalFile update(@RequestBody LocalFile localFile, @PathVariable String id) {
		return localFileService.update(localFile, id);
	}

	@DeleteMapping("/{id}")
	public String delete(@PathVariable String id) {
		return localFileService.delete(id);
	}

}
