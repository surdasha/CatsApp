package ru.surdasha.cats.di.modules;

import android.app.Activity;
import android.content.Context;

import javax.inject.Named;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import ru.surdasha.cats.common.Utils;
import ru.surdasha.cats.di.scopes.PerActivity;
import ru.surdasha.cats.presentation.mappers.CatUIMapper;
import ru.surdasha.cats.presentation.misc.ViewUtils;

@Module
public class UIModule {
    private Activity activity;

    public UIModule(android.app.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    @Named("ActivityContext")
    Context provideContext() {
        return activity;
    }

    @Provides
    @PerActivity
    android.app.Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    ViewUtils provideViewUtils(){
        return new ViewUtils(activity);
    }

    @NonNull
    @PerActivity
    @Provides
    CatUIMapper provideCatUIMapper(ViewUtils viewUtils, Utils utils) {
        return new CatUIMapper(viewUtils, utils);
    }

}
