package ru.surdasha.cats.presentation.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import ru.surdasha.cats.R;


public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void showFragmentWithTag(Fragment fragmentToShow, String tag, boolean isNewFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragmentToHide = getSupportFragmentManager().getPrimaryNavigationFragment();
        if (fragmentToHide != null) {
            fragmentTransaction.detach(fragmentToHide);
        }
        if (isNewFragment) {
            fragmentTransaction.add(R.id.container, fragmentToShow, tag);
        } else {
            fragmentTransaction.attach(fragmentToShow);
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragmentToShow);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    public Fragment getFragmentByTag(String tag){
        return getSupportFragmentManager().findFragmentByTag(tag);
    }
}
