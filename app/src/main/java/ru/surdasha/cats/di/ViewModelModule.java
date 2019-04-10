package ru.surdasha.cats.di;

import java.util.Map;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import ru.surdasha.cats.common.AndroidUtils;
import ru.surdasha.cats.di.scopes.PerActivity;
import ru.surdasha.cats.domain.usecases.AddCatUseCase;
import ru.surdasha.cats.domain.usecases.DeleteCatUseCase;
import ru.surdasha.cats.domain.usecases.DownloadImageUseCase;
import ru.surdasha.cats.domain.usecases.GetAllCatsUseCase;
import ru.surdasha.cats.domain.usecases.GetFavoriteCatsUseCase;
import ru.surdasha.cats.domain.usecases.GetNextCatsUseCase;
import ru.surdasha.cats.domain.usecases.RefreshCatsUseCase;
import ru.surdasha.cats.presentation.mappers.CatUIMapper;
import ru.surdasha.cats.presentation.ui.all.AllCatsViewModel;
import ru.surdasha.cats.presentation.ui.favorites.FavoriteCatsViewModel;

@Module
public class ViewModelModule {

    @IntoMap
    @Provides
    @PerActivity
    @ViewModelKey(FavoriteCatsViewModel.class)
    ViewModel provideFavoriteCatsViewModel(CatUIMapper catUIMapper, DeleteCatUseCase deleteCatUseCase,
                                           GetFavoriteCatsUseCase getFavoriteCatsUseCase){
        return new FavoriteCatsViewModel(catUIMapper, deleteCatUseCase, getFavoriteCatsUseCase);
    }

    @IntoMap
    @Provides
    @PerActivity
    @ViewModelKey(AllCatsViewModel.class)
    ViewModel provideAllCatsViewModel(GetAllCatsUseCase getAllCatsUseCase, RefreshCatsUseCase refreshCatsUseCase,
                                      GetNextCatsUseCase getNextCatsUseCase, DownloadImageUseCase downloadImageUseCase,
                                      AddCatUseCase addCatUseCase, CatUIMapper catUIMapper, AndroidUtils androidUtils ){
        return new AllCatsViewModel(getAllCatsUseCase, refreshCatsUseCase, getNextCatsUseCase, downloadImageUseCase,
                addCatUseCase, catUIMapper, androidUtils);
    }

    @Provides
    @PerActivity
    public ViewModelProvider.Factory bindViewModelFactory(Map<Class<? extends ViewModel>, ViewModel> creators){
        return new ViewModelFactory(creators);
    }
}