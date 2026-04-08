/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

/**
 *
 * @author cano28
 */
import java.util.*;
import com.mycompany.projet_jeu.model.Puzzle;
import com.mycompany.projet_jeu.model.Gamedata;
import MoteurDeJeu.Charger_Jeu;
import MoteurDeJeu.MoteurDeJeu;
import ui.FenetreJeu;
import ui.FenetreAccueil;

public class MainTest {

    public static void main(String[] args) {
        // Lance l'interface d'accueil
        javax.swing.SwingUtilities.invokeLater(() -> {
            new FenetreAccueil();
        });
    }
}
