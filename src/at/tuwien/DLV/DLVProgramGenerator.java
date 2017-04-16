package at.tuwien.DLV;

import at.tuwien.ASP.AspRule;
import at.tuwien.ASP.Literal;
import it.unical.mat.dlv.program.Program;
import it.unical.mat.dlv.program.Rule;

import java.util.Arrays;
import java.util.List;

public class DLVProgramGenerator {

    public Program generateDlvProgram(List<AspRule> rules){

        Program program = new Program();

        for (AspRule rule:rules)
        {
            if(rule == null)
            {
                continue;
            }

            String ruleString = "";

            if(!rule.getHead().isEmpty())
            {
                if(rule.isOr()) {
                    for (Literal literal : rule.getHead()) {
                        if(literal.isNegated()){
                            ruleString += " - ";
                        }
                        ruleString += generateDlvLiteral(literal) + " v ";
                    }
                    ruleString = ruleString.substring(0, ruleString.lastIndexOf(" v "));  // remove last 'v'
                }
                else {
                    // TODO throw exception if head > 1
                    if(rule.getHead().get(0).isNegated())
                    {
                        ruleString += " - ";
                    }
                    ruleString += generateDlvLiteral(rule.getHead().get(0));
                }
            }

            if(!rule.getBody().isEmpty())
            {
                ruleString += " :- ";
                for (Literal literal:rule.getBody()) {
                    if(literal.isNegated()){
                        ruleString += " not ";
                    }
                    ruleString += generateDlvLiteral(literal) + ", ";
                }
                ruleString = ruleString.substring(0,ruleString.lastIndexOf(", "));  // remove last ','
            }

            ruleString += ".";

            System.out.println(ruleString);

            program.add(new Rule(ruleString));

            // TODO check if rule.toString() == null // check with "person X holds more than two jobs Y"
        }

        return program;
    }

    public Program generateDlvProgram(String rules) {
        Program program = new Program();

        rules = rules.replaceAll("\n", "");

        List<String> ruleList = Arrays.asList(rules.split("\\."));

        for (String rule: ruleList) {
            program.add(new Rule(rule + "."));
        }

        return program;
    }

    private String generateDlvLiteral(Literal literal)
    {
        String literalString = "";

        literalString += literal.getPredicateName();

        if(!literal.getTerms().isEmpty())
        {
            literalString += "(";
            for (String term:literal.getTerms()) {
                literalString += term + ",";
            }
            literalString = literalString.substring(0,literalString.lastIndexOf(','));  // remove last ','
            literalString += ")";
        }

        return literalString;
    }

    public String getCode(Program program) {

        String code = "";

        for (Rule rule: program.getRules()) {
            if(rule.toString() == null)
            {
                continue;
            }

            code += rule + "\n";
        }

        return code;
    }
}
