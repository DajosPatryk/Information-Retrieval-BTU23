package com.irsystem;

/**
 * Word stemming after the porter algorithm
 */
public class Stemmer {
    private String _word;

    public Stemmer(String word) {
        this._word = word.toLowerCase();
    }

    // Word Controllers
    public String getWord(){
        this.step1a();
        this.step1b();
        this.step1c();
        this.step2();
        this.step3();
        this.step4();
        this.step5a();
        this.step5b();

        return this._word;
    }
    public boolean isVowel(char c) {
        return "aeiou".indexOf(Character.toLowerCase(c)) != -1;
    }
    public boolean isConsonant(char c) {
        return !isVowel(c);
    }

    /**
     * \measure\
     * [C](VC){m}[V]
     * @return m
     */
    public int measure() {
        int m = 0;
        boolean foundV = false;

        for (char c : this._word.toCharArray()) {
            if (isVowel(c)) {
                foundV = true;
            } else if (foundV) {
                m++;
                foundV = false;
            }
        }
        return m;
    }

    /**
     * \rules\
     * *suffix
     * @param suffix
     * @return boolean
     */
    public boolean endsWith(String suffix) {
        return this._word.endsWith(suffix.toLowerCase());
    }

    /**
     * \rules\
     * *v*
     * @return boolean
     */
    public boolean containsVowel(){
        for(char c : this._word.toCharArray()){
            if(isVowel(c)) return true;
        }
        return false;
    }

    /**
     * \rules\
     * *d
     * @return boolean
     */
    public boolean endsInDoubleConsonant() {
        if (this._word.length() < 2) return false;

        char last = this._word.charAt(_word.length() - 1);
        char secondLast = this._word.charAt(_word.length() - 2);
        return last == secondLast && isConsonant(last);
    }

    /**
     * \rules\
     * *o
     * @return boolean
     */
    public boolean endsInCvc() {
        if (this._word.length() <= 3) return false;

        char last = this._word.charAt(this._word.length() - 1);
        char secondLast = this._word.charAt(this._word.length() - 2);
        char thirdLast = this._word.charAt(this._word.length() - 3);

        if("wxy".indexOf(last) != -1) return false;
        return isConsonant(thirdLast) && isVowel(secondLast) && isConsonant(last);
    }

    /**
     * \rules\
     * S1 -> S2
     * @param from S1
     * @param to S2
     */
    public void replaceSuffix(String from, String to) {
       if(from == "-1") this._word = this._word.substring(0, this._word.length() - 1);
       else if(from == "+1") this._word = this._word + to.toLowerCase();
       else this._word = this._word.replaceFirst(from.toLowerCase() + "$", to.toLowerCase());
    }

    // Stemming
    public void step1a() {
        if(endsWith("SSES")) replaceSuffix("SSES", "SS");
        else if(endsWith("IES")) replaceSuffix("IES", "I");
        else if(endsWith("SS")) replaceSuffix("SS", "SS");
        else if(endsWith("S")) replaceSuffix("S", "");
    }

    public void step1b(){
        if(measure() > 0 && endsWith("EED")) replaceSuffix("EED", "EE");
        else if(containsVowel() && endsWith("ED")){
            replaceSuffix("ED", "");
            if(endsWith("AT")) replaceSuffix("AT", "ATE");
            else if(endsWith("BL")) replaceSuffix("BL", "BLE");
            else if(endsWith("IZ")) replaceSuffix("IZ", "IZE");
            else if(endsInDoubleConsonant() && !(endsWith("L") || endsWith("S") || endsWith("Z"))) replaceSuffix("-1", "");
            else if(measure() == 1 && endsInCvc()) replaceSuffix("+1", "E");
        }
        else if(containsVowel() && endsWith("ING")){
            replaceSuffix("ING", "");
            if(endsWith("AT")) replaceSuffix("AT", "ATE");
            else if(endsWith("BL")) replaceSuffix("BL", "BLE");
            else if(endsWith("IZ")) replaceSuffix("IZ", "IZE");
            else if(endsInDoubleConsonant() && !(endsWith("L") || endsWith("S") || endsWith("Z"))) replaceSuffix("-1", "");
            else if(measure() == 1 && endsInCvc()) replaceSuffix("+1", "E");
        }
    }

    public void step1c(){
        if(containsVowel() && endsWith("Y")) replaceSuffix("Y", "I");
    }

    public void step2(){
        if(measure() > 0){
            if(endsWith("ATIONAL")) replaceSuffix("ATIONAL", "ATE");
            else if(endsWith("TIONAL")) replaceSuffix("TIONAL", "TION");
            else if(endsWith("ENCI")) replaceSuffix("ENCI", "ENCE");
            else if(endsWith("ANCI")) replaceSuffix("ANCI", "ANCE");
            else if(endsWith("IZER")) replaceSuffix("IZER", "IZE");
            else if(endsWith("ABLI")) replaceSuffix("ABLI", "ABLE");
            else if(endsWith("ALLI")) replaceSuffix("ALLI", "AL");
            else if(endsWith("ENTLI")) replaceSuffix("ENTLI", "ENT");
            else if(endsWith("ELI")) replaceSuffix("ELI", "E");
            else if(endsWith("OUSLI")) replaceSuffix("OUSLI", "OUS");
            else if(endsWith("IZATION")) replaceSuffix("IZATION", "IZE");
            else if(endsWith("ATION")) replaceSuffix("ATION", "ATE");
            else if(endsWith("ATOR")) replaceSuffix("ATOR", "ATE");
            else if(endsWith("ALISM")) replaceSuffix("ALISM", "AL");
            else if(endsWith("IVENESS")) replaceSuffix("IVENESS", "IVE");
            else if(endsWith("FULNESS")) replaceSuffix("FULNESS", "FUL");
            else if(endsWith("OUSNESS")) replaceSuffix("OUSNESS", "OUS");
            else if(endsWith("ALITI")) replaceSuffix("ALITI", "AL");
            else if(endsWith("IVITI")) replaceSuffix("IVITI", "IVE");
            else if(endsWith("BILITI")) replaceSuffix("BILITI", "BLE");
            else if(endsWith("XFLURTI")) replaceSuffix("XFLURTI", "XTI");
        }
    }

    public void step3(){
        if(measure() > 0){
            if(endsWith("ICATE")) replaceSuffix("ICATE", "IC");
            else if(endsWith("ATIVE")) replaceSuffix("ATIVE", "");
            else if(endsWith("ALIZE")) replaceSuffix("ALIZE", "AL");
            else if(endsWith("ICITI")) replaceSuffix("ICITI", "IC");
            else if(endsWith("ICAL")) replaceSuffix("ICAL", "IC");
            else if(endsWith("FUL")) replaceSuffix("FUL", "");
            else if(endsWith("NESS")) replaceSuffix("NESS", "");
        }
    }

    public void step4(){
        if(measure() > 1){
            if(endsWith("AL")) replaceSuffix("AL", "");
            else if(endsWith("ANCE")) replaceSuffix("ANCE", "");
            else if(endsWith("ENCE")) replaceSuffix("ENCE", "");
            else if(endsWith("ER")) replaceSuffix("ER", "");
            else if(endsWith("IC")) replaceSuffix("IC", "");
            else if(endsWith("ABLE")) replaceSuffix("ABLE", "");
            else if(endsWith("IBLE")) replaceSuffix("IBLE", "");
            else if(endsWith("ANT")) replaceSuffix("ANT", "");
            else if(endsWith("EMENT")) replaceSuffix("EMENT", "");
            else if(endsWith("MENT")) replaceSuffix("MENT", "");
            else if(endsWith("ENT")) replaceSuffix("ENT", "");
            else if(endsWith("ER")) replaceSuffix("ER", "");
            else if(endsWith("TION")) replaceSuffix("ION", "");
            else if(endsWith("SION")) replaceSuffix("ION", "");
            else if(endsWith("OU")) replaceSuffix("OU", "");
            else if(endsWith("ISM")) replaceSuffix("ISM", "");
            else if(endsWith("ATE")) replaceSuffix("ATE", "");
            else if(endsWith("ITI")) replaceSuffix("ITI", "");
            else if(endsWith("OUS")) replaceSuffix("OUS", "");
            else if(endsWith("IVE")) replaceSuffix("IVE", "");
            else if(endsWith("IZE")) replaceSuffix("IZE", "");

        }
    }

    public void step5a(){
        if(measure() > 1 && endsWith("E")) replaceSuffix("E", "");
        else if(measure() == 1 && !(endsInCvc()) && endsWith("E")) replaceSuffix("E", "");
    }

    public void step5b(){
        if(measure() > 1 && endsInDoubleConsonant() && endsWith("L")) replaceSuffix("-1", "");
    }
}
