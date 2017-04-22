package at.tuwien.entity.asp;

import java.util.ArrayList;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class Literal {
    private String predicateName;
    private ArrayList<String> terms;
    private boolean negated;
    private boolean strongNegated;

    public Literal(String predicateName) {
        this.predicateName = predicateName;
        terms = new ArrayList<>();
        negated = false;
        strongNegated = false;
    }

    public Literal(String predicateName, ArrayList<String> terms) {
        this(predicateName);
        this.terms = terms;
    }

    public Literal(String predicateName, boolean negated) {
        this(predicateName);
        this.negated = negated;
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

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public boolean isStrongNegated() {
        return strongNegated;
    }

    public void setStrongNegated(boolean strongNegated) {
        this.strongNegated = strongNegated;
    }
}
