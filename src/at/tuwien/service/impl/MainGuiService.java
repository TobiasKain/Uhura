package at.tuwien.service.impl;

import at.tuwien.CNL2ASP.*;
import at.tuwien.dao.DaoException;
import at.tuwien.dao.TranslationPatternDAO;
import at.tuwien.dao.WordDAO;
import at.tuwien.dao.impl.JDBCTranslationPatternDAO;
import at.tuwien.dao.impl.JDBCWordDAO;
import at.tuwien.dlv.DLVProgramExecutor;
import at.tuwien.dlv.DLVProgramGenerator;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.Translation;
import at.tuwien.service.IMainGuiService;
import it.unical.mat.dlv.program.Program;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tobiaskain on 13/04/2017.
 */
public class MainGuiService implements IMainGuiService {

    private List<Word> directory;
    private WordDAO wordDAO;

    private List<TranslationPattern> translationPatterns;
    private TranslationPatternDAO translationPatternDAO;

    public MainGuiService() {
        directory = new ArrayList<>();
        try {
            wordDAO = new JDBCWordDAO();
        } catch (DaoException e) {
            e.printStackTrace();
        }
        updateDirectory();

        translationPatterns = new ArrayList<>();
        try {
            translationPatternDAO = new JDBCTranslationPatternDAO();
        } catch (DaoException e) {
            e.printStackTrace();
        }
        updatedTranslationPatterns();
    }

    @Override
    public void updateDirectory() {
        directory.clear();

        try {
            directory.addAll(wordDAO.readAllWords());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatedTranslationPatterns(){
        translationPatterns.clear();

        try {

            translationPatterns.addAll(translationPatternDAO.readAllTranslationPatterns());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Translation translate(String cnlSentences) {

        CnlToAspTranslator cnlToAspTranslator = new CnlToAspTranslator(splitSentences(cnlSentences), directory, translationPatterns);

        Translation translation = cnlToAspTranslator.translate();

        DLVProgramGenerator dlvProgramGenerator = new DLVProgramGenerator();
        Program program = dlvProgramGenerator.generateDlvProgram(translation.getAspRules());

        translation.setAspCode(dlvProgramGenerator.getCode(program));

        return translation;
    }

    @Override
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
        cnlSentences = cnlSentences.replaceAll("\n"," ");
        List<String> sentenceList = new LinkedList<String>(Arrays.asList(cnlSentences.split("\\.")));

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
