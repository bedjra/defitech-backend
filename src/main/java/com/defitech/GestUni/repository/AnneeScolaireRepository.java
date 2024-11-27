package com.defitech.GestUni.repository;

import com.defitech.GestUni.models.Bases.AnneeScolaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnneeScolaireRepository extends JpaRepository<AnneeScolaire, Long> {
}
