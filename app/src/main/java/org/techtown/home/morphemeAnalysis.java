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

        import java.util.List;

public class morphemeAnalysis extends AppCompatActivity {

    private Button btn;
    TextView textView;
    EditText editText;

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

                List<String> partOfSpeech = analyzeResultList.getMorphesByTags("NP");

                for (String str : partOfSpeech) {
                    resultStr.append(str);

                }
                textView.setText(resultStr);
            }
        });

    }
    /*for (Token token : tokenList) {
                    System.out.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos());*/

}
