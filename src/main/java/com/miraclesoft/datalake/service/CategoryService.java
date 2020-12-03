package com.miraclesoft.datalake.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.datalake.model.Category;
import com.miraclesoft.datalake.repository.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	JSONObject jo;
	
	public ResponseEntity<Object> add(Category category) {
		jo =new JSONObject();
		Category temp = categoryRepository.save(category);
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", temp);
		jo.put("Message","Added Category object");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}	

	public ResponseEntity<Object> list() {
		jo = new JSONObject();
		List<Category> Category = categoryRepository.findAll();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", Category);
		jo.put("Message", "List of Category objects");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> findCategory(long id) {
		Category category = null;
		System.out.println("check value: " + (categoryRepository.findById(id)).isPresent());
		if ((categoryRepository.findById(id)).isPresent()) {
			category = (categoryRepository.findById(id)).get();
		} 
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", category);
		jo.put("Message", "One obejct of Category");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> update(Category category, long id) {
		category.setId(id);
		Category temp = categoryRepository.save(category);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", temp);
		jo.put("Message", "Updated Category object");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

	public ResponseEntity<Object> delete(long id) {
		categoryRepository.deleteById(id);
		jo = new JSONObject();
		jo.put("Status", HttpStatus.OK);
		jo.put("Data", id);
		jo.put("Message", "Category object deleted");
		return new ResponseEntity<>(jo.toMap(), HttpStatus.OK);
	}

}
