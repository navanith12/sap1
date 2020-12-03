package com.miraclesoft.datalake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.datalake.model.Category;
import com.miraclesoft.datalake.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/")
	public ResponseEntity<Object> add(@RequestBody Category category) {
		return categoryService.add(category);
	}

	@GetMapping("/")
	public ResponseEntity<Object> dbList() {
		return categoryService.list();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getDbInsert(@PathVariable long id) {
		return categoryService.findCategory(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@RequestBody Category category, @PathVariable long id) {
		return categoryService.update(category, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable long id) {
		return categoryService.delete(id);
	}

}
