# AI Letter Game

An artificial intelligence game, where agents need to negotiate and trade letters to complete their goal, which is to gather all letters for their goal word.

## Authors
All work in the original repository is commited by [Nuno Polónia](https://github.com/nunopolonia) or [Tomé Duarte](https://github.com/tomeduarte). You can reach us through our github accounts.

Work done in this fork is by [Tomé Duarte](https://github.com/tomeduarte).

## Setup

The setup here described expects you will be using Eclipse to build and run the project.

Steps:

1. clone this repository outside your eclipse workspace path (we'll assume it's called `repo`);
2. create standard a new java project in eclipse; do not import the files on wizard.
3. import `repo/ai-letter-game` inside your project's `src/` directory; when importing, select advanced and use the link option;
4. go to the project's libraries and add `repo/lib/jade.jar` and `repo/lib/miglayout.jar` as external JARs in the project libraries;

To run, start `repo/ai-letter-game/AILetterGame.java` as a regular Java Application.

## Usage

When you start the application you'll see a window which enables you to:

- add new players
- remove players
- start the game

Configure the desired number of players and start the game. You can stop it at anytime by clicking the stop game button.
