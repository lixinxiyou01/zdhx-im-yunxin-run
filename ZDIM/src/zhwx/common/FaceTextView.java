package zhwx.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android on 2017/2/27.
 */

public class FaceTextView extends TextView {
    private CharSequence text;
    private Context context;

    public FaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public FaceTextView(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public FaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public CharSequence getText() {
        return text == null ? "" : text;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text;

        String cs = text.toString();
        if (cs.contains("[em_")) {
            for (int i = 1; i < 76; i++) {
                cs.replaceAll("[em_"+i+"]".toString(), "<img src='" + i +"'>".toString());
                if (!cs.contains("[em_")) {
                    break;
                }
            }
        }
        int i = 1;
        while (cs.contains("[em_")) {
            cs = cs.replaceAll("[em_"+i+"]".toString(), "<img src='" + i +"'>".toString());
            i++;
        }

        Spanned span = Html.fromHtml(cs, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = null;
                loadImageFromAsserts(source);
                return drawable;
            }
        }, null);
        super.setText(span, type);
    }

    public Drawable loadImageFromAsserts(String fileName) {
        try {
            InputStream is = getContext().getResources().getAssets().open("qqface/" + fileName + ".gif");
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

