/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

/**
 *
 * @author cano28
 */

import MoteurDeJeu.Charger_Jeu;
import MoteurDeJeu.ChargementJeuException;
import MoteurDeJeu.MoteurDeJeu;
import com.mycompany.projet_jeu.model.Gamedata;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FenetreAccueil extends JFrame {

    private JTextField champPseudo;
    private JButton boutonValider;
    private JLabel labelMessage;
    private JPanel panelScenarios;

    private String pseudo;

    public FenetreAccueil() {
        setTitle("Accueil du jeu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelHaut = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel panelPseudo = new JPanel(new FlowLayout());
        panelPseudo.add(new JLabel("Entrez votre pseudo :"));
        champPseudo = new JTextField(15);
        panelPseudo.add(champPseudo);

        JPanel panelBouton = new JPanel();
        boutonValider = new JButton("Valider");
        panelBouton.add(boutonValider);

        JPanel panelBienvenue = new JPanel();
        labelMessage = new JLabel("Veuillez entrer votre pseudo.");
        panelBienvenue.add(labelMessage);

        panelHaut.add(panelPseudo);
        panelHaut.add(panelBouton);
        panelHaut.add(panelBienvenue);

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

        labelMessage.setText("Bienvenue " + pseudo + ", veuillez choisir votre jeu :");
        afficherScenarios();
    }

    private void afficherScenarios() {
        panelScenarios.removeAll();

        File dossierScenarios = new File("scenarios");

        if (!dossierScenarios.exists() || !dossierScenarios.isDirectory()) {
            panelScenarios.add(new JLabel("Aucun dossier 'scenarios' trouvé."));
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
            boutonScenario.setAlignmentX(Component.CENTER_ALIGNMENT);

            boutonScenario.addActionListener(e -> lancerScenario(dossier.getPath()));

            panelScenarios.add(Box.createVerticalStrut(10));
            panelScenarios.add(boutonScenario);
        }

        panelScenarios.revalidate();
        panelScenarios.repaint();
    }

    private void lancerScenario(String cheminScenario) {
        try {
            Gamedata jeu = Charger_Jeu.chargerJeu(cheminScenario);
            MoteurDeJeu moteur = new MoteurDeJeu(jeu);

            new FenetreJeu(moteur);
            dispose();

        } catch (ChargementJeuException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}