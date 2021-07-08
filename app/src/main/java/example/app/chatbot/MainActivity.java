package example.app.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {


    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecogIntent;
    private  String keeper ="";



    private RecyclerView chatsRv;
    private EditText usermsgedt;
    private FloatingActionButton sendmsgfab;

    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private final String insta = "open instagram";
    private final String settings = "open settings";
    private final String Reminder = "remind me";
    private final String Notify = "notify me";
    private ArrayList<ChatsModel> chatsModelArrayList;
    private chatRvAdapter chatRvAdapter;
    private int notificationId = 1;
    private String getedittext;
    int gethr, getmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoicecmdPermission();

        chatsRv = findViewById(R.id.idRVchats);
        usermsgedt = findViewById(R.id.idEdtMessagew);
        sendmsgfab = findViewById(R.id.idfabsend);

        chatsModelArrayList = new ArrayList<>();
        chatRvAdapter = new chatRvAdapter(chatsModelArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRv.setLayoutManager(manager);
        chatsRv.setAdapter(chatRvAdapter);

        parentRelativeLayout = findViewById(R.id.parentrelative);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecogIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchFound != null)
                {
                    keeper = matchFound.get(0);
                    Toast.makeText(MainActivity.this,"cmd"+keeper,Toast.LENGTH_SHORT).show();
                    usermsgedt.setText(keeper);
                    getResponse(usermsgedt.getText().toString());
                    usermsgedt.setText("");
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });



        sendmsgfab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecogIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }

        });


        sendmsgfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usermsgedt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"please enter your msg",Toast.LENGTH_SHORT).show();
                    return;
                }

                final String a = usermsgedt.getText().toString();
                if(a.equals(insta)){
//                    getResponse(usermsgedt.getText().toString());
//                    usermsgedt.setText("");
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");

                    if(launchIntent != null){
                        startActivity(launchIntent);
                    }else {
                        Toast.makeText(MainActivity.this,"There is no such package",Toast.LENGTH_SHORT).show();
                    }
//                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                }
                if(a.equals(settings)){
//                    getResponse(usermsgedt.getText().toString());
//                    usermsgedt.setText("");

                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                }
                if(a.equals(Reminder) || a.equals(Notify)){
                    openDialog();

                }
                getResponse(usermsgedt.getText().toString());
                usermsgedt.setText("");
            }
        });
    }

    private void getResponse(String message){

        chatsModelArrayList.add(new ChatsModel(message,USER_KEY));
        chatRvAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=157313&key=HnOvzIdjcF3pVh12&uid=[uid]&msg="+message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if(response.isSuccessful()){
                    MsgModel model = response.body();
                    chatsModelArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                    chatRvAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {

                chatsModelArrayList.add(new ChatsModel("plezz revert your question",BOT_KEY));
                chatRvAdapter.notifyDataSetChanged();

            }
        });

    }

    private void openDialog(){

        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(),"Dialog");


    }


    @Override
    public void applyTexts(String EDT,int hr, int min) {

        gethr = hr;
        getmin = min;

        if(EDT!= null ){
            Intent intent = new Intent(MainActivity.this,AlarmReciever.class);
            intent.putExtra("notificationId",notificationId);
            intent.putExtra("todo",EDT);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

            int hour = gethr;
            int minute = getmin;

            Calendar starttime = Calendar.getInstance();
            starttime.set(Calendar.HOUR_OF_DAY,hour);
            starttime.set(Calendar.MINUTE,minute);
            starttime.set(Calendar.SECOND,0);
            long  alarmstarttime = starttime.getTimeInMillis();

            alarm.set(AlarmManager.RTC_WAKEUP,alarmstarttime,alarmIntent);

            Toast.makeText(MainActivity.this,"Done !",Toast.LENGTH_SHORT).show();
        }

    }

//    private void notification(){
//        Intent intent = new Intent(MainActivity.this,AlarmReciever.class);
//        intent.putExtra("notificationId",notificationId);
//        intent.putExtra("todo",getedittext);
//
//        PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
//
//        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        int hour = gethr;
//        int minute = getmin;
//
//        Calendar starttime = Calendar.getInstance();
//        starttime.set(Calendar.HOUR_OF_DAY,hour);
//        starttime.set(Calendar.MINUTE,minute);
//        starttime.set(Calendar.SECOND,0);
//        long  alarmstarttime = starttime.getTimeInMillis();
//
//        alarm.set(AlarmManager.RTC_WAKEUP,alarmstarttime,alarmIntent);
//
//        Toast.makeText(MainActivity.this,"Done !",Toast.LENGTH_SHORT).show();
//
//    }

    private  void  checkVoicecmdPermission(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: "+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


}
