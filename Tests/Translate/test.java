package Translate;

import com.rmtheis.yandtran.detect.Detect;
import com.rmtheis.yandtran.language.Language;
import com.rmtheis.yandtran.translate.Translate;


class test{
    public static void main(String[] args) throws Exception {
        Translate.setKey("trnsl.1.1.20150924T011621Z.e06050bb431b7175.e5452b78ee8d11e4b736035e5f99f2831a57d0e2");

        String translatedText = Translate.execute("watashi wa potato desu", Language.ENGLISH, Language.JAPANESE);
        Language DetectedLang = Detect.execute("わたしはポテトです");

        System.out.println(translatedText);
        System.out.print(DetectedLang.toString());
    }
}