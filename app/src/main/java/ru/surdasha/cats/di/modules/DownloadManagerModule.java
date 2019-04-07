package ru.surdasha.cats.di.modules;

import android.app.DownloadManager;
import android.content.Context;

import javax.inject.Named;

import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import ru.surdasha.cats.di.scopes.PerApplication;

@Module
public class DownloadManagerModule {

    @Provides
    @PerApplication
    @NonNull
    public DownloadManager provideDownloadManager(@Named("AppContext") Context context){
        return (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

}
