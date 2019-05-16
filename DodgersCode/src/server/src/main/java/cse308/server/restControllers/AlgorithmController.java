package cse308.server.restControllers;

import controller.Algorithm;
import cse308.server.dao.BatchedPreferencesDAO;
import cse308.server.dao.PreferenceDAO;
import cse308.server.dao.SummaryDAO;
import model.Preference;
import model.State;
import model.Summary;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class specifies the endpoints and behavior used to run the algorithm.
 */
@RestController
public class AlgorithmController {

    private State state= null;
    private Algorithm algorithm;

    /**
     * This method handles running the graph partitioning portion of the algorithm.
     * @return  unknown
     */
    @RequestMapping("/runGraphPartitioning")
    public Collection<long[]> doGraphPartitioning(@RequestBody PreferenceDAO preference) {
        //TODO: Load state object from DB and set it to the private state obj
        Preference p = preference.makePreference();
        if(state == null) {
            state = State.getState(p.getStateName());
        }

        algorithm = new Algorithm(p, state);
        if (preference.isGraphPartUpdate()) {
            if(state.isGPDone) {
                return null;
            }
            if("done".equals(algorithm.doGraphPartitioning())){
                state.isGPDone = true;
            }
            return state.getClustersSimple();
        } else {
            String gps = algorithm.doGraphPartitioning();
            while(!"done".equals(gps)) {
                gps = algorithm.doGraphPartitioning();
            }
            return state.getClustersSimple();
        }
    }

    /**
     * This method handles running the simulated annealing portion of the algorithm.
     * @return unknown
     */
    @RequestMapping("/runSimulatedAnnealing")
    public SummaryDAO doSimulatedAnnealing() {
        return algorithm.doSimulatedAnnealing().toDAO();
    }

    /**
     * This method handles running the batch processing portion of the algorithm.
     * @return  unknown
     */
    @RequestMapping("/runBatchProcessing")
    public List<Summary> doBatchProcessing(@RequestBody BatchedPreferencesDAO preferenceDAO) {
        final List<Summary> summaryBatch = new LinkedList<>();
        List<Preference> preferences = preferenceDAO.makePreferences();
        for(Preference preference : preferences) {
            final Algorithm algorithm = new Algorithm(preference, state);
            summaryBatch.add(algorithm.doJob());
        }
        return summaryBatch;
    }
}
