package com.lorram.catalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lorram.catalog.dto.CategoryDTO;
import com.lorram.catalog.dto.ProductDTO;
import com.lorram.catalog.entities.Category;
import com.lorram.catalog.entities.Product;
import com.lorram.catalog.repositories.CategoryRepository;
import com.lorram.catalog.repositories.ProductRepository;
import com.lorram.catalog.services.exceptions.DatabaseException;
import com.lorram.catalog.services.exceptions.ObjectNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> list = repository.findAll(pageable);
		return list.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ObjectNotFoundException(id));
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		fromDTO(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
		Product entity = repository.getReferenceById(id);
		fromDTO(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity); 
		}
		catch(EntityNotFoundException e) {
			throw new ObjectNotFoundException(id);
		}
	}
	
	public void delete (Long id) {
		try {
		repository.deleteById(id);
		} 
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void fromDTO(ProductDTO dto, Product entity) {
		
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for (CategoryDTO item : dto.getCategories()) {
			Category category = categoryRepository.getReferenceById(item.getId());
			entity.getCategories().add(category);
		}
	}
	
}
