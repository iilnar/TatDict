package com.example.ilnarsabirzyanov.tatdict;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Dictionary {
    ArrayList<DictionaryRecord> a = new ArrayList<>();

    public boolean readDump(File dumpFile) throws IOException {
        if (!dumpFile.exists() || !dumpFile.canRead()) {
            // TODO write to log that can't write
            return false;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dumpFile), "UTF-8"));
        String word, translation;
        while ((word = br.readLine()) != null && (translation = br.readLine()) != null) {
            a.add(new DictionaryRecord(word, translation));
        }
//        FileInputStream file = new FileInputStream(dumpFile);
//        DataInputStream input = new DataInputStream(file);
//        a.clear();
//        int n = input.readInt();
//        for (int i = 0; i < n; i++) {
//            String word = input.readUTF();
//            String translation = input.readUTF();
//            a.add(new DictionaryRecord(word, translation));
//        }
        return true;
    }

    public void dump() throws IOException {
        File dumpFile = new File(new File(Util.rootFolder), "dump.file.tmp");
        if (!dumpFile.exists() || !dumpFile.canWrite()) {
            // TODO write to log that can't write
            return;
        }
        FileOutputStream file = new FileOutputStream(dumpFile);
        DataOutputStream output = new DataOutputStream(file);
        output.writeInt(a.size());
        for (DictionaryRecord e : a) {
            output.writeUTF(e.word);
            output.writeUTF(e.translation);
        }
        output.close();
        dumpFile.renameTo(new File(Util.rootFolder, "dump.file"));
    }

    public void addWord(String word, String translation) {
        a.add(a.size(), new DictionaryRecord(word, translation));
    }

    private int binarySearch(String prefix, int comp) {
        int l = 0, r = a.size(), m, len = prefix.length();
        while (r - l > 1) {
            m = l + (r - l) / 2;
            if (a.get(m).word.substring(0, Math.min(len, a.get(m).word.length())).compareTo(prefix) >= comp) {
                r = m;
            } else {
                l = m;
            }
        }
        return r;
    }

    public ArrayList<DictionaryRecord> search(String prefix) {
        int l = binarySearch(prefix, 0), r = binarySearch(prefix, 1);
        ArrayList<DictionaryRecord> res = new ArrayList<>();
        for (int i = l; i < r; i++) {
            res.add(a.get(i));
        }
        return res;
    }
}
