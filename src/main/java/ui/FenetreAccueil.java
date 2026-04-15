package ui;

import MoteurDeJeu.Charger_Jeu;
import MoteurDeJeu.ChargementJeuException;
import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Gamedata;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FenetreAccueil extends JFrame {

    private JTextField champPseudo;
    private JLabel labelMessage;
    private JPanel panelScenarios;
    private String pseudo;

    public FenetreAccueil() {
        setTitle("Accueil du jeu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelHaut = new JPanel();
        panelHaut.setLayout(new GridLayout(3, 1));

        JPanel panelPseudo = new JPanel();
        JLabel labelPseudo = new JLabel("Pseudo :");
        champPseudo = new JTextField(15);
        panelPseudo.add(labelPseudo);
        panelPseudo.add(champPseudo);

        JPanel panelBouton = new JPanel();
        JButton boutonValider = new JButton("Valider");
        panelBouton.add(boutonValider);

        JPanel panelInfo = new JPanel();
        labelMessage = new JLabel("Entrez votre pseudo.");
        panelInfo.add(labelMessage);

        panelHaut.add(panelPseudo);
        panelHaut.add(panelBouton);
        panelHaut.add(panelInfo);

        add(panelHaut, BorderLayout.NORTH);

        panelScenarios = new JPanel();
        panelScenarios.setLayout(new BoxLayout(panelScenarios, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(panelScenarios);
        add(scrollPane, BorderLayout.CENTER);

        boutonValider.addActionListener(e -> validerPseudo());

        setVisible(true);
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

        File[] dossiers = dossierScenarios.listFiles(File::isDirectory);

        if (dossiers == null || dossiers.length == 0) {
            panelScenarios.add(new JLabel("Aucun scénario disponible."));
            panelScenarios.revalidate();
            panelScenarios.repaint();
            return;
        }

        for (File dossier : dossiers) {
            JButton boutonScenario = new JButton(dossier.getName());

            boutonScenario.addActionListener(e -> lancerScenario(dossier.getAbsolutePath()));

            panelScenarios.add(boutonScenario);
            panelScenarios.add(Box.createVerticalStrut(10));
        }

        panelScenarios.revalidate();
        panelScenarios.repaint();
    }

    private void lancerScenario(String cheminScenario) {
        try {
            Gamedata jeu = Charger_Jeu.chargerJeu(cheminScenario);
            MoteurDeJeu moteur = new MoteurDeJeu(jeu);

            new FenetreJeu(moteur, cheminScenario);
            dispose();

        } catch (ChargementJeuException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}