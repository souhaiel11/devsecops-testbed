package com.vermeg.testbed.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * LABORATOIRE DE DUPLICATION — test de l'agent de remediation IA
 * ================================================================
 *
 * Ce fichier produit deux familles de findings distinctes :
 *
 *  A) LITTERAUX DUPLIQUES  -> regle SonarQube java:S1192 (CRITICAL)
 *     Correction attendue : extraction en constantes privees statiques.
 *     Difficulte : faible. L'agent doit reussir.
 *
 *  B) BLOCS DUPLIQUES      -> metrique "Duplications %" de SonarQube
 *     Correction attendue : extraction d'une methode privee partagee.
 *     Difficulte : elevee. C'est le vrai test de l'agent.
 *
 * Ne pas corriger manuellement : ce fichier sert d'oracle de validation.
 */
@Service
public class ReportFormatterService {

    // ─────────────────────────────────────────────────────────────
    //  A) LITTERAUX DUPLIQUES — chacun apparait au moins 3 fois
    // ─────────────────────────────────────────────────────────────

    public String describeSeverity(String level) {
        if ("CRITICAL".equals(level)) {
            return "Niveau de gravite non reconnu";
        }
        if ("HIGH".equals(level)) {
            return "Niveau de gravite non reconnu";
        }
        return "Niveau de gravite non reconnu";
    }

    public String describeStatus(int code) {
        if (code == 1) {
            return "Statut indisponible pour ce rapport";
        } else if (code == 2) {
            return "Statut indisponible pour ce rapport";
        }
        return "Statut indisponible pour ce rapport";
    }

    public List<String> buildAuditTrail() {
        List<String> trail = new ArrayList<>();
        trail.add("Entree ajoutee au journal d audit");
        trail.add("Entree ajoutee au journal d audit");
        trail.add("Entree ajoutee au journal d audit");
        trail.add("Entree ajoutee au journal d audit");
        return trail;
    }

    public String resolveOwner(String owner) {
        if (owner == null) {
            return "Proprietaire non renseigne";
        }
        if (owner.isEmpty()) {
            return "Proprietaire non renseigne";
        }
        if ("null".equals(owner)) {
            return "Proprietaire non renseigne";
        }
        return owner;
    }

    // ─────────────────────────────────────────────────────────────
    //  B) BLOCS DUPLIQUES — trois methodes au corps quasi identique
    //     Chaque bloc depasse le seuil de detection (10 lignes)
    // ─────────────────────────────────────────────────────────────

    public String formatSecurityReport(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        String title = data.get("title") != null ? data.get("title").toString() : "champ absent";
        String owner = data.get("owner") != null ? data.get("owner").toString() : "champ absent";
        String date = data.get("date") != null ? data.get("date").toString() : "champ absent";
        sb.append("========================================").append(System.lineSeparator());
        sb.append("Titre       : ").append(title).append(System.lineSeparator());
        sb.append("Proprietaire: ").append(owner).append(System.lineSeparator());
        sb.append("Date        : ").append(date).append(System.lineSeparator());
        sb.append("----------------------------------------").append(System.lineSeparator());
        sb.append("Genere automatiquement, ne pas modifier").append(System.lineSeparator());
        sb.append("========================================").append(System.lineSeparator());
        return sb.toString();
    }

    public String formatQualityReport(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        String title = data.get("title") != null ? data.get("title").toString() : "champ absent";
        String owner = data.get("owner") != null ? data.get("owner").toString() : "champ absent";
        String date = data.get("date") != null ? data.get("date").toString() : "champ absent";
        sb.append("========================================").append(System.lineSeparator());
        sb.append("Titre       : ").append(title).append(System.lineSeparator());
        sb.append("Proprietaire: ").append(owner).append(System.lineSeparator());
        sb.append("Date        : ").append(date).append(System.lineSeparator());
        sb.append("----------------------------------------").append(System.lineSeparator());
        sb.append("Genere automatiquement, ne pas modifier").append(System.lineSeparator());
        sb.append("========================================").append(System.lineSeparator());
        return sb.toString();
    }

    public String formatComplianceReport(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        String title = data.get("title") != null ? data.get("title").toString() : "champ absent";
        String owner = data.get("owner") != null ? data.get("owner").toString() : "champ absent";
        String date = data.get("date") != null ? data.get("date").toString() : "champ absent";
        sb.append("========================================").append(System.lineSeparator());
        sb.append("Titre       : ").append(title).append(System.lineSeparator());
        sb.append("Proprietaire: ").append(owner).append(System.lineSeparator());
        sb.append("Date        : ").append(date).append(System.lineSeparator());
        sb.append("----------------------------------------").append(System.lineSeparator());
        sb.append("Genere automatiquement, ne pas modifier").append(System.lineSeparator());
        sb.append("========================================").append(System.lineSeparator());
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────
    //  B bis) Second groupe de blocs dupliques : logique de calcul
    // ─────────────────────────────────────────────────────────────

    public int scoreSecurityFindings(List<Integer> findings) {
        int total = 0;
        int weighted = 0;
        for (Integer finding : findings) {
            if (finding == null) {
                continue;
            }
            total = total + 1;
            if (finding >= 8) {
                weighted = weighted + 10;
            } else if (finding >= 5) {
                weighted = weighted + 5;
            } else {
                weighted = weighted + 1;
            }
        }
        if (total == 0) {
            return 0;
        }
        return weighted / total;
    }

    public int scoreQualityFindings(List<Integer> findings) {
        int total = 0;
        int weighted = 0;
        for (Integer finding : findings) {
            if (finding == null) {
                continue;
            }
            total = total + 1;
            if (finding >= 8) {
                weighted = weighted + 10;
            } else if (finding >= 5) {
                weighted = weighted + 5;
            } else {
                weighted = weighted + 1;
            }
        }
        if (total == 0) {
            return 0;
        }
        return weighted / total;
    }

    public int scoreComplianceFindings(List<Integer> findings) {
        int total = 0;
        int weighted = 0;
        for (Integer finding : findings) {
            if (finding == null) {
                continue;
            }
            total = total + 1;
            if (finding >= 8) {
                weighted = weighted + 10;
            } else if (finding >= 5) {
                weighted = weighted + 5;
            } else {
                weighted = weighted + 1;
            }
        }
        if (total == 0) {
            return 0;
        }
        return weighted / total;
    }
}
