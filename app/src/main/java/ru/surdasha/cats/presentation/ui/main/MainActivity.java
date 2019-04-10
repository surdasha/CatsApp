package ru.surdasha.cats.presentation.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.surdasha.cats.CatApp;
import ru.surdasha.cats.R;
import ru.surdasha.cats.di.components.UIComponent;
import ru.surdasha.cats.di.modules.UIModule;
import ru.surdasha.cats.presentation.ui.BaseActivity;
import ru.surdasha.cats.presentation.ui.all.AllCatsFragment;
import ru.surdasha.cats.presentation.ui.favorites.FavoriteCatsFragment;

public class MainActivity extends BaseActivity {
    @BindView(R.id.navigation)
    protected BottomNavigationView navigation;

    public UIComponent getUIComponent() {
        return mControllerComponent;
    }

    private UIComponent mControllerComponent;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    navigateAllCats();
                    return true;
                case R.id.navigation_favorites:
                    navigateFavorites();
                    return true;
            }
            return false;
        }
    };

    private void navigateAllCats() {
        Fragment fragment = getFragmentByTag(AllCatsFragment.TAG);
        if (fragment == null){
            AllCatsFragment baseFragment = new AllCatsFragment();
            getUIComponent().inject(baseFragment);
            showFragmentWithTag(baseFragment, AllCatsFragment.TAG, true);
        }else{
            showFragmentWithTag(fragment, AllCatsFragment.TAG, false);
        }
    }

    private void navigateFavorites() {
        Fragment fragment = getFragmentByTag(FavoriteCatsFragment.TAG);
        if (fragment == null){
            FavoriteCatsFragment baseFragment = new FavoriteCatsFragment();
            getUIComponent().inject(baseFragment);
            showFragmentWithTag(baseFragment, FavoriteCatsFragment.TAG, true);
        }else{
            showFragmentWithTag(fragment, FavoriteCatsFragment.TAG, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getUiComponent().inject(this);
        if (savedInstanceState == null) {
            AllCatsFragment baseFragment = new AllCatsFragment();
            getUIComponent().inject(baseFragment);
            showFragmentWithTag(baseFragment, AllCatsFragment.class.getSimpleName(), true);
        }
    }

    private UIComponent getUiComponent() {
        if (mControllerComponent == null) {
            mControllerComponent = CatApp.getAppComponent()
                    .addUIComponent(new UIModule(this));
        }

        return mControllerComponent;
    }

    @Override
    protected void onDestroy(){
        mControllerComponent = null;
        super.onDestroy();
    }

}
