package com.shc;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import rita.RiWordNet;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by uchaudh on 8/2/2015.
 */
public class Lemmatize {

    protected StanfordCoreNLP pipeline;

    public  List<String> suggestion=null;
    public  List<String> synonymList=null;


    /**
     * public constructor to initialize Properties
     */
    public Lemmatize() {

        PrintStream err = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));

        suggestion=new ArrayList<String>();
        synonymList=new ArrayList<String>();
        // Create StanfordCoreNLP object properties, with POS tagging and lemmatization
        Properties props;
        props = new Properties();
//        props.put("annotators", "tokenize, ssplit, pos, lemma,ner, parse");
        props.put("annotators", "tokenize, ssplit, pos,lemma");

        /*
         * This is a pipeline that takes in a string and returns various analyzed linguistic forms.
         * The String is tokenized via a tokenizer (such as PTBTokenizerAnnotator),
         * and then other sequence model style annotation can be used to add things like lemmas,
         * POS tags, and named entities. These are returned as a list of CoreLabels.
         * Other analysis components build and store parse trees, dependency graphs, etc.
         *
         * This class is designed to apply multiple Annotators to an Annotation.
         * The idea is that you first build up the pipeline by adding Annotators,
         * and then you take the objects you wish to annotate and pass them in and
         * get in return a fully annotated object.
         *
         */
        this.pipeline = new StanfordCoreNLP(props);
    }

    /**
     * method to lemmatize input string and generate tokens (keywords)
     * @param documentText
     * @param stopWords
     * @return
     */
    public List<String> lemmatize(String documentText,Set<String> stopWords)
    {
        List<String> lemmas = new LinkedList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        lemmas.removeAll(stopWords);
        return lemmas;
    }

  public void applyNLP(String[] args)
  {
        try {
            PrintStream err = System.err;
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                }
            }));

            System.out.println("Input : "+ args[1]);
            //Stop words to be removed from generated tokens
            String[] synonyms=new String[5];
            Set<String> stopWords = new HashSet<String>();
            stopWords.add("a");
            stopWords.add("an");
            stopWords.add("are");
            stopWords.add("am");
            stopWords.add("I");
            stopWords.add("the");
            stopWords.add("do");
//        stopWords.add("not");
            stopWords.add("want");

            String text = args[1];
            Lemmatize slem = new Lemmatize();

            RiWordNet wordnet = new RiWordNet(args[0]);

            List<String>  lemmatizedOut=slem.lemmatize(text, stopWords);

                for (String word : lemmatizedOut) {
                    synonyms=new String[3];
                    SpellCheckAndCorrect.getCorrection(word);
                    if(!SpellCheckAndCorrect.hitMatch.equals("")) {
                        suggestion.addAll(SpellCheckAndCorrect.suggestions);
                    }
                    else {
                        if(suggestion.size()>0)
                            appendWordToExistingSuggestions(word);
                        else
                        suggestion.add(word);
                        synonyms = wordnet.getAllSynonyms(word, RiWordNet.NOUN, 5);
                        synonymList.addAll(Arrays.asList(synonyms));
                    }

                }
            System.setErr(err);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void appendWordToExistingSuggestions (String newWord )
    {
        for(int count=0; count<suggestion.size();count++)
        {
            String appended=suggestion.get(count)+"+"+newWord;
            suggestion.set(count,appended);
        }

    }

}
