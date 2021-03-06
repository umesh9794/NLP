package com.shc;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.English;
import org.languagetool.rules.Rule;



import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by uchaudh on 8/3/2015.
 */
public class SpellCheckAndCorrect {

    public static String hitMatch="";
    public static List<String> suggestions=null;

    public static void getCorrection(String args) throws IOException{
        hitMatch=new String();
        suggestions= new ArrayList<>();
        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());

        for (Rule rule : langTool.getAllRules()) {
            if (!rule.isDictionaryBasedSpellingRule()) {
                langTool.disableRule(rule.getId());
            }
        }
        //langTool.activateDefaultPatternRules();  -- only needed for LT 2.8 or earlier
//        List<Rule> activeRules= langTool.getAllActiveRules();
        List<RuleMatch> matches = langTool.check(args);

        for (RuleMatch match : matches) {
            List<String> suggestionList=match.getSuggestedReplacements();
            if( suggestionList.size()!=0) {
                hitMatch=suggestionList.get(0);
                if(suggestionList.size()<5)
                    suggestions.addAll(suggestionList);
                else
                    suggestions.addAll(suggestionList.subList(0,5));

                       }
            }

    }
}
