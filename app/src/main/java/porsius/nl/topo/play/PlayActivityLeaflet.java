package porsius.nl.topo.play;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import porsius.nl.topo.R;
import porsius.nl.topo.data.MMap;
import porsius.nl.topo.data.PlayMapInterface;

public class PlayActivityLeaflet extends ActionBarActivity {

    public static TextView questionView;
    private PlayMapInterface playMapInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaflet);
        questionView = (TextView) findViewById(R.id.questionView);

        WebView webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        MMap map = getIntent().getParcelableExtra("MAP");
        playMapInterface = new PlayMapInterface(this, map, new Handler());
        webview.addJavascriptInterface(playMapInterface, "Android");

        String url = ("file:///android_asset/leafletPlay.html");
        webview.setWebViewClient(new MyWebViewClient(webview, url));

        setTitle(map.getName());
    }

    public static void setQuestionViewZoomIn(String text){

        Animation zoomIn = AnimationUtils.loadAnimation(questionView.getContext(), R.anim.zoom_in);

        questionView.startAnimation(zoomIn);
        questionView.setTextColor(Color.parseColor("#ff009a00"));
        questionView.setText(text);

    }
    public static void setQuestionViewText(String text){

        Animation slideInRight = AnimationUtils.loadAnimation(questionView.getContext(), R.anim.slide_in_right);

        questionView.startAnimation(slideInRight);
        questionView.setText(text);
        questionView.setTextColor(Color.BLACK);
    }

    private class MyWebViewClient extends WebViewClient {

        private WebView view;
        private String url;

        public MyWebViewClient(WebView view, String url)
        {
            this.view=view;
            this.url=url;

            view.loadUrl(url);
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {

            questionView.setText(playMapInterface.getQuestion());
        }


    }

}
