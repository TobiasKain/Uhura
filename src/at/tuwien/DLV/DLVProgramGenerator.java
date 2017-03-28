package at.tuwien.DLV;

import at.tuwien.ASP.AspRule;
import at.tuwien.ASP.Literal;
import it.unical.mat.dlv.program.Program;
import it.unical.mat.dlv.program.Rule;

import java.util.List;

public class DLVProgramGenerator {

    public Program generateDlvProgram(List<AspRule> rules){

        Program program = new Program();

        for (AspRule rule:rules)
        {
            String ruleString = "";

            if(!rule.getHead().isEmpty())
            {
                for (Literal literal:rule.getHead()) {
                    ruleString += generateDlvLiteral(literal) + ",";
                }
                ruleString = ruleString.substring(0,ruleString.lastIndexOf(','));  // remove last ','
            }

            if(!rule.getBody().isEmpty())
            {
                ruleString += ":-";
                for (Literal literal:rule.getBody()) {
                    ruleString += generateDlvLiteral(literal) + ",";
                }
                ruleString = ruleString.substring(0,ruleString.lastIndexOf(','));  // remove last ','
            }

            ruleString += ".";

            System.out.println(ruleString);

            program.add(new Rule(ruleString));
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
}
