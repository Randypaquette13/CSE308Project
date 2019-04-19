package controller;

public class Move {

    private District from;
    private District to;
    private Precinct precinct;

    public Move(District initFrom, District  initTo, Precinct initPrecinct){
        from = initFrom;
        to = initTo;
        precinct = initPrecinct;

    }






    public District getFrom(){
        return from;
    }

    public District getTo(){
        return to;
    }

    public Precinct getPrecinct(){
        return precinct;
    }

}