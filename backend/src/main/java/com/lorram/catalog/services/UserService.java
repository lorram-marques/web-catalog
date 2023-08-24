package com.lorram.catalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lorram.catalog.dto.RoleDTO;
import com.lorram.catalog.dto.UserDTO;
import com.lorram.catalog.dto.UserInsertDTO;
import com.lorram.catalog.entities.Role;
import com.lorram.catalog.entities.User;
import com.lorram.catalog.repositories.RoleRepository;
import com.lorram.catalog.repositories.UserRepository;
import com.lorram.catalog.services.exceptions.DatabaseException;
import com.lorram.catalog.services.exceptions.ObjectNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired 
	private BCryptPasswordEncoder encoder;

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repository.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow(() -> new ObjectNotFoundException(id));
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		fromDTO(dto, entity);
		entity.setPassword(encoder.encode(dto.getPassword()));	
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		try {
		User entity = repository.getReferenceById(id);
		fromDTO(dto, entity);
		entity = repository.save(entity);
		return new UserDTO(entity); 
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
	
	private void fromDTO(UserDTO dto, User entity) {
		
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		for (RoleDTO item : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(item.getId());
			entity.getRoles().add(role);
		}
	}
	
}
