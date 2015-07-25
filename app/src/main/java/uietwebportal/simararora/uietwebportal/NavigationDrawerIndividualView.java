package uietwebportal.simararora.uietwebportal;

import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerIndividualView {
    private TextView textView;
    private ImageView imageView;

    public NavigationDrawerIndividualView(TextView textView, ImageView imageView) {
        this.textView = textView;
        this.imageView = imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
