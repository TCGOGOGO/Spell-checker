import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by tcgogogo on 16/10/16.
 */
public class SpellChecker {

    public Map<String, Integer> wordSet;
    public Set<String> dictionary;

    public void start() throws IOException {

        SpellCheckerHelper helper = new SpellCheckerHelper();
        long startTime = System.currentTimeMillis();
        System.out.println("please wait ...");

        wordSet = helper.prepareChecker("eng.txt");

        System.out.println("preparation done, cost time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " second(s)");

        dictionary = wordSet.keySet();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String sentence;
        while ((sentence = input.readLine()) != null) {
            sentence = sentence.trim().toLowerCase();
            if ("bye".equals(sentence)) {
                break;
            }
            boolean flag = false;
            StringBuffer predictSentence = new StringBuffer("");
            String[] inputWords = sentence.split(" ");
            String predictWord = "";
            for (String inputWord : inputWords) {
                if (dictionary.contains(inputWord)) {
                    predictSentence.append(inputWord);
                    predictSentence.append(" ");
                    predictWord = inputWord;
                } else {
                    Set<String> editWordSet = helper.buildEditDistance1WordSet(inputWord);
                    editWordSet.retainAll(dictionary); //获取editWordSet和dictionary的交集
                    if (editWordSet.isEmpty()) {
                        editWordSet = helper.buildEditDistance2WordSet();
                        editWordSet.retainAll(dictionary);
                        if (editWordSet.isEmpty()) {
                            predictSentence.append(inputWord);
                            predictSentence.append(" ");
                            predictWord = inputWord;
                            continue;
                        }
                    }
                    List<String> predictWords = helper.getPredictWord(predictWord, editWordSet);
                    //predictSentence.append(predictWords.get(0));
                    for (int i = 0; i < predictWords.size(); i ++) {
                        if (i != 0) {
                            predictSentence.append("/");
                        }
                        predictSentence.append(predictWords.get(i));
                    }
                    predictSentence.append(" ");
                    predictWord = predictWords.get(0);
                    flag = true;
                }
            }
            if (flag) {
                System.out.println("Do you mean \"" + predictSentence.substring(0, predictSentence.length() - 1) + "\" ?");
            }
            else {
                System.out.println(sentence);
            }
            // System.out.println("wordCount = " + wordCount);
        }
    }
}