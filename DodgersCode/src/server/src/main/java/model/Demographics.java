package model;

import java.util.HashMap;

public class Demographics {
    private final HashMap<DemographicType, Integer> demographicPopulation;
    int demVotes;
    int repVotes;

    public Demographics(HashMap<DemographicType, Integer> demographicPopulation) {
        this.demographicPopulation = demographicPopulation;
    }

    public HashMap<DemographicType, Integer> getDemographicPopulation() {
        return demographicPopulation;
    }


    public void add(Demographics d) {
//        System.out.println("\taddtree:" + this);
//        System.out.println("\taddleaf" + d);
        for(DemographicType demoType : DemographicType.values()) {
            //handle demographic population
            demographicPopulation.put(demoType, demographicPopulation.get(demoType) + d.getDemographicPopulation().get(demoType));
        }
        demVotes += d.demVotes;
        repVotes += d.repVotes;
//        System.out.println("\t\t" + this);
    }

    public void remove(Demographics d) {
//        System.out.println("\tremovetree:" + this);
//        System.out.println("\tremoveleaf" + d);

        for(DemographicType demoType : DemographicType.values()) {
            //handle demographic population
            demographicPopulation.put(demoType, demographicPopulation.get(demoType) - d.getDemographicPopulation().get(demoType));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(DemographicType dType : DemographicType.values()) {
            sb.append(dType);
            sb.append(" Population:");
            sb.append(demographicPopulation.get(dType));
        }
        return sb.toString();
    }

    public static Demographics getDemographicTest() {
        HashMap<DemographicType, Integer> populations = new HashMap<>();
        Demographics d1 = new Demographics(populations);
        return d1;
    }

    public Demographics clone() {
        return new Demographics(new HashMap<>(getDemographicPopulation()));
    }
}
