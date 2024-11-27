package com.defitech.GestUni.service;

import com.defitech.GestUni.models.Bases.Professeur;
import com.defitech.GestUni.repository.ProfesseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfesseurServices {

    @Autowired
    private ProfesseurRepository professeurRepository;

    public List<Professeur> getAllProfesseurs() {
        return professeurRepository.findAll();
    }

    public Professeur getProfesseurById(Long id) {
        return professeurRepository.findById(id).orElse(null);
    }

    public Professeur saveProfesseur(Professeur professeur) {
        return professeurRepository.save(professeur);
    }

    public void deleteProfesseur(Long id) {
        professeurRepository.deleteById(id);
    }
}
