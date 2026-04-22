package ui;

import MoteurDeJeu.Charger_Jeu;
import MoteurDeJeu.ChargementJeuException;
import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Gamedata;
import com.mycompany.projet_jeu.model.Puzzle;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FenetreAccueil extends JFrame {

    // Partie accueil
    private JTextField champPseudo;
    private JLabel labelMessage;
    private JPanel panelScenarios;
    private String pseudo;

    // Partie jeu
    private MoteurDeJeu moteur;
    private String cheminScenario;

    private JTextArea zoneTexte;
    private JLabel labelImage;
    private JPanel panelChoix;

    // Organisation générale
    private CardLayout cardLayout;
    private JPanel panelPrincipal;
    private JPanel panelAccueil;
    private JPanel panelJeu;

    public FenetreAccueil() {
        setTitle("Escape Game");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);

        creerInterfaceAccueil();
        creerInterfaceJeu();

        panelPrincipal.add(panelAccueil, "ACCUEIL");
        panelPrincipal.add(panelJeu, "JEU");

        add(panelPrincipal);

        cardLayout.show(panelPrincipal, "ACCUEIL");

        setVisible(true);
    }

    private void creerInterfaceAccueil() {
        panelAccueil = new JPanel(new BorderLayout());

        JPanel panelHaut = new JPanel();
        panelHaut.setLayout(new GridLayout(3, 1));

        JPanel panelPseudo = new JPanel();
        JLabel labelPseudo = new JLabel("Pseudo :");
        champPseudo = new JTextField(15);
        panelPseudo.add(labelPseudo);
        panelPseudo.add(champPseudo);

        JPanel panelBouton = new JPanel();
        JButton boutonValider = new JButton("Valider");
        JButton boutonImporterZip = new JButton("Importer un scénario .zip");
        panelBouton.add(boutonValider);
        panelBouton.add(boutonImporterZip);

        JPanel panelInfo = new JPanel();
        labelMessage = new JLabel("Entrez votre pseudo.");
        panelInfo.add(labelMessage);

        panelHaut.add(panelPseudo);
        panelHaut.add(panelBouton);
        panelHaut.add(panelInfo);

        panelAccueil.add(panelHaut, BorderLayout.NORTH);

        panelScenarios = new JPanel();
        panelScenarios.setLayout(new BoxLayout(panelScenarios, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelScenarios);
        panelAccueil.add(scrollPane, BorderLayout.CENTER);

        boutonValider.addActionListener(e -> validerPseudo());
        boutonImporterZip.addActionListener(e -> importerScenarioZip());
    }

    private void creerInterfaceJeu() {
        panelJeu = new JPanel(new BorderLayout());

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

        panelJeu.add(panelCentre, BorderLayout.CENTER);
        panelJeu.add(panelChoix, BorderLayout.SOUTH);
    }

    private void validerPseudo() {
        pseudo = champPseudo.getText().trim();

        if (pseudo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le pseudo ne doit pas être vide.");
            return;
        }

        labelMessage.setText("Bienvenue " + pseudo + " ! Choisissez un scénario :");
        afficherScenarios();
    }

    private void afficherScenarios() {
        panelScenarios.removeAll();

        File dossierScenarios = new File("scenarios");

        if (!dossierScenarios.exists() || !dossierScenarios.isDirectory()) {
            panelScenarios.add(new JLabel("Dossier 'scenarios' introuvable."));
            panelScenarios.revalidate();
            panelScenarios.repaint();
            return;
        }

        File[] fichiers = dossierScenarios.listFiles(file ->
                file.isDirectory() || file.getName().toLowerCase().endsWith(".zip"));

        if (fichiers == null || fichiers.length == 0) {
            panelScenarios.add(new JLabel("Aucun scénario disponible."));
            panelScenarios.revalidate();
            panelScenarios.repaint();
            return;
        }

        for (File fichier : fichiers) {
            JButton boutonScenario = new JButton(fichier.getName());
            boutonScenario.addActionListener(e -> lancerScenario(fichier.getAbsolutePath()));

            panelScenarios.add(boutonScenario);
            panelScenarios.add(Box.createVerticalStrut(10));
        }

        panelScenarios.revalidate();
        panelScenarios.repaint();
    }

    private void importerScenarioZip() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner un scénario .zip");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers ZIP", "zip"));

        int resultat = fileChooser.showOpenDialog(this);

        if (resultat != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fichierSelectionne = fileChooser.getSelectedFile();

        if (fichierSelectionne == null || !fichierSelectionne.getName().toLowerCase().endsWith(".zip")) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fichier .zip valide.");
            return;
        }

        try {
            File dossierScenarios = new File("scenarios");

            if (!dossierScenarios.exists()) {
                dossierScenarios.mkdirs();
            }

            Path source = fichierSelectionne.toPath();
            Path destination = new File(dossierScenarios, fichierSelectionne.getName()).toPath();

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

            JOptionPane.showMessageDialog(this, "Scénario importé avec succès : " + fichierSelectionne.getName());

            if (pseudo != null && !pseudo.isBlank()) {
                afficherScenarios();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'import du scénario : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lancerScenario(String cheminScenarioSelectionne) {
        try {
            String dossierScenarioACharger = cheminScenarioSelectionne;

            if (cheminScenarioSelectionne.toLowerCase().endsWith(".zip")) {
                dossierScenarioACharger = ZipUtils.extraireScenarioZip(cheminScenarioSelectionne);
            }

            Gamedata jeu = Charger_Jeu.chargerJeu(dossierScenarioACharger);
            moteur = new MoteurDeJeu(jeu);
            this.cheminScenario = dossierScenarioACharger;

            mettreAJourAffichageJeu();
            cardLayout.show(panelPrincipal, "JEU");

        } catch (ChargementJeuException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mettreAJourAffichageJeu() {
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

        JButton boutonAccueil = new JButton("Retour accueil");
        boutonAccueil.addActionListener(e -> retourAccueil());
        panelChoix.add(boutonAccueil);

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
            mettreAJourAffichageJeu();
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
                "- Joueur : " + pseudo + "\n" +
                "- Énigmes résolues : " + moteur.getNbEnigmesResolues()
        );

        panelChoix.removeAll();

        JButton boutonRecommencer = new JButton("Recommencer");
        boutonRecommencer.addActionListener(e -> {
            moteur.recommencer();
            mettreAJourAffichageJeu();
        });

        JButton boutonAccueil = new JButton("Retour accueil");
        boutonAccueil.addActionListener(e -> retourAccueil());

        panelChoix.add(boutonRecommencer);
        panelChoix.add(boutonAccueil);

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
            mettreAJourAffichageJeu();
        }
    }

    private void retourAccueil() {
        moteur = null;
        cheminScenario = null;

        zoneTexte.setText("");
        labelImage.setIcon(null);
        labelImage.setText("");

        panelChoix.removeAll();
        panelChoix.revalidate();
        panelChoix.repaint();

        cardLayout.show(panelPrincipal, "ACCUEIL");
    }
}