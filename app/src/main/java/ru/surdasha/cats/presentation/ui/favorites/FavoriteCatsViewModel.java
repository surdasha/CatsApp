package ru.surdasha.cats.presentation.ui.favorites;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.surdasha.cats.domain.usecases.DeleteCatUseCase;
import ru.surdasha.cats.domain.usecases.GetFavoriteCatsUseCase;
import ru.surdasha.cats.presentation.mappers.CatUIMapper;
import ru.surdasha.cats.presentation.models.CatUI;

public class FavoriteCatsViewModel extends ViewModel {
    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Inject
    GetFavoriteCatsUseCase getFavoriteCatsUseCase;
    @Inject
    DeleteCatUseCase deleteCatUseCase;
    @Inject
    CatUIMapper catUIMapper;
    private final MutableLiveData<List<CatUI>> cats = new MutableLiveData<>();
    private final MutableLiveData<Throwable> loadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    @Inject
    public FavoriteCatsViewModel() {
    }

    public void getCats(){
        loading.setValue(true);
        Disposable disposable = getFavoriteCatsUseCase.getFavoriteCats()
                .map(cats -> catUIMapper.domainToUI(cats))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cats -> {
                    loading.setValue(false);
                    this.cats.setValue(cats);
                },throwable -> {
                    loading.setValue(false);
                    loadError.setValue(throwable);
                }, () -> {
                    loading.setValue(false);
                    this.cats.setValue(new ArrayList<>());
                });
        compositeDisposable.add(disposable);
    }

    public MutableLiveData<List<CatUI>> getCatsObservable(){
        return cats;
    }
    public MutableLiveData<Throwable> getLoadError() {
        return loadError;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }
    public void deleteFromFavorite(CatUI catUI){
        deleteCatUseCase.deleteCat(catUIMapper.uiToDomain(catUI))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {

                },throwable -> {

                });
    }

}
