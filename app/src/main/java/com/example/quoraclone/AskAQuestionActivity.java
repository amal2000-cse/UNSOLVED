package com.example.quoraclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class AskAQuestionActivity extends AppCompatActivity {
    Toolbar toolbar;
    Spinner spinner;
    EditText questionbox;
    ImageView imageview;
    Button CancelBtn, postquestionBtn;
    String askedByName;
    DatabaseReference askedByRef;
    ProgressDialog loader;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storagereference;
    Uri imageurl;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String onlineuserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_aquestion);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // toolbar = findViewById(R.id.question_toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ask a Question");
        spinner = findViewById(R.id.spinner);
        questionbox = findViewById(R.id.question_text);
        imageview = findViewById(R.id.questionImage);
        CancelBtn = findViewById(R.id.cancel);
        postquestionBtn = findViewById(R.id.PostQuestion);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineuserId = mUser.getUid();
        askedByRef = FirebaseDatabase.getInstance().getReference("users").child(onlineuserId);
        askedByRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                askedByName = snapshot.child("fullname").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        storagereference = FirebaseStorage.getInstance().getReference("question");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.topics));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItem().equals("select topic")) {
                    Toast.makeText(AskAQuestionActivity.this, "Please select a valid topic", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, 1);


            }
        });


        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postquestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performValidations();

            }


        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }


    String getQuestionText(){


        return questionbox.getText().toString().trim();


    }
    String getTopic(){
        return spinner.getSelectedItem().toString();

    }

    String mDate = DateFormat.getDateInstance().format(new Date());
    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("questions posts");

    private void performValidations() {
        if(getQuestionText().isEmpty()){
            questionbox.setError("Question Required");

        }
        else if(getTopic().equals("select topic")){
            Toast.makeText(this,"Select a valid topic",Toast.LENGTH_SHORT).show();

        }
        else if(!getQuestionText().isEmpty() && !getTopic().equals("") && imageurl == null){
            uploadAQuestionWithNoImage();

        }else if(!getQuestionText().isEmpty() && !getTopic().equals("") && imageurl != null){
            uploadAQuestionwithImage();
        }
    }
    private void startLoader(){
        loader.setMessage("posting your Question");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

    }

    private String getfileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }
    private void uploadAQuestionWithNoImage(){
        startLoader();
        String postid = ref.push().getKey();

        HashMap<String,Object>hashMap =new HashMap<>();
        hashMap.put("postid",postid);
        hashMap.put("question",getQuestionText());
        hashMap.put("publisher",onlineuserId);
        hashMap.put("Topic",getTopic());
        hashMap.put("askedby",askedByName);
        hashMap.put("date",mDate);

        ref.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AskAQuestionActivity.this,"Question Posted Successfully", Toast.LENGTH_SHORT ).show();
                    loader.dismiss();
                    startActivity(new Intent(AskAQuestionActivity.this,HomeActivity.class));
                    finish();

                }
                else{
                    Toast.makeText(AskAQuestionActivity.this,"could not upload Image"+task.getException().toString(), Toast.LENGTH_SHORT ).show();
                    loader.dismiss();
                }
            }
        });
    }

    private void uploadAQuestionwithImage(){
        startLoader();
        final StorageReference fileReference;
//        Uri imageuri = null;
        fileReference = storagereference.child(System.currentTimeMillis()+"."+ getfileExtension(imageurl));
        uploadTask = fileReference.putFile(imageurl);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isComplete()){
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    Uri downloadUri =(Uri) task.getResult();
                    myUrl = downloadUri.toString();
                    String postid = ref.push().getKey();

                    HashMap<String,Object>hashMap =new HashMap<>();
                    hashMap.put("postid",postid);
                    hashMap.put("question",getQuestionText());
                    hashMap.put("publisher",onlineuserId);
                    hashMap.put("Topic",getTopic());
                    hashMap.put("askedby",askedByName);
                    hashMap.put("questionimage",myUrl);
                    hashMap.put("date",mDate);

                    ref.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AskAQuestionActivity.this,"Question Posted Successfully", Toast.LENGTH_SHORT ).show();
                                loader.dismiss();
                                startActivity(new Intent(AskAQuestionActivity.this,HomeActivity.class));
                                finish();

                            }
                            else{
                                Toast.makeText(AskAQuestionActivity.this,"could not upload Image"+task.getException().toString(), Toast.LENGTH_SHORT ).show();
                                loader.dismiss();
                            }
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AskAQuestionActivity.this,"Failed To Upload the Question", Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCodes, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCodes,data);
        if(requestCode==1 && resultCodes == RESULT_OK && data!=null){
            imageurl = data.getData();
            imageview.setImageURI(imageurl);
        }

    }
}