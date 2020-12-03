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

import com.miraclesoft.datalake.model.Packetsizeconfig;
import com.miraclesoft.datalake.service.PacketsizeconfigService;

@RestController
@RequestMapping("/config")
public class PacketsizeconfigController {
	
	@Autowired
	private PacketsizeconfigService packetsizeconfigService;

	
	@PostMapping("/")
	public Packetsizeconfig add(@RequestBody Packetsizeconfig packetsizeconfig) {
		return packetsizeconfigService.add(packetsizeconfig);
	}
	
	@GetMapping("/")
	public List<Packetsizeconfig> getList(){
		return packetsizeconfigService.list();
	}

	@GetMapping("/{id}")
	public Packetsizeconfig getOne(@PathVariable long id) {
		return packetsizeconfigService.findById(id);
	}

	@PutMapping("/{id}")
	public Packetsizeconfig update(@RequestBody Packetsizeconfig packetsizeconfig, @PathVariable long id) {
		return packetsizeconfigService.update(packetsizeconfig, id);
	}

	@DeleteMapping("/{id}")
	public Long delete(@PathVariable Long id) {
		return packetsizeconfigService.delete(id);
	}

}
