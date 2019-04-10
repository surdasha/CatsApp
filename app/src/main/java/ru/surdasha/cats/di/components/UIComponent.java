package ru.surdasha.cats.di.components;

import dagger.Subcomponent;
import ru.surdasha.cats.di.ViewModelModule;
import ru.surdasha.cats.di.modules.UIModule;
import ru.surdasha.cats.di.scopes.PerActivity;
import ru.surdasha.cats.presentation.ui.all.AllCatsFragment;
import ru.surdasha.cats.presentation.ui.favorites.FavoriteCatsFragment;
import ru.surdasha.cats.presentation.ui.main.MainActivity;

@PerActivity
@Subcomponent(modules = {UIModule.class, ViewModelModule.class})
public interface UIComponent {

    void inject(MainActivity activity);
    void inject(FavoriteCatsFragment favoriteCatsFragment);
    void inject(AllCatsFragment allCatsFragment);
}
