package com.example.ilnarsabirzyanov.tatdict;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

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
}
