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

public class MainTest {

    public static void main(String[] args) throws Exception {
        
        Gamedata game = Charger_Jeu.chargerJeu();
        MoteurDeJeu moteur = new MoteurDeJeu(game);
        new FenetreJeu(moteur);
    }
}