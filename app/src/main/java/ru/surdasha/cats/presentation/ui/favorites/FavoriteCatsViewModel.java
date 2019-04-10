package ru.surdasha.cats.presentation.ui.favorites;

import java.util.List;

import javax.inject.Inject;

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
import ru.surdasha.cats.presentation.models.State;

public class FavoriteCatsViewModel extends ViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final GetFavoriteCatsUseCase getFavoriteCatsUseCase;
    private final DeleteCatUseCase deleteCatUseCase;
    private final CatUIMapper catUIMapper;

    private final MutableLiveData<List<CatUI>> catsList = new MutableLiveData<>();
    private final MutableLiveData<CatUI> deletedCat = new MutableLiveData<>();
    private final MutableLiveData<State> catsDeletingState = new MutableLiveData<>();
    private final MutableLiveData<State> catsLoadingState = new MutableLiveData<>();

    @Inject
    public FavoriteCatsViewModel(CatUIMapper catUIMapper, DeleteCatUseCase deleteCatUseCase, GetFavoriteCatsUseCase getFavoriteCatsUseCase) {
        this.catUIMapper = catUIMapper;
        this.deleteCatUseCase = deleteCatUseCase;
        this.getFavoriteCatsUseCase = getFavoriteCatsUseCase;
    }

    public MutableLiveData<State> getCatsLoadingState() {
        return catsLoadingState;
    }
    public MutableLiveData<State> getCatsDeletingState() {
        return catsDeletingState;
    }
    public MutableLiveData<List<CatUI>> getCatsList() {
        return catsList;
    }
    public MutableLiveData<CatUI> getDeleteCat() {
        return deletedCat;
    }

    public void loadFavoriteCats(){
        catsLoadingState.setValue(new State().loading());
        Disposable disposable = getFavoriteCatsUseCase.getFavoriteCats()
                .map(cats -> catUIMapper.domainToUI(cats))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cats -> {
                    this.catsLoadingState.setValue(new State().success());
                    catsList.setValue(cats);
                },throwable -> {
                    catsLoadingState.setValue(new State().error(throwable));
                }, () -> {
                    catsLoadingState.setValue(new State().success());
                });
        compositeDisposable.add(disposable);
    }

    public void deleteFavoriteCat(CatUI catUI){
        catsDeletingState.setValue(new State().loading());
        deleteCatUseCase.deleteCat(catUIMapper.uiToDomain(catUI))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    catsDeletingState.setValue(new State().success());
                    deletedCat.setValue(catUI);
                },throwable -> {
                    catsDeletingState.setValue(new State().error(throwable));
                });
    }

    protected void onCleared(){
        super.onCleared();
        compositeDisposable.clear();
    }

}
