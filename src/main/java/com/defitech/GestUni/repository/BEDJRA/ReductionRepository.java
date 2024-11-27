package com.defitech.GestUni.repository.BEDJRA;


import com.defitech.GestUni.models.BEDJRA.Reduction;
import com.defitech.GestUni.models.Bases.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReductionRepository extends JpaRepository<Reduction, Long> {

}
