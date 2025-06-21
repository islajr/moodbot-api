# MoodBot API

This repository contains the source code for the API of MoodBot - An emotional support and logging chat app.

## Initial Project Structure

Contained in the repository's source folder at this point in time are three folders, namely:
- backend
- ai
- shared-docs

The 'backend' folder is for all things backend and backend architecture.

The 'ai' folder is for the ai model and everything concerning LLMs and such.

The 'shared-docs' folder is there to house the postman collections and all other forms of documentation that defines how interaction within this whole project should go.
This includes endpoints exposed for specific purposes, and guidelines for frontend integration.

I have taken the liberty of adding some stock files in the directories. This is mainly to facilitate the pushing of the directories to this remote URL, as git does not push empty directories.
These files may be removed at the whim of the developer.

This README file will be updated as required during the course of development.

## Collaboration rules and Version Control

Sequel to the above, both the backend and AI developer should create branches, separate from the master branch, to add new features.
These features may then be merged with the main branch after some sort of integration testing.

This is to ensure that the main line of production is not riddled with error-filled code and also to make sure that damage control is in place in case of any unfortunate bugs or failed integrations.

