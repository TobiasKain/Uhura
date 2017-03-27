package at.tuwien.CNL2ASP;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.trees.Tree;

import java.io.StringReader;
import java.util.List;

/**
 * Created by tobiaskain on 26/03/2017.
 */
public class StanfordParser {

    private static StanfordParser stanfordParser;
    private final static String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    private final TokenizerFactory<CoreLabel> tokenizerFactory;
    private final LexicalizedParser parser;
    private Morphology morphology;


    private StanfordParser()
    {
        tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");
        parser = LexicalizedParser.loadModel(PCG_MODEL);
        morphology = new Morphology();
    }



    public static StanfordParser getInstance() {

        if(stanfordParser == null)
        {
            stanfordParser = new StanfordParser();
        }

        return stanfordParser;
    }

    public Tree parse(String str) {
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    private List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer =
                tokenizerFactory.getTokenizer(
                        new StringReader(str));
        return tokenizer.tokenize();
    }

    public void printTaggedList(List<String> list){

        for (String str:list) {
            Tree tree = parse(str);

            List<Tree> leaves = tree.getLeaves();
            // Print words and Pos Tags
            for (Tree leaf : leaves) {
                Tree parent = leaf.parent(tree);
                System.out.print(leaf.label().value() + "-" + parent.label().value() + " ");
            }
            System.out.println();
        }
    }

    public String getBaseFormOfWord(String word){
        return morphology.stem(word);
    }
}
