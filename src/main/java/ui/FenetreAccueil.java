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

/**
 * Fenêtre principale de l'application Escape Game.
 * <p>
 * Cette classe gère l'écran d'accueil, la saisie du pseudo,
 * l'importation d'un scénario, le lancement d'une partie,
 * l'affichage des énigmes et l'écran de fin.
 * </p>
 *
 * @author cano28
 */
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

    /**
     * Construit la fenêtre principale et initialise les interfaces accueil et jeu.
     */
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

    /**
     * Crée l'interface de l'écran d'accueil.
     */
    private void creerInterfaceAccueil() {
        panelAccueil = new JPanel(new BorderLayout());

        JPanel panelHaut = new JPanel();
        panelHaut.setLayout(new GridLayout(3, 1));

        // Zone de saisie du pseudo.
        JPanel panelPseudo = new JPanel();
        JLabel labelPseudo = new JLabel("Pseudo :");
        champPseudo = new JTextField(15);
        panelPseudo.add(labelPseudo);
        panelPseudo.add(champPseudo);

        // Boutons de validation et d'importation.
        JPanel panelBouton = new JPanel();
        JButton boutonValider = new JButton("Valider");
        JButton boutonImporterZip = new JButton("Importer un scénario .zip");
        panelBouton.add(boutonValider);
        panelBouton.add(boutonImporterZip);

        // Message d'information affiché à l'utilisateur.
        JPanel panelInfo = new JPanel();
        labelMessage = new JLabel("Entrez votre pseudo.");
        panelInfo.add(labelMessage);

        panelHaut.add(panelPseudo);
        panelHaut.add(panelBouton);
        panelHaut.add(panelInfo);

        panelAccueil.add(panelHaut, BorderLayout.NORTH);

        // Zone contenant les scénarios disponibles.
        panelScenarios = new JPanel();
        panelScenarios.setLayout(new BoxLayout(panelScenarios, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelScenarios);
        panelAccueil.add(scrollPane, BorderLayout.CENTER);

        boutonValider.addActionListener(e -> validerPseudo());
        boutonImporterZip.addActionListener(e -> importerScenarioZip());
    }

    /**
     * Crée l'interface utilisée pendant une partie.
     */
    private void creerInterfaceJeu() {
        panelJeu = new JPanel(new BorderLayout());

        // Zone d'affichage de la consigne.
        zoneTexte = new JTextArea();
        zoneTexte.setEditable(false);
        zoneTexte.setLineWrap(true);
        zoneTexte.setWrapStyleWord(true);
        zoneTexte.setFont(new Font("Serif", Font.PLAIN, 20));
        zoneTexte.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollTexte = new JScrollPane(zoneTexte);

        // Zone d'affichage de l'image.
        labelImage = new JLabel("", SwingConstants.CENTER);

        JPanel panelCentre = new JPanel(new BorderLayout());
        panelCentre.add(scrollTexte, BorderLayout.NORTH);
        panelCentre.add(labelImage, BorderLayout.CENTER);

        // Zone des choix/réponses.
        panelChoix = new JPanel(new FlowLayout());

        panelJeu.add(panelCentre, BorderLayout.CENTER);
        panelJeu.add(panelChoix, BorderLayout.SOUTH);
    }

    /**
     * Valide le pseudo saisi par l'utilisateur.
     * <p>
     * Si le pseudo est valide, les scénarios disponibles sont affichés.
     * </p>
     */
    private void validerPseudo() {
        pseudo = champPseudo.getText().trim();

        if (pseudo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le pseudo ne doit pas être vide.");
            return;
        }

        labelMessage.setText("Bienvenue " + pseudo + " ! Choisissez un scénario :");
        afficherScenarios();
    }

    /**
     * Affiche les scénarios présents dans le dossier local {@code scenarios}.
     */
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

    /**
     * Importe un scénario au format ZIP dans le dossier local {@code scenarios}.
     */
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
            JOptionPane.showMessageDialog(
                    this,
                    "Erreur lors de l'import du scénario : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Lance un scénario sélectionné.
     *
     * @param cheminScenarioSelectionne chemin du dossier ou du fichier ZIP du scénario
     */
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

    /**
     * Met à jour l'affichage du jeu selon l'énigme actuelle.
     */
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

        // Affichage adapté au type d'énigme.
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

        // Boutons communs à toutes les énigmes.
        JButton boutonAbandonner = new JButton("Abandonner");
        boutonAbandonner.addActionListener(e -> abandonnerPartie());
        panelChoix.add(boutonAbandonner);

        JButton boutonAccueil = new JButton("Retour accueil");
        boutonAccueil.addActionListener(e -> retourAccueil());
        panelChoix.add(boutonAccueil);

        panelChoix.revalidate();
        panelChoix.repaint();
    }

    /**
     * Affiche l'image associée à une énigme.
     *
     * @param puzzle énigme dont l'image doit être affichée
     */
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

    /**
     * Envoie la réponse de l'utilisateur au moteur de jeu.
     *
     * @param reponse réponse saisie ou sélectionnée par l'utilisateur
     */
    private void envoyerReponse(String reponse) {
        try {
            moteur.passerAuPuzzleSuivant(reponse.trim());
            mettreAJourAffichageJeu();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Affiche l'écran de fin de partie avec le résultat et le résumé.
     */
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

    /**
     * Demande confirmation puis abandonne la partie si l'utilisateur accepte.
     */
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

    /**
     * Réinitialise l'affichage du jeu et retourne à l'écran d'accueil.
     */
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