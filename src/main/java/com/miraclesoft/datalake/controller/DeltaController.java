package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Delta;
import com.miraclesoft.datalake.service.DeltaService;

@RestController
@RequestMapping("/delta")
public class DeltaController {

	@Autowired
	private DeltaService deltaService;

	@PostMapping("/")
	public Delta add(@RequestBody Delta delta) {
		return deltaService.addDelta(delta);
	}

	@GetMapping("/")
	public List<Delta> deltaList() {
		return deltaService.listofdelta();
	}

	@GetMapping("/{extractor}")
	public Delta getDelta(@PathVariable String extractor) {
		return deltaService.getDeltaByExtractor(extractor);
	}

	@PutMapping("/{id}")
	public Delta update(@RequestBody Delta delta, @PathVariable long id) {
		return deltaService.updateDelta(delta, id);
	}

//	@DeleteMapping("/{id}")
//	public String delete(@PathVariable String id) {
//		return deltaService.delete(id);
//	}
//	
}
