package ui;

import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Puzzle;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FenetreJeu extends JFrame {

    private MoteurDeJeu moteur;
    private String cheminScenario;

    private JTextArea zoneTexte;
    private JLabel labelImage;
    private JPanel panelChoix;

    public FenetreJeu(MoteurDeJeu moteur, String cheminScenario) {
        this.moteur = moteur;
        this.cheminScenario = cheminScenario;

        setTitle("Jeu narratif");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        zoneTexte = new JTextArea();
        zoneTexte.setEditable(false);
        zoneTexte.setLineWrap(true);
        zoneTexte.setWrapStyleWord(true);
        zoneTexte.setFont(new Font("Serif", Font.PLAIN, 20));
        zoneTexte.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollTexte = new JScrollPane(zoneTexte);

        labelImage = new JLabel("", SwingConstants.CENTER);

        JPanel panelCentre = new JPanel(new BorderLayout());
        panelCentre.add(scrollTexte, BorderLayout.NORTH);
        panelCentre.add(labelImage, BorderLayout.CENTER);

        panelChoix = new JPanel(new FlowLayout());

        add(panelCentre, BorderLayout.CENTER);
        add(panelChoix, BorderLayout.SOUTH);

        mettreAJourAffichage();

        setVisible(true);
    }

    private void mettreAJourAffichage() {
        panelChoix.removeAll();

        if (moteur.estTermine()) {
            afficherEcranFin();
            return;
        }

        Puzzle puzzle = moteur.getPuzzleActuel();

        if (puzzle == null) {
            JOptionPane.showMessageDialog(this, "Erreur : puzzle actuel introuvable.");
            return;
        }

        zoneTexte.setText(puzzle.getPrompt());
        afficherImage(puzzle);

        if ("text".equals(puzzle.getType())) {
            JTextField champReponse = new JTextField(15);
            panelChoix.add(champReponse);

            JButton boutonValider = new JButton("Valider");
            boutonValider.addActionListener(e -> envoyerReponse(champReponse.getText()));
            panelChoix.add(boutonValider);

            champReponse.addActionListener(e -> envoyerReponse(champReponse.getText()));

        } else if ("boolean".equals(puzzle.getType())) {
            JButton boutonVrai = new JButton("Vrai");
            boutonVrai.addActionListener(e -> envoyerReponse("true"));
            panelChoix.add(boutonVrai);

            JButton boutonFaux = new JButton("Faux");
            boutonFaux.addActionListener(e -> envoyerReponse("false"));
            panelChoix.add(boutonFaux);

        } else if ("qcm".equals(puzzle.getType())) {
            if (puzzle.getChoices() != null) {
                for (String choix : puzzle.getChoices()) {
                    JButton boutonChoix = new JButton(choix);
                    boutonChoix.addActionListener(e -> envoyerReponse(choix));
                    panelChoix.add(boutonChoix);
                }
            }
        }

        JButton boutonAbandonner = new JButton("Abandonner");
        boutonAbandonner.addActionListener(e -> abandonnerPartie());
        panelChoix.add(boutonAbandonner);

        panelChoix.revalidate();
        panelChoix.repaint();
    }

    private void afficherImage(Puzzle puzzle) {
        if (puzzle.getImage() != null && !puzzle.getImage().isEmpty()) {
            String cheminImage = cheminScenario + File.separator + puzzle.getImage();
            File fichierImage = new File(cheminImage);

            if (fichierImage.exists()) {
                ImageIcon icone = new ImageIcon(cheminImage);
                Image image = icone.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
                labelImage.setIcon(new ImageIcon(image));
                labelImage.setText("");
            } else {
                labelImage.setIcon(null);
                labelImage.setText("Image introuvable : " + puzzle.getImage());
            }
        } else {
            labelImage.setIcon(null);
            labelImage.setText("");
        }
    }

    private void envoyerReponse(String reponse) {
        try {
            moteur.passerAuPuzzleSuivant(reponse.trim());
            mettreAJourAffichage();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherEcranFin() {
        labelImage.setIcon(null);
        labelImage.setText("");

        String resultat;
        if (moteur.estVictoire()) {
            resultat = "Victoire !";
        } else if (moteur.estDefaite()) {
            resultat = "Défaite.";
        } else if (moteur.estAbandonne()) {
            resultat = "Partie abandonnée.";
        } else {
            resultat = "Fin de partie.";
        }

        zoneTexte.setText(
            resultat + "\n\n" +
            "Résumé :\n" +
            "- Énigmes résolues : " + moteur.getNbEnigmesResolues()
        );

        JButton boutonRecommencer = new JButton("Recommencer");
        boutonRecommencer.addActionListener(e -> {
            moteur.recommencer();
            mettreAJourAffichage();
        });

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(e -> dispose());

        panelChoix.add(boutonRecommencer);
        panelChoix.add(boutonQuitter);

        panelChoix.revalidate();
        panelChoix.repaint();
    }

    private void abandonnerPartie() {
        int choix = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment abandonner la partie ?",
            "Abandon",
            JOptionPane.YES_NO_OPTION
        );

        if (choix == JOptionPane.YES_OPTION) {
            moteur.abandonner();
            mettreAJourAffichage();
        }
    }
}