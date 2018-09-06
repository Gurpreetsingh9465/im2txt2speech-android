package com.example.amans.myeyes;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class Vocabulary {
    private HashMap<Integer,String> vec2word;
    private HashMap<String,Integer> word2vec;
    public final String start_token="<S>";
    public final String end_token="</S>";
    public final String unk_token="<UNK>";
    Vocabulary(AssetManager getAssets)
    {
        vec2word = new HashMap<Integer, String>();
        word2vec = new HashMap<String, Integer>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets.open("dictionary.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                String[] l = mLine.split("    ");
                String word = l[0];
                Integer vec = Integer.parseInt(l[1]);
                vec2word.put(vec,word);
                word2vec.put(word,vec);
            }
        } catch (IOException e) {
            Log.i("inference",e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.i("message",e.toString());
                }
            }
        }
    }
    public Integer getToken(String word)
    {
        return word2vec.get(word);

    }
    public String getWord(Integer token)
    {
        return vec2word.get(token);
    }
    public HashMap<Integer,String> getVec2word()
    {
        return  vec2word;
    }
    public HashMap<String, Integer> getWord2vec() {
        return word2vec;
    }
}
