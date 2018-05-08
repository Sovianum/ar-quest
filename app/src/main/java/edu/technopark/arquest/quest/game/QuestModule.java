package edu.technopark.arquest.quest.game;

import android.content.Context;

import com.viro.core.Object3D;
import com.viro.core.PhysicsBody;
import com.viro.core.PhysicsShapeSphere;
import com.viro.core.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.technopark.arquest.App;
import edu.technopark.arquest.GameModule;
import edu.technopark.arquest.R;
import edu.technopark.arquest.common.CollectionUtils;
import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.script.ActionCondition;
import edu.technopark.arquest.game.script.ObjectState;
import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.Quest;
import edu.technopark.arquest.model.VisualResource;

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
                "Тайна пьяного черепа",
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
