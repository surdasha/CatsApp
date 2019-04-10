package ru.surdasha.cats.di.components;

import dagger.Component;
import ru.surdasha.cats.di.modules.AndroidModule;
import ru.surdasha.cats.di.modules.ContextModule;
import ru.surdasha.cats.di.modules.DataModule;
import ru.surdasha.cats.di.modules.DomainModule;
import ru.surdasha.cats.di.modules.UIModule;
import ru.surdasha.cats.di.modules.UtilsModule;
import ru.surdasha.cats.di.scopes.PerApplication;

@PerApplication
@Component(modules = {AndroidModule.class, ContextModule.class,
        DataModule.class, DomainModule.class, UtilsModule.class})
public interface AppComponent {
    UIComponent addUIComponent(UIModule module);
    ContextModule contextModule(ContextModule context);
}
