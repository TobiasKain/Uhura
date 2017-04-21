package at.tuwien.service;

import at.tuwien.entity.Word;
import at.tuwien.entity.asp.Translation;

import java.util.List;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public interface IMainGuiService {
    void updateDirectory();

    Translation translate(String cnlSentences);

    List<String> solve(String aspRules, String filter);
}
