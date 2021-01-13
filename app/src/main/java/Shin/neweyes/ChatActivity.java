package Shin.neweyes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private String CHAT_NAME;
    private String USER_NAME;
    private String HELPER_NAME;
    private String ADDRESS_NAME;
    private String LATITUDE;
    private String LONGTITUDE;
    private Double TOLATITUDE;
    private Double TOLONGTITUDE;

    private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private  FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        // 위젯 ID 참조
        chat_view = (ListView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();   // 사용자 정보 가져오기
        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장


        Intent intent = getIntent();
        CHAT_NAME = intent.getStringExtra("chatName");
        HELPER_NAME = intent.getStringExtra("helperName");

        String url = "http://202.31.147.47:8091/?action=stream";
        WebView webView =(WebView) findViewById(R.id.webview1);    // 영상 스트리밍 웹뷰
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);


        databaseReference.child("chatuser").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);           // Use의 모든 데이터를 chatuser의 데이터로 입력.
                USER_NAME =user.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("chatuser").child(HELPER_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);           // Use의 모든 데이터를 chatuser의 데이터로 입력.
                LATITUDE = user.getLatitude();
                LONGTITUDE = user.getLongtitude();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 채팅 방 입장
        openChat(CHAT_NAME);



        // 메시지 전송 버튼에0 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {           // 메시지 전송
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;
                ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString()); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("NewEyes").child(CHAT_NAME).child(HELPER_NAME).push().setValue(chat); // 데이터 푸쉬
                chat_edit.setText(""); //입력창 초기화

            }
        });

        chat_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
   /*             databaseReference.child("chatuser").child(HELPER_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent1 = new Intent(ChatActivity.this, MapActivity.class);
                        intent1.putExtra("latitude", LATITUDE);
                        intent1.putExtra("longtitude", LONGTITUDE);
                        startActivity(intent1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
                Intent intent1 = new Intent(ChatActivity.this, LoActivity.class);
                startActivity(intent1);
            }
        });

    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        adapter.add(chatDTO.getUserName() + " : " + chatDTO.getMessage());
        chat_view.setSelection(adapter.getCount() - 1);
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        adapter.remove(chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

    private void openChat(String chatName) {
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter

                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_view.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("NewEyes").child(CHAT_NAME).addListenerForSingleValueEvent(new ValueEventListener() {    //  목적지 채팅방의 모든 데이터 불러옴.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child("NewEyes").child(CHAT_NAME).child(HELPER_NAME).addChildEventListener(new ChildEventListener() {    // "chat" 목적지(채팅방), uid 하위 데이터들을 불러옴.
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {                 // 메시지 출력.
                            addMessage(dataSnapshot, adapter);
                        Log.e("LOG", "s:"+s);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        removeMessage(dataSnapshot, adapter);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}