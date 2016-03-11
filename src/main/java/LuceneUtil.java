import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uchaudh on 8/4/2015.
 */
public class LuceneUtil {

    private LuceneUtil() {}
    private static List<String> tokens= new ArrayList< String>();

    public static List<String> removeStopWords(String textFile,CharArraySet stopWords ) throws Exception {
        TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_4_10_4, new StringReader(textFile.trim()));

        tokenStream = new StopFilter(Version.LUCENE_4_10_4, tokenStream, stopWords);
//        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            tokens.add(term);
//            sb.append(term + " ");
        }
        return tokens;
    }


    public  static List<String> tokenizeString(String textFile) throws IOException{

        EnglishAnalyzer ena=new EnglishAnalyzer(Version.LUCENE_4_10_4);

        TokenStream tokenStream=ena.tokenStream(textFile.trim(), new StringReader(textFile.trim()));


//        StringBuilder sb = new StringBuilder();
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            int startOffset = offsetAttribute.startOffset();
            int endOffset = offsetAttribute.endOffset();
            String term = charTermAttribute.toString();
            tokens.add(term);
//            sb.append(term + " ");
        }
        return tokens;
    }

}
