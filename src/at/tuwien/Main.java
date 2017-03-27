package at.tuwien;


import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import at.tuwien.ASP.AspRule;
import at.tuwien.CNL2ASP.CnlToAspTranslator;
import at.tuwien.CNL2ASP.SentenceValidationException;
import at.tuwien.CNL2ASP.StanfordParser;
import at.tuwien.DLV.DLVProgramExecutor;
import at.tuwien.DLV.DLVProgramGenerator;
import edu.stanford.nlp.process.Morphology;
import it.unical.mat.dlv.program.Program;


public class Main {

    public static void main(String[] args) throws SentenceValidationException {

        List<String> inputStrings = new ArrayList<>();
        /*inputStrings.add("Roberta is a person.");
        inputStrings.add("Thelma is a person.");
        inputStrings.add("Steve is a person.");
        inputStrings.add("Pete is a person.");
        inputStrings.add("Roberta is female.");
        inputStrings.add("Thelma is female.");
        inputStrings.add("Steve is male.");
        inputStrings.add("Pete is male.");

        inputStrings.add("Chef is a job.");
        inputStrings.add("Guard is a job.");
        inputStrings.add("Nurse is a job.");
        inputStrings.add("Telephone operator is a job.");
        inputStrings.add("Police officer is a job.");
        inputStrings.add("Teacher is a job.");
        inputStrings.add("Actor is a job.");
        inputStrings.add("Boxer is a job.");*/

        inputStrings.add("If a person holds a job as nurse then the person is male.");

        StanfordParser.getInstance().printTaggedList(inputStrings);

        CnlToAspTranslator cnlToAspTranslator = new CnlToAspTranslator(inputStrings);

        List<AspRule> aspRules = cnlToAspTranslator.translate();

        DLVProgramGenerator dlvProgramGenerator = new DLVProgramGenerator();
        Program program = dlvProgramGenerator.generateDlvProgram(aspRules);

        DLVProgramExecutor dlvProgramExecutor = new DLVProgramExecutor();
        List<String> models = dlvProgramExecutor.executeProgram(program);

        printModels(models);
    }

    private static void printModels(List<String> models)
    {
        int count = 1;

        for (String model: models) {
            System.out.print(String.format("Model %d: %n%s",count,model));
        }
    }
}
