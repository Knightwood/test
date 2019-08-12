package android.crystal.browserii;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class Search_bar_Activity extends AppCompatActivity {
private static final String SEARCH_TEXT="search_text";
    static String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar_);
        search();

    }

    private void search(){

        final EditText editText = findViewById(R.id.search_bar);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    text = editText.getText().toString();
                    if(text.isEmpty()){
                        return false;
                    }else if(text.startsWith("http://")){
                        //sdfsafasf
                        text="http://"+text;
                        Intent intent = new Intent();
                        intent.putExtra("result",text);
                        Search_bar_Activity.this.setResult(RESULT_OK,intent);
                        finish();

                    }else if(text.startsWith("https://")){
                        text="https://"+text;
                        Intent intent = new Intent();
                        intent.putExtra("result",text);
                        Search_bar_Activity.this.setResult(RESULT_OK,intent);
                        finish();}
                    Intent intent = new Intent();
                    intent.putExtra("result",text);
                    Search_bar_Activity.this.setResult(RESULT_OK,intent);
                    Search_bar_Activity.this.finish();
                }
                return false;
            }
        });
    }
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, Search_bar_Activity.class);
        return intent;
    }
}
