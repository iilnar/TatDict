package com.example.ilnarsabirzyanov.tatdict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    ArrayList<DictionaryRecord> a = new ArrayList<>();
    static private Map<Character, Integer> alphabet;
    enum State {
        RUNNING, DONE;
    }

    public State state;

    Dictionary() {
        if (alphabet == null) {
            alphabet = new HashMap<>();
            alphabet.put(' ', -1);
            alphabet.put('-', -1);
            alphabet.put('а', 0);
            alphabet.put('А', 0);
            alphabet.put('ә', 1);
            alphabet.put('Ә', 1);
            alphabet.put('б', 2);
            alphabet.put('Б', 2);
            alphabet.put('в', 3);
            alphabet.put('В', 3);
            alphabet.put('г', 4);
            alphabet.put('Г', 4);
            alphabet.put('д', 5);
            alphabet.put('Д', 5);
            alphabet.put('е', 6);
            alphabet.put('Е', 6);
            alphabet.put('ё', 7);
            alphabet.put('Ё', 7);
            alphabet.put('ж', 8);
            alphabet.put('Ж', 8);
            alphabet.put('җ', 9);
            alphabet.put('Җ', 9);
            alphabet.put('з', 10);
            alphabet.put('З', 10);
            alphabet.put('и', 11);
            alphabet.put('И', 11);
            alphabet.put('й', 12);
            alphabet.put('Й', 12);
            alphabet.put('к', 13);
            alphabet.put('К', 13);
            alphabet.put('л', 14);
            alphabet.put('Л', 14);
            alphabet.put('м', 15);
            alphabet.put('М', 15);
            alphabet.put('н', 16);
            alphabet.put('Н', 16);
            alphabet.put('ң', 17);
            alphabet.put('Ң', 17);
            alphabet.put('о', 18);
            alphabet.put('О', 18);
            alphabet.put('ө', 19);
            alphabet.put('Ө', 19);
            alphabet.put('п', 20);
            alphabet.put('П', 20);
            alphabet.put('р', 21);
            alphabet.put('Р', 21);
            alphabet.put('с', 22);
            alphabet.put('С', 22);
            alphabet.put('т', 23);
            alphabet.put('Т', 23);
            alphabet.put('у', 24);
            alphabet.put('У', 24);
            alphabet.put('ү', 25);
            alphabet.put('Ү', 25);
            alphabet.put('ф', 26);
            alphabet.put('Ф', 26);
            alphabet.put('х', 27);
            alphabet.put('Х', 27);
            alphabet.put('һ', 28);
            alphabet.put('Һ', 28);
            alphabet.put('ц', 29);
            alphabet.put('Ц', 29);
            alphabet.put('ч', 30);
            alphabet.put('Ч', 30);
            alphabet.put('ш', 31);
            alphabet.put('Ш', 31);
            alphabet.put('щ', 32);
            alphabet.put('Щ', 32);
            alphabet.put('ъ', 33);
            alphabet.put('Ъ', 33);
            alphabet.put('ы', 34);
            alphabet.put('Ы', 34);
            alphabet.put('ь', 35);
            alphabet.put('Ь', 35);
            alphabet.put('э', 36);
            alphabet.put('Э', 36);
            alphabet.put('ю', 37);
            alphabet.put('Ю', 37);
            alphabet.put('я', 38);
            alphabet.put('Я', 38);
        }
    }

    public boolean readDump(File dumpFile) throws IOException {
        state = State.RUNNING;
        if (!dumpFile.exists() || !dumpFile.canRead()) {
            // TODO write to log that can't write
            return false;
        }
        a.clear();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dumpFile), "UTF-8"));
        String word, translation;
        while ((word = br.readLine()) != null && (translation = br.readLine()) != null) {
            a.add(new DictionaryRecord(word, translation));
        }
        br.close();
        state = State.DONE;
        return true;
    }

    public void addWord(String word, String translation) {
        a.add(a.size(), new DictionaryRecord(word, translation));
    }

    private int compare(String a, String b) {
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            int c1 = -1, c2 = -1;
            if (alphabet.containsKey(a.charAt(i))) {
                c1 = alphabet.get(a.charAt(i));
            }
            if (alphabet.containsKey(b.charAt(i))) {
                c2 = alphabet.get(b.charAt(i));
            }
            if (c1 < c2) {
                return -1;
            } else if (c1 > c2) {
                return 1;
            }
        }
        if (a.length() < b.length()) {
            return -1;
        } else if (a.length() > b.length()) {
            return 1;
        }
        return 0;
    }

    private int binarySearch(String prefix, int comp) {
        int l = -1, r = a.size(), m, len = prefix.length();
        while (r - l > 1) {
            m = l + (r - l) / 2;
            if (compare(a.get(m).word.substring(0, Math.min(len, a.get(m).word.length())), prefix) >= comp) {
                r = m;
            } else {
                l = m;
            }
        }
        return r;
    }

    public ArrayList<DictionaryRecord> search(String prefix) {
        ArrayList<DictionaryRecord> res = new ArrayList<>();
        int l = binarySearch(prefix, 0), r = binarySearch(prefix, 1);
        for (int i = l; i < r; i++) {
            res.add(a.get(i));
        }
        return res;
    }

    static int editDistance(String a, String b) {
		int n = a.length() + 1;
		int m = b.length() + 1;
        if (n > m) {
            return editDistance(b, a);
        }
		int k = 2;
		int[] cost = new int[m];
		int[] newCost = new int[m];
		for (int i = 0; i < m; i++) {
			cost[i] = i;
			newCost[i] = Integer.MAX_VALUE / 4;
		}
		for (int j = 1; j < n; j++) {
	    	newCost[0] = j;
	    	for (int i = Math.max(1, j - k); i < Math.min(m, j + k + 1); i++) {
				if (a.charAt(j - 1) == b.charAt(i - 1)) {
					newCost[i] = cost[i - 1];
				} else {
					newCost[i] = cost[i - 1] + 1;
				}
				newCost[i] = Math.min(newCost[i], cost[i] + 1);
				newCost[i] = Math.min(newCost[i], newCost[i - 1] + 1);
	    	}
	    	int[] t = cost;
	    	cost = newCost;
	    	newCost = t;
		}
		return cost[m - 1];
    }


    private int levenshteinDistance(String a, String b) {
        int k = 2;
        if (a.length() > b.length()) {
            String t = b;
            b = a;
            a = t;
        }
        a = "." + a;
        b = "." + b;
        int n = a.length(), m = b.length();
        int[] d = new int[m];
        int[] dOld;
        for (int i = 0; i < m; i++) {
            d[i] = i;
        }
        for (int i = 1; i < n; i++) {
            dOld = d;
            d = new int[m];
            d[0] = i;
            for (int j = 1; j < m; j++) {
                d[j] = dOld[j - 1];
                if (a.charAt(i) != b.charAt(j)) {
                    d[j]++;
                }
                d[j] = Math.min(d[j], Math.min(d[j - 1] + 1, dOld[j] + 1));
            }
        }
        return d[m - 1];
    }

    public ArrayList<DictionaryRecord> deepSearch(String word) {
        ArrayList<DictionaryRecord> res = new ArrayList<>();
        for (DictionaryRecord dr : a) {
            if (editDistance(word, dr.word) <= 2) {
                res.add(dr);
            }
        }
        return res;
    }
}
