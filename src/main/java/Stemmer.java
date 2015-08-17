import com.shc.Lemmatize;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.FileInputStream;

import rita.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by uchaudh on 8/3/2015.
 */
public class Stemmer {

    public static void main(String[] args) throws IOException, net.didion.jwnl.JWNLException {


        Lemmatize lt=new Lemmatize();
        lt.applyNLP(new String[]{"C:\\Users\\uchaudh\\Downloads\\WordNet-3.0", "hndbgs"});

        List<String> sugg=lt.suggestion;
        List<String>syn=lt.synonymList;

        Lemmatize lt1=new Lemmatize();
        lt1.applyNLP(new String[]{"C:\\Users\\uchaudh\\Downloads\\WordNet-3.0", "shoes"});

        List<String> sugg1=lt1.suggestion;
        List<String>syn1=lt1.synonymList;

        RiWordNet wordnet = new RiWordNet("C:\\Users\\uchaudh\\Downloads\\WordNet-3.0");
        String word = "refrigator";
        String[] synonyms = wordnet.getAllSynonyms(word, "v");

        JWNL.initialize(new FileInputStream("C:\\Users\\uchaudh\\NLP\\src\\main\\resources\\properties.xml"));
        final Dictionary dictionary = Dictionary.getInstance();

        IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, "prom");

        Synset[] senses = indexWord.getSenses();

        for (Synset set : senses) {
            System.out.println(indexWord + ": " + set.getGloss());
        }
    }
}
