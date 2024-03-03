package com.baraa.firebase.whatsapp.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.adapters.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    FloatingActionButton fab;
    FirebaseAuth auth;

    private final String USER_ID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setState("online");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.homeToolbar);
        fab = findViewById(R.id.viewContactActivity);

        auth = FirebaseAuth.getInstance();

        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setTitle("Whatsapp");

        toolbar.setTitleTextColor(Color.WHITE);

        toolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_profile)
                startActivity(new Intent(HomeActivity.this, ViewProfile.class));

            else if(id == R.id.menu_logout){
                auth.signOut();
                Intent intent = new Intent(this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        });

        tabLayout.addTab(tabLayout.newTab().setText("CHATS"));
        tabLayout.addTab(tabLayout.newTab().setText("STATUS"));
        tabLayout.addTab(tabLayout.newTab().setText("CALLS"));
//                                   normal     selected
        tabLayout.setTabTextColors(Color.GRAY, Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //start Contact Activity
        fab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ViewContacts.class)));

    }// End onCreate method
    private void setState(String state){
        DatabaseReference reference;

        reference = FirebaseDatabase.getInstance().getReference("users").child(USER_ID);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("state",state);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setState("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setState("offline");
    }
}