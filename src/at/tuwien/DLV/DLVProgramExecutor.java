package at.tuwien.DLV;

import it.unical.mat.dlv.program.Program;
import it.unical.mat.wrapper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class DLVProgramExecutor {

    public List<String> executeProgram(Program program){

        DLVInvocation invocation= DLVWrapper.getInstance(). createInvocation("/Users/tobiaskain/dlv.i386-apple-darwin.bin");

        DLVInputProgram inputProgram=new DLVInputProgramImpl();

        inputProgram.includeProgram(program);

        final List<ModelResult> models= new ArrayList();
        ModelHandler modelHandler=new ModelHandler(){
            final public void handleResult(DLVInvocation obsd, ModelResult res) {
                models.add(res);
            }
        };

        try {
            invocation.subscribe(modelHandler);
            invocation.setInputProgram(inputProgram);

            invocation.run();

            invocation.waitUntilExecutionFinishes();

        } catch (DLVInvocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return generateModelStringList(models);
    }

    private List<String> generateModelStringList(List<ModelResult> modelResults)
    {
        List<String> models = new ArrayList<>();

        for (ModelResult model: modelResults) {
            models.add(((Model) model).toString());
        }

        return models;
    }

}
