package at.tuwien.service.impl;

import at.tuwien.CNL2ASP.*;
import at.tuwien.dao.DaoException;
import at.tuwien.dao.TranslationPatternDAO;
import at.tuwien.dao.WordDAO;
import at.tuwien.dao.impl.JDBCTranslationPatternDAO;
import at.tuwien.dao.impl.JDBCWordDAO;
import at.tuwien.dlv.DLVException;
import at.tuwien.dlv.DLVProgramExecutor;
import at.tuwien.dlv.DLVProgramGenerator;
import at.tuwien.entity.TranslationPattern;
import at.tuwien.entity.Word;
import at.tuwien.entity.asp.Translation;
import at.tuwien.gui.TranslationType;
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

    private TranslationType translationType;

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
    public Translation translate(String cnlSentences) throws DLVException {

        CnlToAspTranslator cnlToAspTranslator = new CnlToAspTranslator(splitSentences(cnlSentences), directory, translationPatterns);

        Translation translation = cnlToAspTranslator.translate();

        DLVProgramGenerator dlvProgramGenerator = new DLVProgramGenerator();
        translation.setAspCode(dlvProgramGenerator.generateDlvProgram(translation.getAspRules()));

        return translation;
    }

    @Override
    public List<String> solve(String aspRules, String filter) throws DLVException {
        DLVProgramGenerator dlvProgramGenerator = new DLVProgramGenerator();

        Program program = dlvProgramGenerator.generateDlvProgram(aspRules);

        DLVProgramExecutor dlvProgramExecutor = new DLVProgramExecutor();
        List<String> models = dlvProgramExecutor.executeProgram(program,filter);

        return models;
    }

    @Override
    public TranslationType getTranslationType() {
        return translationType;
    }

    @Override
    public void setTranslationType(TranslationType translationType) {
        this.translationType = translationType;
    }

    private List<String> splitSentences(String cnlSentences)
    {
        List<String> sentenceList = new LinkedList<String>(Arrays.asList(cnlSentences.split("\\.")));
        List<String> resultList = new ArrayList<>();

        if(cnlSentences.lastIndexOf('.') != cnlSentences.length()-1) // check if last character is .
        {
            sentenceList.remove(sentenceList.size()-1);
        }

        for (String s : sentenceList) {

            for(int i = 0; i < countNewLines(s); i++){
                resultList.add("\n");
            }
            s = s.replaceAll("\n","");

            resultList.add(s.trim() + ".");
        }

        return resultList;
    }

    private int countNewLines(String sentence){
        int newLineCount = 0;

        for(int i = 0; i < sentence.length(); i++){
            if(sentence.charAt(i) == ' '){
                continue;
            } else if(sentence.charAt(i) == '\n'){
                newLineCount ++;
            } else {
                return newLineCount;
            }
        }

        return newLineCount;
    }

}
