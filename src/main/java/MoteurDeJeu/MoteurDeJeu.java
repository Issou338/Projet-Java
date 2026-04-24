package MoteurDeJeu;

import com.mycompany.projet_jeu.model.Gamedata;
import com.mycompany.projet_jeu.model.Puzzle;

/**
 * Classe représentant le moteur de jeu d’un escape game.
 *
 * <p>
 * Cette classe gère la progression dans un scénario :
 * - navigation entre les énigmes,
 * - validation des réponses,
 * - détection de la fin de partie (victoire, défaite, abandon),
 * - comptage du nombre d’énigmes résolues.
 * </p>
 *
 * <p>
 * Le moteur s’appuie sur un objet Gamedata qui contient
 * la description complète du scénario.
 * </p>
 *
 * @author cano28
 */
public class MoteurDeJeu {

    /** Données du scénario */
    private Gamedata jeu;

    /** Identifiant de l’énigme actuellement en cours */
    private String idPuzzleActuel;

    /** Nombre d’énigmes résolues par le joueur */
    private int nbEnigmesResolues;

    /** Indique si le joueur a abandonné la partie */
    private boolean abandonne;

    /**
     * Constructeur du moteur de jeu.
     *
     * @param jeu données du scénario à charger
     */
    public MoteurDeJeu(Gamedata jeu) {
        this.jeu = jeu;
        this.idPuzzleActuel = jeu.getStart();
        this.nbEnigmesResolues = 0;
        this.abandonne = false;
    }

    /**
     * Retourne l’énigme actuelle.
     *
     * @return puzzle courant ou null si la partie est terminée
     */
    public Puzzle getPuzzleActuel() {
        if (estTermine()) {
            return null;
        }
        return jeu.getPuzzle(idPuzzleActuel);
    }

    /**
     * Passe à l’énigme suivante en fonction de la réponse donnée.
     *
     * @param reponse réponse fournie par le joueur
     * @throws IllegalStateException si la partie est déjà terminée
     * @throws IllegalArgumentException si aucune route valide n’est trouvée
     *                                  ou si la route pointe vers une énigme inexistante
     */
    public void passerAuPuzzleSuivant(String reponse) {

        if (estTermine()) {
            throw new IllegalStateException("La partie est déjà terminée.");
        }

        Puzzle puzzleActuel = getPuzzleActuel();
        String prochainId = puzzleActuel.getNextPuzzleId(reponse);

        if (prochainId == null) {
            throw new IllegalArgumentException(
                "Aucune route valide pour la réponse \"" + reponse + "\" dans " + idPuzzleActuel
            );
        }

        boolean estFin = prochainId.startsWith("end_");

        if (!estFin && !jeu.hasPuzzle(prochainId)) {
            throw new IllegalArgumentException(
                "Route invalide : " + idPuzzleActuel + " -> " + prochainId + " (énigme inexistante)"
            );
        }

        nbEnigmesResolues++;
        idPuzzleActuel = prochainId;
    }

    /**
     * Indique si la partie est terminée.
     *
     * @return true si la partie est finie, false sinon
     */
    public boolean estTermine() {
        return abandonne || (idPuzzleActuel != null && idPuzzleActuel.startsWith("end_"));
    }

    /**
     * Indique si la partie est gagnée.
     *
     * @return true si victoire, false sinon
     */
    public boolean estVictoire() {
        return "end_win".equals(idPuzzleActuel);
    }

    /**
     * Indique si la partie est perdue.
     *
     * @return true si défaite, false sinon
     */
    public boolean estDefaite() {
        return "end_lose".equals(idPuzzleActuel);
    }

    /**
     * Indique si le joueur a abandonné la partie.
     *
     * @return true si abandon, false sinon
     */
    public boolean estAbandonne() {
        return abandonne;
    }

    /**
     * Permet au joueur d’abandonner la partie.
     */
    public void abandonner() {
        abandonne = true;
    }

    /**
     * Retourne le nombre d’énigmes résolues.
     *
     * @return nombre d’énigmes validées
     */
    public int getNbEnigmesResolues() {
        return nbEnigmesResolues;
    }

    /**
     * Réinitialise la partie.
     *
     * <p>
     * Le joueur revient à la première énigme et le compteur est remis à zéro.
     * </p>
     */
    public void recommencer() {
        idPuzzleActuel = jeu.getStart();
        nbEnigmesResolues = 0;
        abandonne = false;
    }
}