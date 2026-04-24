package com.mycompany.projet_jeu.model;

import java.util.Map;

/**
 * Représente un scénario complet d'escape game.
 * <p>
 * Cette classe correspond directement au contenu du fichier {@code manifest.json}.
 * Elle contient toutes les informations nécessaires pour jouer un scénario :
 * - les métadonnées (id, titre, auteur),
 * - la version du schéma,
 * - l'identifiant de l'énigme de départ,
 * - l'ensemble des énigmes du scénario.
 * </p>
 *
 * <p>
 * Les énigmes sont stockées sous forme de dictionnaire
 * (clé = identifiant de l'énigme, valeur = objet {@link Puzzle}).
 * </p>
 *
 * @author cano28
 */
public class Gamedata {

    /** Version du schéma du scénario */
    private String schemaVersion;

    /** Identifiant unique du scénario */
    private String id;

    /** Titre du scénario */
    private String title;

    /** Auteur du scénario */
    private String author;

    /** Identifiant de l'énigme de départ */
    private String start;

    /** Ensemble des énigmes du scénario */
    private Map<String, Puzzle> puzzles;

    /**
     * Retourne la version du schéma utilisée.
     *
     * @return version du schéma
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Retourne l'identifiant du scénario.
     *
     * @return identifiant du scénario
     */
    public String getId() {
        return id;
    }

    /**
     * Retourne le titre du scénario.
     *
     * @return titre du scénario
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retourne l'auteur du scénario.
     *
     * @return nom de l'auteur
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Retourne l'identifiant de l'énigme de départ.
     *
     * @return identifiant de la première énigme
     */
    public String getStart() {
        return start;
    }

    /**
     * Retourne la liste des énigmes du scénario.
     *
     * @return dictionnaire des énigmes (id → Puzzle)
     */
    public Map<String, Puzzle> getPuzzles() {
        return puzzles;
    }

    /**
     * Récupère une énigme à partir de son identifiant.
     *
     * @param id identifiant de l'énigme
     * @return l'objet {@link Puzzle} correspondant ou null si absent
     */
    public Puzzle getPuzzle(String id) {
        if (puzzles == null) {
            return null;
        }
        return puzzles.get(id);
    }

    /**
     * Vérifie si une énigme existe dans le scénario.
     *
     * @param id identifiant de l'énigme
     * @return true si l'énigme existe, false sinon
     */
    public boolean hasPuzzle(String id) {
        return puzzles != null && puzzles.containsKey(id);
    }
}