package detection.face.facedetection;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

public class RegistrationActivity extends AppCompatActivity {
    EditText firstName = null;
    EditText lastName = null;
    EditText email = null;
    EditText username = null;
    EditText password = null;
    EditText reTypePassword = null;
    EditText age = null;
    EditText preferredFood = null;
    Context mContext = this;
    CountDownTimer cTimer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        reTypePassword = findViewById(R.id.repassword);
        age = findViewById(R.id.age);
        preferredFood = findViewById(R.id.preferred_food);
    }

    public void register(View v){
        if(firstName.getText().toString().equals("")){
            Toast.makeText(mContext,Constant.FIRST_NAME_REQUIRED,Toast.LENGTH_LONG).show();
        }else if(lastName.getText().toString().equals("")){
            Toast.makeText(mContext,Constant.LAST_NAME_REQUIRED,Toast.LENGTH_LONG).show();
        }else if(email.getText().toString().equals("")){
            Toast.makeText(mContext,Constant.EMAIL_REQUIRED,Toast.LENGTH_LONG).show();
        }else  if(username.getText().toString().equals("")){
            Toast.makeText(mContext,Constant.USERNAME_REQUIRED,Toast.LENGTH_LONG).show();
        }else if(password.getText().length() < 6){
            Toast.makeText(mContext,Constant.ATLEAST_SIX_CHARACTERS,Toast.LENGTH_LONG).show();
        }else if(!password.getText().toString().equals(reTypePassword.getText().toString())){
            Toast.makeText(mContext,Constant.MISMATCH_PASSWORD,Toast.LENGTH_LONG).show();
        }else if(age.getText().toString().equals("")){
            Toast.makeText(mContext,Constant.AGE_REQUIRED,Toast.LENGTH_LONG).show();
        }else if(preferredFood.getText().toString().equals("")){
            Toast.makeText(mContext,Constant.PREFERRED_FOOD_REQUIRED,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(mContext,Constant.PLEASE_WAIT,Toast.LENGTH_LONG).show();
            new ApiClass(Constant.REGISTRATION_URL,
                    firstName.getText().toString(),
                    lastName.getText().toString(),
                    username.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString(),
                    age.getText().toString(),
                    preferredFood.getText().toString(),
                    Constant.REGISTRATION).execute();

            cTimer = new CountDownTimer(7000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    if(GlobalVO.getRegistrationResponse().equals("existing")){
                        Toast.makeText(mContext,Constant.EXISTING,Toast.LENGTH_LONG).show();
                    }else if(GlobalVO.getRegistrationResponse().equals("")){
                        Toast.makeText(mContext,Constant.CONNECT_UNSUCCESSFUL,Toast.LENGTH_SHORT).show();
                    }else if(GlobalVO.getRegistrationResponse().equals("success")) {
                        Toast.makeText(mContext,Constant.REGISTRATION_SUCCESS,Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
