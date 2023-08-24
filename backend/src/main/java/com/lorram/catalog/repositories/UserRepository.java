package com.lorram.catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lorram.catalog.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}