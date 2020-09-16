package org.techtown.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class morphemeAnalysis extends AppCompatActivity {

    private Button btn;
    TextView textView;
    EditText editText;
    HashMap<String, Integer> tagList = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morpheme_analysis);

        btn = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.sample_EditText);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //형태소 분석
                Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
                StringBuilder resultStr = new StringBuilder();

                String strToAnalyze = new String(editText.getText().toString());

                KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);

                List<Token> tokenList = analyzeResultList.getTokenList();

                List<String> partOfSpeech = analyzeResultList.getMorphesByTags("NNG");

                for (String str : partOfSpeech) {
                    Integer freq = tagList.get(str);
                    tagList.put(str, (freq == null) ? 1 : freq + 1);  //빈도수 체크
                }

                List<Map.Entry<String, Integer>> list = new LinkedList<>(tagList.entrySet());

                Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        int comparision = (o1.getValue() - o2.getValue()) * -1;
                        return comparision == 0 ? o1.getKey().compareTo(o2.getKey()) : comparision;
                    }
                });

                // 순서유지를 위해 LinkedHashMap을 사용
                Map<String, Integer> sortedMap = new LinkedHashMap<>();
                for (Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext(); ) {
                    Map.Entry<String, Integer> entry = iter.next();
                    sortedMap.put(entry.getKey(), entry.getValue());
                }

                System.out.println(sortedMap);

                tagList.clear();
                sortedMap.clear();

            }


        });
    }

}
    /*for (Token token : tokenList) {
                    System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos());*/


