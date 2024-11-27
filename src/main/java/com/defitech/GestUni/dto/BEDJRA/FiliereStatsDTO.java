package com.defitech.GestUni.dto.BEDJRA;

import com.defitech.GestUni.enums.NiveauEtude;

public class FiliereStatsDTO {

        private Long filiereId;
        private String nomFiliere;
        private NiveauEtude niveau;
        private String nomParcours;
        private long totalEtudiants;


        public Long getFiliereId() {
                return filiereId;
        }

        public void setFiliereId(Long filiereId) {
                this.filiereId = filiereId;
        }

        public String getNomFiliere() {
                return nomFiliere;
        }

        public void setNomFiliere(String nomFiliere) {
                this.nomFiliere = nomFiliere;
        }

        public NiveauEtude getNiveau() {
                return niveau;
        }

        public void setNiveau(NiveauEtude niveau) {
                this.niveau = niveau;
        }

        public String getNomParcours() {
                return nomParcours;
        }

        public void setNomParcours(String nomParcours) {
                this.nomParcours = nomParcours;
        }

        public long getTotalEtudiants() {
                return totalEtudiants;
        }

        public void setTotalEtudiants(long totalEtudiants) {
                this.totalEtudiants = totalEtudiants;
        }
}
