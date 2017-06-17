package br.ufes.inf.hfilho.previsodotempo.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by helder on 17/06/17.
 */

public class RegexController {
    public static String extractGroup(String query, String regex, int groupID){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(query);
        if(m.find()){
            return m.group(groupID);
        }
        return null;
    }
}
