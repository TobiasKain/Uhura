import at.tuwien.entity.TranslationPattern;
import at.tuwien.nl2cln.NL2CNLTranslator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by tobiaskain on 23/04/2017.
 */
public class NL2CNLTranslationTest {

    @Test
    public void translateTest()
    {
        TranslationPattern tp1 = new TranslationPattern();
        tp1.setNlSentence("A and B are C.");
        tp1.setRegexPattern(".* and .* are .*.");
        tp1.setTranslation("If X is a A then X is a C. " +
                "If X is a B then X is a C.");

        List<TranslationPattern> translationPatternList = new ArrayList<>();
        translationPatternList.add(tp1);

        NL2CNLTranslator nl2CNLTranslator = new NL2CNLTranslator(translationPatternList);

        List<String> translations = nl2CNLTranslator.translate("cats and dogs are animals.");

        assertTrue(translations.size() == 2);
        assertEquals("If X is a cats then X is a animals.",translations.get(0));
        assertEquals(" If X is a dogs then X is a animals.",translations.get(1));
    }
}
