****************************************************
*
*   				TP1 - IHM - 
*				SwingStates : les boutons
*
****************************************************

Binome : Ilias Afrass & Anas Taibi



L'objectif de ce TP est d'utiliser le widget canvas et une machine à états de la librairie SwingStates pour créer un bouton et
aussi visualiser les états.

les états qu'on a implementé sont :

	* stat start			=> l'etat initial
	
	* stat over				=> le bord du rectangle s'épaississe lorsque la souris le survole.(on change aussi le text du bouton pr savoir l'état)
	
	* stat pressed			=> le font du rectangle change de couleur(JAUNE) si l'utilisateur enfonce le bouton de la souris au-dessus de lui(on change aussi le text du bouton pr savoir l'état)
	
	* stat hold				=>   le pointeur sort du rectangle.
	
	* attente d'un double clic	=> on attend un double click quand tu click un seul click.
	
	* double clic 				=> l'etat ou tu clique deux clique succesif.
	
	* un double clic qui se transforme en 1 clic et demi => l'etat ou tu clique un clique et 1/2 click.
	
	* holding le clic et demi		=> le pointeur sort pendant l'état precedent.
							
	* un demi click					=> l'etat ou on clique 1/2 click.
	
	* holding un demi clic			=> le pointeur sort pendant l'état precedent.




