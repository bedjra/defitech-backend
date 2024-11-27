package com.defitech.GestUni.models.Bases;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DirecteurEtude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long directeurEtudeId;

    private String email;
}
