package ru.surdasha.cats.di;

import java.util.Map;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import ru.surdasha.cats.di.scopes.PerActivity;
import ru.surdasha.cats.di.scopes.PerApplication;
import ru.surdasha.cats.presentation.ui.favorites.FavoriteCatsViewModel;

@Module
public class ViewModelModule {

    @IntoMap
    @Provides
    @PerActivity
    @ViewModelKey(FavoriteCatsViewModel.class)
    ViewModel bindListViewModel(FavoriteCatsViewModel listViewModel){
        return new FavoriteCatsViewModel();
    }

    @Provides
    @PerActivity
    public ViewModelProvider.Factory bindViewModelFactory(Map<Class<? extends ViewModel>, ViewModel> creators){
        return new ViewModelFactory(creators);
    }
}