````markdown
# 📚 Trieur de Mots Alpha : Un Outil de Tri de Texte Avancé

Bienvenue dans le dépôt du "Trieur de Mots Alpha", une application Swing robuste et intuitive, conçue pour organiser vos listes de mots avec une précision alphabétique inégalée. Développée avec une attention méticuleuse à la qualité du code, à la performance et à l'expérience utilisateur, cette solution se positionne comme un outil indispensable pour la gestion de données textuelles.

Welcome to the "Alpha Word Sorter" repository, a robust and intuitive Swing application designed to organize your word lists with unparalleled alphabetical precision. Developed with meticulous attention to code quality, performance, and user experience, this solution stands as an indispensable tool for text data management.

---

## ✨ Fonctionnalités Clés / ✨ Key Features

* **Tri Alphabétique Polyvalent** : Triez des listes de mots en ordre ascendant (A-Z) ou descendant (Z-A).
    * *Versatile Alphabetical Sorting* : Sort word lists in ascending (A-Z) or descending (Z-A) order.
* **Options de Tri Intelligentes** :
    * 🔍 Ignorer la casse (ex: "Mot" et "mot" sont traités de manière identique).
    * 🌍 Ignorer les accents et les signes diacritiques (ex: "été" et "ete" sont considérés comme équivalents).
    * 🗑️ Suppression des doublons pour des listes épurées.
    * *Intelligent Sorting Options* :
        * 🔍 Ignore Case (e.g., "Word" and "word" are treated identically).
        * 🌍 Ignore Accents and Diacritics (e.g., "été" and "ete" are considered equivalent).
        * 🗑️ Duplicate Removal for clean lists.
* **Internationalisation (I18n) Avancée** : Prise en charge de plusieurs langues pour l'interface utilisateur (Français 🇫🇷 et Anglais 🇬🇧) et tri sensible à la locale, assurant une conformité avec les règles linguistiques spécifiques.
    * *Advanced Internationalization (I18n)* : Supports multiple UI languages (French 🇫🇷 and English 🇬🇧) and locale-sensitive sorting, ensuring compliance with specific linguistic rules.
* **Opérations de Fichier Intégrées** :
    * 📂 Charger des mots depuis un fichier texte existant.
    * 💾 Sauvegarder les résultats triés dans un nouveau fichier texte.
    * *Integrated File Operations* :
        * 📂 Load words from an existing text file.
        * 💾 Save sorted results to a new text file.
* **Interface Utilisateur Réactive (UI Non-Bloquante)** : Les opérations de tri sont exécutées en arrière-plan grâce à `SwingWorker`, garantissant une fluidité de l'interface même avec de grandes quantités de données. Des indicateurs de statut clairs informent l'utilisateur en temps réel.
    * *Responsive User Interface (Non-Blocking UI)* : Sorting operations run in a background thread using `SwingWorker`, ensuring UI responsiveness even with large datasets. Clear status indicators provide real-time user feedback.
* **Presse-papiers Intégration** : Copiez facilement les résultats triés directement dans votre presse-papiers.
    * *Clipboard Integration* : Easily copy sorted results directly to your clipboard.

---

## 🛠 Technologies Utilisées / 🛠 Technologies Used

* **Java SE 6** : Le langage de programmation principal.
    * *Java SE 6* : The primary programming language.
* **Swing** : Le toolkit graphique pour l'interface utilisateur.
    * *Swing* : The graphical user interface toolkit.
* **`java.text.Collator`** : Pour un tri alphabétique précis et sensible à la locale.
    * *`java.text.Collator`* : For accurate and locale-sensitive alphabetical sorting.
* **`javax.swing.SwingWorker`** : Pour des opérations asynchrones et une UI fluide.
    * *`javax.swing.SwingWorker`* : For asynchronous operations and a fluid UI.

---

## 🚀 Démarrage Rapide / 🚀 Quick Start

Pour compiler et exécuter l'application sur votre système :

To compile and run the application on your system:

### Prérequis / Prerequisites

* **Java Development Kit (JDK) 6 ou plus récent.**
    * *Java Development Kit (JDK) 6 or newer.*

### Compilation / Compilation

Naviguez jusqu'au répertoire contenant le fichier `TrieurDeMotsAlpha.java` et exécutez la commande suivante dans votre terminal :

Navigate to the directory containing the `TrieurDeMotsAlpha.java` file and execute the following command in your terminal:

```bash
javac TrieurDeMotsAlpha.java
````

### Exécution / Execution

Après la compilation, vous pouvez lancer l'application avec :

After compilation, you can launch the application with:

```bash
java TrieurDeMotsAlpha
```

-----

## 💡 Guide d'Utilisation / 💡 Usage Guide

1.  **Saisir les Mots** : Dans la section "1. Saisir les mots", tapez ou collez votre liste de mots. Les mots peuvent être séparés par des espaces, des sauts de ligne, des virgules ou des points-virgules.
      * *Enter Words* : In the "1. Enter Words" section, type or paste your word list. Words can be separated by spaces, new lines, commas, or semicolons.
2.  **Charger un Fichier (Nouveau)** : Utilisez le bouton "Charger un fichier..." pour importer des mots directement depuis un fichier texte sur votre ordinateur.
      * *Load File (New)* : Use the "Load from File..." button to import words directly from a text file on your computer.
3.  **Sélectionner les Options de Tri** : Dans la section "2. Options de Tri", choisissez vos préférences :
      * Ordre (Ascendant/Descendant)
      * Ignorer la casse
      * Ignorer les accents
      * Supprimer les doublons
      * *Select Sort Options* : In the "2. Sort Options" section, choose your preferences:
          * Order (Ascending/Descending)
          * Ignore Case
          * Ignore Accents
          * Remove Duplicates
4.  **Changer la Langue (Nouveau)** : Utilisez la liste déroulante "Langue:" pour basculer l'interface utilisateur entre le Français et l'Anglais, et ajuster les règles de tri sensibles à la locale.
      * *Change Language (New)* : Use the "Language:" dropdown to switch the user interface between French and English, and adjust locale-sensitive sorting rules.
5.  **Lancer le Tri** : Cliquez sur le bouton "TRIER" pour afficher les mots organisés dans la zone de résultat. L'interface restera réactive même pour les grandes listes.
      * *Start Sorting* : Click the "SORT" button to display the organized words in the result area. The interface will remain responsive even for large lists.
6.  **Manipuler le Résultat** :
      * "Copier Résultat" : Transfère la liste triée vers votre presse-papiers.
      * "Effacer Résultat" : Vide la zone de résultat.
      * "Sauvegarder le résultat..." (Nouveau) : Exporte la liste triée vers un fichier texte de votre choix.
      * *Handle Result* :
          * "Copy Result" : Transfers the sorted list to your clipboard.
          * "Clear Result" : Empties the result area.
          * "Save Result As..." (New) : Exports the sorted list to a text file of your choice.
7.  **Barre de Statut** : Le bas de la fenêtre affiche des messages d'état en temps réel sur l'opération en cours.
      * *Status Bar* : The bottom of the window displays real-time status messages about the current operation.

-----

## 🌟 Qualité du Code et Maintenabilité / 🌟 Code Quality and Maintainability

Ce projet a été développé avec des principes d'ingénierie logicielle solides :

This project has been developed with strong software engineering principles:

  * **Code Propre et Lisible** : Suivi rigoureux des conventions de codage Java, utilisation de noms significatifs pour les variables, méthodes et classes, et formatage cohérent.
      * *Clean and Readable Code* : Strict adherence to Java coding conventions, use of meaningful names for variables, methods, and classes, and consistent formatting.
  * **Robustesse et Gestion des Erreurs** : Implémentation de mécanismes de gestion d'erreurs robustes pour les opérations de fichier et les entrées utilisateur, avec des retours clairs à l'utilisateur et des logs de débogage internes.
      * *Robustness and Error Handling* : Implementation of robust error handling mechanisms for file operations and user inputs, with clear feedback to the user and internal debugging logs.
  * **Documentation Complète** : Javadoc détaillé en anglais et en français pour chaque composant et méthode, facilitant la compréhension et la maintenance du code par toute équipe de développement.
      * *Comprehensive Documentation* : Detailed Javadoc in both English and French for every component and method, facilitating code understanding and maintenance by any development team.
  * **Séparation des Préoccupations (Partielle)** : Bien que le projet soit contenu dans un seul fichier pour simplifier le déploiement Java 6, une attention particulière a été portée à la modularité logique des méthodes (UI vs. logique métier) pour une meilleure maintenabilité.
      * *Separation of Concerns (Partial)* : Although the project is contained within a single file for Java 6 deployment simplicity, particular attention has been paid to the logical modularity of methods (UI vs. business logic) for improved maintainability.

-----

## 🤝 Contribution / 🤝 Contributing

Les contributions sont les bienvenues \! Si vous souhaitez améliorer ce projet, n'hésitez pas à :

Contributions are welcome\! If you wish to improve this project, feel free to:

1.  Forker le dépôt.
      * Fork the repository.
2.  Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`).
      * Create a feature branch (`git checkout -b feature/AmazingAmazingFeature`).
3.  Committer vos changements (`git commit -m 'Add some AmazingFeature'`).
      * Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Pusher vers la branche (`git push origin feature/AmazingFeature`).
      * Push to the branch (`git push origin feature/AmazingFeature`).
5.  Ouvrir une Pull Request.
      * Open a Pull Request.

-----

## 👤 Auteur / 👤 Author

  * **Samyn-Antoy ABASSE** - Conçu et développé.
      * *Samyn-Antoy ABASSE* - Designed and Developed.

```
```
