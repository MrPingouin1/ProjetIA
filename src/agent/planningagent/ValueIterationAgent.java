package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.HashMapUtil;

import java.util.HashMap;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import environnement.Action2D;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	/**
	 * discount facteur
	 */
	protected double gamma;

	/**
	 * fonction de valeur des etats
	 */
	protected HashMap<Etat,Double> V;
	
	/**
	 * 
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,  MDP mdp) {
		super(mdp);
		this.gamma = gamma;
		V = new HashMap<Etat,Double>();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
		this.notifyObs();
		
	}
	
	public ValueIterationAgent(MDP mdp) {
		this(0.9,mdp);

	}
	
	/**
	 * 
	 * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
	 * et notifie ses observateurs.
	 * Ce n'est pas la version inplace (qui utilise nouvelle valeur de V pour mettre a jour ...)
	 */
	@Override
	public void updateV(){
		//delta est utilise pour detecter la convergence de l'algorithme
		//lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
		//delta < epsilon 
		this.delta=0.0;

		//*** MON CODE
		try {
			HashMap<Etat, Double> newV = new HashMap<>();

			for (Etat s : mdp.getEtatsAccessibles()) {
				double max = Double.MIN_VALUE;
				double value;

				for (Action a : mdp.getActionsPossibles(s)) {
					value = 0.0;
					for (Map.Entry<Etat, Double> cible : mdp.getEtatTransitionProba(s, a).entrySet()) {
						value += cible.getValue() * (mdp.getRecompense(s, a, cible.getKey()) + gamma * V.get(cible.getKey()));
					}
					if (value > max)
						max = value;
				}
				newV.put(s, max);
			}

			V = newV;
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		//******************* laisser la notification a la fin de la methode	
		this.notifyObs();
	}
	
	
	/**
	 * renvoi l'action executee par l'agent dans l'etat e 
	 * Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		List<Action> actions = this.getPolitique(e);
		if (actions.size() == 0)
			return Action2D.NONE;
		else{//choix aleatoire
			return actions.get(rand.nextInt(actions.size()));
		}

		
	}

	@Override
	public double getValeur(Etat _e) {
		return  V.get(_e);
	}

	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans etat 
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		// retourne action de meilleure valeur dans _e selon V, 
		// retourne liste vide si aucune action legale (etat absorbant)

		//*** MON CODE
		List<Action> returnactions = mdp.getActionsPossibles(_e);
		try {
			double best = Double.MIN_VALUE;
			double value;

			for (Action a : mdp.getActionsPossibles(_e)) {
				value = 0.0;
				for (Map.Entry<Etat, Double> cible : mdp.getEtatTransitionProba(_e, a).entrySet()) {
					value += cible.getValue() * (mdp.getRecompense(_e, a, cible.getKey()) + gamma * V.get(cible.getKey()));
				}
				if(value >= best){
					if (value > best){
						best = value;
						returnactions.clear();
					}
					returnactions.add(a);
				}
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		return returnactions;
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.V.clear();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
		this.notifyObs();
	}

	public HashMap<Etat,Double> getV() {
		return V;
	}

	public double getGamma() {
		return gamma;
	}

	@Override
	public void setGamma(double _g){
		System.out.println("gamma= "+gamma);
		this.gamma = _g;
	}

}
