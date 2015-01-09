package porsius.nl.topo.data;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by linda on 03/11/14.
 */
public class QuestionView extends TextView {

    public  TextView view;
    private String text;

    CustmViewListener custmViewListener;

    public CustmViewListener getCustmViewListener() {
        return custmViewListener;
    }

    public void setCustmViewListener(CustmViewListener custmViewListener) {
        this.custmViewListener = custmViewListener;
    }

    public QuestionView(Context context) {
        super(context);
    }

    public QuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onDraw(Canvas canvas) {

     this.draw(canvas, text);

    }

    public void draw(Canvas canvas, String text)
    {
        super.onDraw(canvas);

        if (getCustmViewListener() != null) {
            getCustmViewListener().onUpdateValue(text);
        }
    }
    public void newText(String text)
    {
        this.text=text;

    }
    public interface CustmViewListener {
        void onUpdateValue(String updatedValue);
    }
}
