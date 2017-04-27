package at.tuwien.dlv;

import at.tuwien.entity.asp.AspRule;
import at.tuwien.entity.asp.Literal;
import at.tuwien.entity.asp.NewLine;
import it.unical.mat.dlv.program.Program;
import it.unical.mat.dlv.program.Rule;
import org.h2.util.New;

import java.util.Arrays;
import java.util.List;

public class DLVProgramGenerator {

    public String generateDlvProgram(List<AspRule> rules){

        String program = "";

        for (AspRule rule:rules)
        {
            if(rule == null)
            {
                continue;
            }

            if(rule instanceof NewLine){
                program += "\n";
                continue;
            }

            String ruleString = "";

            if (!rule.getHead().isEmpty()) {
                if (rule.isOr()) {
                    for (Literal literal : rule.getHead()) {
                        if (literal.isNegated()) {
                            ruleString += " - ";
                        }
                        ruleString += generateDlvLiteral(literal) + " v ";
                    }
                    ruleString = ruleString.substring(0, ruleString.lastIndexOf(" v "));  // remove last 'v'
                } else {
                    // TODO throw exception if head > 1
                    if (rule.getHead().get(0).isNegated()) {
                        ruleString += " - ";
                    }
                    ruleString += generateDlvLiteral(rule.getHead().get(0));
                }
            }

            if (!rule.getBody().isEmpty()) {
                ruleString += " :- ";
                for (Literal literal : rule.getBody()) {
                    if (literal.isNegated()) {
                        ruleString += " not ";
                    }
                    if (literal.isStrongNegated()) {
                        ruleString += "-";
                    }
                    ruleString += generateDlvLiteral(literal) + ", ";
                }
                ruleString = ruleString.substring(0, ruleString.lastIndexOf(", "));  // remove last ','
            }

            ruleString += ".";

            program += ruleString.trim();
        }

        return program;
    }

    public Program generateDlvProgram(String rules) throws DLVException {
        Program program = new Program();

        rules = rules.replaceAll("\n", "");

        List<String> ruleList = Arrays.asList(rules.split("\\."));

        for (String rule: ruleList) {
            try {
                program.add(new Rule(rule + "."));
            }catch (Exception e){
                throw new DLVException(e.getMessage());
            }
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

    public String getCode(Program program) throws DLVException {

        String code = "";

        for (Rule rule: program.getRules()) {
            try {
                code += rule + "\n";
            }catch (Exception e)
            {
                throw new DLVException("Check sentences for unsupported characters.");
            }
        }

        return code;
    }
}
