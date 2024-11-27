package com.defitech.GestUni.service.BEDJRA;

import com.defitech.GestUni.enums.BEDJRA.StatutEcheance;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EcheanceService {

    public List<StatutEcheance> getStatutsEnAttente() {
        // Retourne une liste des statuts d'échéance en attente
        return Arrays.asList(StatutEcheance.PARTIELLEMENT_PAYEE, StatutEcheance.EN_ATTENTE);
    }
}
