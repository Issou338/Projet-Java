package Lancement;

import ui.FenetreAccueil;

/**
 * Classe principale de lancement de l'application.
 *
 * <p>
 * Cette classe contient la méthode main qui démarre l'interface graphique
 * de l'escape game.
 * </p>
 *
 * <p>
 * L'interface est lancée dans le thread graphique de Swing via
 * SwingUtilities.invokeLater afin de respecter les bonnes pratiques
 * de programmation des interfaces utilisateur.
 * </p>
 *
 * @author cano28
 */
public class MainLancer {

    /**
     * Point d'entrée de l'application.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(FenetreAccueil::new);
    }
}