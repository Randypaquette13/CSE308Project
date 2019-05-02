package model;

public class Edge {
    Cluster c1;
    Cluster c2;
    private double joinability = -1;

    public Edge(Cluster c1, Cluster c2, double joinability) {
        this.c1 = c1;
        this.c2 = c2;
        this.joinability = joinability;
    }

    /**
     * Joinability is calculated based on the difference in the demographic percentages of the two clusters
     * @return joinability
     */
    public double getJoinability() {
        if(joinability != -1) {
            return joinability;
        }
        double totalDemographicDifference = 0;
        for(int ii = 0; ii < DemographicType.values().length; ii++) {
            totalDemographicDifference += Math.abs(c1.getDemographicPercentages()[ii] - c2.getDemographicPercentages()[ii]);
        }
        joinability = 1.0 / totalDemographicDifference;

        return joinability;
    }
}