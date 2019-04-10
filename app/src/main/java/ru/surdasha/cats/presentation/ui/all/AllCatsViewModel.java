package ru.surdasha.cats.presentation.ui.all;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import ru.surdasha.cats.common.AndroidUtils;
import ru.surdasha.cats.domain.usecases.AddCatUseCase;
import ru.surdasha.cats.domain.usecases.DownloadImageUseCase;
import ru.surdasha.cats.domain.usecases.GetAllCatsUseCase;
import ru.surdasha.cats.domain.usecases.GetNextCatsUseCase;
import ru.surdasha.cats.domain.usecases.RefreshCatsUseCase;
import ru.surdasha.cats.presentation.mappers.CatUIMapper;
import ru.surdasha.cats.presentation.models.CatUI;
import ru.surdasha.cats.presentation.models.State;

public class AllCatsViewModel extends ViewModel {
    private final CompositeDisposable actionsDisposable = new CompositeDisposable();
    private final CompositeDisposable gettingCatsDisposable = new CompositeDisposable();
    private final GetAllCatsUseCase getAllCatsUseCase;
    private final RefreshCatsUseCase refreshCatsUseCase;
    private final GetNextCatsUseCase getNextCatsUseCase;
    private final DownloadImageUseCase downloadImageUseCase;
    private final AddCatUseCase addCatUseCase;
    private final CatUIMapper catUIMapper;
    private final AndroidUtils androidUtils;

    private final MutableLiveData<List<CatUI>> allCatsList = new MutableLiveData<>();
    private final MutableLiveData<List<CatUI>> nextCatsList = new MutableLiveData<>();
    private final MutableLiveData<CatUI> addedCat = new MutableLiveData<>();
    private final MutableLiveData<State> catAddingState = new MutableLiveData<>();
    private final MutableLiveData<State> catsLoadingAllState = new MutableLiveData<>();
    private final MutableLiveData<State> catsLoadingNextState = new MutableLiveData<>();
    private final MutableLiveData<State> catsRefreshingState = new MutableLiveData<>();
    private final MutableLiveData<State> catsDownloadImageState = new MutableLiveData<>();

    public MutableLiveData<List<CatUI>> getAllCatsList() { return allCatsList; }
    public MutableLiveData<CatUI> getAddedCat() { return addedCat; }
    public MutableLiveData<State> getCatAddingState() { return catAddingState; }
    public MutableLiveData<State> getCatsLoadingAllState() { return catsLoadingAllState; }
    public MutableLiveData<State> getCatsLoadingNextState() { return catsLoadingNextState; }
    public MutableLiveData<State> getCatsRefreshingState() { return catsRefreshingState; }
    public MutableLiveData<State> getCatsDownloadImageState() { return catsDownloadImageState; }

    private final static int SCROLL_THRESHOLD = 2;
    private PublishProcessor<Integer> scrollProcessor = PublishProcessor.create();
    private CatUI tempImageDownloadCat;

    @Inject
    public AllCatsViewModel(GetAllCatsUseCase getAllCatsUseCase, RefreshCatsUseCase refreshCatsUseCase,
                            GetNextCatsUseCase getNextCatsUseCase, DownloadImageUseCase downloadImageUseCase,
                            AddCatUseCase addCatUseCase, CatUIMapper catUIMapper, AndroidUtils androidUtils) {
        this.getAllCatsUseCase = getAllCatsUseCase;
        this.refreshCatsUseCase = refreshCatsUseCase;
        this.getNextCatsUseCase = getNextCatsUseCase;
        this.downloadImageUseCase = downloadImageUseCase;
        this.addCatUseCase = addCatUseCase;
        this.catUIMapper = catUIMapper;
        this.androidUtils = androidUtils;
    }

    public void loadAllCats() {
        unsubscribe();
        Disposable disposable = getAllCatsUseCase.getAllCats()
                .map(cats -> catUIMapper.domainToUI(cats))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> {
                    catsDownloadImageState.setValue(new State().loading());
                })
                .doOnTerminate(() -> {

                })
                .subscribe(cats -> {
                    catsLoadingAllState.setValue(new State().success());
                    allCatsList.setValue(cats);
                    subscribeToNextCats();
                }, throwable -> {
                    catsLoadingAllState.setValue(new State().error(throwable));
                    subscribeToNextCats();
                }, () -> {
                    catsLoadingAllState.setValue(new State().success());
                    subscribeToNextCats();
                });
        gettingCatsDisposable.add(disposable);
    }

    public void refreshCats() {
        unsubscribe();
        Disposable disposable = refreshCatsUseCase.getRefreshedCats()
                .map(cats -> catUIMapper.domainToUI(cats))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 ->  {
                    catsRefreshingState.setValue(new State().loading());
                })
                .doOnTerminate(() -> {

                })
                .subscribe(cats -> {
                    allCatsList.setValue(cats);
                    catsRefreshingState.setValue(new State().success());
                    subscribeToNextCats();
                }, throwable -> {
                    catsRefreshingState.setValue(new State().error(throwable));
                    subscribeToNextCats();
                });
        gettingCatsDisposable.add(disposable);
    }

    public void subscribeToNextCats() {
        unsubscribe();
        Disposable disposable = scrollProcessor
                .onBackpressureDrop()
                .concatMap(page -> {
                    catsLoadingNextState.setValue(new State().loading());
                    return getNextCatsUseCase.getNextCats()
                            .subscribeOn(Schedulers.io())
                            .toFlowable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnTerminate(() -> {

                            });
                })
                .toObservable()
                .map(cat -> catUIMapper.domainToUI(cat))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cats -> {
                    nextCatsList.setValue(cats);
                    catsLoadingNextState.setValue(new State().success());
                    subscribeToNextCats();
                }, throwable -> {
                    catsLoadingNextState.setValue(new State().error(throwable));
                    subscribeToNextCats();
                });
        gettingCatsDisposable.add(disposable);
    }

    public void addToFavorite(CatUI catUI) {
        catAddingState.setValue(new State().loading());
        Disposable disposable = addCatUseCase.addCat(catUIMapper.uiToDomain(catUI))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    addedCat.setValue(catUI);
                    catAddingState.setValue(new State().success());
                }, throwable -> {
                    catAddingState.setValue(new State().error(throwable));
                });
        actionsDisposable.add(disposable);
    }

    public void downloadImage() {
        catsDownloadImageState.setValue(new State().loading());
        Disposable disposable = downloadImageUseCase.downloadImage(catUIMapper.uiToDomain(tempImageDownloadCat))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(downloadId -> {
                    tempImageDownloadCat.setTempDownloadId(downloadId);
                    catsDownloadImageState.setValue(new State().success());
                }, throwable -> {
                    catsDownloadImageState.setValue(new State().error(throwable));
                });
        actionsDisposable.add(disposable);
    }

    public void onScrolled(int count, int lastVisibleItemIndex) {
        if (count <= (lastVisibleItemIndex + SCROLL_THRESHOLD)) {
            scrollProcessor.onNext(count);
        }
    }

    public void setTempImageDownloadCat(CatUI catUI){
        this.tempImageDownloadCat = catUI;
    }

    public boolean checkPermissionsRequired(){
        return androidUtils.checkRequiredPermission();
    }

    public void unsubscribe() {
        gettingCatsDisposable.clear();
    }

    protected void onCleared(){
        super.onCleared();
        actionsDisposable.clear();
    }

    public MutableLiveData<List<CatUI>> getNextCatsList() {
        return nextCatsList;
    }
}
