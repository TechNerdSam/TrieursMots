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
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Logiciel "Trieur de Mots Alpha"
 * Permet de trier des listes de mots par ordre alphabétique avec diverses options.
 * Conçu pour être robuste, maintenable et convivial.
 * Compatible Java 6.
 *
 * Alpha Word Sorter Software
 * Allows sorting lists of words alphabetically with various options.
 * Designed to be robust, maintainable, and user-friendly.
 * Compatible with Java 6.
 *
 * @author Gemini
 * @version 1.2 (Refactorisation pour Qualité, Robustesse, Maintenabilité et Documentation)
 */
public class TrieurDeMotsAlpha {

    // --- UI Components ---
    private JFrame frame;
    private JTextArea inputArea;
    private JTextArea resultArea;
    private JRadioButton ascRadioButton;
    private JRadioButton descRadioButton;
    private JCheckBox ignoreCaseCheckBox;
    private JCheckBox ignoreAccentsCheckBox;
    private JCheckBox removeDuplicatesCheckBox;
    private JLabel statusLabel;
    private JButton sortButton;
    private JButton clearInputButton;
    private JButton copyResultButton;
    private JButton clearResultButton;
    private JButton loadFileButton;
    private JButton saveFileButton;
    private JComboBox<String> languageComboBox;

    // --- Panel References for direct access to TitledBorders ---
    private JPanel mainPanel;
    private JPanel inputSectionPanel;
    private JPanel optionsPanel;
    private JPanel resultSectionPanel;

    // --- Locale Management ---
    private Locale currentLocale = Locale.FRENCH; // Default locale
    private Map<String, Map<String, String>> translations; // Stores UI translations

    // --- Event Listeners ---
    private ActionListener languageComboBoxListener; // Dedicated listener for language combo box

    // --- Constants ---
    /**
     * Pattern to remove combining diacritical marks (accents).
     * Modèle pour supprimer les marques diacritiques combinantes (accents).
     */
    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Regex for word separators: space, new line, comma, semicolon.
     * Regex pour les séparateurs de mots : espace, saut de ligne, virgule, point-virgule.
     */
    private static final String WORD_SEPARATORS_REGEX = "[\\s,;\\n]+";

    // --- Translation Keys (Centralized for readability) ---
    private static final String KEY_APP_TITLE = "appTitle";
    private static final String KEY_INPUT_SECTION_TITLE = "inputSectionTitle";
    private static final String KEY_INPUT_AREA_TOOLTIP = "inputAreaToolTip";
    private static final String KEY_CLEAR_INPUT_BUTTON = "clearInputButton";
    private static final String KEY_CLEAR_INPUT_BUTTON_TOOLTIP = "clearInputButtonToolTip";
    private static final String KEY_OPTIONS_SECTION_TITLE = "optionsSectionTitle";
    private static final String KEY_ASC_RADIO_BUTTON = "ascRadioButton";
    private static final String KEY_DESC_RADIO_BUTTON = "descRadioButton";
    private static final String KEY_IGNORE_CASE_CHECKBOX = "ignoreCaseCheckBox";
    private static final String KEY_IGNORE_ACCENTS_CHECKBOX = "ignoreAccentsCheckBox";
    private static final String KEY_REMOVE_DUPLICATES_CHECKBOX = "removeDuplicatesCheckBox";
    private static final String KEY_SORT_BUTTON = "sortButton";
    private static final String KEY_SORT_BUTTON_TOOLTIP = "sortButtonToolTip";
    private static final String KEY_RESULT_SECTION_TITLE = "resultSectionTitle";
    private static final String KEY_RESULT_AREA_TOOLTIP = "resultAreaToolTip";
    private static final String KEY_COPY_RESULT_BUTTON = "copyResultButton";
    private static final String KEY_COPY_RESULT_BUTTON_TOOLTIP = "copyResultButtonToolTip";
    private static final String KEY_CLEAR_RESULT_BUTTON = "clearResultButton";
    private static final String KEY_CLEAR_RESULT_BUTTON_TOOLTIP = "clearResultButtonToolTip";
    private static final String KEY_STATUS_READY = "statusReady";
    private static final String KEY_STATUS_INPUT_CLEARED = "statusInputCleared";
    private static final String KEY_STATUS_RESULT_CLEARED = "statusResultCleared";
    private static final String KEY_STATUS_SORTING = "statusSorting";
    private static final String KEY_STATUS_INPUT_EMPTY = "statusInputEmpty";
    private static final String KEY_STATUS_NO_VALID_WORDS = "statusNoValidWords";
    private static final String KEY_STATUS_SORT_COMPLETE = "statusSortComplete";
    private static final String KEY_STATUS_SORT_ERROR = "statusSortError";
    private static final String KEY_STATUS_CLIPBOARD_COPY = "statusClipboardCopy";
    private static final String KEY_STATUS_CLIPBOARD_EMPTY = "statusClipboardEmpty";
    private static final String KEY_LOAD_FILE_BUTTON = "loadFileButton";
    private static final String KEY_LOAD_FILE_BUTTON_TOOLTIP = "loadFileButtonToolTip";
    private static final String KEY_SAVE_FILE_BUTTON = "saveFileButton";
    private static final String KEY_SAVE_FILE_BUTTON_TOOLTIP = "saveFileButtonToolTip";
    private static final String KEY_STATUS_FILE_LOADED = "statusFileLoaded";
    private static final String KEY_STATUS_FILE_LOAD_ERROR = "statusFileLoadError";
    private static final String KEY_STATUS_FILE_SAVED = "statusFileSaved";
    private static final String KEY_STATUS_FILE_SAVE_ERROR = "statusFileSaveError";
    private static final String KEY_LANGUAGE_LABEL = "languageLabel";
    private static final String KEY_LOCALE_SELECT_LABEL = "localeSelectLabel";
    private static final String KEY_FRENCH_LANGUAGE = "frenchLanguage";
    private static final String KEY_ENGLISH_LANGUAGE = "englishLanguage";
    private static final String KEY_FILE_CHOOSER_TITLE_LOAD = "fileChooserTitleLoad";
    private static final String KEY_FILE_CHOOSER_TITLE_SAVE = "fileChooserTitleSave";


    /**
     * Entry point of the application.
     * Launches the GUI on the Event Dispatch Thread (EDT).
     *
     * Point d'entrée de l'application.
     * Lance l'interface graphique sur le Event Dispatch Thread (EDT).
     *
     * @param args Command line arguments (not used).
     * Arguments de ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        // Apply system Look and Feel for better aesthetics
        // Appliquer le Look and Feel du système pour une meilleure esthétique
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error applying native system Look and Feel. Falling back to Metal. [Erreur lors de l'application du Look and Feel du système natif. Retour au Look and Feel Metal.]");
            // Fallback to Metal Look and Feel in case of failure
            // Utiliser le Look and Feel Metal par défaut en cas d'échec
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Error applying Metal Look and Feel. [Erreur lors de l'application du Look and Feel Metal.]");
                ex.printStackTrace(); // For debugging / Pour le débogage
            }
        }

        // Launch the user interface on the Swing Event Dispatch Thread
        // Lancer l'interface utilisateur dans le thread de dispatch d'événements Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    TrieurDeMotsAlpha window = new TrieurDeMotsAlpha();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    // Print stack trace in case of initialization error
                    // Imprimer la trace de la pile en cas d'erreur lors de l'initialisation
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Constructor for the TrieurDeMotsAlpha application.
     * Loads translations and initializes the GUI.
     *
     * Constructeur de l'application TrieurDeMotsAlpha.
     * Charge les traductions et initialise l'interface graphique.
     */
    public TrieurDeMotsAlpha() {
        loadTranslations();
        initialize();
        updateUIStrings(); // Update UI strings after initialization
    }

    /**
     * Loads the translation strings for different locales.
     * For this example, translations are hardcoded. For a larger application,
     * external .properties files would be used with ResourceBundle.
     *
     * Charge les chaînes de traduction pour différentes locales.
     * Pour cet exemple, les traductions sont codées en dur. Pour une application plus grande,
     * des fichiers .properties externes seraient utilisés avec ResourceBundle.
     */
    private void loadTranslations() {
        translations = new LinkedHashMap<String, Map<String, String>>();

        // French Translations / Traductions Françaises
        Map<String, String> fr = new LinkedHashMap<String, String>();
        fr.put(KEY_APP_TITLE, "Trieur de Mots Alpha");
        fr.put(KEY_INPUT_SECTION_TITLE, "1. Saisir les mots");
        fr.put(KEY_INPUT_AREA_TOOLTIP, "Tapez ou collez les mots. Séparateurs reconnus : espace, saut de ligne, virgule, point-virgule.");
        fr.put(KEY_CLEAR_INPUT_BUTTON, "Effacer Saisie");
        fr.put(KEY_CLEAR_INPUT_BUTTON_TOOLTIP, "Vider la zone de saisie.");
        fr.put(KEY_OPTIONS_SECTION_TITLE, "2. Options de Tri");
        fr.put(KEY_ASC_RADIO_BUTTON, "Ordre Ascendant (A-Z)");
        fr.put(KEY_DESC_RADIO_BUTTON, "Ordre Descendant (Z-A)");
        fr.put(KEY_IGNORE_CASE_CHECKBOX, "Ignorer la casse (ex: Mot = mot)");
        fr.put(KEY_IGNORE_ACCENTS_CHECKBOX, "Ignorer les accents (ex: été = ete)");
        fr.put(KEY_REMOVE_DUPLICATES_CHECKBOX, "Supprimer les doublons");
        fr.put(KEY_SORT_BUTTON, "TRIER");
        fr.put(KEY_SORT_BUTTON_TOOLTIP, "Lancer le tri des mots selon les options sélectionnées.");
        fr.put(KEY_RESULT_SECTION_TITLE, "3. Résultat du Tri");
        fr.put(KEY_RESULT_AREA_TOOLTIP, "Les mots triés s'afficheront ici.");
        fr.put(KEY_COPY_RESULT_BUTTON, "Copier Résultat");
        fr.put(KEY_COPY_RESULT_BUTTON_TOOLTIP, "Copier la liste triée dans le presse-papiers.");
        fr.put(KEY_CLEAR_RESULT_BUTTON, "Effacer Résultat");
        fr.put(KEY_CLEAR_RESULT_BUTTON_TOOLTIP, "Vider la zone de résultat.");
        fr.put(KEY_STATUS_READY, "Prêt. Entrez des mots et cliquez sur TRIER.");
        fr.put(KEY_STATUS_INPUT_CLEARED, "Zone de saisie effacée.");
        fr.put(KEY_STATUS_RESULT_CLEARED, "Zone de résultat effacée.");
        fr.put(KEY_STATUS_SORTING, "Tri en cours...");
        fr.put(KEY_STATUS_INPUT_EMPTY, "La zone de saisie est vide. Aucun mot à trier.");
        fr.put(KEY_STATUS_NO_VALID_WORDS, "Aucun mot valide trouvé après nettoyage.");
        fr.put(KEY_STATUS_SORT_COMPLETE, "Tri terminé. %d mot(s) trié(s).");
        fr.put(KEY_STATUS_SORT_ERROR, "Erreur lors du tri : ");
        fr.put(KEY_STATUS_CLIPBOARD_COPY, "Résultat copié dans le presse-papiers !");
        fr.put(KEY_STATUS_CLIPBOARD_EMPTY, "Rien à copier depuis la zone de résultat.");
        fr.put(KEY_LOAD_FILE_BUTTON, "Charger un fichier...");
        fr.put(KEY_LOAD_FILE_BUTTON_TOOLTIP, "Charger des mots depuis un fichier texte.");
        fr.put(KEY_SAVE_FILE_BUTTON, "Sauvegarder le résultat...");
        fr.put(KEY_SAVE_FILE_BUTTON_TOOLTIP, "Sauvegarder les mots triés dans un fichier texte.");
        fr.put(KEY_STATUS_FILE_LOADED, "Fichier chargé : %s");
        fr.put(KEY_STATUS_FILE_LOAD_ERROR, "Erreur lors du chargement du fichier : ");
        fr.put(KEY_STATUS_FILE_SAVED, "Résultat sauvegardé dans : %s");
        fr.put(KEY_STATUS_FILE_SAVE_ERROR, "Erreur lors de la sauvegarde du fichier : ");
        fr.put(KEY_LANGUAGE_LABEL, "Langue:");
        fr.put(KEY_LOCALE_SELECT_LABEL, "Locale de tri:");
        fr.put(KEY_FRENCH_LANGUAGE, "Français");
        fr.put(KEY_ENGLISH_LANGUAGE, "Anglais");
        fr.put(KEY_FILE_CHOOSER_TITLE_LOAD, "Charger un fichier texte");
        fr.put(KEY_FILE_CHOOSER_TITLE_SAVE, "Sauvegarder le résultat trié");
        translations.put("fr", fr);

        // English Translations / Traductions Anglaises
        Map<String, String> en = new LinkedHashMap<String, String>();
        en.put(KEY_APP_TITLE, "Alpha Word Sorter");
        en.put(KEY_INPUT_SECTION_TITLE, "1. Enter Words");
        en.put(KEY_INPUT_AREA_TOOLTIP, "Type or paste words. Recognized separators: space, new line, comma, semicolon.");
        en.put(KEY_CLEAR_INPUT_BUTTON, "Clear Input");
        en.put(KEY_CLEAR_INPUT_BUTTON_TOOLTIP, "Clear the input area.");
        en.put(KEY_OPTIONS_SECTION_TITLE, "2. Sort Options");
        en.put(KEY_ASC_RADIO_BUTTON, "Ascending Order (A-Z)");
        en.put(KEY_DESC_RADIO_BUTTON, "Descending Order (Z-A)");
        en.put(KEY_IGNORE_CASE_CHECKBOX, "Ignore Case (e.g., Word = word)");
        en.put(KEY_IGNORE_ACCENTS_CHECKBOX, "Ignore Accents (e.g., été = ete)");
        en.put(KEY_REMOVE_DUPLICATES_CHECKBOX, "Remove Duplicates");
        en.put(KEY_SORT_BUTTON, "SORT");
        en.put(KEY_SORT_BUTTON_TOOLTIP, "Start sorting words based on selected options.");
        en.put(KEY_RESULT_SECTION_TITLE, "3. Sorted Result");
        en.put(KEY_RESULT_AREA_TOOLTIP, "Sorted words will appear here.");
        en.put(KEY_COPY_RESULT_BUTTON, "Copy Result");
        en.put(KEY_COPY_RESULT_BUTTON_TOOLTIP, "Copy the sorted list to clipboard.");
        en.put(KEY_CLEAR_RESULT_BUTTON, "Clear Result");
        en.put(KEY_CLEAR_RESULT_BUTTON_TOOLTIP, "Clear the result area.");
        en.put(KEY_STATUS_READY, "Ready. Enter words and click SORT.");
        en.put(KEY_STATUS_INPUT_CLEARED, "Input area cleared.");
        en.put(KEY_STATUS_RESULT_CLEARED, "Result area cleared.");
        en.put(KEY_STATUS_SORTING, "Sorting in progress...");
        en.put(KEY_STATUS_INPUT_EMPTY, "Input area is empty. No words to sort.");
        en.put(KEY_STATUS_NO_VALID_WORDS, "No valid words found after cleaning.");
        en.put(KEY_STATUS_SORT_COMPLETE, "Sorting complete. %d word(s) sorted.");
        en.put(KEY_STATUS_SORT_ERROR, "Error during sorting: ");
        en.put(KEY_STATUS_CLIPBOARD_COPY, "Result copied to clipboard!");
        en.put(KEY_STATUS_CLIPBOARD_EMPTY, "Nothing to copy from result area.");
        en.put(KEY_LOAD_FILE_BUTTON, "Load from File...");
        en.put(KEY_LOAD_FILE_BUTTON_TOOLTIP, "Load words from a text file.");
        en.put(KEY_SAVE_FILE_BUTTON, "Save Result As...");
        en.put(KEY_SAVE_FILE_BUTTON_TOOLTIP, "Save sorted words to a text file.");
        en.put(KEY_STATUS_FILE_LOADED, "File loaded: %s");
        en.put(KEY_STATUS_FILE_LOAD_ERROR, "Error loading file: ");
        en.put(KEY_STATUS_FILE_SAVED, "Result saved to: %s");
        en.put(KEY_STATUS_FILE_SAVE_ERROR, "Error saving file: ");
        en.put(KEY_LANGUAGE_LABEL, "Language:");
        en.put(KEY_LOCALE_SELECT_LABEL, "Sorting Locale:");
        en.put(KEY_FRENCH_LANGUAGE, "French");
        en.put(KEY_ENGLISH_LANGUAGE, "English");
        en.put(KEY_FILE_CHOOSER_TITLE_LOAD, "Load Text File");
        en.put(KEY_FILE_CHOOSER_TITLE_SAVE, "Save Sorted Result");
        translations.put("en", en);
    }

    /**
     * Updates all UI strings based on the currently selected locale.
     * This method is called during initialization and when the language selection changes.
     *
     * Met à jour toutes les chaînes de l'interface utilisateur en fonction de la locale sélectionnée.
     * Cette méthode est appelée lors de l'initialisation et lorsque la sélection de langue change.
     */
    private void updateUIStrings() {
        Map<String, String> currentLangMap = translations.get(currentLocale.getLanguage());
        if (currentLangMap == null) {
            // Fallback to English if the current locale's language is not found
            // Retour à l'anglais si la langue de la locale actuelle n'est pas trouvée
            currentLangMap = translations.get("en");
        }

        frame.setTitle(currentLangMap.get(KEY_APP_TITLE));

        // Update TitledBorders / Mise à jour des TitledBorders
        ((TitledBorder) inputSectionPanel.getBorder()).setTitle(currentLangMap.get(KEY_INPUT_SECTION_TITLE));
        inputArea.setToolTipText(currentLangMap.get(KEY_INPUT_AREA_TOOLTIP));
        clearInputButton.setText(currentLangMap.get(KEY_CLEAR_INPUT_BUTTON));
        clearInputButton.setToolTipText(currentLangMap.get(KEY_CLEAR_INPUT_BUTTON_TOOLTIP));

        ((TitledBorder) optionsPanel.getBorder()).setTitle(currentLangMap.get(KEY_OPTIONS_SECTION_TITLE));
        ascRadioButton.setText(currentLangMap.get(KEY_ASC_RADIO_BUTTON));
        descRadioButton.setText(currentLangMap.get(KEY_DESC_RADIO_BUTTON));
        ignoreCaseCheckBox.setText(currentLangMap.get(KEY_IGNORE_CASE_CHECKBOX));
        ignoreAccentsCheckBox.setText(currentLangMap.get(KEY_IGNORE_ACCENTS_CHECKBOX));
        removeDuplicatesCheckBox.setText(currentLangMap.get(KEY_REMOVE_DUPLICATES_CHECKBOX));
        sortButton.setText(currentLangMap.get(KEY_SORT_BUTTON));
        sortButton.setToolTipText(currentLangMap.get(KEY_SORT_BUTTON_TOOLTIP));

        ((TitledBorder) resultSectionPanel.getBorder()).setTitle(currentLangMap.get(KEY_RESULT_SECTION_TITLE));
        resultArea.setToolTipText(currentLangMap.get(KEY_RESULT_AREA_TOOLTIP));
        copyResultButton.setText(currentLangMap.get(KEY_COPY_RESULT_BUTTON));
        copyResultButton.setToolTipText(currentLangMap.get(KEY_COPY_RESULT_BUTTON_TOOLTIP));
        clearResultButton.setText(currentLangMap.get(KEY_CLEAR_RESULT_BUTTON));
        clearResultButton.setToolTipText(currentLangMap.get(KEY_CLEAR_RESULT_BUTTON_TOOLTIP));

        statusLabel.setText(currentLangMap.get(KEY_STATUS_READY));
        loadFileButton.setText(currentLangMap.get(KEY_LOAD_FILE_BUTTON));
        loadFileButton.setToolTipText(currentLangMap.get(KEY_LOAD_FILE_BUTTON_TOOLTIP));
        saveFileButton.setText(currentLangMap.get(KEY_SAVE_FILE_BUTTON));
        saveFileButton.setToolTipText(currentLangMap.get(KEY_SAVE_FILE_BUTTON_TOOLTIP));

        // --- Handle languageComboBox updates safely ---
        // Temporarily remove the listener to prevent it from firing during programmatic item changes
        // Retirer temporairement l'écouteur pour éviter qu'il ne se déclenche lors des changements programmatiques
        if (languageComboBoxListener != null) {
            languageComboBox.removeActionListener(languageComboBoxListener);
        }

        languageComboBox.removeAllItems(); // This can trigger an ActionEvent
        languageComboBox.addItem(translations.get("fr").get(KEY_FRENCH_LANGUAGE));
        languageComboBox.addItem(translations.get("en").get(KEY_ENGLISH_LANGUAGE));

        // Set the selected item based on currentLocale
        // Définir l'élément sélectionné en fonction de la locale actuelle
        if (currentLocale.getLanguage().equals("fr")) {
            languageComboBox.setSelectedItem(translations.get("fr").get(KEY_FRENCH_LANGUAGE));
        } else {
            languageComboBox.setSelectedItem(translations.get("en").get(KEY_ENGLISH_LANGUAGE));
        }

        // Re-add the listener after all modifications
        // Remettre l'écouteur après toutes les modifications
        if (languageComboBoxListener != null) {
            languageComboBox.addActionListener(languageComboBoxListener);
        }
    }


    /**
     * Initializes the contents of the main application frame.
     * Sets up all UI components, layouts, and initial listeners.
     *
     * Initialise le contenu de la fenêtre principale de l'application.
     * Configure tous les composants d'interface utilisateur, les mises en page et les écouteurs initiaux.
     */
    private void initialize() {
        // --- Main Frame Configuration / Configuration de la fenêtre principale ---
        frame = new JFrame(); // Title will be updated by updateUIStrings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(650, 750)); // Minimum size for good readability / Taille minimale pour une bonne lisibilité
        frame.setLocationRelativeTo(null); // Center the window on screen / Centrer la fenêtre sur l'écran

        // Main panel with a border for overall spacing
        // Panneau principal avec une bordure pour l'espacement global
        mainPanel = new JPanel(new BorderLayout(10, 10)); // Horizontal and vertical spacing / Espacement horizontal et vertical
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margin around all content / Marge autour de tout le contenu
        frame.setContentPane(mainPanel);

        // --- Input Section (North) / Section de Saisie (Nord) ---
        inputSectionPanel = new JPanel(new BorderLayout(0, 5)); // Vertical spacing / Espacement vertical
        inputSectionPanel.setBorder(new TitledBorder("")); // Title will be updated by updateUIStrings
        mainPanel.add(inputSectionPanel, BorderLayout.NORTH);

        inputArea = new JTextArea();
        inputArea.setLineWrap(true); // Automatic line wrap / Retour à la ligne automatique
        inputArea.setWrapStyleWord(true); // Wrap at word boundaries / Retour à la ligne sur les mots entiers
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setPreferredSize(new Dimension(400, 120)); // Preferred size for the input area / Taille préférée pour la zone de saisie
        inputSectionPanel.add(inputScrollPane, BorderLayout.CENTER);

        // Panel for input buttons (Clear Input and Load File)
        // Panneau pour les boutons d'entrée (Effacer Saisie et Charger Fichier)
        JPanel inputButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        clearInputButton = new JButton(""); // Text will be updated by updateUIStrings
        clearInputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_INPUT_CLEARED));
            }
        });
        inputButtonsPanel.add(clearInputButton);

        loadFileButton = new JButton(""); // Text will be updated by updateUIStrings
        loadFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });
        inputButtonsPanel.add(loadFileButton);
        inputSectionPanel.add(inputButtonsPanel, BorderLayout.SOUTH);


        // --- Center Section (Options and Sort Button) / Section Centrale (Options et Bouton Trier) ---
        JPanel centerSectionPanel = new JPanel();
        centerSectionPanel.setLayout(new BoxLayout(centerSectionPanel, BoxLayout.Y_AXIS)); // Vertical stacking / Empilement vertical
        mainPanel.add(centerSectionPanel, BorderLayout.CENTER);

        // Options Panel / Panneau des Options
        optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new TitledBorder("")); // Title will be updated by updateUIStrings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5); // Spacing around components / Espacement autour des composants
        gbc.anchor = GridBagConstraints.WEST; // Align to the left / Alignement à gauche

        ascRadioButton = new JRadioButton("", true); // Text will be updated by updateUIStrings
        descRadioButton = new JRadioButton(""); // Text will be updated by updateUIStrings
        ButtonGroup orderGroup = new ButtonGroup();
        orderGroup.add(ascRadioButton);
        orderGroup.add(descRadioButton);

        gbc.gridx = 0; gbc.gridy = 0; optionsPanel.add(ascRadioButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0; optionsPanel.add(descRadioButton, gbc);

        ignoreCaseCheckBox = new JCheckBox(""); // Text will be updated by updateUIStrings
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; optionsPanel.add(ignoreCaseCheckBox, gbc);

        ignoreAccentsCheckBox = new JCheckBox(""); // Text will be updated by updateUIStrings
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; optionsPanel.add(ignoreAccentsCheckBox, gbc);

        removeDuplicatesCheckBox = new JCheckBox(""); // Text will be updated by updateUIStrings
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; optionsPanel.add(removeDuplicatesCheckBox, gbc);

        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerSectionPanel.add(optionsPanel);

        // Sort Button Panel / Panneau du Bouton Trier
        JPanel sortButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortButtonPanel.setBorder(new EmptyBorder(10, 0, 10, 0)); // Vertical margin / Marge verticale
        sortButton = new JButton(""); // Text will be updated by updateUIStrings
        sortButton.setFont(sortButton.getFont().deriveFont(Font.BOLD, 16f)); // Larger and bold font / Police plus grande et en gras
        sortButton.setPreferredSize(new Dimension(150, 40)); // Button size / Taille du bouton
        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSortTask(); // Launch the asynchronous sort task / Lance la tâche de tri asynchrone
            }
        });
        sortButtonPanel.add(sortButton);
        sortButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerSectionPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Small spacer / Petit espaceur
        centerSectionPanel.add(sortButtonPanel);
        centerSectionPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Small spacer / Petit espaceur


        // --- Result Section (South) / Section Résultat (Sud) ---
        resultSectionPanel = new JPanel(new BorderLayout(0, 5));
        resultSectionPanel.setBorder(new TitledBorder("")); // Title will be updated by updateUIStrings
        mainPanel.add(resultSectionPanel, BorderLayout.SOUTH);

        resultArea = new JTextArea();
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false); // Result area is not editable / Zone de résultat non modifiable
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(400, 150));
        resultSectionPanel.add(resultScrollPane, BorderLayout.CENTER);

        // Panel for result action buttons (Copy Result, Clear Result, Save Result)
        // Panneau pour les boutons d'action du résultat (Copier Résultat, Effacer Résultat, Sauvegarder Résultat)
        JPanel resultActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        copyResultButton = new JButton(""); // Text will be updated by updateUIStrings
        copyResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyResultToClipboard();
            }
        });
        resultActionsPanel.add(copyResultButton);

        clearResultButton = new JButton(""); // Text will be updated by updateUIStrings
        clearResultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
                statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_RESULT_CLEARED));
            }
        });
        resultActionsPanel.add(clearResultButton);

        saveFileButton = new JButton(""); // Text will be updated by updateUIStrings
        saveFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveResultToFile();
            }
        });
        resultActionsPanel.add(saveFileButton);
        resultSectionPanel.add(resultActionsPanel, BorderLayout.SOUTH);


        // --- Status Bar and Language Selection (Extreme South of the frame) ---
        // Barre de Statut et Sélection de Langue (Extrême Sud de la fenêtre)
        JPanel southPanel = new JPanel(new BorderLayout());

        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel langLabel = new JLabel(translations.get(currentLocale.getLanguage()).get(KEY_LANGUAGE_LABEL));
        languageComboBox = new JComboBox<String>(new String[]{
            translations.get("fr").get(KEY_FRENCH_LANGUAGE),
            translations.get("en").get(KEY_ENGLISH_LANGUAGE)
        });

        // Initialize the language combo box listener
        // Initialiser l'écouteur du JComboBox de langue
        languageComboBoxListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedLangText = (String) languageComboBox.getSelectedItem();
                // Defensive null check: selectedLangText could be null if no item is selected (e.g., during programmatic removal)
                // Vérification défensive de null : selectedLangText peut être null si aucun élément n'est sélectionné (par exemple, lors d'une suppression programmatique)
                if (selectedLangText != null) {
                    if (selectedLangText.equals(translations.get("fr").get(KEY_FRENCH_LANGUAGE))) {
                        currentLocale = Locale.FRENCH;
                    } else if (selectedLangText.equals(translations.get("en").get(KEY_ENGLISH_LANGUAGE))) {
                        currentLocale = Locale.ENGLISH;
                    }
                    updateUIStrings(); // Re-update UI after locale change / Re-mise à jour de l'interface après le changement de locale
                }
            }
        };
        languageComboBox.addActionListener(languageComboBoxListener); // Add the listener after initialization

        languagePanel.add(langLabel);
        languagePanel.add(languageComboBox);

        statusLabel = new JLabel(""); // Text will be initialized by updateUIStrings
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Internal margin / Marge interne

        southPanel.add(languagePanel, BorderLayout.NORTH);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        frame.add(southPanel, BorderLayout.SOUTH); // Add directly to the frame / Ajout direct à la fenêtre

        frame.pack(); // Adjust frame size to content / Ajuste la taille de la fenêtre au contenu
        // Ensure minimum size / Assurer la taille minimale
        if (frame.getWidth() < 650 || frame.getHeight() < 750) {
             frame.setSize(650, 750);
        }
    }

    /**
     * Initiates the word sorting process in a background thread using SwingWorker.
     * This prevents the UI from freezing during potentially long operations.
     *
     * Lance le processus de tri des mots dans un thread d'arrière-plan en utilisant SwingWorker.
     * Cela empêche l'interface utilisateur de se figer lors d'opérations potentiellement longues.
     */
    private void startSortTask() {
        statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_SORTING));
        setUIEnabled(false); // Disable UI during sorting / Désactiver l'interface pendant le tri

        final String inputText = inputArea.getText();

        SwingWorker<String, String> sortWorker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                if (inputText == null || inputText.trim().isEmpty()) {
                    return translations.get(currentLocale.getLanguage()).get(KEY_STATUS_INPUT_EMPTY);
                }

                // Split words using predefined separators / Diviser les mots en utilisant les séparateurs prédéfinis
                String[] rawWords = inputText.split(WORD_SEPARATORS_REGEX);
                List<String> words = new ArrayList<String>();
                for (String rawWord : rawWords) {
                    String trimmedWord = rawWord.trim();
                    if (!trimmedWord.isEmpty()) {
                        words.add(trimmedWord);
                    }
                }

                if (words.isEmpty()) {
                    return translations.get(currentLocale.getLanguage()).get(KEY_STATUS_NO_VALID_WORDS);
                }

                // Handle duplicates if the option is checked
                // Gérer les doublons si l'option est cochée
                if (removeDuplicatesCheckBox.isSelected()) {
                    // LinkedHashMap maintains insertion order for unique words
                    // LinkedHashMap maintient l'ordre d'insertion pour les mots uniques
                    Map<String, String> uniqueWordsMap = new LinkedHashMap<String, String>();
                    boolean currentIgnoreCase = ignoreCaseCheckBox.isSelected();
                    boolean currentIgnoreAccents = ignoreAccentsCheckBox.isSelected();

                    for (String word : words) {
                        String key = word;
                        if (currentIgnoreCase) {
                            key = key.toLowerCase(currentLocale);
                        }
                        if (currentIgnoreAccents) {
                            key = removeAccents(key); // Use accent-free version for the key / Utiliser la version sans accent pour la clé
                        }
                        if (!uniqueWordsMap.containsKey(key)) {
                            uniqueWordsMap.put(key, word); // Store the original word / Stocker le mot original
                        }
                    }
                    words = new ArrayList<String>(uniqueWordsMap.values());
                }

                final boolean effectiveIgnoreCase = ignoreCaseCheckBox.isSelected();
                final boolean effectiveIgnoreAccents = ignoreAccentsCheckBox.isSelected();
                final Locale sortingLocale = currentLocale; // Capture the locale for sorting / Capturer la locale pour le tri

                // Sort the list of words / Tri de la liste de mots
                Collections.sort(words, new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        // Collator for locale-sensitive string comparison
                        // Collator pour la comparaison de chaînes sensible à la locale
                        Collator collator = Collator.getInstance(sortingLocale);

                        if (effectiveIgnoreAccents) {
                            // Accents are ignored, compare accent-free versions
                            // Les accents sont ignorés, nous comparons les versions sans accent
                            String s1NoAccents = removeAccents(s1);
                            String s2NoAccents = removeAccents(s2);
                            if (effectiveIgnoreCase) {
                                // Ignore accents AND ignore case
                                // Ignorer accents ET ignorer casse
                                return s1NoAccents.compareToIgnoreCase(s2NoAccents);
                            } else {
                                // Ignore accents BUT respect case
                                // Ignorer accents MAIS respecter casse
                                return s1NoAccents.compareTo(s2NoAccents);
                            }
                        } else {
                            // Accents are respected, use Collator strength
                            // Les accents sont respectés, utiliser la force du Collator
                            if (effectiveIgnoreCase) {
                                // Respect accents, Ignore case (secondary strength ignores case differences)
                                // Respecter accents, Ignorer casse (force secondaire ignore les différences de casse)
                                collator.setStrength(Collator.SECONDARY);
                            } else {
                                // Respect accents AND respect case (tertiary strength distinguishes case and accents)
                                // Respecter accents ET respecter casse (force tertiaire distingue casse et accents)
                                collator.setStrength(Collator.TERTIARY);
                            }
                            return collator.compare(s1, s2);
                        }
                    }
                });

                if (descRadioButton.isSelected()) {
                    Collections.reverse(words);
                }

                // Build the result string / Construire la chaîne de résultat
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    sb.append(word).append("\n"); // One word per line / Un mot par ligne
                }
                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    String result = get(); // Retrieve the result from doInBackground
                    // Récupérer le résultat de doInBackground
                    if (result.equals(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_INPUT_EMPTY)) ||
                        result.equals(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_NO_VALID_WORDS))) {
                        resultArea.setText("");
                        statusLabel.setText(result);
                    } else {
                        resultArea.setText(result);
                        statusLabel.setText(String.format(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_SORT_COMPLETE), words.size()));
                    }
                } catch (Exception ex) {
                    resultArea.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_SORT_ERROR) + ex.getMessage());
                    statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_SORT_ERROR));
                    ex.printStackTrace(); // For debugging / Pour le débogage
                } finally {
                    setUIEnabled(true); // Re-enable UI after task completion / Réactiver l'interface après la fin de la tâche
                }
            }
        };

        sortWorker.execute(); // Execute the SwingWorker / Exécuter le SwingWorker
    }

    /**
     * Enables or disables UI components.
     * Used to prevent user interaction during background operations.
     *
     * Active ou désactive les composants de l'interface utilisateur.
     * Utilisé pour empêcher l'interaction de l'utilisateur pendant les opérations en arrière-plan.
     *
     * @param enabled True to enable, false to disable.
     * True pour activer, false pour désactiver.
     */
    private void setUIEnabled(boolean enabled) {
        inputArea.setEnabled(enabled);
        clearInputButton.setEnabled(enabled);
        loadFileButton.setEnabled(enabled);
        ascRadioButton.setEnabled(enabled);
        descRadioButton.setEnabled(enabled);
        ignoreCaseCheckBox.setEnabled(enabled);
        ignoreAccentsCheckBox.setEnabled(enabled);
        removeDuplicatesCheckBox.setEnabled(enabled);
        sortButton.setEnabled(enabled);
        copyResultButton.setEnabled(enabled);
        clearResultButton.setEnabled(enabled);
        saveFileButton.setEnabled(enabled);
        languageComboBox.setEnabled(enabled);
    }

    /**
     * Removes accents from a string.
     * Utilizes Unicode Normalization Form D (NFD) to separate base characters from diacritics,
     * then removes all combining diacritical marks.
     *
     * Supprime les accents d'une chaîne de caractères.
     * Utilise la forme de normalisation Unicode D (NFD) pour séparer les caractères de base des diacritiques,
     * puis supprime toutes les marques diacritiques combinantes.
     *
     * @param text The string potentially containing accents.
     * La chaîne avec potentiellement des accents.
     * @return The string without accents.
     * La chaîne sans accents.
     */
    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        // Normalize to NFD (Canonical Decomposition) to separate base characters from diacritics
        // Normalise en NFD (Décomposition Canonique) pour séparer les caractères de base des diacritiques
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        // Remove all combining diacritical marks
        // Supprime toutes les marques diacritiques combinantes
        return DIACRITICS_PATTERN.matcher(normalizedText).replaceAll("");
    }

    /**
     * Copies the content of the result area to the system clipboard.
     * Provides user feedback via the status label.
     *
     * Copie le contenu de la zone de résultat dans le presse-papiers du système.
     * Fournit un retour d'information à l'utilisateur via le label de statut.
     */
    private void copyResultToClipboard() {
        String resultText = resultArea.getText();
        if (resultText != null && !resultText.isEmpty()) {
            StringSelection stringSelection = new StringSelection(resultText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_CLIPBOARD_COPY));
        } else {
            statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_CLIPBOARD_EMPTY));
        }
    }

    /**
     * Loads the content of a text file into the input area.
     * Uses a JFileChooser to allow the user to select a file.
     * Handles potential IOException during file reading.
     *
     * Charge le contenu d'un fichier texte dans la zone de saisie.
     * Utilise un JFileChooser pour permettre à l'utilisateur de sélectionner un fichier.
     * Gère les IOException potentielles lors de la lecture du fichier.
     */
    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(translations.get(currentLocale.getLanguage()).get(KEY_FILE_CHOOSER_TITLE_LOAD));
        int userSelection = fileChooser.showOpenDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            BufferedReader reader = null; // Declare outside try-block for finally
            try {
                reader = new BufferedReader(new FileReader(fileToLoad));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                inputArea.setText(content.toString());
                statusLabel.setText(String.format(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_FILE_LOADED), fileToLoad.getName()));
            } catch (IOException ex) {
                statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_FILE_LOAD_ERROR) + ex.getMessage());
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        System.err.println("Error closing BufferedReader: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Saves the content of the result area to a text file.
     * Uses a JFileChooser to allow the user to select a save location and filename.
     * Ensures the file has a .txt extension. Handles potential IOException during file writing.
     *
     * Sauvegarde le contenu de la zone de résultat dans un fichier texte.
     * Utilise un JFileChooser pour permettre à l'utilisateur de sélectionner un emplacement et un nom de fichier.
     * S'assure que le fichier a l'extension .txt. Gère les IOException potentielles lors de l'écriture du fichier.
     */
    private void saveResultToFile() {
        String resultText = resultArea.getText();
        if (resultText == null || resultText.isEmpty()) {
            statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_CLIPBOARD_EMPTY)); // Reusing message
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(translations.get(currentLocale.getLanguage()).get(KEY_FILE_CHOOSER_TITLE_SAVE));
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Ensure the file has a .txt extension
            // Assurez-vous que le fichier a une extension .txt
            if (!fileToSave.getName().toLowerCase(currentLocale).endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }

            BufferedWriter writer = null; // Declare outside try-block for finally
            try {
                writer = new BufferedWriter(new FileWriter(fileToSave));
                writer.write(resultText);
                statusLabel.setText(String.format(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_FILE_SAVED), fileToSave.getName()));
            } catch (IOException ex) {
                statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_FILE_SAVE_ERROR) + ex.getMessage());
                ex.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        System.err.println("Error closing BufferedWriter: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}