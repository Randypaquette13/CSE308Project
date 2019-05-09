package controller;

import model.*;

import java.util.*;

public class Algorithm {
    private Preference pref;
    private State state;

    public Algorithm(Preference pref, State state) {
        this.pref = pref;
        this.state = state;
    }

    /**
     * This is just an example of how to run the algorithm
     */
    public Summary doJob() {
        doGraphPartitioning();
        return doSimulatedAnnealing();
    }

    /**
     * Since the measure is relevant on the district level, the calculation will need
     * to get each measure value for each district
     *
     * @return the output of the objective function
     */
    private double calculateObjectiveFunction() {
        double objFunOutput = 0;
        for(District d : state.getDistrictSet()) {
            for(MeasureType m : MeasureType.values()) {
                objFunOutput += m.calculateMeasure(d) * pref.getWeight(m);
            }
        }
        return objFunOutput;
    }

    private double[] calculateTotalMeasuresScores() {
        double[] measureScores = new double[MeasureType.values().length];

        for(District d : state.getDistrictSet()) {
            for(int ii = 0; ii < MeasureType.values().length; ii++) {
                measureScores[ii] = MeasureType.values()[ii].calculateMeasure(d);
            }
        }
        for(int ii = 0; ii < measureScores.length; ii++) {
            measureScores[ii] = measureScores[ii]/(double)state.getDistrictSet().size();
        }
        return measureScores;
    }

    public void doGraphPartitioning() {
        //you must reset the state so we dont have to make extra database calls
        state.reset();
        while(state.getClusters().size() != pref.getNumDistricts()) {
            int targetNumClusters = (int)Math.ceil(state.getClusters().size() / 2);
            int maxTargetPop = (int)Math.ceil(state.getPopulation() / targetNumClusters);
            int minTargetPop = 0;   //TODO: load percentage to ignore from config file

            ((List<Cluster>) state.getClusters()).sort(Comparator.comparingInt(Cluster::getPopulation));

            Collection<Cluster> mergedClusters = new LinkedList<>();
            while(!state.getClusters().isEmpty()) {
                final ClusterPair clusterPair = state.findCandidateClusterPair();
                if(clusterPair == null){
                    break;
                }
                Cluster c = state.combinePair(clusterPair.getC1(), clusterPair.getC2());
                mergedClusters.add(c);
            }
            (state.getClusters()).addAll(mergedClusters);
        }
    }

    public Summary doSimulatedAnnealing() {
        double lastObjFunVal = 0;
        int annealingSteps = 0;
        Move candidateMove;
        //anneal until the objective function output is acceptable or the max steps is reached
        while(calculateObjectiveFunction() < Configuration.OBJECTIVE_FUNCTION_GOAL && annealingSteps < Configuration.MAX_ANNEALING_STEPS) {
            candidateMove = state.findCandidateMove();

            if(candidateMove != null) {
                state.doMove(candidateMove);
                final double currObjFunVal = calculateObjectiveFunction();

                if((currObjFunVal - lastObjFunVal) > Configuration.OBJECTIVE_FUNCTION_MIN_CHANGE) {
                    lastObjFunVal = currObjFunVal;
                } else {
                    state.undoMove();
                }
            } else {
                break;
            }
            //TODO send update steps to client here if it is just one batch job
            annealingSteps++;
        }
        return new Summary(state,lastObjFunVal,calculateTotalMeasuresScores());
    }

}
