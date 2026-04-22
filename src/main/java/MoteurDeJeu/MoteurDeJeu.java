/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MoteurDeJeu;

/**
 *
 * @author cano28
 */
import com.mycompany.projet_jeu.model.Gamedata;
import com.mycompany.projet_jeu.model.Puzzle;

public class MoteurDeJeu {

    private Gamedata jeu;
    private String idPuzzleActuel;
    private int nbEnigmesResolues;
    private boolean abandonne;

    public MoteurDeJeu(Gamedata jeu) {
        this.jeu = jeu;
        this.idPuzzleActuel = jeu.getStart();
        this.nbEnigmesResolues = 0;
        this.abandonne = false;
    }

    public Puzzle getPuzzleActuel() {
        if (estTermine()) {
            return null;
        }
        return jeu.getPuzzle(idPuzzleActuel);
    }

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

    public boolean estTermine() {
        return abandonne || (idPuzzleActuel != null && idPuzzleActuel.startsWith("end_"));
    }

    public boolean estVictoire() {
        return "end_win".equals(idPuzzleActuel);
    }

    public boolean estDefaite() {
        return "end_lose".equals(idPuzzleActuel);
    }

    public boolean estAbandonne() {
        return abandonne;
    }

    public void abandonner() {
        abandonne = true;
    }

    public int getNbEnigmesResolues() {
        return nbEnigmesResolues;
    }

    public void recommencer() {
        idPuzzleActuel = jeu.getStart();
        nbEnigmesResolues = 0;
        abandonne = false;
    }
}