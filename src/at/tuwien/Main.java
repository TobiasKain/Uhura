package at.tuwien;


import java.util.ArrayList;
import java.util.List;

import at.tuwien.ASP.AspRule;
import at.tuwien.CNL2ASP.CnlToAspTranslator;
import at.tuwien.CNL2ASP.SentenceValidationException;
import at.tuwien.CNL2ASP.StanfordParser;
import at.tuwien.DLV.DLVProgramExecutor;
import at.tuwien.DLV.DLVProgramGenerator;
import it.unical.mat.dlv.program.Program;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI/main_gui.fxml"));
        primaryStage.setTitle("CNL2ASP-Translator");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }


    public static void main(String[] args) throws SentenceValidationException {

        launch(args);

        List<String> inputStrings = new ArrayList<>();

        inputStrings.add("Roberta is a person."); // B.1.0
        inputStrings.add("Thelma is a person."); // B.1.0
        inputStrings.add("Steve is a person."); // B.1.0
        inputStrings.add("Pete is a person."); // B.1.0

        inputStrings.add("Roberta is female."); // B.1.1
        inputStrings.add("Thelma is female."); // B.1.1

        inputStrings.add("Steve is male."); // B.1.2
        inputStrings.add("Pete is male."); // B.1.2

        inputStrings.add("Exclude that person X is male and that person X is female."); // B.1.3

        inputStrings.add("If there is a person X and there is a job Y then person X holds job Y or person X does not hold job Y."); // B.2.1
        inputStrings.add("Exclude that there is a job Y and that person X holds more than one job Y."); // B.2.2
        inputStrings.add("Exclude that there is a job Y and that person X holds less than one job Y."); // B.2.3

        inputStrings.add("Exclude that there is a person X and that person X holds more than two jobs Y."); // B.3.1
        inputStrings.add("Exclude that there is a person X and that person X holds less than two jobs Y."); // B.3.2

        inputStrings.add("Chef is a job."); // B.4.0
        inputStrings.add("Guard is a job."); // B.4.0
        inputStrings.add("Nurse is a job."); // B.4.0
        inputStrings.add("Telephone operator is a job."); // B.4.0
        inputStrings.add("Police officer is a job."); // B.4.0
        inputStrings.add("Teacher is a job."); // B.4.0
        inputStrings.add("Actor is a job."); // B.4.0
        inputStrings.add("Boxer is a job."); // B.4.0

        inputStrings.add("If a person X holds a job as nurse then person X is male."); // B.5.0
        inputStrings.add("If a person X holds a job as actor then person X is male."); // B.5.1

        inputStrings.add("If a person X holds a job as chef and a person Y holds a job as telephone operator then a person Y is a husband of a  person X."); // B.6.0
        inputStrings.add("If a person X is a husband of a person Y then person X is male."); // B.6.1
        inputStrings.add("If a person X is a husband of a person Y then person Y is female."); // B.6.2

        inputStrings.add("Exclude that Roberta holds a job as boxer."); // B.7.0

        inputStrings.add("Exclude that Pete is educated."); // B.8.0
        inputStrings.add("If a person X holds a job as nurse then person X is educated."); // B.8.1
        inputStrings.add("If a person X holds a job as police officer then person X is educated."); // B.8.2
        inputStrings.add("If a person X holds a job as teacher then person X is educated."); // B.8.3

        inputStrings.add("Exclude that Roberta holds a job as chef."); // B.9.1a
        inputStrings.add("Exclude that Roberta holds a job as police officer."); // B.9.1b
        inputStrings.add("Exclude that a person X holds a job as chef and that person X holds a job as police officer."); // B.9.2


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

        System.out.println("\n\n----------- Models -----------");

        for (String model: models) {
            System.out.print(String.format("\nModel %d: %n%s",count,model));
            count++;
        }
    }
}
