package at.tuwien.Service;

import at.tuwien.ASP.AspRule;
import at.tuwien.CNL2ASP.CnlToAspTranslator;
import at.tuwien.DLV.DLVProgramExecutor;
import at.tuwien.DLV.DLVProgramGenerator;
import it.unical.mat.dlv.program.Program;
import it.unical.mat.dlv.program.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tobiaskain on 13/04/2017.
 */
public class MainGuiService {

    public String translate(String cnlSentences) {
        CnlToAspTranslator cnlToAspTranslator = new CnlToAspTranslator(splitSentences(cnlSentences));

        List<AspRule> aspRules = cnlToAspTranslator.translate();

        DLVProgramGenerator dlvProgramGenerator = new DLVProgramGenerator();
        Program program = dlvProgramGenerator.generateDlvProgram(aspRules);

        return dlvProgramGenerator.getCode(program);
    }

    public List<String> solve(String aspRules, String filter)
    {
        DLVProgramGenerator dlvProgramGenerator = new DLVProgramGenerator();

        Program program = dlvProgramGenerator.generateDlvProgram(aspRules);

        DLVProgramExecutor dlvProgramExecutor = new DLVProgramExecutor();
        List<String> models = dlvProgramExecutor.executeProgram(program,filter);

        return models;
    }

    private List<String> splitSentences(String cnlSentences)
    {
        cnlSentences = cnlSentences.replaceAll("\n","");
        List<String> sentenceList = Arrays.asList(cnlSentences.split("\\."));

        if(cnlSentences.lastIndexOf('.') != cnlSentences.length()-1) // check if last character is .
        {
            sentenceList.remove(sentenceList.size()-1);
        }

        for (int i = 0; i < sentenceList.size(); i++) {
            sentenceList.set(i,sentenceList.get(i) + ".");
        }

        return sentenceList;
    }

}
