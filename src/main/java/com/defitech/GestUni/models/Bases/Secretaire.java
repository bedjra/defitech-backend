package com.defitech.GestUni.models.Bases;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Secretaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int secretaireId;

    private String email;
    private String nom;
}
