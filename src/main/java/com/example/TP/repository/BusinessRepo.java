package com.example.TP.repository;

import com.example.TP.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepo extends JpaRepository<Business, Long> {

}
