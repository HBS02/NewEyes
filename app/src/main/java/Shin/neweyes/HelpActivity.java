package Shin.neweyes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.speech.tts.TextToSpeech.ERROR;

public class HelpActivity extends AppCompatActivity {
    private EditText chat_name;
    private ImageButton chat_button;
    private ImageButton stt_button;
    private boolean voice = false;
    private Intent intent;
    private SpeechRecognizer sst;
    private TextToSpeech tts;              // TTS 변수 선언

    Map<String, Object> map = new HashMap<>();

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();            // 로그인 사용자 정보 가져옴,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final FirebaseUser firebaseuser = firebaseAuth.getCurrentUser();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {       // 해당 권한이 승낙 상태인지 거절 상태인지 확인 (접근 승낙 상태 일때)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);                     // 사용자에게 접근권한 설정을 요구하는 다이얼로그 띄움
        }

        chat_name = (EditText) findViewById(R.id.chat_name);            // 목적지
        chat_button = (ImageButton) findViewById(R.id.chat_button);          // 개설버튼
        stt_button = (ImageButton) findViewById(R.id.chat_button2);          // 음성버튼

         intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");





         stt_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                voice(chat_name);
              }
         });


        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voice == false) {
                    tts.speak("개설합니다. 한번 더 눌러주세요", TextToSpeech.QUEUE_FLUSH, null);
                    voice = true;
                } else {
                    voice = false;
                    if (chat_name.getText().toString().equals(""))
                        return;

                    final String CHAT_NAME = chat_name.getText().toString();        // 목적지 입력

                    map.put(firebaseuser.getUid(), "");          // firebase.getuid 값 = "" 입력
                    databaseReference.child("NewEyes").child(CHAT_NAME).updateChildren(map);     // CHAT_NAME(목적지) 하위 값에 getuid = "" 값 입력

                    databaseReference.child("chatuser").child(firebaseuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {    // "chatuser"데이터의 uid 하위 데이터들을 불러옴.
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {                  // 데이터 변경
                            databaseReference.child("chatuser").child(firebaseuser.getUid()).child("chatName").setValue(CHAT_NAME);       // 채팅방 개설하면 "chatuser"데이터의 chatname 데이터를 목적지로 변경
                            databaseReference.child("chatuser").child(firebaseuser.getUid()).child("chat").setValue(true);        // 채팅방 개설하면 "chatuser"의 chat 데이터를 true 로 변경
                            // 기존 chatName과 chat의 데이터는 "", false (User.class)
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(HelpActivity.this, UserChatActivity.class);
                    intent.putExtra("chatName", CHAT_NAME);
                    intent.putExtra("userName", firebaseuser.getUid());
                    startActivity(intent);
                }
            }
        });


    }

    private void voice(final EditText chat_name) {
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // 사용자가 말하기 시작할 준비가되면 호출됩니다.
                Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                tts.speak("목적지", TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onBeginningOfSpeech() {
                // 사용자가 말하기 시작했을 때 호출됩니다.
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // 입력받는 소리의 크기를 알려줍니다.
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // 사용자가 말을 시작하고 인식이 된 단어를 buffer에 담습니다.
            }

            @Override
            public void onEndOfSpeech() {
                // 사용자가 말하기를 중지하면 호출됩니다.
            }

            @Override
            public void onError(int error) {
                // 네트워크 또는 인식 오류가 발생했을 때 호출됩니다.
            }


            @Override
            public void onPartialResults(Bundle partialResults) {
                // 부분 인식 결과를 사용할 수 있을 때 호출됩니다.
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // 향후 이벤트를 추가하기 위해 예약됩니다.
            }

            @Override
            public void onResults(Bundle bundle) {
                String key = SpeechRecognizer.RESULTS_RECOGNITION;
                ArrayList<String> mResult = bundle.getStringArrayList(key);
                String[] rs = new String[mResult.size()];
                mResult.toArray(rs);
                chat_name.setText("" + rs[0]);
            }


        };
        sst = SpeechRecognizer.createSpeechRecognizer(this);
        sst.setRecognitionListener(listener);
        sst.startListening(intent);
    }

}