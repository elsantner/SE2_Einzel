package at.aau.elsantner.se2demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TEXT_OUTPUT = "text_output";

    private TCPClient client;

    // Bind requires ButterKnife
    // Disable "NonConstantResourceId" in build.gradle
    @BindView(R.id.editTextMNr)
    EditText txtMNr;

    @BindView(R.id.lblOutput)
    TextView lblOutput;

    @BindView(R.id.progressSpinner)
    ProgressBar progressSpinner;

    @BindString(R.string.server_url)
    String server_url;

    @BindString(R.string.server_port)
    String server_port;

    @OnClick(R.id.btnSend)
    void submit() {
        try {
            progressSpinner.setVisibility(View.VISIBLE);
            client.send(txtMNr.getText().toString())
                    .subscribeOn(Schedulers.io())               // execute "send()" in thread of unbounded thread pool (Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())  // execute "subscribe()" + methods of Observer on mainThread
                    .subscribe(new Observer<String>() {         // For RxJava see: https://proandroiddev.com/understanding-rxjava-subscribeon-and-observeon-744b0c6a41ea
                        @Override
                        public void onSubscribe(Disposable d) {
                            // nothing
                        }

                        @Override
                        public void onNext(String msg) {
                            Log.d("MainActivity", "Response: " + msg);
                            lblOutput.setText(msg);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("MainActivity", "Error sending message: ", e);
                            lblOutput.setText(String.format(getResources().getString(R.string.error_placeholder), e.getMessage()));
                        }

                        @Override
                        public void onComplete() {
                            progressSpinner.setVisibility(View.INVISIBLE);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initiate widget binding
        ButterKnife.bind(this);
        setupTCPClient();
    }

    private void setupTCPClient() {
        this.client = new TCPClient(server_url, Integer.parseInt(server_port));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save label text
        outState.putString(TEXT_OUTPUT, lblOutput.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Restore label text
        lblOutput.setText(savedInstanceState.getString(TEXT_OUTPUT));
        super.onRestoreInstanceState(savedInstanceState);
    }
}