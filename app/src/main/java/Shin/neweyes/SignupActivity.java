package Shin.neweyes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();             // FirebaseDatabase 인스턴스를 추가로 사용할 수 있도록 firebaseDatabase 객체에 할당합니다.
    private DatabaseReference databaseReference = firebaseDatabase.getReference();          //  databaseReference 객체를 프로젝트의 부모 (이 예제에서는 "your-project-parent" )의 "message" 하위 요소로 참조합니다. 그래서 그것은 "your-project-parent/message"
    private  FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();            // 로그인 사용자 정보 가져옴,


    private EditText email_join;
    private EditText pwd_join;
    private Button btn;
    private EditText address_join;
    private EditText name_join;

    String email = "";
    String uid = "";
    String pwd = "";
    String address = "";
    String name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email_join = (EditText) findViewById(R.id.sign_up_email);
        pwd_join = (EditText) findViewById(R.id.sign_up_pwd);
        btn = (Button) findViewById(R.id.sign_up_btn);
        address_join = (EditText) findViewById(R.id.sign_up_address);
        name_join = (EditText) findViewById(R.id.sign_up_name);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_join.getText().toString().trim(); // String 타입 텍스트 사용, 문자열 공백 포함된 겨우 제거
                pwd = pwd_join.getText().toString().trim();
                address = address_join.getText().toString().trim();
                name = name_join.getText().toString().trim();
                /*uid = FirebaseAuth.getInstance().getCurrentUser().getUid();*/
                if(email == null || pwd == null || name == null || address == null)
                {
                    return;
                }


                firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    User user = new User(firebaseUser.getUid(), email, name, address);
                                    databaseReference.child("chatuser").child(firebaseUser.getUid()).setValue(user);   // "chatuser" 프로젝트의 uid에 user 정보 입력.
                                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                   finish();
                                } else {
                                    Toast.makeText(SignupActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
            }
        });


    }
}