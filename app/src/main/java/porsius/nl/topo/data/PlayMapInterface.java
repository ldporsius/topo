package porsius.nl.topo.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import porsius.nl.topo.R;
import porsius.nl.topo.play.PlayActivityLeaflet;

/**
 * Created by linda on 01/11/14.
 */
public class PlayMapInterface {
    Context context;
    MMap map;
    TextView questionView;
    List<MMapLoc> list;
    Handler handler;

    /**
     * Instantiate the interface and set the context
     */
    public PlayMapInterface(Context c) {
        this(c, null, null);
    }

    public PlayMapInterface(Context c, MMap map, Handler h) {
        this.context = c;
        this.map = map;
        this.handler = h;

        MapLocDatasource mapLocDatasource = new MapLocDatasource(context);
        mapLocDatasource.open();

        if(map!=null) {
            list = mapLocDatasource.getAllLocs(map.getId());
        }

        mapLocDatasource.close();
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public Drawable getDrawable()
    {

        return context.getResources().getDrawable(R.drawable.marker_green);
    }

    @JavascriptInterface
    public String getMark()
    {
        String result = "";

        for(int i=0;i<list.size();i++){
            double lat = list.get(i).getLat();
            double lng = list.get(i).getLng();

            if(lat!=0 && lng!=0)
                result = result + lat + "," + lng + "|";
        }
        while(result.endsWith("|"))
        {
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public int isAnswerCorrect(int index)
    {
        String name = list.get(index).getName();
        String question = String.valueOf(PlayActivityLeaflet.questionView.getText());
        if(name==null || question==null)
            return 0;
        if(name.equalsIgnoreCase(question))
        {

            return 1;
        }

        return 0;
    }
    @JavascriptInterface
    public void showName(int index)
    {
        Toast.makeText(context, list.get(index).getName(), Toast.LENGTH_SHORT).show();
        if(isAnswerCorrect(index)==1) {
            list.get(index).setAnswered(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views

                    PlayActivityLeaflet.setQuestionViewZoomIn("Goed!");
                }

            });

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                    String question = getQuestion() != null ? getQuestion() : "Alles goed!";
                    PlayActivityLeaflet.setQuestionViewText(question);
                }

            }, 2000);
        }
    }
    private int getNumberAnswered()
    {
        int number = 0;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getAnswered())
                number++;
        }
        return number;
    }
    public String getQuestion()
    {
        int min = 0;
        int max = list.size();
        String question = null;
        boolean answered;
        int index = 0;

        if(list.size()> getNumberAnswered()) {
            Random random = new Random();
            index = random.nextInt(max - min) + min;
            while (list.get(index).getAnswered()) {
                index = random.nextInt(max - min) + min;
            }

            question = list.get(index).getName();

        }

        return question;
    }
}