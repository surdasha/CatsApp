package ru.surdasha.cats.presentation.ui;

import android.view.View;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BaseFragment extends Fragment {
    private Unbinder unbinder;

    public void bindBaseUI(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
