package com.example.quoraclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quoraclone.Adapters.PostAdapter;
import com.example.quoraclone.Model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer_Layout;
    Toolbar toolbar;
    FloatingActionButton fab;
    private RecyclerView recycleView;
    private ProgressBar progress_circular;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private CircleImageView navHeaderImage;
    private TextView navHeaderEmail,navHeaderName;

    private DatabaseReference userRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fab = findViewById(R.id.fab);

        drawer_Layout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.home_toolbar);
        //check here may be need to change the id(important)
//        Objects.requireNonNull(getSupportActionBar()).setTitle("Unsolved App");
        NavigationView navigationView = null;
        try {
            // setSupportActionBar(toolbar);
            progress_circular = findViewById(R.id.progress_circular);
            recycleView = findViewById(R.id.recycleView);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recycleView.setHasFixedSize(true);
            recycleView.setLayoutManager(linearLayoutManager);


            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_Layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer_Layout.addDrawerListener(toggle);
            toggle.syncState();
        } catch (Exception E) {
            Toast.makeText(getApplication(), E.getMessage(), Toast.LENGTH_LONG).show();
        }
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AskAQuestionActivity.class);
            startActivity(intent);
        });

        navHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);  //issue here redo thing
        navHeaderName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        navHeaderImage = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_image);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                navHeaderName.setText(snapshot.child("username").getValue().toString());
                navHeaderEmail.setText(snapshot.child("email").getValue().toString());
                Glide.with(HomeActivity.this).load(snapshot.child("profileimageurl").getValue().toString()).into(navHeaderImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });




        postList = new ArrayList<>();
        postAdapter = new PostAdapter(HomeActivity.this, postList);
        recycleView.setAdapter(postAdapter);


        readQuestionsPosts();

    }

    private void readQuestionsPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_finance:
                Intent intent= new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent.putExtra("title","finance");
                startActivity(intent);
                break;
            case R.id.nav_sports:
                Intent intentS= new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intentS.putExtra("title","sports");
                startActivity(intentS);
                break;
            case R.id.nav_food:
                Intent intentF= new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intentF.putExtra("title","food");
                startActivity(intentF);
                break;

        }
        drawer_Layout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onBackPressed(){
        if(drawer_Layout.isDrawerOpen(GravityCompat.START)){
            drawer_Layout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}