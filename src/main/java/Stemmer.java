import com.shc.Lemmatize;
import com.shc.Lemmatize_old;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by uchaudh on 8/3/2015.
 */
public class Stemmer {

    public static void main(String[] args) throws IOException, net.didion.jwnl.JWNLException {

        String input="sheo";
        Lemmatize lt=new Lemmatize();
        lt.applyNLP(new String[]{"C:\\Users\\uchaudh\\Downloads\\WordNet-3.0", input});

        List<String> sugg= new CopyOnWriteArrayList(lt.suggestion);
        List<String>syn=new CopyOnWriteArrayList (lt.synonymList);





        Set<String> set2= new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set2.addAll(sugg);

        Set<String> concurrentSet= new CopyOnWriteArraySet(set2);
        Set<String> synSet= new LinkedHashSet<>();

        for(String s:concurrentSet)
        {
            char[] inputCharArray= input.toCharArray();
            char[] repCharArray= s.toCharArray();
            Arrays.sort(inputCharArray);
            Arrays.sort(repCharArray);

            if( Arrays.equals(inputCharArray,repCharArray))
            {
                synSet.add(s);
                concurrentSet.remove(s);
            }
        }

        synSet.addAll(concurrentSet);



        System.out.println("Suggestions for misspelled word: "+ synSet  );
        System.out.println("Synonyms for Word: "+ syn );


        for(String searchable : synSet) {

            if(searchable.substring(0,1).equalsIgnoreCase(input.substring(0,1))) {
                System.out.println("Querying SOLR for " +searchable);
                JSONObject jo = (JSONObject) new JSONTokener(IOUtils.toString(new URL("http://solrx311p.qa.ch3.s.com:8580/solr/core1/select?q=" + searchable + "&wt=json&rows=10").openStream())).nextValue();
//                JSONObject jo = (JSONObject) new JSONTokener(IOUtils.toString(new URL("http://solrx311p.qa.ch3.s.com:8580/solr/core1/select?q=scarves&wt=json&indent=true&rows=10").openStream())).nextValue();
                JSONArray ja = (JSONArray) jo.getJSONObject("response").get("docs");
                if (ja.length() != 0) {
                    Object resp0 = ja.getJSONObject(0).get("name");
                    Object resp1 = ja.getJSONObject(1).get("name");
//                    Object resp2 = ja.getJSONObject(2).get("name");
//                    Object resp3 = ja.getJSONObject(3).get("name");
                    System.out.println("SOLR Response :");
                    System.out.println("1: "+ resp0 +"\n2: "+resp1+"\n");
                }
                else
                    System.out.println("No item found for literal: "+searchable);
            }

        }
//        Object arr= jo.get("response");


        JWNL.initialize(new FileInputStream("C:\\Users\\uchaudh\\NLP\\src\\main\\resources\\properties.xml"));
        final Dictionary dictionary = Dictionary.getInstance();

        IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, "prom");

        Synset[] senses = indexWord.getSenses();

        for (Synset set : senses) {
            System.out.println(indexWord + ": " + set.getGloss());
        }
    }



    public static void callGoogleService() throws IOException
    {
        URL url = new URL("http://suggestqueries.google.com/complete/search?output=toolbar&hl=en&q=appliancesrefrigeratorssidebyside");
        String query = "search?output=toolbar&hl=en&q=lether%20jaket";

        //make connection
        URLConnection urlc = url.openConnection();


        //use post mode
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);

        //send query
//        PrintStream ps = new PrintStream(urlc.getOutputStream());
//        ps.print(query);
//        ps.close();

        //get result
        StringBuilder sb=new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String l = null;
        while ((l=br.readLine())!=null) {
            System.out.println(l);
            sb.append(l);
        }
        System.out.println(sb);
        br.close();
    }
}
