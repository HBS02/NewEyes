package Shin.neweyes;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;              // TTS 변수 선언
    private boolean helpbutton = false;
    private boolean helperbutoon = false;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();            // 로그인 사용자 정보 가져옴,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton button1 = (ImageButton) findViewById(R.id.button);
        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
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


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helpbutton == false) {
                    tts.speak("도움을 받으세요. 한번 더 눌러주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    helpbutton = true;
                    helperbutoon = false;
                }
                else {
                    helpbutton = false;
                    databaseReference.child("chatuser").child(firebaseuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user =dataSnapshot.getValue(User.class);           // Use의 모든 데이터를 chatuser의 데이터로 입력.
                            if(user.chat==true)
                            {
                                Intent intent = new Intent(getApplicationContext(), UserChatActivity.class);
                                intent.putExtra("chatName", user.chatName);
                                intent.putExtra("userName", firebaseuser.getUid());
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helperbutoon==false) {

                    tts.speak("도움을 주세요. 한번 더 눌러주세요", TextToSpeech.QUEUE_FLUSH, null);
                    helperbutoon = true;
                    helpbutton = false;
                }
                else {
                    helperbutoon = false;
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


}