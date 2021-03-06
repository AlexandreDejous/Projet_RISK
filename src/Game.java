import edu.princeton.cs.introcs.StdDraw;
import java.util.*;


public class Game {
	
	Board Plateau = new Board();
	
	public void initialize(int x , int y) {
		StdDraw.setCanvasSize(x, y);
		StdDraw.setXscale(0, x);
		StdDraw.setYscale(0, y);
	}
	
	
	
	public void menu(int x, int y) {
		
		int tictac = 2000 ;  // en ms. tictac/2 = temps d'affichage de chaque image
		boolean Menu1 = true; //bouleen pour savoir quel image est affichee en ce moment
		StdDraw.picture(x/2, y/2, "Menu/Menu1.png");

		while (!StdDraw.isMousePressed()) {
			if (System.currentTimeMillis() % tictac < tictac/2 && !Menu1) {
				StdDraw.picture(x/2, y/2, "Menu/Menu1.png");
				Menu1 = true;
			}
			if(System.currentTimeMillis() % tictac > tictac/2 && Menu1){
				StdDraw.picture(x/2, y/2, "Menu/Menu2.png");
				Menu1 = false;
			}				
		}
		
		StdDraw.picture(x/2, y/2, "Menu/Menu3.png"); //Menu de selection
		int PlayerCount = 0;
		int ActivateAI = 0;
		
		
		while (!StdDraw.isKeyPressed(32)) {
			if (StdDraw.isMousePressed()) {	
				if (this.isBetween(228.0, 407.0, 366.0, 506.0)) {
					PlayerCount = 2;
				}
				if (this.isBetween(387.0, 394.0, 524.0, 535.0)) {
					PlayerCount = 3;
				}
				if (this.isBetween(536.0, 379.0, 678.0, 539.0)) {
					PlayerCount = 4;
				}
				if (this.isBetween(699.0, 359.0, 839.0, 539.0)) {
					PlayerCount = 5;
				}
				if (this.isBetween(868.0, 365.0, 1055.0, 537.0)) {
					PlayerCount = 6;
				}
				if (this.isBetween(265.0, 158.0, 543.0, 280.0)) {
					ActivateAI = 1;
				}
				if (this.isBetween(759.0, 158.0, 1038.0, 281.0)) {
					ActivateAI = 2;
				}
				
				
				//Affichage Dynamique
				if (PlayerCount ==0 || ActivateAI == 0) {					
					StdDraw.picture(x/2, y/2, "Menu/Menu3.png");
				}
				
				if (PlayerCount !=0 && ActivateAI != 0) {
					StdDraw.picture(x/2, y/2, "Menu/Menu4.png");
				}
				switch(PlayerCount) {
				case 2: this.drawRectangle(228.0, 407.0, 366.0, 506.0);
				break;
				case 3: this.drawRectangle(387.0, 394.0, 524.0, 535.0);
				break;
				case 4: this.drawRectangle(536.0, 379.0, 678.0, 539.0);
				break;
				case 5: this.drawRectangle(699.0, 359.0, 839.0, 539.0);
				break;
				case 6: this.drawRectangle(868.0, 365.0, 1055.0, 537.0);
				break;					
				}
				switch(ActivateAI) {
					case 1: this.drawRectangle(265.0, 158.0, 543.0, 280.0);
					break;
					case 2: this.drawRectangle(759.0, 158.0, 1038.0, 281.0);
					break;					
				}
				if (this.isBetween(487.0, 15.0, 805.0, 135.0 )) {
					if (PlayerCount !=0 && ActivateAI != 0) {
						StdDraw.pause(100);	// isMousePressed n'aime pas quand on lance une methode alors qu'il ne s'est pas encore reset
						this.launch(PlayerCount, ActivateAI);
					}
				}
				StdDraw.pause(100);
			}
		}												
	}
	
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	public void launch(int PlayerCount, int ActivateAI){
		
		this.Plateau.initialize(PlayerCount, ActivateAI);
		StdDraw.pause(150);
		//premier tour de renforcement commun
		for (int playingPlayer=1; playingPlayer <= 6; playingPlayer++) {
			if (this.Plateau.players[playingPlayer-1].alive) {
				this.Plateau.players[playingPlayer-1].reinforcements = this.calculateReinforcements(playingPlayer);
				this.listenPhase1(playingPlayer);
			}
		}			
		while (this.moreThan1Alive()){
			for (int playingPlayer=1; playingPlayer <= 6; playingPlayer++) {
				if (this.Plateau.players[playingPlayer-1].alive) {
					this.Plateau.players[playingPlayer-1].reinforcements = this.calculateReinforcements(playingPlayer);
					this.listenPhase1(playingPlayer);
					this.listenPhase2(playingPlayer);
					this.resetMovement(playingPlayer);
					this.moreThan1Alive();
					if (this.winner()!=0) {
						//PRINT WINNER
						StdDraw.picture(1361.0/2, 675.0/2, "Menu/WIN.png");
						this.victoryMessage(playingPlayer);
						StdDraw.show();
						break;
					}
					
					
					/*double X1=0, X2=0, Y1=0, Y2=0;
					int i = 0;
					
					while (!StdDraw.isKeyPressed(32)) {
						
						if (StdDraw.isMousePressed()) {	
							if(i == 0) {
								i=1;
								X1 = StdDraw.mouseX();
								Y1 = StdDraw.mouseY();
							}else {
								X2 = StdDraw.mouseX();
								Y2 = StdDraw.mouseY();
								System.out.println(X1 +", "+Y1 +", "+X2 +", "+Y2);
								i = 0;
							}
							StdDraw.pause(150);//Pause car isMousePressed reste true pendant qq ms (de trop !)
						}
						//R�cup�rateur de hitbox V2
						//nous avons utilis� cette fonction pour r�cup�rer des coordon�es de hitbox format�es dans la console
						//fonctionne en synergie avec isBetween
					}*/
				}													
			}
		}
	}
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	public boolean isBetween(double x1, double y1, double x2, double y2) {
		if (StdDraw.mouseX()>x1 && StdDraw.mouseX()<x2 && StdDraw.mouseY()>y1 && StdDraw.mouseY()<y2) {
			return true;
			
		}
		return false;
		//Dans cette fonction, rentrez les coordonn�es de votre hitbox.
		//Pour r�cup�rer les coordonn�es d'une hitbox facilement, utilisez le code qui convertit vos clicks en coord x et y dans la console
		//cliquez en bas � gauche puis en haut � droite de votre hitbox, puis utilisez dans l'ordre d'affichage dans la console les nombres en
		//argument de la fonction (4 nombres par hitbox si tout est bon)
	}
	
	public void drawRectangle(double x1, double y1, double x2, double y2) {
		StdDraw.rectangle((x1+x2)/2,(y1+y2)/2,(x2-x1)/2,(y2-y1)/2);
	}
	
	//v�rifie que il y ait plus d'un joueur en vie et met � jour le statut de chaque joueur (toujours en course ou hors-jeu)
	public boolean moreThan1Alive() {
		
		for (int i = 0; i<6; i++) {
			this.Plateau.players[i].alive=false;
			for (int j=0; j<42; j++) {
				if(this.Plateau.territories[j].player == i+1) {
					this.Plateau.players[i].alive=true;
				}
			}
		}
		int count = 0;
		for (int i = 0; i<6;i++) {
			if(this.Plateau.players[i].alive) {
				count += 1;
			}
		}
		if (count>1) {
			return true;
		}
		return false;
	}
	
	//imprime un message de victoire personnalis� en fonction de la couleur du joueur
	public void victoryMessage(int player){
		double x = 530.0;
		double y = 300.0;
		if(player == 1) {
			StdDraw.text(x, y, "Cyan wins !");
		}
		if(player == 2) {
			StdDraw.text(x, y, "Black wins !");
		}
		if(player == 3) {
			StdDraw.text(x, y, "White wins !");
		}
		if(player == 4) {
			StdDraw.text(x, y, "Grey wins !");
		}
		if(player == 5) {
			StdDraw.text(x, y, "Green wins !");
		}
		if(player == 6) {
			StdDraw.text(x, y, "Beige wins !");
		}
		
	}
	
	//retourne le gagnant du jeu, 0 sinon
	public int winner() {
		int count = 0;
		int player = 0;
		for (int i = 0; i <6; i++) {
			if(this.Plateau.players[i].alive == true) {
				player = i+1;
				count ++;
			}
		}
		if(count==1) {
			return player;
		}
		return 0;
	}
	
	//calcule les renforts de ce tour pour le joueur et reset le nombre de territoires r�cemment conquis
	public int calculateReinforcements(int player) {
		int T = 0;//Territoires controles
		int N = 0;//Nombre de territoires par regions controlees
		int M = 0;//Nombre de regiments gagnes en fonction des territoires captures
		int[] territoryPerRegion = {0,0,0,0,0,0};
		for (int i = 0; i<42; i++) {
			if(this.Plateau.territories[i].player == player) {
				T++;
				territoryPerRegion[this.Plateau.territories[i].region-1]++;
			}
		}
		if(territoryPerRegion[0]==9) {
			N = N + 9;
		}
		if(territoryPerRegion[1]==7) {
			N = N + 7;
		}
		if(territoryPerRegion[2]==12) {
			N = N + 12;
		}
		if(territoryPerRegion[3]==4) {
			N = N + 4;
		}
		if(territoryPerRegion[4]==6) {
			N = N + 6;
		}
		if(territoryPerRegion[5]==4) {
			N = N + 4;
		}
		for(int i = 0; i<this.Plateau.players[player-1].recentlyCaptured;i++) {
			M= M + (int )(Math.random()*2); //50% de chance de gagner un regiment
		}
		this.Plateau.players[player-1].recentlyCaptured = 0;
		return (T/3)+(N/2)+M; //retourne le nombre de renforts
		
	}
	
	//permet l'activation de la premi�re phase de jeu, et g�re les interactions homme machine
	public void listenPhase1(int playingPlayer) {
		
		this.Plateau.actualize(0, playingPlayer, 0);
		boolean stillOnPanel = true;
		boolean clickedInSea = true;
		while(Plateau.players[playingPlayer-1].reinforcements != 0) {
			if(StdDraw.isMousePressed()) {
				clickedInSea = true;
				for (int i = 0; i<42 ; i++) {
					if(this.isBetween(Plateau.territories[i].X-28, Plateau.territories[i].Y-28,Plateau.territories[i].X+28 , Plateau.territories[i].Y+28)) {
						if(Plateau.territories[i].player == playingPlayer) {
							this.Plateau.actualize(2, playingPlayer, i+1);
							while(stillOnPanel) {
								stillOnPanel = this.listenReinforcements(playingPlayer, i+1) ;
								this.Plateau.actualize(2, playingPlayer, i+1);
								//Si le joueur n'a plus de renforts a attribuer, on lui fait quitter le panel de force
								if(Plateau.players[playingPlayer-1].reinforcements == 0 && this.powerInHand(playingPlayer) == 0 ) {
									stillOnPanel = false;
								}
							}
							stillOnPanel = true;
							this.abortRHand(playingPlayer);
						}else {
							this.Plateau.actualize(1, playingPlayer, i+1);
							StdDraw.pause(150);
						}
						clickedInSea = false;						
					}
				}
				if (this.isBetween(1025.0, 0.0, 1364.0, 669.0)) {
					clickedInSea = false;
				}
				if(clickedInSea) {
					this.Plateau.actualize(0, playingPlayer, 0);
				}
			}
			this.clearHand(playingPlayer);
		}	
	}
	
	public void listenPhase2(int playingPlayer) {
		int orderFeedback = 100;
		while (orderFeedback!=101) {
			if (StdDraw.isMousePressed()) {
				if(this.isBetween(0.0,0.0,1025.0,670.0)) {
					for (int i = 0; i<42 ; i++) {
						if(this.isBetween(Plateau.territories[i].X-28, Plateau.territories[i].Y-28,Plateau.territories[i].X+28 , Plateau.territories[i].Y+28)) {
							if(Plateau.territories[i].player == playingPlayer) {
								orderFeedback = 100;
								while(orderFeedback == 100) {//tant que feedback est different de 100 (100 = recommencer), continuer de boucler
									this.Plateau.actualize(4, playingPlayer, i+1);
									orderFeedback = this.listenOrders(playingPlayer, i+1) ;
									this.Plateau.actualize(4, playingPlayer, i+1);
									if(orderFeedback <43 && orderFeedback >0) { // lancer un mouvement ou une attaque
										this.move(playingPlayer, i+1, orderFeedback);
										orderFeedback = 100;
										this.Plateau.actualize(4, playingPlayer, i+1);
									}
									if(orderFeedback == 0) {//a clique dans la mer, reset de la main du joueur
										this.abortHand(playingPlayer, i+1);
										this.Plateau.actualize(0, playingPlayer, 0);
									}
								} //fin du while
							}
							else {//si le territoire n'appartient pas au joueur
								this.Plateau.actualize(3, playingPlayer, i+1);//afficher des informations sur les troupes ennemies
								orderFeedback = 100;
								while(orderFeedback == 100) {
									orderFeedback = this.listenIntel(playingPlayer, i+1) ;
									if (orderFeedback == 0) {
										this.Plateau.actualize(0, playingPlayer, 0);
									}else {
										this.Plateau.actualize(3, playingPlayer, i+1);
									}
								}
							}
						}
					}	
				}
			}
		}
	}
	
	
	
	//permet d'activer des hitbox sp�cifiques pour effectuer des op�rations sur un territoire en particulier
	//activation des hitbow pour le panel des ordres de mouvement et d'attaque
	public int listenOrders( int player , int territory) {
		StdDraw.pause(150);
		while(true) {
			if (StdDraw.isMousePressed()) {
				
				if(this.isBetween(1063.0, 311.0, 1083.0, 335.0)) {
					this.substractHand(player, 3, 2, territory);
				}
				if(this.isBetween(1090.0, 278.0, 1118.0, 311.0)) {
					this.addHand(player, 3, 2, territory);
				}
				if(this.isBetween(1161.0, 314.0, 1189.0, 334.0)) {
					this.substractHand(player, 2, 1, territory);
				}
				if(this.isBetween(1191.0, 280.0, 1217.0, 308.0)) {
					this.addHand(player, 2, 1, territory);
				}
				if(this.isBetween(1260.0, 308.0, 1286.0, 333.0)) {
					this.substractHand(player, 1, 0, territory);
				}
				if(this.isBetween(1292.0, 278.0, 1317.0, 311.0)) {
					this.addHand(player, 1, 0, territory);
				}
				
				//1 PT MVT ^           (op�ration sur les unit�s ayant encore 1 pt de mouvement restant)
				
				if(this.isBetween(1063.0, 237.0, 1091.0, 263.0)) {
					this.substractHand(player, 3, 1, territory);
				}
				if(this.isBetween(1090.0, 209.0, 1117.0, 238.0)) {
					this.addHand(player, 3, 1, territory);
				}
				if(this.isBetween(1161.0, 238.0, 1185.0, 262.0)) {
					this.substractHand(player, 2, 0, territory);
				}
				if(this.isBetween(1188.0, 208.0, 1218.0, 237.0)) {
					this.addHand(player, 2, 0, territory);
				}
				
				//2 PT MVT ^
				
				if(this.isBetween(1062.0, 166.0, 1089.0, 192.0)) {
					this.substractHand(player, 3, 0, territory);
				}
				if(this.isBetween(1089.0, 135.0, 1118.0, 167.0)) {
					this.addHand(player, 3, 0, territory);
				}
				
				//3 PT MVT ^
				
				if(this.isBetween(1225.0, 22.0, 1329.0, 78.0)) {
					return 101; //Le joueur choisit de terminer son tour, indique � la supermethode de stopper l'ecoute de la phase 2
				}
				
				//PASSE LE TOUR ^
				
				for (int i = 0; i<42 ; i++) {
					if(this.isBetween(Plateau.territories[i].X-28, Plateau.territories[i].Y-28,Plateau.territories[i].X+28 , Plateau.territories[i].Y+28)) {
						if(i+1 == territory) {
							return 100; //le joueur a juste reclicke sur son territoire, on garde le panel intact
						}else {
							
						}
						return i+1; // retourne l'id du territoire recepteur d'une attaque ou d'un mouvement de troupes
					}
				}								
				if(this.isBetween(1025.0, 0.0, 1364.0, 669.0)) {
					return 100; //est toujours sur panel ou a clique sur mm territoire, indique � la supermethode de l'appeler a nouveau
				}
				else {
					return 0;//indique a la supermethode de cesser le while
				}
			}																
		}
	}
	
	//permet en particulier d'�couter le bouton de passage de tour qui se trouve sur le panel d'infos sur les ennemis
	public int listenIntel( int player , int territory) {
		StdDraw.pause(150);
		while(true) {
			if (StdDraw.isMousePressed()) {
				
				
				if(this.isBetween(1225.0, 22.0, 1329.0, 78.0)) {
					
					return 101; //Le joueur choisit de terminer son tour, indique � la supermethode de stopper l'ecoute de la phase 2
				}
				
				//PASSE ^
				
				if(this.isBetween(1025.0, 0.0, 1364.0, 669.0)) {
					return 100; //est toujours sur panel ou a clique sur mm territoire, indique � la supermethode de l'appeler a nouveau
				}
				else {
					return 0;//indique a la supermethode de cesser le while
				}
			}			
		}
	}
	
	//permet d'ajouter des unit�s facilement dans la main d'un joueur sans avoir � se soucier de son nb de troupes totales
	public void addHand(int player, int unit, int mvt, int territory) {
		//mvt = points de mouvements deja consommes par l'unite
		//territory = territoire de d�part de l'unite
		//unit = type d'unite, 1 = canonnier 2 = musketman 3 = horseman
		if(this.Plateau.territories[territory-1].troopsInTerritory()>1) {
			if (unit == 1) {
				if(this.Plateau.territories[territory-1].canonnier[mvt]>0) {
					this.Plateau.territories[territory-1].canonnier[mvt]--;
					this.Plateau.players[player-1].canonnier++;
				}
			}
			if (unit == 2) {
				if(this.Plateau.territories[territory-1].musketman[mvt]>0) {
					this.Plateau.territories[territory-1].musketman[mvt]--;
					this.Plateau.players[player-1].musketman[mvt]++;
				}
			}
			if (unit == 3) {
				if(this.Plateau.territories[territory-1].horseman[mvt]>0) {
					this.Plateau.territories[territory-1].horseman[mvt]--;
					this.Plateau.players[player-1].horseman[mvt]++;
				}
			}
		}
		
	}
	public void substractHand(int player, int unit, int mvt, int territory) {
		//mvt = points de mouvements deja consommes par l'unite
		//territory = territoire de d�part de l'unite
		//unit = type d'unite, 1 = canonnier 2 = musketman 3 = horseman
		if (unit == 1) {
			if(this.Plateau.players[player-1].canonnier>0) {
				this.Plateau.players[player-1].canonnier--;
				this.Plateau.territories[territory-1].canonnier[mvt]++;
			}
		}
		if (unit == 2) {
			if(this.Plateau.players[player-1].musketman[mvt]>0) {
				this.Plateau.players[player-1].musketman[mvt]--;
				this.Plateau.territories[territory-1].musketman[mvt]++;
			}
		}
		if (unit == 3) {
			if(this.Plateau.players[player-1].horseman[mvt]>0) {
				this.Plateau.players[player-1].horseman[mvt]--;
				this.Plateau.territories[territory-1].horseman[mvt]++;
			}
		}
		
	}
	public void clearHand(int player) {
		this.Plateau.players[player-1].canonnier = 0;
		this.Plateau.players[player-1].musketman[0] = 0;
		this.Plateau.players[player-1].musketman[1] = 0;
		this.Plateau.players[player-1].horseman[0] = 0;
		this.Plateau.players[player-1].horseman[1] = 0;
		this.Plateau.players[player-1].horseman[2] = 0;
	}
	
	//reset la main du joueur et r�attribue les unit�s en main l� ou elles ont �t� prises
	public void abortHand(int player, int territory) {
		this.Plateau.territories[territory-1].canonnier[0] += this.Plateau.players[player-1].canonnier;
		this.Plateau.territories[territory-1].musketman[0] += this.Plateau.players[player-1].musketman[0];
		this.Plateau.territories[territory-1].musketman[1] += this.Plateau.players[player-1].musketman[1];
		this.Plateau.territories[territory-1].horseman[0] += this.Plateau.players[player-1].horseman[0];
		this.Plateau.territories[territory-1].horseman[1] += this.Plateau.players[player-1].horseman[1];
		this.Plateau.territories[territory-1].horseman[2] += this.Plateau.players[player-1].horseman[2];
		this.clearHand(player);
	}
	
	
	
	//permet d'activer les hitbox pour un territoire en particulier vis � vis de l'attribution des renforts
	public boolean listenReinforcements( int player , int territory) {
		StdDraw.pause(150);
		while(true) {
			if (StdDraw.isMousePressed()) {
				if(this.isBetween(1064.0, 406.0, 1119.0, 461.0)) {
					this.addInRHand(player, 3);
				}
				if(this.isBetween(1162.0, 404.0, 1218.0, 460.0)) {
					this.addInRHand(player, 2);
				}			
				if(this.isBetween(1260.0, 406.0, 1317.0, 462.0)) {
					this.addInRHand(player, 1);
				}	
				if(this.isBetween(1062.0, 322.0, 1117.0, 376.0)) {
					this.addInRHand(player, 3);
				}
				if(this.isBetween(1165.0, 324.0, 1216.0, 377.0)) {
					this.addInRHand(player, 2);
				}
				if(this.isBetween(1262.0, 324.0, 1316.0, 377.0)) {
					this.addInRHand(player, 1);
				}
				if(this.isBetween(1134.0, 209.0, 1245.0, 277.0)) {
					this.abortRHand(player);
				}
				if(this.isBetween(1105.0, 54.0, 1272.0, 107.0)) {
					this.confirmR(player, territory);
				}
				if(this.isBetween(1025.0, 0.0, 1364.0, 669.0) || this.isBetween(Plateau.territories[territory-1].X-28, Plateau.territories[territory-1].Y-28,Plateau.territories[territory-1].X+28 , Plateau.territories[territory-1].Y+28)) {
					return true; //est toujours sur panel ou a clique sur mm territoire, indique � la supermethode de l'appeler a nouveau
				}
				else {
					return false;
				}
			}																
		}
	}
	
	//permet de reset les points de mouvements de chaque unit� des territoires d'un joueur
	//utilis� � la fin du tour d'un joueur (permet aussi une synergie avec la fonction bataille, qui n'op�re que sur des d�fenseurs
	//dont les pts de mouvements sont a 0
	public void resetMovement(int player) {
		for (int i =0; i<42; i++) {
			if (this.Plateau.territories[i].player == player) {
				this.Plateau.territories[i].musketman[0]=this.Plateau.territories[i].getMusketman();
				this.Plateau.territories[i].musketman[1]=0;
				this.Plateau.territories[i].musketman[2]=0;
				this.Plateau.territories[i].horseman[0]=this.Plateau.territories[i].getHorseman();
				this.Plateau.territories[i].horseman[1]=0;
				this.Plateau.territories[i].horseman[2]=0;
				this.Plateau.territories[i].horseman[3]=0;
				this.Plateau.territories[i].canonnier[0]=this.Plateau.territories[i].getCanonnier();
				this.Plateau.territories[i].canonnier[1]=0;
			}
		}
	}
	
	
	
	
	
		//ADD IN REINFORCEMENTS HAND
		//methode qui prend en entree un joueur, l'unit� desiree en vue de potentiel attribution de renforts (musketman = 2, canonnier = 1 et horseman = 3)
		//et qui change ou non la main de renforts du joueur (sil il possede assez de renforts)
		public void addInRHand(int player, int unit) {
			
			
			if(this.Plateau.players[player-1].reinforcements >= 1 && unit == 2) {
				this.Plateau.players[player-1].reinforcements --;
				this.Plateau.players[player-1].musketman[0]+=1;
			}
			if(this.Plateau.players[player-1].reinforcements >= 3 && unit == 3) {
				this.Plateau.players[player-1].reinforcements = this.Plateau.players[player-1].reinforcements - 3;
				this.Plateau.players[player-1].horseman[0]+=1;
			}
			if(this.Plateau.players[player-1].reinforcements >= 7 && unit == 1) {
				this.Plateau.players[player-1].reinforcements = this.Plateau.players[player-1].reinforcements - 7;
				this.Plateau.players[player-1].canonnier+=1;
			}
		}
		//Utilisee lorque l'user ne confirme pas les renforts ou clique autre part de la map
		//Met a zero la main du joueur et rembourse les renforts
		public void abortRHand(int player) {
			int powerInHand = this.powerInHand(player);
			this.Plateau.players[player-1].reinforcements += powerInHand;
			this.Plateau.players[player-1].musketman[0] = 0;
			this.Plateau.players[player-1].horseman[0] = 0;
			this.Plateau.players[player-1].canonnier = 0;
		}
		
		public int powerInHand(int player) {
			return this.Plateau.players[player-1].musketman[0]+(3*this.Plateau.players[player-1].horseman[0])+(7*this.Plateau.players[player-1].canonnier);
		}
		//CONFIRM REINFORCEMENTS
		//Assigne les troupes � leur territoire et vide la main du joueur
		//Prend en entree le joueur et l'ID territoire
		public void confirmR (int player, int territory) {
			this.Plateau.territories[territory-1].musketman[0]+= this.Plateau.players[player-1].musketman[0];
			this.Plateau.territories[territory-1].horseman[0]+= this.Plateau.players[player-1].horseman[0];
			this.Plateau.territories[territory-1].canonnier[0]+= this.Plateau.players[player-1].canonnier;
			this.Plateau.players[player-1].musketman[0] = 0;
			this.Plateau.players[player-1].horseman[0] = 0;
			this.Plateau.players[player-1].canonnier = 0;
		}
	
		public int move(int player, int territory, int target ) {
			

			boolean isAdjacent = false;
			for (int i = 0; i <6; i++) {
				if (this.Plateau.territories[territory-1].adjacency[i]==target) {
					isAdjacent = true;
				}
			}
			if(isAdjacent) {//si les deux territoires sont adjacents				
				if(this.Plateau.territories[territory-1].player==this.Plateau.territories[target-1].player){//deplacement vers allie
					    this.Plateau.territories[target-1].horseman[1]+=this.Plateau.players[player-1].horseman[0];
					    this.Plateau.territories[target-1].horseman[2]+=this.Plateau.players[player-1].horseman[1];
					    this.Plateau.territories[target-1].horseman[3]+=this.Plateau.players[player-1].horseman[2];
					    this.Plateau.territories[target-1].musketman[1]+=this.Plateau.players[player-1].musketman[0];
					    this.Plateau.territories[target-1].musketman[2]+=this.Plateau.players[player-1].musketman[1];
					    this.Plateau.territories[target-1].canonnier[1]+=this.Plateau.players[player-1].canonnier;
					    this.clearHand(player);
					    
					    return 0;
				}
				else if(this.Plateau.players[player-1].troopsInHand() <= 3 && this.Plateau.players[player-1].troopsInHand() != 0) {
					int[][]ATKmatrix = {{0,0,0},{0,0,0},{0,0,0}}; //premiere colonne: pts d'ATK, 2nd : priorit� defensive, 3e : pts de mvt deja utilises
					int[][]DEFmatrix = {{0,0},{0,0}}; //ATK et priorite
					
					//parcourt la main du joueur et remplit une matrice facilement lisible par le programme (ATKmatrix)					   										
					//(0)(0)(0) <- pts d'atk tir�s au sort
					//(0)(0)(0) <- type de l'unit� (d�sign�e par son nombre de pts de mouvement max)
					//(0)(0)(0) <- points de mouvements deja utilis�s par l'unite
					// ^          Premi�re unit� de la main du joueur (qui deviendra apr�s tri l'unit� la plus puissante ou prioritaire)
					//    ^       Snd unit� 
					//       ^    3e unit�
					//on formatte les donn�es puis on les trie pour les traiter facilement, on utilise le m�me principe avec la matrice de defense
					
					
					//on cr�e la matrice d'atk
					for (int i = 0; i < 3; i++) {
						for (int mus = 0; mus <2; mus++) {
							if(ATKmatrix[i][0]==0) {
								if(this.Plateau.players[player-1].musketman[mus]!=0) {
									this.Plateau.players[player-1].musketman[mus]-=1;
									ATKmatrix[i][0] = (int )(Math.random() * 6 + 1);
									ATKmatrix[i][1] = 2;
									ATKmatrix[i][2] = mus;
								}
							}
						}
						for (int hor = 0; hor <3; hor++) {
							if(ATKmatrix[i][0]==0) {
								if(this.Plateau.players[player-1].horseman[hor]!=0) {
									this.Plateau.players[player-1].horseman[hor]--;
									ATKmatrix[i][0] = (int )(Math.random() * 7 + 2);
									ATKmatrix[i][1] = 3;
									ATKmatrix[i][2] = hor;
								}
							}
						}
						if(ATKmatrix[i][0]==0) {
							if(this.Plateau.players[player-1].canonnier!=0) {
								this.Plateau.players[player-1].canonnier--;
								ATKmatrix[i][0] = (int)(Math.random() * 9 + 4);
								ATKmatrix[i][1] = 1;
								ATKmatrix[i][2] = 0;
							}
						}
						
						
					}
					
					//defense matrix
					for (int i = 0; i < 2 ; i++) {
						if(this.Plateau.territories[target-1].musketman[0] > 0) {
							if(DEFmatrix[i][0] == 0) {
								this.Plateau.territories[target-1].musketman[0]--;
								DEFmatrix[i][0]= (int )(Math.random() * 6 + 1);
								DEFmatrix[i][1]= 2;
							}
						}
						if(this.Plateau.territories[target-1].canonnier[0] > 0) {
							if(DEFmatrix[i][0] == 0) {
								this.Plateau.territories[target-1].canonnier[0]--;
								DEFmatrix[i][0]= (int )(Math.random() * 7 + 4);
								DEFmatrix[i][1]= 1;
							}
						}
						if(this.Plateau.territories[target-1].horseman[0] > 0) {
							if(DEFmatrix[i][0] == 0) {
								this.Plateau.territories[target-1].horseman[0]--;
								DEFmatrix[i][0]= (int )(Math.random() * 9 + 2);
								DEFmatrix[i][1]= 3;
							}
						}
					}
					
					//tri de la matrice d'ATK selon les atk plus puissantes ou priorit�s
					int memory1;
					int memory2;
					int memory3;
					for (int i = 0; i<2; i++) {
						for (int j =0; j<2; j++) {
							if(ATKmatrix[j][0]<ATKmatrix[j+1][0] || (ATKmatrix[j][0]==ATKmatrix[j+1][0] && ATKmatrix[j][1]<ATKmatrix[j+1][1])) { //swap
								memory1 = ATKmatrix[j][0];
								memory2 = ATKmatrix[j][1];
								memory3 = ATKmatrix[j][2];
								ATKmatrix[j][0] = ATKmatrix[j+1][0];
								ATKmatrix[j][1] = ATKmatrix[j+1][1];
								ATKmatrix[j][2] = ATKmatrix[j+1][2];
								ATKmatrix[j+1][0] = memory1;
								ATKmatrix[j+1][1] = memory2;
								ATKmatrix[j+1][2] = memory3;
							}						
						}						
					}
					
					//tri matrice defense
					if(DEFmatrix[0][0]<DEFmatrix[1][0] || (DEFmatrix[0][0]==DEFmatrix[1][0] && DEFmatrix[0][1]<DEFmatrix[1][1] && DEFmatrix[1][1]!=3)) {
						//si l'atk de 2 est sup�rieur � l'attaque de 1, swap
						//si l'atk de 2 et 1 sont egales et que le code d'unite (pt de mvt max) de 2 est plus grand que le code d'unite de 1
						//excepte si ce code est 3 (horseman), alors swap. Ceci donne toujours 2 - 1 - 3 (soldat - canon - cavalier)
						memory1 = DEFmatrix[0][0];
						memory2 = DEFmatrix[0][1];
						DEFmatrix[0][0] = DEFmatrix[1][0];
						DEFmatrix[0][1] = DEFmatrix[1][1];
						DEFmatrix[1][0] = memory1;
						DEFmatrix[1][1] = memory2;
					}
					
					
					//"combat" des matrices, le 1er affronte le premier, le second affronte le second et la ligne perdante est mise � 0
				    //ce qui aura pour effet plus tard de supprimer l'unit�
					for (int i=0; i<2; i++) {
						if(DEFmatrix[i][0] >= ATKmatrix[i][0]) {
							ATKmatrix[i][0]=0;
							ATKmatrix[i][1]=0;
							ATKmatrix[i][2]=0;
						}else {
							DEFmatrix[i][0]=0;
							DEFmatrix[i][1]=0;
						}
					}
					
					
					//rapatriement matrice de defense dans territoire target
					//on utilise les donn�es formatt�es pour ajouter un nombre d'unit� � nos r�serves ce qui a en r�alit� pour effet
					//de restituer les unit�s victorieuses
					for (int i=0; i<2; i++) {
						if(DEFmatrix[i][1]==2) {//si unit = 2, rapatrier un musketman
							this.Plateau.territories[target-1].musketman[0]++;
							DEFmatrix[i][0]=0;
							DEFmatrix[i][1]=0;
						}
						if(DEFmatrix[i][1]==1) {//si unit = 1, rapatrier un canonnier
							this.Plateau.territories[target-1].canonnier[0]++;
							DEFmatrix[i][0]=0;
							DEFmatrix[i][1]=0;
						}
						if(DEFmatrix[i][1]==3) {//si unit = 3, rapatrier un horseman
							this.Plateau.territories[target-1].horseman[0]++;
							DEFmatrix[i][0]=0;
							DEFmatrix[i][1]=0;
						}
					}
					
					
					
					if(ATKmatrix[0][0]+ATKmatrix[1][0]+ATKmatrix[1][0] != 0) {//si matrice d'atk n'est pas vide
						if(this.Plateau.territories[target-1].troopsInTerritory() ==0) {//si le territoire target est sans defense (puisqu'on a d�ja rapatri� les d�fenseurs)
							this.Plateau.territories[target-1].player = player;//territoire appartient desormais � player
							this.Plateau.players[player-1].recentlyCaptured++;
							for (int i = 0; i<3; i++) {
								if(ATKmatrix[i][1]==2) {//si l'unite dans cette colonne de la matrice est un musketman, mettre a 0 cette colonne et
									//transferer le musketman vers la memoire d'unites du territoire cible (on lui retire un pt de mouvement neanmoins)
									this.Plateau.territories[target-1].musketman[ATKmatrix[i][2]+1]++;
									ATKmatrix[i][0]=0;
									ATKmatrix[i][1]=0;
									ATKmatrix[i][2]=0;									
								}
								if(ATKmatrix[i][1]==1) {
									this.Plateau.territories[target-1].canonnier[ATKmatrix[i][2]+1]++;
									ATKmatrix[i][0]=0;
									ATKmatrix[i][1]=0;
									ATKmatrix[i][2]=0;
								}
								if(ATKmatrix[i][1]==3) {
									this.Plateau.territories[target-1].horseman[ATKmatrix[i][2]+1]++;
									ATKmatrix[i][0]=0;
									ATKmatrix[i][1]=0;
									ATKmatrix[i][2]=0;
								}
								
							}
						}else {
							//sinon, si le territoire ne peut pas encore nous appartenir
							//rapatrier unites vers territoire de depart sans leur faire perdre de pt de mouvement 
							for (int i = 0; i<3; i++) {
								if(ATKmatrix[i][1]==2) {
									
									this.Plateau.players[player-1].musketman[ATKmatrix[i][2]]++;
									ATKmatrix[i][0]=0;
									ATKmatrix[i][1]=0;
									ATKmatrix[i][2]=0;
								}
								if(ATKmatrix[i][1]==1) {
									this.Plateau.players[player-1].canonnier++;
									ATKmatrix[i][0]=0;
									ATKmatrix[i][1]=0;
									ATKmatrix[i][2]=0;
								}
								if(ATKmatrix[i][1]==3) {
									this.Plateau.players[player-1].horseman[ATKmatrix[i][2]]++;
									ATKmatrix[i][0]=0;
									ATKmatrix[i][1]=0;
									ATKmatrix[i][2]=0;
								}								
							}
						}
					}							  											  					 
				}																		    						     						  										
				}
			return 0;
		}		
}
