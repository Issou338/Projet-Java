package ui;

import javax.swing.*;
import java.awt.*;

import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Puzzle;

public class FenetreJeu extends JFrame {

    private MoteurDeJeu moteur;
    private JTextArea zoneTexte;
    private JPanel panelChoix;

    public FenetreJeu(MoteurDeJeu moteur) {
        this.moteur = moteur;

        setTitle("Jeu Narratif");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        zoneTexte = new JTextArea();
        zoneTexte.setEditable(false);
        zoneTexte.setLineWrap(true);
        zoneTexte.setWrapStyleWord(true);
        zoneTexte.setFont(new Font("Serif", Font.PLAIN, 20));

        panelChoix = new JPanel();
        panelChoix.setLayout(new FlowLayout());

        add(new JScrollPane(zoneTexte), BorderLayout.CENTER);
        add(panelChoix, BorderLayout.SOUTH);

        mettreAJourAffichage();

        setVisible(true);
    }

    private void mettreAJourAffichage() {
        Puzzle puzzleActuel = moteur.getPuzzleActuel();

        zoneTexte.setText(puzzleActuel.getPrompt());

        panelChoix.removeAll();

        if (moteur.estTermine()) {
            JButton boutonFin = new JButton("Fin");
            boutonFin.addActionListener(e -> System.exit(0));
            panelChoix.add(boutonFin);
        } else if ("boolean".equals(puzzleActuel.getType())) {
            JButton boutonChemin1 = new JButton("La mer");
            JButton boutonChemin2 = new JButton("La forêt");

            boutonChemin1.addActionListener(e -> {
                moteur.passerAuPuzzleSuivant("true");
                mettreAJourAffichage();
            });

            boutonChemin2.addActionListener(e -> {
                moteur.passerAuPuzzleSuivant("false");
                mettreAJourAffichage();
            });

            panelChoix.add(boutonChemin1);
            panelChoix.add(boutonChemin2);
        } else if ("texte".equals(puzzleActuel.getType())) {
            JTextField champTexte = new JTextField(10);
            JButton boutonValider = new JButton("Valider");

            boutonValider.addActionListener(e -> {
                String reponseDuJoueur = champTexte.getText();
                moteur.passerAuPuzzleSuivant(reponseDuJoueur);
                mettreAJourAffichage();
            });

            panelChoix.add(champTexte);
            panelChoix.add(boutonValider);
        } else if (puzzleActuel.getChoices() != null) {
            for (String choix : puzzleActuel.getChoices()) {
                JButton bouton = new JButton(choix);

                bouton.addActionListener(e -> {
                    moteur.passerAuPuzzleSuivant(choix);
                    mettreAJourAffichage();
                });

                panelChoix.add(bouton);
            }
        }

        if (!moteur.estTermine()) {
            JButton boutonAbandonner = new JButton("Abandonner");
            boutonAbandonner.addActionListener(e -> System.exit(0));
            panelChoix.add(boutonAbandonner);
        }

        panelChoix.revalidate();
        panelChoix.repaint();
    }
}
