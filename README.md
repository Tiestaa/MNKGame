### Descrizione Progetto
Il progetto consiste nel creare un algoritmo in java che, in modo intelligente, riesca a giocare ad un MNKgame, generalizzazione del celebre TicTacToe.
NisshokuPlayer è la classe principale che contiene la funzione per la scelta (public MNKCell selectcell()). Nisshoku player si basa sull’algoritmo studiato a lezione dell “’Iterative deepening”, in aggiunta abbiamo implementato: una classe per la valutazione delle situazioni di gioco ([Heuristic](./NisshokuPlayer/Heuristic.java)), una classe per la scelta delle celle durante iterative deepening e alphabeta pruning ([HashMapNFC](./NisshokuPlayer/HashMapNFC.java)) ed infine una classe per gestire le configurazioni che abbiamo già valutato nel corso dell’algoritmo ([TranspositionTable](./NisshokuPlayer/TranspositionTable.java)).  
Per maggiori informazioni, [clicca qui](./Relazione/Relazione_MNKgame_Testa_Sami%20.pdf).  
Valutazione: 29/30

### Compilazione e Running

#### Compilazione

- Command-line compile.  In the root directory run:
	```javac  mnkgame/*.java NisshokuPlayer/*.java```

#### Running 
you can run the project in 2 different ways:
##### MNKGame application:

- Human vs Computer.  In the root directory run:

	``` java mnkgame.MNKGame [M][N][K] mnkgame.RandomPlayer ```


- Computer vs Computer. In the root directory run:
	```java mnkgame.MNKGame [M][N][K] mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer ```

##### MNKPlayerTester application:

- Output score only:
	```java mnkgame.MNKPlayerTester [M][N][K] mnkgame.[Player] NisshokuPlayer.NisshokuPlayer```

- Verbose output

	```java mnkgame.MNKPlayerTester [M][N][K] mnkgame.[Player] NisshokuPlayer.NisshokuPlayer -v```

- Verbose output and customized timeout (1 sec) and number of game repetitions (10 rounds)
	```java mnkgame.MNKPlayerTester [M][N][K] mnkgame.[Player] NisshokuPlayer.NisshokuPlayer -v -t 1 -r 10s```

#### Nota

Implementazione del gioco e dell'interfaccia è a cura del professore. Riporto la licenza:
```
	Copyright (C) 2021 Pietro Di Lena
	
	This file is part of the MNKGame v2.0 software developed for the
	students of the course "Algoritmi e Strutture di Dati" first 
	cycle degree/bachelor in Computer Science, University of Bologna
	A.Y. 2020-2021.

	MNKGame is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This  is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this file.  If not, see <https://www.gnu.org/licenses/>.
```
