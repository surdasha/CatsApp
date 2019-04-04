package ru.surdasha.cats.presentation.ui.all;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import ru.surdasha.cats.R;
import ru.surdasha.cats.presentation.models.CatUI;
import ru.surdasha.cats.presentation.ui.BaseFragment;

public class AllCatsFragment extends BaseFragment implements AllCatsView {

    @BindView(R.id.rvCats)
    RecyclerView rvCats;
    @InjectPresenter
    AllCatsPresenter allCatsPresenter;
    AllCatsAdapter allCatsAdapter;
    @BindView(R.id.groupCats)
    Group groupCats;
    @BindView(R.id.groupEmpty)
    Group groupEmpty;
    @BindView(R.id.groupLoading)
    Group groupLoading;
    @BindView(R.id.groupError)
    Group groupError;
    @BindView(R.id.swRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_cats, container, false);
        bindBaseUI(view);
        setUpAdapter();
        setUpOnScrollListener();
        setUpSwipeRefresh();
        allCatsPresenter.getFirstCats();
        allCatsPresenter.subscribeToNextCats();
        return view;
    }

    private void setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> allCatsPresenter.refreshCats());
    }

    private void setUpAdapter() {
        allCatsAdapter = new AllCatsAdapter(getActivity());
        allCatsAdapter.setOnDownloadClickListener(catUI -> {
            allCatsPresenter.downloadImage(catUI);
        });
        allCatsAdapter.setOnLikeClickListener(catUI -> {
            allCatsPresenter.addToFavorite(catUI);
        });
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvCats.setLayoutManager(layoutManager);
        rvCats.setAdapter(allCatsAdapter);
    }

    private void setUpOnScrollListener() {
        rvCats.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                allCatsPresenter.onScrolled(
                        layoutManager.getItemCount(),
                        layoutManager.findLastVisibleItemPosition()
                );
            }
        });
    }

    @Override
    public void onSuccessLoading(List<CatUI> cats) {
        groupCats.setVisibility(View.VISIBLE);
        allCatsAdapter.refreshData(cats);
    }

    @Override
    public void onStartFirstLoading() {
        groupError.setVisibility(View.GONE);
        groupCats.setVisibility(View.GONE);
        groupEmpty.setVisibility(View.GONE);
        groupLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEndFirstLoading() {
        groupLoading.setVisibility(View.GONE);
    }

    @Override
    public void onErrorFirstLoading() {
        groupError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEmptyList() {
        groupEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorAddToFavorites() {
        Toast.makeText(getActivity(), "Не удалось сохранить кота в избранное", Toast.LENGTH_SHORT);
    }

    @Override
    public void onStartRefreshing() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onEndRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onErrorRefreshing() {
        Toast.makeText(getActivity(), "Не удалоcь обновить котов", Toast.LENGTH_SHORT);
    }

    @Override
    public void onSuccessNextLoading(List<CatUI> cats) {
        allCatsAdapter.addData(cats);
    }

    @Override
    public void onStartNextLoading() {
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onEndNextLoading() {
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onErrorNextLoading() {
        Toast.makeText(getActivity(), "Не удалоcь обновить котов", Toast.LENGTH_SHORT);
    }

    @Override
    public void onSuccessAddToFavorites() {
        Toast.makeText(getActivity(), "Картинка успешно сохранена в избранное", Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.ibRetry)
    public void onRetry(){
        allCatsPresenter.getFirstCats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allCatsPresenter.unsubscribe();
    }
}
