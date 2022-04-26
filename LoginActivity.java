package ru.tmkstd.cardgamedurakonline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    static  final int GOOGLE_SIGN = 123;
    FirebaseAuth mAuth;
    Button enterAgain;
    ProgressBar progressBar;
    TextView textView;
    FirebaseUser user;

    SharedPreferences save;
    SharedPreferences.Editor editor;

    final int versionGame = 9;
    long coin = -1;
    boolean versionStart = false;


    GoogleSignInClient mGoogleSignInClient;

    ValueEventListener CoinListener,VersionGameLinstener;
    FirebaseDatabase database;
    DatabaseReference myRef;

    int colorField;
    final int MY_PERMISSIONS_REQUEST_INTERNET = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("LLL1","LoginActivity onCreate()");
//        Toast.makeText(this,"111111111111111111111111", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
        }
        mAuth = FirebaseAuth.getInstance(); // Сказали так надо 1111111
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progress_circularLogin);
        textView = findViewById(R.id.textLogin);
        RelativeLayout layout = findViewById(R.id.loginLayout);
        enterAgain = new Button(this);

        RelativeLayout.LayoutParams paramsBtn = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsBtn.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramsBtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsBtn.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        paramsBtn.bottomMargin=btsHeight*55/10;
//        paramsBtn.rightMargin = maxX/10;
        enterAgain.setText("Попробовать ещё");
        enterAgain.setLayoutParams(paramsBtn);
        enterAgain.setVisibility(View.INVISIBLE);

//        name.setMaxLines(1);
        enterAgain.setOnClickListener(v->enterAgainClick());
        layout.addView(enterAgain);

        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        colorField = save.getInt("colorField", 0);
        switch (colorField){
            case 0: layout.setBackgroundResource(R.drawable.fon_one); break;
            case 1: layout.setBackgroundResource(R.drawable.fon_two); break;
            case 2: layout.setBackgroundResource(R.drawable.fon_three); break;
            case 3: layout.setBackgroundResource(R.drawable.fon_four); break;
            default:layout.setBackgroundResource(R.drawable.fon_one); break;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            //Запрос разрешения
            if(ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,Manifest.permission.INTERNET)) {

                AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
                quitDialog.setTitle("Для подключения нужен интернет.");
                quitDialog.setPositiveButton("Предоставить разрешение", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
                    }
                });
                quitDialog.setNegativeButton("Отказ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                quitDialog.show();
            }
            else {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
            }
        } else {
            //Разрешение уже есть
//            StartApps();
            secondStart();
        }
    }
    private void enterAgainClick() {
        enterAgain.setVisibility(View.INVISIBLE);
        secondStart();
    }

    void secondStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.my_Id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }

    void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////        updateUI(currentUser);
//    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET:{
                if (grandResults.length > 0
                        && grandResults[0] == PackageManager.PERMISSION_GRANTED){
//                    StartApps();
                    secondStart();
                }
                return;
            }
            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

                progressBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            } catch (ApiException e) {
                Log.d("eeewewew"," " + e);
                Log.d("eeewewew", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this,"Сервер Google не доступен.", Toast.LENGTH_SHORT).show();
                enterAgain.setVisibility(View.VISIBLE);
            }
        }
    }
    private void updateUI(FirebaseUser user) {
        if(user != null) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            serverConnectedAndLoad(user);
        }
    }

    void serverConnectedAndLoad(FirebaseUser user) {
        VersionGameLinstener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class)!=null) {
                    if (dataSnapshot.getValue(Long.class) > versionGame) {
                        versionStart = false;
                    } else {
                        versionStart = true;
                    }

                    myRef.child("Coins").child(user.getUid()).addListenerForSingleValueEvent(CoinListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        CoinListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Long.class) == null){
                    coin = 100;
                    myRef.child("Coins").child(user.getUid()).setValue(coin);
                }
                else {
                    coin = dataSnapshot.getValue(Long.class);
                }
                if(coin<0) coin = 0l;
                save = getSharedPreferences("Save", MODE_PRIVATE);
                editor = save.edit();
//        editor.putString("name",name.getText().toString());
                editor.putLong("coin",coin);
                editor.putBoolean("version",versionStart);
                editor.commit();
                startNextActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.child("Version").addListenerForSingleValueEvent(VersionGameLinstener);
    }
    void startNextActivity() {
        Intent intent = new Intent(LoginActivity.this, LoadActivity.class);
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
//    void Logout() {
//        FirebaseAuth.getInstance().signOut();
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, task -> {
//                    updateUI(null);
//                });
//
//    }

    @Override
    public void onPause() {
        Log.e("LoginActivity  ", "onPause()");
        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        SharedPreferences.Editor editor = save.edit();

        Log.d("LLL1","onPause LoginA" + this.findViewById(android.R.id.content).getWidth()+ " " + this.findViewById(android.R.id.content).getHeight());
        int maxX = this.findViewById(android.R.id.content).getWidth();
        int maxY = this.findViewById(android.R.id.content).getHeight();
        int sizeResultCard = 145;
        int maxGameX= sizeResultCard * 4;
        if(maxX>720) maxGameX = 720;//это для больших экранов
        int maxGameY = (int)(maxGameX*((double)maxY/maxX));
//        editor.putInt("maxGameX",maxX);
//        editor.putInt("maxGameY",maxY);
        editor.putInt("maxGameX",maxGameX);
        if(user!=null)
            editor.putString("myKeyId",user.getUid());
        editor.putInt("maxGameY",maxGameY);
        editor.putInt("MaxX",maxX);
        editor.putInt("MaxY",maxY);
        editor.commit();
        super.onPause();
    }
}
