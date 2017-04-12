package pacman.environnementRL;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;
/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire

 */
public class EtatPacmanMDPClassic implements Etat , Cloneable{
	private List<Point> listPacman;
	private List<Point> listGhost;
	private List<Point> listDot;

	
	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman) {
		listPacman = new ArrayList<>();
		for (int i = 0; i < _stategamepacman.getNumberOfPacmans(); i++) {
			if (!_stategamepacman.getPacmanState(i).isDead())
				listPacman.add(new Point(_stategamepacman.getPacmanState(i).getX(), _stategamepacman.getPacmanState(i).getY()));
		}

		listGhost = new ArrayList<>();
		for (int i = 0; i < _stategamepacman.getNumberOfGhosts(); i++) {
			if (!_stategamepacman.getGhostState(i).isDead())
				listGhost.add(new Point(_stategamepacman.getGhostState(i).getX(), _stategamepacman.getGhostState(i).getY()));
		}

		listDot = new ArrayList<>();
		for (int i = 0; i < _stategamepacman.getMaze().getSizeX(); i++) {
			for (int j = 0; j < _stategamepacman.getMaze().getSizeY(); j++) {
				if(!_stategamepacman.getMaze().isFood(i,j))
					listDot.add(new Point(i,j));
			}
		}
	}
	
	@Override
	public String toString() {
		return "" ;
	}

	@Override
	public int hashCode() {
		int result = listPacman != null ? listPacman.hashCode() : 0;
		result = 31 * result + (listGhost != null ? listGhost.hashCode() : 0);
		result = 31 * result + (listDot != null ? listDot.hashCode() : 0);
		return result;
	}

	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la 
			// methode super.clone()
			clone = (EtatPacmanMDPClassic)super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		


		// on renvoie le clone
		return clone;
	}



	

}
