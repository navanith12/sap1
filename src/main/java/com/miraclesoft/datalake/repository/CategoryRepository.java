package com.miraclesoft.datalake.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.datalake.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
