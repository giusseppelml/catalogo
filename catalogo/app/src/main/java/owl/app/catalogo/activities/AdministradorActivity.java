package owl.app.catalogo.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import owl.app.catalogo.R;
import owl.app.catalogo.adapters.PagerAdministracionAdapter;
import owl.app.catalogo.utils.SharedPrefManager;

public class AdministradorActivity extends AppCompatActivity {

    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        setToolbar();

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayoutAdministracion);
        tabLayout.addTab(tabLayout.newTab().setText("Usuarios"));
        tabLayout.addTab(tabLayout.newTab().setText("Categorias"));
        tabLayout.addTab(tabLayout.newTab().setText("Ventas"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        // este si se realiza en activity: getSupportFragmentManager();
        // este se realiza si es enfragment: getFragmentManager()
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewPagerAdministracion);
        PagerAdapter adapter = new PagerAdministracionAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.toolbarAdministracion);
        setSupportActionBar(myToolbar);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdministradorActivity.this, MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_administrativo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logOutActionBarAdministrativo:
                SharedPrefManager.getInstance(AdministradorActivity.this).logout();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}