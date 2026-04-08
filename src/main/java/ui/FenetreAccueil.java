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

        JPanel panelHaut = new JPanel(new GridLayout(3, 1));

        JPanel panelPseudo = new JPanel();
        panelPseudo.add(new JLabel("Pseudo :"));
        champPseudo = new JTextField(15);
        panelPseudo.add(champPseudo);

        JButton boutonValider = new JButton("Valider");

        JPanel panelBouton = new JPanel();
        panelBouton.add(boutonValider);

        labelMessage = new JLabel("Entrez votre pseudo.");
        JPanel panelMessage = new JPanel();
        panelMessage.add(labelMessage);

        panelHaut.add(panelPseudo);
        panelHaut.add(panelBouton);
        panelHaut.add(panelMessage);

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
        System.out.println("Chemin absolu : " + dossierScenarios.getAbsolutePath());
        System.out.println("Existe ? " + dossierScenarios.exists());
        System.out.println("Est un dossier ? " + dossierScenarios.isDirectory());

        File[] dossiers = dossierScenarios.listFiles(File::isDirectory);
        System.out.println("Nombre de scénarios : " + (dossiers == null ? 0 : dossiers.length));

        if (!dossierScenarios.exists() || !dossierScenarios.isDirectory()) {
            panelScenarios.add(new JLabel("Dossier scenarios introuvable."));
            panelScenarios.revalidate();
            panelScenarios.repaint();
            return;
        }

        if (dossiers == null || dossiers.length == 0) {
            panelScenarios.add(new JLabel("Aucun scénario disponible."));
            panelScenarios.revalidate();
            panelScenarios.repaint();
            return;
        }

        for (File dossier : dossiers) {
            JButton boutonScenario = new JButton(dossier.getName());

            boutonScenario.addActionListener(e -> lancerScenario(dossier.getPath()));

            panelScenarios.add(boutonScenario);
            panelScenarios.add(Box.createVerticalStrut(10));
        }

        panelScenarios.revalidate();
        panelScenarios.repaint();
    }

    private void lancerScenario(String cheminScenario) {
        System.out.println("Scénario lancé : " + cheminScenario);

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