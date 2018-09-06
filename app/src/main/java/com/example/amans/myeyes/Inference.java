package com.example.amans.myeyes;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class Inference {
    private final int res = 346;
    private Vocabulary vocab;
    private final String feed_image = "convert_image/Cast";
    private final String[] conv_passed = {"lstm/initial_state"};
    private final String input_feed = "input_feed:0";
    private final String lstmStateFeed = "lstm/state_feed";
    private final int max_len = 20;
    private TensorFlowInferenceInterface inference;
    private final String lstmState  = "lstm/state";
    private final String softmax = "softmax";
    private int[] intValues;
    private final String TAG = "inference";
    Inference(AssetManager getAssets)
    {
         intValues = new int[res * res];
         inference = new TensorFlowInferenceInterface(getAssets, "optimized.pb");
         vocab = new Vocabulary(getAssets);
    }
    public String decodeImage(Bitmap image)
    {
        float[] results;
        String discription = "";
        try
        {
            results = imagePass(image);
            Integer final_token = vocab.getToken(vocab.end_token);
            Integer cur_token = vocab.getToken(vocab.start_token);
            for (int traverse = 0;traverse<max_len && cur_token != final_token;traverse++)
            {
                long token[] = {cur_token};
                inference.feed(input_feed,token,1);
                inference.feed(lstmStateFeed,results,1,1024);
                String[] s = {softmax,lstmState};
                inference.run(s);
                float[] softmaxResult = new float[12000];
                inference.fetch(softmax,softmaxResult);
                inference.fetch(lstmState,results);
                int maxIndex = 0;
                for (int i = 0; i < softmaxResult.length; i++) {
                    float newnumber = softmaxResult[i];
                    if ((newnumber > softmaxResult[maxIndex])) {
                        maxIndex = i;
                    }
                }
                cur_token = maxIndex;
                if (cur_token != final_token)
                {
                    discription+=vocab.getWord(maxIndex) + " ";
                }
            }
        }
        catch (Exception e)
        {
            Log.i(TAG,e.toString());
        }
        return discription;
    }

    private float[] imagePass(Bitmap image)
    {
        float[] results = new float[1024];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        float[] floatValues = new float[image.getWidth()*image.getHeight()*3];
        int index = 0;
        for (int i = 0;i<image.getWidth();i++)
        {
            for (int j=0;j<image.getHeight();j++)
            {
                int pixel = image.getPixel(i,j);
                Integer r = Color.red(pixel);
                Integer g = Color.green(pixel) ;
                Integer b = Color.blue(pixel) ;
                floatValues[index] = (float) r;
                floatValues[index + 1] = (float) g;
                floatValues[index+2] = (float) b ;
                index+=3;
            }
        }
        inference.feed(feed_image, floatValues,  image.getWidth(), image.getHeight(), 3);
        inference.run(conv_passed);
        inference.fetch(conv_passed[0],results);
        return results;
    }
}
