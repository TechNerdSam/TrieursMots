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
 * Logiciel "Trieur de Mots Alpha" - Refonte Majeure (Corrigée)
 * Version stable : Correction du bug de ClassCastException dans setComponentsEnabled.
 *
 * @version 2.1 (Bugfix Edition)
 */
public class TrieurDeMotsAlpha {

    // --- Composants de l'interface (UI) ---
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

    // --- Références aux Panneaux (pour changer les titres dynamiquement) ---
    private JPanel inputSectionPanel;
    private JPanel optionsPanel;
    private JPanel resultSectionPanel;

    // --- Gestion de la Locale (Langue & Tri) ---
    private Locale currentLocale = Locale.FRENCH; // Par défaut
    private Map<String, Map<String, String>> translations; // Stockage des textes UI
    private ActionListener languageComboBoxListener;

    // --- Constantes Techniques ---
    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final String WORD_SEPARATORS_REGEX = "[\\s,;\\n\\t]+";

    // --- Clés de Traduction ---
    private static final String KEY_APP_TITLE = "appTitle";
    private static final String KEY_INPUT_SECTION = "inputSection";
    private static final String KEY_INPUT_TOOLTIP = "inputTooltip";
    private static final String KEY_BTN_CLEAR = "btnClear";
    private static final String KEY_BTN_LOAD = "btnLoad";
    private static final String KEY_OPTIONS_SECTION = "optionsSection";
    private static final String KEY_OPT_ASC = "optAsc";
    private static final String KEY_OPT_DESC = "optDesc";
    private static final String KEY_OPT_CASE = "optCase";
    private static final String KEY_OPT_ACCENTS = "optAccents";
    private static final String KEY_OPT_DEDUP = "optDedup";
    private static final String KEY_BTN_SORT = "btnSort";
    private static final String KEY_RESULT_SECTION = "resultSection";
    private static final String KEY_BTN_COPY = "btnCopy";
    private static final String KEY_BTN_SAVE = "btnSave";
    private static final String KEY_STATUS_READY = "statusReady";
    private static final String KEY_STATUS_SORTING = "statusSorting";
    private static final String KEY_STATUS_DONE = "statusDone";
    private static final String KEY_STATUS_ERROR = "statusError";
    private static final String KEY_LANG_LABEL = "langLabel";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossible d'appliquer le style système.");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TrieurDeMotsAlpha();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public TrieurDeMotsAlpha() {
        loadTranslations();
        initializeUI();
        updateUIStrings();
        frame.setVisible(true);
    }

    private void loadTranslations() {
        translations = new LinkedHashMap<String, Map<String, String>>();

        Map<String, String> fr = new LinkedHashMap<String, String>();
        fr.put(KEY_APP_TITLE, "Trieur de Mots Alpha v2.1");
        fr.put(KEY_INPUT_SECTION, "1. Saisir ou Charger");
        fr.put(KEY_INPUT_TOOLTIP, "Collez votre texte ici.");
        fr.put(KEY_BTN_CLEAR, "Effacer");
        fr.put(KEY_BTN_LOAD, "Charger Fichier...");
        fr.put(KEY_OPTIONS_SECTION, "2. Algorithme de Tri");
        fr.put(KEY_OPT_ASC, "Croissant (A → Z)");
        fr.put(KEY_OPT_DESC, "Décroissant (Z → A)");
        fr.put(KEY_OPT_CASE, "Ignorer la casse (a = A)");
        fr.put(KEY_OPT_ACCENTS, "Ignorer les accents (é = e)");
        fr.put(KEY_OPT_DEDUP, "Supprimer les doublons");
        fr.put(KEY_BTN_SORT, "TRIER LES MOTS");
        fr.put(KEY_RESULT_SECTION, "3. Résultat");
        fr.put(KEY_BTN_COPY, "Copier");
        fr.put(KEY_BTN_SAVE, "Sauvegarder...");
        fr.put(KEY_STATUS_READY, "Prêt. En attente de saisie.");
        fr.put(KEY_STATUS_SORTING, "Traitement en cours...");
        fr.put(KEY_STATUS_DONE, "Terminé ! %d mots triés.");
        fr.put(KEY_STATUS_ERROR, "Erreur : ");
        fr.put(KEY_LANG_LABEL, "Langue / Language :");
        translations.put("fr", fr);

        Map<String, String> en = new LinkedHashMap<String, String>();
        en.put(KEY_APP_TITLE, "Alpha Word Sorter v2.1");
        en.put(KEY_INPUT_SECTION, "1. Input or Load");
        en.put(KEY_INPUT_TOOLTIP, "Paste your text here.");
        en.put(KEY_BTN_CLEAR, "Clear");
        en.put(KEY_BTN_LOAD, "Load File...");
        en.put(KEY_OPTIONS_SECTION, "2. Sorting Algorithm");
        en.put(KEY_OPT_ASC, "Ascending (A → Z)");
        en.put(KEY_OPT_DESC, "Descending (Z → A)");
        en.put(KEY_OPT_CASE, "Ignore Case (a = A)");
        en.put(KEY_OPT_ACCENTS, "Ignore Accents (é = e)");
        en.put(KEY_OPT_DEDUP, "Remove Duplicates");
        en.put(KEY_BTN_SORT, "SORT WORDS");
        en.put(KEY_RESULT_SECTION, "3. Result");
        en.put(KEY_BTN_COPY, "Copy");
        en.put(KEY_BTN_SAVE, "Save As...");
        en.put(KEY_STATUS_READY, "Ready. Waiting for input.");
        en.put(KEY_STATUS_SORTING, "Processing...");
        en.put(KEY_STATUS_DONE, "Done! %d words sorted.");
        en.put(KEY_STATUS_ERROR, "Error: ");
        en.put(KEY_LANG_LABEL, "Language / Langue:");
        translations.put("en", en);
    }

    private void initializeUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 750));
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        frame.setContentPane(mainPanel);

        // --- SECTION NORD ---
        inputSectionPanel = new JPanel(new BorderLayout(0, 5));
        inputSectionPanel.setBorder(new TitledBorder(""));
        
        inputArea = new JTextArea(8, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputSectionPanel.add(inputScroll, BorderLayout.CENTER);

        JPanel inputBtnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loadFileButton = new JButton();
        loadFileButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { loadFile(); }});
        clearInputButton = new JButton();
        clearInputButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { inputArea.setText(""); statusLabel.setText("Cleared."); }});
        
        inputBtnBar.add(loadFileButton);
        inputBtnBar.add(clearInputButton);
        inputSectionPanel.add(inputBtnBar, BorderLayout.SOUTH);
        
        mainPanel.add(inputSectionPanel, BorderLayout.NORTH);

        // --- SECTION CENTRE ---
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        optionsPanel = new JPanel();
        // Utilisation d'un layout simple pour éviter les confusions de hiérarchie
        optionsPanel.setLayout(new BorderLayout());
        optionsPanel.setBorder(new TitledBorder(""));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ascRadioButton = new JRadioButton("", true);
        descRadioButton = new JRadioButton("");
        ButtonGroup group = new ButtonGroup();
        group.add(ascRadioButton);
        group.add(descRadioButton);
        radioPanel.add(ascRadioButton);
        radioPanel.add(descRadioButton);
        
        JPanel checkPanel = new JPanel(new GridLayout(3, 1));
        ignoreCaseCheckBox = new JCheckBox("");
        ignoreAccentsCheckBox = new JCheckBox("");
        removeDuplicatesCheckBox = new JCheckBox("");
        checkPanel.add(ignoreCaseCheckBox);
        checkPanel.add(ignoreAccentsCheckBox);
        checkPanel.add(removeDuplicatesCheckBox);

        optionsPanel.add(radioPanel, BorderLayout.NORTH);
        optionsPanel.add(checkPanel, BorderLayout.CENTER);
        
        centerContainer.add(optionsPanel);
        centerContainer.add(Box.createVerticalStrut(10));

        sortButton = new JButton("");
        sortButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        sortButton.setBackground(new Color(60, 120, 216));
        sortButton.setForeground(Color.DARK_GRAY);
        sortButton.setFocusPainted(false);
        sortButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sortButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSort();
            }
        });
        centerContainer.add(sortButton);
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // --- SECTION SUD ---
        JPanel southContainer = new JPanel(new BorderLayout(0, 10));
        
        resultSectionPanel = new JPanel(new BorderLayout(0, 5));
        resultSectionPanel.setBorder(new TitledBorder(""));

        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        resultSectionPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel resultBtnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        copyResultButton = new JButton();
        copyResultButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { copyToClipboard(); }});
        saveFileButton = new JButton();
        saveFileButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { saveFile(); }});
        
        resultBtnBar.add(copyResultButton);
        resultBtnBar.add(saveFileButton);
        resultSectionPanel.add(resultBtnBar, BorderLayout.SOUTH);
        
        southContainer.add(resultSectionPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Init...");
        statusLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC));

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        langPanel.add(new JLabel("Lang:"));
        languageComboBox = new JComboBox<String>(new String[]{"Français", "English"});
        languageComboBoxListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(languageComboBox.getSelectedItem().equals("Français")) currentLocale = Locale.FRENCH;
                else currentLocale = Locale.ENGLISH;
                updateUIStrings();
            }
        };
        languageComboBox.addActionListener(languageComboBoxListener);
        langPanel.add(languageComboBox);

        statusBar.add(statusLabel, BorderLayout.CENTER);
        statusBar.add(langPanel, BorderLayout.EAST);
        
        southContainer.add(statusBar, BorderLayout.SOUTH);
        
        mainPanel.add(southContainer, BorderLayout.SOUTH);
        
        frame.pack();
    }

    private void updateUIStrings() {
        String lang = currentLocale.getLanguage();
        if (!translations.containsKey(lang)) lang = "en";
        Map<String, String> txt = translations.get(lang);

        frame.setTitle(txt.get(KEY_APP_TITLE));
        ((TitledBorder) inputSectionPanel.getBorder()).setTitle(txt.get(KEY_INPUT_SECTION));
        inputArea.setToolTipText(txt.get(KEY_INPUT_TOOLTIP));
        clearInputButton.setText(txt.get(KEY_BTN_CLEAR));
        loadFileButton.setText(txt.get(KEY_BTN_LOAD));
        
        ((TitledBorder) optionsPanel.getBorder()).setTitle(txt.get(KEY_OPTIONS_SECTION));
        ascRadioButton.setText(txt.get(KEY_OPT_ASC));
        descRadioButton.setText(txt.get(KEY_OPT_DESC));
        ignoreCaseCheckBox.setText(txt.get(KEY_OPT_CASE));
        ignoreAccentsCheckBox.setText(txt.get(KEY_OPT_ACCENTS));
        removeDuplicatesCheckBox.setText(txt.get(KEY_OPT_DEDUP));
        sortButton.setText(txt.get(KEY_BTN_SORT));
        
        ((TitledBorder) resultSectionPanel.getBorder()).setTitle(txt.get(KEY_RESULT_SECTION));
        copyResultButton.setText(txt.get(KEY_BTN_COPY));
        saveFileButton.setText(txt.get(KEY_BTN_SAVE));
        
        statusLabel.setText(txt.get(KEY_STATUS_READY));
        
        frame.repaint();
    }

    private void performSort() {
        final String text = inputArea.getText();
        if (text == null || text.trim().isEmpty()) return;

        // Appel de la méthode corrigée
        setComponentsEnabled(false);
        
        statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_SORTING));
        statusLabel.setForeground(Color.BLUE);

        final boolean isAsc = ascRadioButton.isSelected();
        final boolean ignoreCase = ignoreCaseCheckBox.isSelected();
        final boolean ignoreAccents = ignoreAccentsCheckBox.isSelected();
        final boolean removeDupes = removeDuplicatesCheckBox.isSelected();
        final Locale sortLocale = currentLocale;

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            private int count = 0;

            @Override
            protected String doInBackground() throws Exception {
                String[] rawWords = text.split(WORD_SEPARATORS_REGEX);
                List<String> words = new ArrayList<String>();
                for (String w : rawWords) if (!w.trim().isEmpty()) words.add(w.trim());

                if (removeDupes) {
                    Map<String, String> uniqueMap = new LinkedHashMap<String, String>();
                    for (String w : words) {
                        String key = w;
                        if (ignoreCase) key = key.toLowerCase(sortLocale);
                        if (ignoreAccents) key = removeAccents(key);
                        if (!uniqueMap.containsKey(key)) uniqueMap.put(key, w);
                    }
                    words = new ArrayList<String>(uniqueMap.values());
                }

                final Collator collator = Collator.getInstance(sortLocale);
                if (ignoreCase && !ignoreAccents) collator.setStrength(Collator.SECONDARY);
                else if (ignoreCase && ignoreAccents) collator.setStrength(Collator.PRIMARY);
                else collator.setStrength(Collator.TERTIARY);

                Comparator<String> comp = new Comparator<String>() {
                    public int compare(String s1, String s2) {
                        String str1 = ignoreAccents ? removeAccents(s1) : s1;
                        String str2 = ignoreAccents ? removeAccents(s2) : s2;
                        return collator.compare(str1, str2);
                    }
                };

                Collections.sort(words, comp);
                if (!isAsc) Collections.reverse(words);

                StringBuilder sb = new StringBuilder();
                for (String w : words) sb.append(w).append("\n");
                count = words.size();
                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    resultArea.setText(get());
                    resultArea.setCaretPosition(0);
                    String msg = String.format(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_DONE), count);
                    statusLabel.setText(msg);
                    statusLabel.setForeground(new Color(0, 100, 0));
                } catch (Exception e) {
                    statusLabel.setText(translations.get(currentLocale.getLanguage()).get(KEY_STATUS_ERROR) + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    e.printStackTrace();
                } finally {
                    // Appel de la méthode corrigée pour réactiver
                    setComponentsEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private String removeAccents(String text) {
        if (text == null) return null;
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return DIACRITICS_PATTERN.matcher(normalized).replaceAll("");
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                reader.close();
                inputArea.setText(sb.toString());
                statusLabel.setText("Fichier chargé : " + chooser.getSelectedFile().getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lecture fichier: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        if (resultArea.getText().isEmpty()) return;
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                if (!f.getName().contains(".")) f = new File(f.getAbsolutePath() + ".txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                writer.write(resultArea.getText());
                writer.close();
                statusLabel.setText("Fichier sauvegardé : " + f.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Erreur écriture fichier: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void copyToClipboard() {
        if (resultArea.getText().isEmpty()) return;
        StringSelection selection = new StringSelection(resultArea.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        statusLabel.setText("Résultat copié dans le presse-papiers !");
    }

    // --- CORRECTION MAJEURE ICI ---
    // Au lieu de parcourir les composants dynamiquement (source du bug),
    // on active/désactive les composants connus explicitement.
    private void setComponentsEnabled(boolean enabled) {
        // Champs et boutons principaux
        inputArea.setEnabled(enabled);
        sortButton.setEnabled(enabled);
        loadFileButton.setEnabled(enabled);
        clearInputButton.setEnabled(enabled);
        copyResultButton.setEnabled(enabled);
        saveFileButton.setEnabled(enabled);
        
        // Options
        ascRadioButton.setEnabled(enabled);
        descRadioButton.setEnabled(enabled);
        ignoreCaseCheckBox.setEnabled(enabled);
        ignoreAccentsCheckBox.setEnabled(enabled);
        removeDuplicatesCheckBox.setEnabled(enabled);
        
        // Langue
        languageComboBox.setEnabled(enabled);
    }
}