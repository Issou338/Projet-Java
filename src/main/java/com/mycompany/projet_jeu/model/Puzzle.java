package com.mycompany.projet_jeu.model;

import java.util.List;
import java.util.Map;

/**
 * Représente une énigme (puzzle) dans un scénario de jeu.
 * <p>
 * Un puzzle contient :
 * - un type (text, qcm, boolean),
 * - une consigne (prompt),
 * - une image associée,
 * - éventuellement des choix (pour les QCM),
 * - un ensemble de routes définissant la progression du jeu.
 * </p>
 *
 * <p>
 * Les routes permettent de déterminer la prochaine énigme en fonction
 * de la réponse donnée par l'utilisateur.
 * </p>
 *
 * @author mael
 */
public class Puzzle {

    /** Type de l'énigme (text, qcm ou boolean) */
    private String type;

    /** Consigne affichée à l'utilisateur */
    private String prompt;

    /** Chemin de l'image associée à l'énigme */
    private String image;

    /** Liste des choix possibles (uniquement pour les QCM) */
    private List<String> choices;

    /** Table de routage : réponse → identifiant de la prochaine énigme */
    private Map<String, String> routes;

    /**
     * Retourne le type de l'énigme.
     *
     * @return type de l'énigme
     */
    public String getType() {
        return type;
    }

    /**
     * Retourne la consigne de l'énigme.
     *
     * @return texte de la consigne
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Retourne le chemin de l'image associée à l'énigme.
     *
     * @return chemin de l'image
     */
    public String getImage() {
        return image;
    }

    /**
     * Retourne la liste des choix possibles (pour les QCM).
     *
     * @return liste des choix ou null si non applicable
     */
    public List<String> getChoices() {
        return choices;
    }

    /**
     * Retourne les routes de l'énigme.
     *
     * @return table de routage (réponse → prochaine énigme)
     */
    public Map<String, String> getRoutes() {
        return routes;
    }

    /**
     * Détermine l'identifiant de la prochaine énigme en fonction
     * de la réponse fournie par l'utilisateur.
     *
     * <p>
     * Si la réponse correspond à une clé dans {@code routes}, la route
     * associée est utilisée. Sinon, la route par défaut "*" est utilisée.
     * </p>
     *
     * @param input réponse de l'utilisateur
     * @return identifiant de la prochaine énigme ou null si aucune route valide
     */
    public String getNextPuzzleId(String input) {

        if (routes == null || routes.isEmpty()) {
            return null;
        }

        if (input != null && routes.containsKey(input)) {
            return routes.get(input);
        }

        return routes.get("*");
    }
}