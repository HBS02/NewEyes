package Shin.neweyes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListActivity extends AppCompatActivity {

    private String CHAT_NAME;
    private String HELPER_NAME;
    private ListView chat_list;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();         // FirebaseDatabase 인스턴스를 추가로 사용할 수 있도록 firebaseDatabase 객체에 할당합니다.
    private DatabaseReference databaseReference = firebaseDatabase.getReference();       //  databaseReference 객체를 프로젝트의 부모 (이 예제에서는 "your-project-parent" )의 "message" 하위 요소로 참조합니다. 그래서 그것은 "your-project-parent/message"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        chat_list = (ListView) findViewById(R.id.chat_list);
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_list.setAdapter(adapter);
        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("NewEyes").addChildEventListener(new ChildEventListener() {             // 채팅 리스트 불러오기 위한 NewEyes 데이터 불러옴
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                adapter.add(dataSnapshot.getKey());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CHAT_NAME = ((TextView) view).getText().toString();

                databaseReference.child("NewEyes").child(CHAT_NAME).addChildEventListener(new ChildEventListener() {                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HELPER_NAME = dataSnapshot.getKey();                // HELPER_NAME = 시각장애인 uid (채팅방을 만든 유저의 UID)
                        Intent intent = new Intent(ListActivity.this, ChatActivity.class);
                        intent.putExtra("chatName", CHAT_NAME);             // 채팅방 (목적지)
                        intent.putExtra("helperName", HELPER_NAME);         // uid
                        startActivity(intent);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

}