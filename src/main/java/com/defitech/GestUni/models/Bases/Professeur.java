package com.defitech.GestUni.models.Bases;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Professeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professeurId;

    private String nom;
    private String email;
    private String prenom;

//    @OneToMany
//    @JoinColumn(referencedColumnName = "cours_id", insertable = false)
//    private List<UE> ues;


}
