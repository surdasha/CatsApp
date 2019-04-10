package ru.surdasha.cats.presentation.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ru.surdasha.cats.R;
import ru.surdasha.cats.presentation.models.CatUI;
import ru.surdasha.cats.presentation.ui.BaseFragment;

public class FavoriteCatsFragment extends BaseFragment {
    public static final String TAG = FavoriteCatsFragment.class.getSimpleName();
    @BindView(R.id.rvCats)
    RecyclerView rvCats;
    @BindView(R.id.groupCats)
    Group groupCats;
    @BindView(R.id.groupEmpty)
    Group groupEmpty;
    @BindView(R.id.groupLoading)
    Group groupLoading;
    @BindView(R.id.groupError)
    Group groupError;

    @NonNull
    private FavoriteCatsAdapter favoriteCatsAdapter;
    @NonNull
    private FavoriteCatsViewModel viewModel;
    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        subscribeToDataStates();
        subscribeToData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_cats, container, false);
        bindBaseUI(view);
        setUpAdapter();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.loadFavoriteCats();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FavoriteCatsViewModel.class);
    }

    private void subscribeToData() {
        viewModel.getCatsList().observe(this, catUIS -> {
            if (catUIS.isEmpty()) {
                onEmptyList();
            } else {
                onShowCats(catUIS);
            }
        });
        viewModel.getDeleteCat().observe(this, catUI -> {
            onShowSuccessDeleting(catUI);
        });
    }

    private void subscribeToDataStates() {
        viewModel.getCatsDeletingState().observe(this, state -> {
            if (state.isError()) {
                onShowErrorDeleting();
            }
        });
        viewModel.getCatsLoadingState().observe(this, state -> {
            if (state.isLoading()) {
                onShowLoading();
            } else {
                onHideLoadingCats();
                if (state.isError()) {
                    onShowErrorLoading();
                }
            }
        });
    }

    private void setUpAdapter() {
        favoriteCatsAdapter = new FavoriteCatsAdapter(getActivity(), model -> viewModel.deleteFavoriteCat(model));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvCats.setLayoutManager(layoutManager);
        rvCats.setAdapter(favoriteCatsAdapter);
    }


    public void onShowCats(List<CatUI> cats) {
        groupCats.setVisibility(View.VISIBLE);
        favoriteCatsAdapter.refreshData(cats);
    }


    public void onShowLoading() {
        groupError.setVisibility(View.GONE);
        groupCats.setVisibility(View.GONE);
        groupEmpty.setVisibility(View.GONE);
        groupLoading.setVisibility(View.VISIBLE);
    }


    public void onHideLoadingCats() {
        groupLoading.setVisibility(View.GONE);
    }


    public void onShowErrorLoading() {
        groupError.setVisibility(View.VISIBLE);
    }


    public void onEmptyList() {
        groupEmpty.setVisibility(View.VISIBLE);
    }


    public void onShowErrorDeleting() {
        Toast.makeText(getActivity(), getString(R.string.error_delete_cat), Toast.LENGTH_SHORT).show();
    }


    public void onShowSuccessDeleting(CatUI catUI) {
        favoriteCatsAdapter.deleteData(catUI);
    }

    @OnClick(R.id.ibRetry)
    public void onRetry() {
        viewModel.loadFavoriteCats();
    }
}
