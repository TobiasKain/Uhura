package at.tuwien.cnl2asp;

import at.tuwien.entity.ManualTranslation;
import at.tuwien.entity.asp.AspRule;

import java.util.List;

/**
 * Created by tobiaskain on 02/05/2017.
 */
public class ManualTranslator {
    private List<ManualTranslation> manualTranslations;

    public ManualTranslator(List<ManualTranslation> manualTranslations) {
        this.manualTranslations = manualTranslations;
    }

    public AspRule translate(String sentence){
        AspRule aspRule = new AspRule();

        for (ManualTranslation m:manualTranslations) {
            if(sentence.trim().equals(m.getCnlSentence().trim())){
                aspRule.setRule(m.getAspRule());
                return aspRule;
            }
        }

        return null;
    }
}
