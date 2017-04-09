import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by tcgogogo on 17/4/7.
 */
public class SpellCheckerHelper {

    public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final int CON = 1;
    public static int wordCount = 0;
    public Map<Map<String, String>, Double> preWordMap;
    public Map<String, Integer> wordSet = new HashMap<>();
    public Set<String> editDistance1WordSetCache;

    public Map<String, Integer> prepareChecker(String wordSample) throws IOException {
        preWordMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(wordSample));
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        String line;
        String pre = null;
        while ((line = br.readLine()) != null) {
            String[] words = line.split("[^a-zA-Z-.']");
            for (String word : words) {
                if (!word.equals("") && pattern.matcher(word).matches()) {
                    word = word.toLowerCase();
                    if (wordSet.get(word) == null) {
                        wordSet.put(word, 1);
                    } else {
                        wordSet.put(word, wordSet.get(word) + 1);
                    }
                    if (pre != null) {
                        Map<String, String> mp = new HashMap<>();
                        mp.put(pre, word);
                        if (preWordMap.get(mp) == null) {
                            preWordMap.put(mp, 1.0);
                        } else {
                            preWordMap.put(mp, preWordMap.get(mp) + 1.0);
                        }
                    }
                    pre = word;
                    wordCount++;
                }
            }
        }
        br.close();

        for (Map.Entry<Map<String, String>, Double> entry : preWordMap.entrySet()) {
            for (Map.Entry entry2 : entry.getKey().entrySet()) {
                entry.setValue(entry.getValue() / wordSet.get(entry2.getKey()) * 1000.0);
//                if (entry2.getKey().equals("today")) {
//                    System.out.println(entry2.getKey() + " " + entry2.getValue() + " = " + entry.getValue());
//                }
            }
        }
        return wordSet;
    }

    public List<String> getPredictWord(String pre, Set<String> editWordSet) {
        List<String> wordsList = new LinkedList<>(editWordSet);
        Collections.sort(wordsList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Map<String, String> mp1 = new HashMap<>();
                mp1.put(pre, o1);
                Map<String, String> mp2 = new HashMap<>();
                mp2.put(pre, o2);
                Double p1 = preWordMap.get(mp1);
                Double p2 = preWordMap.get(mp2);
                if (p1 != null && p2 != null) {
                    if (p2 > p1) {
                        return 1;
                    } else if (p2 < p1){
                        return -1;
                    } else {
                        return wordSet.get(o2).compareTo(wordSet.get(o1));
                    }
                } else if (p1 != null) {
                    return -1;
                } else if (p2 != null) {
                    return 1;
                }else {
                    return wordSet.get(o2).compareTo(wordSet.get(o1));
                }
            }
        });
        return wordsList.size() > CON ? wordsList.subList(0, CON) : wordsList;
    }

    public Set<String> buildEditDistance1WordSet(String inputWord) {
        Set<String> editDistance1WordSet = new HashSet<>();
        int len = inputWord.length();
        //delete one
        for (int i = 0; i < len; i ++) {
            editDistance1WordSet.add(inputWord.substring(0, i) + inputWord.substring(i + 1));
        }

        //swap closed two
        for (int i = 0; i < len - 1; i ++) {
            editDistance1WordSet.add(inputWord.substring(0, i) + inputWord.charAt(i + 1) + inputWord.charAt(i) + inputWord.substring(i + 2));
        }

        //change one
        for (int i = 0; i < len; i ++) {
            for (char ch : ALPHABET) {
                editDistance1WordSet.add(inputWord.substring(0, i) + ch + inputWord.substring(i + 1));
            }
        }

        //insert one
        for (int i = 0; i <= len; i ++) {
            for (char ch : ALPHABET) {
                editDistance1WordSet.add(inputWord.substring(0, i) + ch + inputWord.substring(i));
            }
        }
        editDistance1WordSetCache = editDistance1WordSet;
        return editDistance1WordSet;
    }

    public Set<String> buildEditDistance2WordSet() {
        Set<String> editDistance2WordSet = new HashSet<>();
        for (String editWord1 : editDistance1WordSetCache) {
            editDistance2WordSet.addAll(buildEditDistance1WordSet(editWord1));
        }
        return editDistance2WordSet;
    }
}
