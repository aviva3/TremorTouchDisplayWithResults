package idoelad.finalproject.tremortouch.displaywithresults;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import br.com.thinkti.android.filechooser.FileChooser;


public class MainActivity extends Activity {
	public String LOG_TAG = "Main activity";

	/** Called when the activity is first created. */
	private static final int FILE_CHOOSER = 11;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFileChooser();
    }
    
    private void loadFileChooser(){
    	Intent intent = new Intent(this, FileChooser.class);
    	ArrayList<String> extensions = new ArrayList<String>();
    	extensions.add(".csv");
    	intent.putStringArrayListExtra("filterFileExtension", extensions);
    	startActivityForResult(intent, FILE_CHOOSER);
    }

}
