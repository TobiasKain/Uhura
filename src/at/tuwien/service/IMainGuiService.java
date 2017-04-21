package at.tuwien.service;

import at.tuwien.CNL2ASP.Translation;

import java.util.List;

/**
 * Created by tobiaskain on 21/04/2017.
 */
public interface IMainGuiService {
    Translation translate(String cnlSentences);

    List<String> solve(String aspRules, String filter);
}
