package edu.technopark.arquest.quest.game;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.model.Quest;

@Module
public class QuestModule {
    @Inject
    GameModule gameModule;

    @Inject
    Context context;

    @Provides
    @Singleton
    public QuestModule provideQuestModule() {
        App.getAppComponent().inject(this);
        return this;
    }

    public List<Quest> getQuests() {
        Quest q = new Quest(
                2,
                "Тайна старого черепа",
                "Вы попадаете в очень странное место с загадочными персонажами. Ваша цель " +
                        "покинуть это место. Распросив разных существ, вы понимаете, что вам нужна таинственная карта," +
                        "которую можно найти у местного свина-торговца. Удастся ли вам договориться и выбраться?", 3
        );

        if (gameModule.isWithAR()) {
            q.addPlace(getSkullPlace());
        } else {
            q.addPlace(new Place());
        }

        return Collections.singletonList(q);
    }

    public Quest getIntroQuest() {
        Quest q = new Quest(
                2,
                "Обучающий квест",
                "Обучающий квест", 3
        );

        if (gameModule.isWithAR()) {
            q.addPlace(getIntroPlace());
        } else {
            q.addPlace(new Place());
        }
        return q;
    }

    public Place getIntroPlace() {
        final float mainScale = 0.001f;
        final float smallScale = 0.0001f;
        final String assetPrefix = "file:///android_asset/";
        IntroPlaceConstructor constructor = new IntroPlaceConstructor(mainScale, smallScale, assetPrefix);
        return constructor.getPlace();
    }

    public Place getSkullPlace() {
        final float mainScale = 0.0005f;
        final float smallScale = mainScale / 3;
        final String assetPrefix = "file:///android_asset/scene/";

        SkullPlaceConstructor constructor = new SkullPlaceConstructor(mainScale, smallScale, assetPrefix);
        return constructor.getPlace();
    }
}
