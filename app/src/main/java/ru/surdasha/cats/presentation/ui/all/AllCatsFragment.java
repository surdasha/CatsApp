package ru.surdasha.cats.presentation.ui.all;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import ru.surdasha.cats.R;
import ru.surdasha.cats.presentation.models.CatUI;
import ru.surdasha.cats.presentation.ui.BaseFragment;
import ru.surdasha.cats.presentation.ui.main.MainActivity;

public class AllCatsFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    public static final String TAG = AllCatsFragment.class.getSimpleName();

    private static final String STATE_POSITION_OFFSET = "STATE_POSITION_OFFSET";
    private static final String STATE_POSITION_INDEX = "STATE_POSITION_INDEX";
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @BindView(R.id.rvCats)
    RecyclerView rvCats;
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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvLoading)
    TextView tvLoading;
    @BindView(R.id.ibRetry)
    Button ibRetry;
    @BindView(R.id.groupNext)
    Group groupNext;
    LinearLayoutManager layoutManager;
    Bundle savedInstanceState;
    @NonNull
    private AllCatsViewModel viewModel;
    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        initViewModel();
        subscribeToDataStates();
        subscribeToData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_cats, container, false);
        bindBaseUI(view);
        setUpAdapter();
        setUpOnScrollListener();
        setUpSwipeRefresh();
        setUpPreload();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).getUIComponent().inject(this);
        viewModel.loadAllCats();
        subscribeToDownloadManager();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AllCatsViewModel.class);
    }

    private void subscribeToData() {
        viewModel.getAllCatsList().observe(this, catUIS -> {
            if (catUIS.isEmpty()){
                onEmptyList();
            }else {
                onSuccessLoading(catUIS);
            }
        });
        viewModel.getAddedCat().observe(this, catUI -> {
            onSuccessAddToFavorites();
        });
        viewModel.getNextCatsList().observe(this, catUIS -> {
            if (!catUIS.isEmpty())
            onSuccessNextLoading(catUIS);
        });
    }

    private void subscribeToDataStates() {
        viewModel.getCatsLoadingAllState().observe(this, state -> {
            if (state.isLoading()) {
                onStartFirstLoading();
            } else {
                onEndFirstLoading();
                if (state.isError()) {
                    onErrorFirstLoading();
                }
            }
        });
        viewModel.getCatAddingState().observe(this, state -> {
            if (state.isError()) {
                onErrorAddToFavorites();
            }else{
                onSuccessAddToFavorites();
            }
        });
        viewModel.getCatsRefreshingState().observe(this, state -> {
            if (state.isLoading()) {
                onStartRefreshing();
            } else {
                onEndRefreshing();
                if (state.isError()) {
                    onErrorRefreshing();
                }
            }
        });
        viewModel.getCatsDownloadImageState().observe(this, state -> {
            if (state.isSuccess()) {
                onStartImageDownload();
            } else {
                if (state.isError()) {
                    onErrorImageDownload();
                }
            }
        });
        viewModel.getCatsLoadingNextState().observe(this, state -> {
            if (state.isLoading()) {
                onStartNextLoading();
            } else {
                onEndNextLoading();
                if (state.isError()) {
                    onErrorNextLoading();
                }
            }
        });
    }

    private void setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshCats());
    }

    private void setUpAdapter() {
        allCatsAdapter = new AllCatsAdapter(getActivity());
        allCatsAdapter.setOnDownloadClickListener(catUI -> {
            onDownloadClick(catUI);
        });
        allCatsAdapter.setOnLikeClickListener(catUI -> {
            viewModel.addToFavorite(catUI);
        });
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvCats.setLayoutManager(layoutManager);
        rvCats.setAdapter(allCatsAdapter);
    }

    private void setUpPreload() {
        final int maxPreloadCount = 10;
        ListPreloader.PreloadSizeProvider sizeProvider =
                new AllCatsPreloadSizeProvider();
        ListPreloader.PreloadModelProvider modelProvider = new AllCatsPreloadModelProvider(getActivity(), allCatsAdapter.getItems());
        RecyclerViewPreloader<String> preloader =
                new RecyclerViewPreloader<String>(this.getActivity(), modelProvider, sizeProvider, maxPreloadCount);
        rvCats.addOnScrollListener(preloader);
    }

    private void setUpOnScrollListener() {
        rvCats.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewModel.onScrolled(
                        layoutManager.getItemCount(),
                        layoutManager.findLastVisibleItemPosition()
                );
            }
        });
    }

    private void subscribeToDownloadManager() {
        getActivity().registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void onDownloadClick(CatUI catUI) {
        //save cat to variable due to permissions check
        viewModel.setTempImageDownloadCat(catUI);
        if (viewModel.checkPermissionsRequired()) {
            checkForPermission();
        } else {
            viewModel.downloadImage();
        }
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            for (CatUI catUI : allCatsAdapter.getItems()) {
                if (catUI.getTempDownloadId() != 0 && catUI.getTempDownloadId() == id) {
                    onSuccessImageDownload();
                }
            }
        }
    };

    public void onSuccessLoading(List<CatUI> cats) {
        groupCats.setVisibility(View.VISIBLE);
        allCatsAdapter.refreshData(cats);
        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(STATE_POSITION_INDEX);
            int offset = savedInstanceState.getInt(STATE_POSITION_OFFSET);
            layoutManager.scrollToPositionWithOffset(index, offset);
        }

    }

    public void onStartFirstLoading() {
        groupError.setVisibility(View.GONE);
        groupCats.setVisibility(View.GONE);
        groupEmpty.setVisibility(View.GONE);
        groupLoading.setVisibility(View.VISIBLE);
    }

    public void onEndFirstLoading() {
        groupLoading.setVisibility(View.GONE);
    }

    public void onErrorFirstLoading() {
        groupError.setVisibility(View.VISIBLE);
    }

    public void onEmptyList() {
        groupEmpty.setVisibility(View.VISIBLE);
    }

    public void onErrorAddToFavorites() {
        Toast.makeText(getActivity(), getString(R.string.cant_save_to_favorites), Toast.LENGTH_SHORT).show();
    }

    public void onStartImageDownload() {
        Toast.makeText(getActivity(), getString(R.string.image_load_start), Toast.LENGTH_SHORT).show();
    }

    public void onSuccessImageDownload() {
        Toast.makeText(getActivity(), getString(R.string.image_load_success), Toast.LENGTH_SHORT).show();
    }

    public void onErrorImageDownload() {
        Toast.makeText(getActivity(), getString(R.string.error_load_image), Toast.LENGTH_SHORT).show();
    }

    public void onStartRefreshing() {
        swipeRefreshLayout.setRefreshing(true);
    }

    public void onEndRefreshing() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onErrorRefreshing() {
        Toast.makeText(getActivity(), getString(R.string.error_refresh_cats), Toast.LENGTH_SHORT).show();
    }

    public void onSuccessNextLoading(List<CatUI> cats) {
        allCatsAdapter.addData(cats);
    }

    public void onStartNextLoading() {
        groupNext.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
    }

    public void onEndNextLoading() {
        groupNext.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(true);
    }

    public void onErrorNextLoading() {
        Toast.makeText(getActivity(), getString(R.string.error_get_next_cats), Toast.LENGTH_SHORT).show();
    }

    public void onSuccessAddToFavorites() {
        Toast.makeText(getActivity(), getString(R.string.success_saving_favorites), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ibRetry)
    public void onRetry() {
        viewModel.loadAllCats();
    }


    public void checkForPermission() {
        if (isPermissionGranted()) {
            viewModel.downloadImage();
        } else {
            requestRuntimePermission();
        }
    }

    private boolean isPermissionGranted() {
        return EasyPermissions.hasPermissions(this.getActivity(), WRITE_PERMISSION);
    }

    private void requestRuntimePermission() {
        EasyPermissions.requestPermissions(this, getString(R.string.give_permission_load),
                REQUEST_PERMISSION_CODE, WRITE_PERMISSION);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (isPermissionGranted()) {
            viewModel.downloadImage();
        } else {
            requestRuntimePermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (rvCats != null) {
            int index = layoutManager.findFirstVisibleItemPosition();
            View topView = rvCats.getChildAt(0);
            int offset = topView != null ? topView.getTop() : 0;
            bundle.putInt(STATE_POSITION_INDEX, index);
            bundle.putInt(STATE_POSITION_OFFSET, offset);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(onDownloadComplete);
    }
}
