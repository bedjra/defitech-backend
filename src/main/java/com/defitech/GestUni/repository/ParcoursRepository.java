package com.defitech.GestUni.repository;

import com.defitech.GestUni.models.Bases.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParcoursRepository extends JpaRepository<Parcours, Long> {
    Optional<Parcours> findByNomParcours(String nomParcours);


}
