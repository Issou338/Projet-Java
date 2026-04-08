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

    public MoteurDeJeu(Gamedata jeu) {
        this.jeu = jeu;
        this.idPuzzleActuel = jeu.getStart();
    }

    
    public Puzzle getPuzzleActuel() {
        return jeu.getPuzzle(idPuzzleActuel);
    }

     public void passerAuPuzzleSuivant(String reponse) {
        Puzzle puzzleActuel = getPuzzleActuel();
        String prochainId = puzzleActuel.getNextPuzzleId(reponse);

        if (prochainId == null) {
            throw new IllegalArgumentException(
                "Aucune route valide pour la réponse \"" + reponse + "\" dans " + idPuzzleActuel
            );
        }

        if (!jeu.hasPuzzle(prochainId)) {
            throw new IllegalArgumentException(
                "Route invalide : " + idPuzzleActuel + " -> " + prochainId + " (énigme inexistante)"
            );
        }
     }

    public boolean estTermine() {
        return idPuzzleActuel.startsWith("end");
    }

    public void recommencer() {
        idPuzzleActuel = jeu.getStart();
    }
}