# MailClean

App Android (Kotlin, Jetpack Compose) de **gestion IA multi-comptes de mails** : Gmail, Outlook, Yahoo.
Objectif : gérer la boîte mail de A à Z, augmenté par une IA locale sur l'appareil.

## Fonctionnalités prévues

- Multi-comptes (Gmail API, Microsoft Graph, Yahoo IMAP) — unité de base : le compte.
- Tri des mails dans des **catégories créées par l'IA** (clustering + nommage en français), dossier créé seulement à la validation.
- **Désinscription des listes de diffusion** (en-tête List-Unsubscribe, RFC 8058).
- Mode **suggestions à valider** + journal d'actions réversible pour les déplacements.
- Moteur IA local sur l'appareil (interface interchangeable, Ollama auto-hébergé possible plus tard).

## Statut : V0.1.0 (squelette)

- Modèles de données, interface MailProvider, adaptateurs Gmail/Graph (squelettes).
- Détecteur List-Unsubscribe + tests unitaires.
- Écran d'accueil Compose, icône adaptative, CI « Build APK » (APK renommé `MailClean-v<version>.apk`).

## Feuille de route

- V0.1 : connexion comptes (Gmail API + Graph), lecture synchro, écran de suggestions de tri.
- V0.2 : moteur IA sur l'appareil, clustering + nommage de catégories, tri par lots.
- V0.3 : désinscription, journal d'actions/annulation.
- V0.4 : Yahoo (IMAP), réponses brouillons/résumés, mode auto par catégorie validée.

## Conventions

Jetpack Compose, AGP 8.4 / Kotlin 2.0, minSdk 26, Gradle wrapper généré en CI (jamais commité), zéro dépendance réseau en dehors de `HttpJsonClient`.

## Prérequis développeur

- Projet Google Cloud : Gmail API activée, écran de consentement, client OAuth (redirect `mailclean://oauth/gmail`).
- Azure : appli avec scopes Graph `Mail.ReadWrite` + `offline_access` (redirect `mailclean://oauth/graph`).
