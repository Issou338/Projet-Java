package ui;

import javax.swing.*;
import java.awt.*;

import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Puzzle;

public class FenetreJeu extends JFrame {

    MoteurDeJeu moteur;
    JTextArea zoneTexte = new JTextArea(6, 40);
    JLabel labelImage = new JLabel("", SwingConstants.CENTER);
    JPanel panelChoix = new JPanel();

    public FenetreJeu(MoteurDeJeu moteur) {
        this.moteur = moteur;

        setTitle("L'aventure de l'ile aux pirates");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        zoneTexte.setEditable(false);
        zoneTexte.setLineWrap(true);
        zoneTexte.setWrapStyleWord(true);
        zoneTexte.setFont(new Font("Papyrus", Font.BOLD, 22));
        zoneTexte.setBackground(new Color(240, 228, 198));
        zoneTexte.setForeground(new Color(60, 40, 20));
        zoneTexte.setMargin(new Insets(20, 20, 20, 20));

        Color fondNavire = new Color(30, 15, 5);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(fondNavire);

        JScrollPane scrollPane = new JScrollPane(zoneTexte);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panelCentral.add(scrollPane, BorderLayout.NORTH);
        panelCentral.add(labelImage, BorderLayout.CENTER);

        panelChoix.setBackground(fondNavire);

        add(panelCentral, BorderLayout.CENTER);
        add(panelChoix, BorderLayout.SOUTH);

        mettreAJourAffichage();
        setVisible(true);
    }

    private void mettreAJourAffichage() {
        Puzzle puzzle = moteur.getPuzzleActuel();

        zoneTexte.setText(puzzle.getPrompt());

        if (puzzle.getImage() != null && !puzzle.getImage().isEmpty()) {
            ImageIcon iconeOriginale = new ImageIcon(puzzle.getImage());
            Image imageRedimensionnee = iconeOriginale.getImage().getScaledInstance(700, 350, Image.SCALE_SMOOTH);
            labelImage.setIcon(new ImageIcon(imageRedimensionnee));
        } else {
            labelImage.setIcon(null);
        }

        panelChoix.removeAll();

        if ("texte".equals(puzzle.getType())) {
            JTextField champ = new JTextField(10);
            champ.setFont(new Font("Papyrus", Font.BOLD, 18));
            champ.setBackground(new Color(240, 228, 198));
            champ.setForeground(new Color(60, 40, 20));
            champ.addActionListener(e -> envoyerReponse(champ.getText()));
            panelChoix.add(champ);
            creerBouton("Valider", e -> envoyerReponse(champ.getText()));

        } else if ("boolean".equals(puzzle.getType())) {
            creerBouton("La mer", e -> envoyerReponse("true"));
            creerBouton("La forêt", e -> envoyerReponse("false"));

        } else if (puzzle.getChoices() != null) {
            for (String choix : puzzle.getChoices()) {
                creerBouton(choix, e -> envoyerReponse(choix));
            }
        }

        if (!moteur.estTermine()) {
            creerBouton("Abandonner", e -> System.exit(0));
        } else {
            creerBouton("Recommencer", e -> {
                moteur.recommencer();
                mettreAJourAffichage();
            });
            creerBouton("Quitter le jeu", e -> System.exit(0));
        }

        panelChoix.revalidate();
        panelChoix.repaint();
    }

    private void envoyerReponse(String reponse) {
        moteur.passerAuPuzzleSuivant(reponse.trim());
        mettreAJourAffichage();
    }

    private void creerBouton(String texte, java.awt.event.ActionListener action) {
        JButton bouton = new JButton(texte);
        bouton.addActionListener(action);

        bouton.setBackground(new Color(101, 67, 33));
        bouton.setForeground(new Color(255, 215, 0));
        bouton.setFont(new Font("Papyrus", Font.BOLD, 18));
        bouton.setFocusPainted(false);
        bouton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 20, 10), 3),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelChoix.add(bouton);
    }
}
