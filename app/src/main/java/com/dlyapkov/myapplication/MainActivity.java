package com.dlyapkov.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dlyapkov.myapplication.Entity.Weather;
import com.dlyapkov.myapplication.Services.HttpsService;
import com.dlyapkov.myapplication.database.App;
import com.dlyapkov.myapplication.database.EducationDao;
import com.dlyapkov.myapplication.database.EducationSource;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String FREE = "free";
    private static String PRO = "pro";

    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";
    private GoogleSignInClient googleSignInClient;

    private LinearLayout linerNavBar;
    private com.google.android.gms.common.SignInButton buttonSignIn;
    private Button buttonSignOut;
    private ImageView iconImage;
    private TextView textName;
    private TextView textEmail;

    DialogBuilderFragment dlgCustom;
    private boolean isBound = false;
    private EducationSource educationSource;
    private WeatherRecyclerAdapter adapter;
    private BroadcastReceiver internetReceiver = new InternetReceiver();
    NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(internetReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

        startService(new Intent(this, HttpsService.class));

        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
        dlgCustom = new DialogBuilderFragment();

        Http.initRetrofit(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayout);
        EducationDao educationDao = App.getInstance().getEducationDao();
        educationSource = new EducationSource(educationDao);

        adapter = new WeatherRecyclerAdapter(educationSource, this);
        recyclerView.setAdapter(adapter);
        initGetToken();
        initNotificationChannel();
        googleAuth();
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (getResources().getString(R.string.flavor).equals(FREE)) {
            Menu menu = navigationView.getMenu();
            for (int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++) {
                MenuItem menuItem= menu.getItem(menuItemIndex);
                if(menuItem.getItemId() == R.id.nav_maps){
                    menuItem.setVisible(false);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(searchText, "Такого города нет в списке", Snackbar.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:

                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_maps:
                Intent mapsIntent = new Intent(this, MapsActivity.class);
                startActivity(mapsIntent);
                break;
            case R.id.nav_settings:
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.nav_about_developer:
                dlgCustom.show(getSupportFragmentManager(), "about the Developer");
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void googleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        View header = navigationView.getHeaderView(0);
        linerNavBar = (LinearLayout) header.findViewById(R.id.layout_nav_bar);
        buttonSignIn = header.findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        buttonSignOut = header.findViewById(R.id.button_sign_out);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignOut();
            }
        });

        iconImage = header.findViewById(R.id.imageView);
        textName = header.findViewById(R.id.text_name);
        textEmail = header.findViewById(R.id.text_email);
    }

    private void googleSignOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        enableSing();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableSing();
        // Проверим, входил ли пользователь в это приложение через Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Пользователь уже входил, сделаем кнопку недоступной
            disableSing();
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            if (account.getPhotoUrl() != null)
                updateUI(account.getEmail(), account.getDisplayName(), String.valueOf(account.getPhotoUrl()));
            updateUI(account.getEmail(), account.getDisplayName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты аутентификации уже
            // готовы
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Регистрация прошла успешно
            disableSing();
            Log.d("QWEQWEQWWE", String.valueOf(account.getPhotoUrl()));
            if (account.getPhotoUrl() != null)
                updateUI(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().getHost());
            updateUI(account.getEmail(), account.getDisplayName());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signIn() {
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    private void updateUI(String email, String name, String url) {
        textEmail.setText(email);
        textName.setText(name);
        Picasso.get()
                .load(url)
              //  .transform(new CircleTransformation())
                .into(iconImage);
    }

    private void updateUI(String email, String name) {
        textEmail.setText(email);
        textName.setText(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, HttpsService.class));
        unregisterReceiver(internetReceiver);
    }

    public void displayError(String error) {
        Snackbar.make(findViewById(R.id.constraintLayout), error, Snackbar.LENGTH_LONG).show();
    }

    public void updateOrAddWeather(Weather weather) {
        educationSource.updateOrAddWeather(weather);
    }

    public void addWeather(Weather weather) {
        educationSource.addWeather(weather);
    }

    private void initGetToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("PushMessage", "getInstanceId failed", task.getException());
                            return;
                        }
                    }
                });
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void enableSing() {
        linerNavBar.setVisibility(View.INVISIBLE);
        buttonSignIn.setVisibility(View.VISIBLE);
        buttonSignOut.setVisibility(View.INVISIBLE);
        textName.setVisibility(View.INVISIBLE);
        textEmail.setVisibility(View.INVISIBLE);
        iconImage.setVisibility(View.INVISIBLE);
    }

    private void disableSing() {
        linerNavBar.setVisibility(View.VISIBLE);
        buttonSignIn.setVisibility(View.INVISIBLE);
        buttonSignOut.setVisibility(View.VISIBLE);
        textName.setVisibility(View.VISIBLE);
        textEmail.setVisibility(View.VISIBLE);
        iconImage.setVisibility(View.VISIBLE);
    }
}
