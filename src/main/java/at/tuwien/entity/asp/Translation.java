package at.tuwien.entity.asp;

import at.tuwien.entity.asp.AspRule;

import java.util.List;

/**
 * Created by tobiaskain on 13/04/2017.
 */
public class Translation {

    private String aspCode;
    private List<AspRule> aspRules;
    private List<String> errors;

    public String getAspCode() {
        return aspCode;
    }

    public void setAspCode(String aspCode) {
        this.aspCode = aspCode;
    }

    public List<AspRule> getAspRules() {
        return aspRules;
    }

    public void setAspRules(List<AspRule> aspRules) {
        this.aspRules = aspRules;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
