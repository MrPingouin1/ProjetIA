package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
	/**
	 *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
	 */
	protected HashMap<Etat,HashMap<Action,Double>> qvaleurs;
	
	//AU CHOIX: vous pouvez utiliser une Map avec des Pair pour les clés si vous préférez
	//protected HashMap<Pair<Etat,Action>,Double> qvaleurs;

	
	/**
	 * 
	 * @param alpha
	 * @param gamma
	 * @param Environnement
	 * @param nbS attention ici il faut tous les etats (meme obstacles) car Q avec tableau ...
	 * @param nbA
	 */
	public QLearningAgent(double alpha, double gamma,
			Environnement _env) {
		super(alpha, gamma,_env);
		qvaleurs = new HashMap<Etat,HashMap<Action,Double>>();
		
		
	
	}


	
	
	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  (plusieurs actions sont renvoyees si valeurs identiques)
	 *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		// retourne action de meilleures valeurs dans _e selon Q : utiliser getQValeur()
		// retourne liste vide si aucune action legale (etat terminal)

		List<Action> returnactions = new ArrayList<>();

		if (this.getActionsLegales(e).size() == 0){//etat  absorbant; impossible de le verifier via environnement
			System.out.println("aucune action legale");
			return new ArrayList<Action>();
		}

		Double valeurMax = this.getValeur(e);
		for (Action action : this.getActionsLegales(e)) {
			if (this.getQValeur(e, action) == valeurMax) {
				returnactions.add(action);
			}
		}

		if(returnactions.isEmpty())
			returnactions = this.getActionsLegales(e);

		return returnactions;
	}
	
	@Override
	public double getValeur(Etat e) {
		Double valeur = 0.0;
		HashMap<Action,Double> etat = this.qvaleurs.get(e);
		if(etat != null){
			valeur = etat.entrySet()
					.stream()
					.max((entry1, entry2) -> getQValeur(e, entry1.getKey()) > getQValeur(e, entry2.getKey()) ? 1 : -1)
					.map(Map.Entry::getValue).orElse(0.0);
		}
		return valeur;
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		if(qvaleurs.get(e) == null || qvaleurs.get(e).get(a) == null)
			return 0.0;

		return qvaleurs.get(e).get(a);
	}
	
	
	
	@Override
	public void setQValeur(Etat e, Action a, double d) {
		qvaleurs.get(e).put(a,d);

		if(d > vmax)
			vmax = d;
		if(d < vmin)
			vmin = d;

		this.notifyObs();
	}
	
	
	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL)
			System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);

		HashMap<Action,Double> etat = qvaleurs.get(e);
		if(etat == null){
			HashMap<Action,Double> actionReward = new HashMap<>();
			actionReward.put(a,reward);
			qvaleurs.put(e,actionReward);
		}else{
			Double valeur = etat.get(a);
			if(valeur == null){
				valeur = reward;
			}else{
				valeur = (1-alpha)*valeur + alpha*(reward + gamma*this.getValeur(esuivant));
			}
			this.setQValeur(e,a,valeur);
		}
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		qvaleurs.clear();
		this.notifyObs();
	}









	


}
