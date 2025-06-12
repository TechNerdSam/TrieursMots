import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Logiciel "Trieur de Mots Alpha"
 * Permet de trier des listes de mots par ordre alphabétique avec diverses options.
 * Compatible Java 6.
 * * @author Gemini
 * @version 1.0
 */
public class TrieurDeMotsAlpha {

    private JFrame frame;
    private JTextArea inputArea;
    private JTextArea resultArea;
    private JRadioButton ascRadioButton;
    private JRadioButton descRadioButton;
    private JCheckBox ignoreCaseCheckBox;
    private JCheckBox ignoreAccentsCheckBox;
    private JCheckBox removeDuplicatesCheckBox;
    private JLabel statusLabel;

    // Pattern pour enlever les marques diacritiques combinantes (accents)
    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Lance l'application.
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        // Appliquer le Look and Feel du système pour une meilleure esthétique
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossible d'appliquer le Look and Feel du système natif.");
            // Utiliser le Look and Feel Metal par défaut en cas d'échec
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Impossible d'appliquer le Look and Feel Metal.");
            }
        }

        // Lancer l'interface utilisateur dans le thread de dispatch d'événements Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    TrieurDeMotsAlpha window = new TrieurDeMotsAlpha();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    // Imprimer la trace de la pile en cas d'erreur lors de l'initialisation
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Crée l'application.
     */
    public TrieurDeMotsAlpha() {
        initialize();
    }

    /**
     * Initialise le contenu de la frame.
     */
    private void initialize() {
        // --- Configuration de la fenêtre principale ---
        frame = new JFrame("Trieur de Mots Alpha");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(550, 650)); // Taille minimale pour une bonne lisibilité
        frame.setLocationRelativeTo(null); // Centrer la fenêtre

        // Panneau principal avec une bordure pour l'espacement global
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Espacement horizontal et vertical
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Marge autour de tout le contenu
        frame.setContentPane(mainPanel);

        // --- Section de Saisie (Nord) ---
        JPanel inputSectionPanel = new JPanel(new BorderLayout(0, 5)); // Espacement vertical
        inputSectionPanel.setBorder(new TitledBorder("1. Saisir les mots"));

        inputArea = new JTextArea();
        inputArea.setToolTipText("Tapez ou collez les mots. Séparateurs reconnus : espace, saut de ligne, virgule, point-virgule.");
        inputArea.setLineWrap(true); // Retour à la ligne automatique
        inputArea.setWrapStyleWord(true); // Retour à la ligne sur les mots entiers
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setPreferredSize(new Dimension(400, 120)); // Taille préférée pour la zone de saisie
        inputSectionPanel.add(inputScrollPane, BorderLayout.CENTER);

        JButton clearInputButton = new JButton("Effacer Saisie");
        clearInputButton.setToolTipText("Vider la zone de saisie.");
        clearInputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                statusLabel.setText("Zone de saisie effacée.");
            }
        });
        JPanel clearInputButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        clearInputButtonPanel.add(clearInputButton);
        inputSectionPanel.add(clearInputButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(inputSectionPanel, BorderLayout.NORTH);

        // --- Section Centrale (Options et Bouton Trier) ---
        JPanel centerSectionPanel = new JPanel();
        centerSectionPanel.setLayout(new BoxLayout(centerSectionPanel, BoxLayout.Y_AXIS)); // Empilement vertical

        // Panneau des Options
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new TitledBorder("2. Options de Tri"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5); // Espacement autour des composants
        gbc.anchor = GridBagConstraints.WEST; // Alignement à gauche

        ascRadioButton = new JRadioButton("Ordre Ascendant (A-Z)", true);
        descRadioButton = new JRadioButton("Ordre Descendant (Z-A)");
        ButtonGroup orderGroup = new ButtonGroup();
        orderGroup.add(ascRadioButton);
        orderGroup.add(descRadioButton);

        gbc.gridx = 0; gbc.gridy = 0; optionsPanel.add(ascRadioButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0; optionsPanel.add(descRadioButton, gbc);

        ignoreCaseCheckBox = new JCheckBox("Ignorer la casse (ex: Mot = mot)", true);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; optionsPanel.add(ignoreCaseCheckBox, gbc);

        ignoreAccentsCheckBox = new JCheckBox("Ignorer les accents (ex: été = ete)", true);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; optionsPanel.add(ignoreAccentsCheckBox, gbc);

        removeDuplicatesCheckBox = new JCheckBox("Supprimer les doublons");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; optionsPanel.add(removeDuplicatesCheckBox, gbc);

        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerSectionPanel.add(optionsPanel);

        // Panneau du Bouton Trier
        JPanel sortButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortButtonPanel.setBorder(new EmptyBorder(10, 0, 10, 0)); // Marge verticale
        JButton sortButton = new JButton("TRIER");
        sortButton.setFont(sortButton.getFont().deriveFont(Font.BOLD, 16f)); // Police plus grande et en gras
        sortButton.setToolTipText("Lancer le tri des mots selon les options sélectionnées.");
        sortButton.setPreferredSize(new Dimension(150, 40)); // Taille du bouton
        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSort();
            }
        });
        sortButtonPanel.add(sortButton);
        sortButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerSectionPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Petit espaceur
        centerSectionPanel.add(sortButtonPanel);
        centerSectionPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Petit espaceur

        mainPanel.add(centerSectionPanel, BorderLayout.CENTER);

        // --- Section Résultat (Sud) ---
        JPanel resultSectionPanel = new JPanel(new BorderLayout(0, 5));
        resultSectionPanel.setBorder(new TitledBorder("3. Résultat du Tri"));

        resultArea = new JTextArea();
        resultArea.setToolTipText("Les mots triés s'afficheront ici.");
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false); // Zone de résultat non modifiable
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(400, 150));
        resultSectionPanel.add(resultScrollPane, BorderLayout.CENTER);

        JPanel resultActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton copyResultButton = new JButton("Copier Résultat");
        copyResultButton.setToolTipText("Copier la liste triée dans le presse-papiers.");
        copyResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyResultToClipboard();
            }
        });
        JButton clearResultButton = new JButton("Effacer Résultat");
        clearResultButton.setToolTipText("Vider la zone de résultat.");
        clearResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
                statusLabel.setText("Zone de résultat effacée.");
            }
        });
        resultActionsPanel.add(copyResultButton);
        resultActionsPanel.add(clearResultButton);
        resultSectionPanel.add(resultActionsPanel, BorderLayout.SOUTH);
        mainPanel.add(resultSectionPanel, BorderLayout.SOUTH);

        // --- Barre de Statut (Extrême Sud de la frame) ---
        statusLabel = new JLabel("Prêt. Entrez des mots et cliquez sur TRIER.");
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Marge interne
        frame.add(statusLabel, BorderLayout.SOUTH); // Ajout direct à la frame, pas au mainPanel

        frame.pack(); // Ajuste la taille de la fenêtre au contenu
        if (frame.getWidth() < 550 || frame.getHeight() < 650) { // Assurer la taille minimale
             frame.setSize(550, 650);
        }
    }

    /**
     * Exécute le processus de tri.
     */
    private void performSort() {
        statusLabel.setText("Tri en cours...");
        String inputText = inputArea.getText();

        if (inputText == null || inputText.trim().isEmpty()) {
            resultArea.setText("");
            statusLabel.setText("La zone de saisie est vide. Aucun mot à trier.");
            return;
        }

        // Séparateurs : espace, saut de ligne, virgule, point-virgule
        String[] rawWords = inputText.split("[\\s,;\\n]+");
        List<String> words = new ArrayList<String>();
        for (String rawWord : rawWords) {
            String trimmedWord = rawWord.trim();
            if (!trimmedWord.isEmpty()) {
                words.add(trimmedWord);
            }
        }

        if (words.isEmpty()) {
            resultArea.setText("");
            statusLabel.setText("Aucun mot valide trouvé après nettoyage.");
            return;
        }

        // Gestion des doublons si l'option est cochée
        if (removeDuplicatesCheckBox.isSelected()) {
            Map<String, String> uniqueWordsMap = new LinkedHashMap<String, String>(); // Conserve l'ordre d'insertion initial
            boolean 당시_ignoreCase = ignoreCaseCheckBox.isSelected(); // 'final' implicite pour classe anonyme
            boolean 당시_ignoreAccents = ignoreAccentsCheckBox.isSelected();

            for (String word : words) {
                String key = word;
                if (당시_ignoreCase) {
                    key = key.toLowerCase(Locale.FRENCH);
                }
                if (당시_ignoreAccents) {
                    key = removeAccents(key); // Utiliser la version sans accent pour la clé
                }
                if (!uniqueWordsMap.containsKey(key)) {
                    uniqueWordsMap.put(key, word); // Stocker le mot original
                }
            }
            words = new ArrayList<String>(uniqueWordsMap.values());
        }

        final boolean effectiveIgnoreCase = ignoreCaseCheckBox.isSelected();
        final boolean effectiveIgnoreAccents = ignoreAccentsCheckBox.isSelected();

        // Tri de la liste de mots
        try {
            Collections.sort(words, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    Collator collator = Collator.getInstance(Locale.FRENCH);

                    if (effectiveIgnoreAccents) {
                        // Les accents sont ignorés, nous comparons les versions sans accent
                        String s1NoAccents = removeAccents(s1);
                        String s2NoAccents = removeAccents(s2);
                        if (effectiveIgnoreCase) {
                            // Ignorer accents ET ignorer casse
                            return s1NoAccents.compareToIgnoreCase(s2NoAccents);
                        } else {
                            // Ignorer accents MAIS respecter casse
                            return s1NoAccents.compareTo(s2NoAccents);
                        }
                    } else {
                        // Les accents sont respectés, utiliser Collator
                        if (effectiveIgnoreCase) {
                            // Respecter accents, Ignorer casse
                            collator.setStrength(Collator.SECONDARY);
                        } else {
                            // Respecter accents ET respecter casse
                            // "Mot" avant "mot" est géré par Collator.TERTIARY pour le français
                            collator.setStrength(Collator.TERTIARY);
                        }
                        return collator.compare(s1, s2);
                    }
                }
            });

            if (descRadioButton.isSelected()) {
                Collections.reverse(words);
            }

            // Affichage du résultat
            StringBuilder sb = new StringBuilder();
            for (String word : words) {
                sb.append(word).append("\n"); // Un mot par ligne
            }
            resultArea.setText(sb.toString());
            statusLabel.setText("Tri terminé. " + words.size() + " mot(s) trié(s).");

        } catch (Exception ex) {
            resultArea.setText("Erreur lors du tri : " + ex.getMessage());
            statusLabel.setText("Une erreur est survenue.");
            ex.printStackTrace(); // Pour le débogage
        }
    }

    /**
     * Supprime les accents d'une chaîne de caractères.
     * @param text La chaîne avec potentiellement des accents.
     * @return La chaîne sans accents.
     */
    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        // Normalise en NFD (Canonical Decomposition) pour séparer les caractères de base des diacritiques
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        // Supprime toutes les marques diacritiques combinantes
        return DIACRITICS_PATTERN.matcher(normalizedText).replaceAll("");
    }

    /**
     * Copie le contenu de la zone de résultat dans le presse-papiers.
     */
    private void copyResultToClipboard() {
        String resultText = resultArea.getText();
        if (resultText != null && !resultText.isEmpty()) {
            StringSelection stringSelection = new StringSelection(resultText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            statusLabel.setText("Résultat copié dans le presse-papiers !");
        } else {
            statusLabel.setText("Rien à copier depuis la zone de résultat.");
        }
    }
}

