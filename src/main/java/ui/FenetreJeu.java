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

        panelChoix = new JPanel();
        panelChoix.setLayout(new FlowLayout());

        add(panelCentre, BorderLayout.CENTER);
        add(panelChoix, BorderLayout.SOUTH);

        mettreAJourAffichage();

        setVisible(true);
    }

    private void mettreAJourAffichage() {
        Puzzle puzzle = moteur.getPuzzleActuel();

        if (puzzle == null) {
            JOptionPane.showMessageDialog(this, "Erreur : puzzle actuel introuvable.");
            return;
        }

        zoneTexte.setText(puzzle.getPrompt());
        afficherImage(puzzle);

        panelChoix.removeAll();

        if (moteur.estTermine()) {
            JButton boutonRecommencer = new JButton("Recommencer");
            boutonRecommencer.addActionListener(e -> {
                moteur.recommencer();
                mettreAJourAffichage();
            });
            panelChoix.add(boutonRecommencer);

            JButton boutonQuitter = new JButton("Quitter");
            boutonQuitter.addActionListener(e -> System.exit(0));
            panelChoix.add(boutonQuitter);

        } else {

            if ("texte".equals(puzzle.getType())) {
                JTextField champReponse = new JTextField(15);
                panelChoix.add(champReponse);

                JButton boutonValider = new JButton("Valider");
                boutonValider.addActionListener(e -> envoyerReponse(champReponse.getText()));
                panelChoix.add(boutonValider);

                champReponse.addActionListener(e -> envoyerReponse(champReponse.getText()));

            } else if ("boolean".equals(puzzle.getType())) {

                if (puzzle.getChoices() != null && puzzle.getChoices().size() >= 2) {
                    String texteFalse = puzzle.getChoices().get(0);
                    String texteTrue = puzzle.getChoices().get(1);

                    JButton boutonTrue = new JButton(texteTrue);
                    boutonTrue.addActionListener(e -> envoyerReponse("true"));
                    panelChoix.add(boutonTrue);

                    JButton boutonFalse = new JButton(texteFalse);
                    boutonFalse.addActionListener(e -> envoyerReponse("false"));
                    panelChoix.add(boutonFalse);
                } else {
                    JLabel labelErreur = new JLabel("Erreur : une énigme boolean doit avoir 2 choix.");
                    panelChoix.add(labelErreur);
                }

            } else if ("qcm".equals(puzzle.getType())) {

                if (puzzle.getChoices() != null) {
                    for (String choix : puzzle.getChoices()) {
                        JButton boutonChoix = new JButton(choix);
                        boutonChoix.addActionListener(e -> envoyerReponse(choix));
                        panelChoix.add(boutonChoix);
                    }
                }

            } else {
                JLabel labelErreur = new JLabel("Type d'énigme non géré : " + puzzle.getType());
                panelChoix.add(labelErreur);
            }

            JButton boutonAbandonner = new JButton("Abandonner");
            boutonAbandonner.addActionListener(e -> System.exit(0));
            panelChoix.add(boutonAbandonner);
        }

        panelChoix.revalidate();
        panelChoix.repaint();
    }

    private void afficherImage(Puzzle puzzle) {
        if (puzzle.getImage() != null && !puzzle.getImage().isEmpty()) {
            String cheminImage = cheminScenario + File.separator + "images" + File.separator + puzzle.getImage();

            File fichierImage = new File(cheminImage);

            if (fichierImage.exists()) {
                ImageIcon icone = new ImageIcon(cheminImage);
                Image image = icone.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
                labelImage.setIcon(new ImageIcon(image));
                labelImage.setText("");
            } else {
                labelImage.setIcon(null);
                labelImage.setText("Image introuvable");
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
}