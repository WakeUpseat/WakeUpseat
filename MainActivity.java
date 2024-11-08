package com.user.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.net.URL;

public class MainActivity extends Activity {
    Button refresh_button;
    String subway = "숭실대입구(살피재)"; // 역 이름
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh_button = findViewById(R.id.refresh_button);

        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(subway);

            }
        });
    }

    private void run(String subway) {
        StrictMode.enableDefaults(); // 권장하지 않지만 예외 처리를 위해 사용합니다.

        TextView status1 = findViewById(R.id.result); // 파싱된 결과 확인용 텍스트뷰
        status1.setText("");

        boolean insubwayId = false, inupdnLine = false, inarvlMsg2 = false, inarvlMsg3 = false, inlstcarAt = false, intotal = false;
        String subwayId = null, updnLine = null, arvlMsg2 = null, arvlMsg3 = null, lstcarAt = null, total = null;

        String[] list_subwayId = new String[4], list_updnLine = new String[4], list_arvlMsg2 = new String[4], list_arvlMsg3 = new String[4], list_lstcarAt = new String[4];
        int list_len = 0;

        try {
            // API 호출 URL 설정, API 키는 getString으로 가져와야 함
            URL url = new URL("http://swopenAPI.seoul.go.kr/api/subway/"
                    + getString(R.string.SEOUL_Subway_API_KEY) + "/xml/realtimeStationArrival/0/5/" + subway);

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱을 시작합니다.");

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("subwayId")) {
                            insubwayId = true;
                        } else if (parser.getName().equals("updnLine")) {
                            inupdnLine = true;
                        } else if (parser.getName().equals("arvlMsg2")) {
                            inarvlMsg2 = true;
                        } else if (parser.getName().equals("arvlMsg3")) {
                            inarvlMsg3 = true;
                        } else if (parser.getName().equals("lstcarAt")) {
                            inlstcarAt = true;
                        } else if (parser.getName().equals("ㅇㅀㅁㅇㅎ")) {
                            status1.setText(status1.getText() + "에러");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (insubwayId) {
                            subwayId = parser.getText();
                            insubwayId = false;
                        } else if (inupdnLine) {
                            updnLine = parser.getText();
                            inupdnLine = false;
                        } else if (inarvlMsg2) {
                            arvlMsg2 = parser.getText();
                            inarvlMsg2 = false;
                        } else if (inarvlMsg3) {
                            arvlMsg3 = parser.getText();
                            inarvlMsg3 = false;
                        } else if (inlstcarAt) {
                            lstcarAt = parser.getText();
                            inlstcarAt = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("row")) {
                            list_subwayId[list_len] = subwayId;
                            list_updnLine[list_len] = updnLine;
                            list_arvlMsg2[list_len] = arvlMsg2;
                            list_arvlMsg3[list_len] = arvlMsg3;
                            list_lstcarAt[list_len++] = lstcarAt;
                            status1.setText(status1.getText() + "호선 : " + subwayId
                                    + "\n 상하행 구분 : " + updnLine
                                    + "\n 첫번째 도착정보 : " + arvlMsg2
                                    + "\n 두번째 도착정보 : " + arvlMsg3
                                    + "\n 막차여부 : " + lstcarAt + "\n");
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch (Exception e) {
            status1.setText("에러가 발생했습니다.");
            e.printStackTrace();
        }
    }
    private String[] setup(){
        System.out.println();
        return null;
    }
}