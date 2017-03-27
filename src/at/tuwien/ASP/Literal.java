package at.tuwien.ASP;

import java.util.ArrayList;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class Literal {
    private String predicateName;
    private ArrayList<String> terms;

    public Literal(String predicateName) {
        this.predicateName = predicateName;
        terms = new ArrayList<>();
    }

    public Literal(String predicateName, ArrayList<String> terms) {
        this.predicateName = predicateName;
        this.terms = terms;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public void setPredicateName(String predicateName) {
        this.predicateName = predicateName;
    }

    public ArrayList<String> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<String> terms) {
        this.terms = terms;
    }
}
