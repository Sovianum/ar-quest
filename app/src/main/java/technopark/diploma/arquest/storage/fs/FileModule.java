package technopark.diploma.arquest.storage.fs;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileModule {
    public static final String QUESTS_DIR = "quests";
    private File root;
    private File questsDir;

    @Provides
    @Singleton
    public FileModule provideFileService(Context context) {
        return new FileModule(context);
    }

    public FileModule(Context context) {
        root = context.getFilesDir();
        questsDir = new File(root, QUESTS_DIR);
        if (!questsDir.exists()) {
            if (!questsDir.mkdir()) {
                throw new RuntimeException("failed to create quests dir");
            }
        }
    }

    // gets or creates quest data directory
    public QuestDir getQuestDir(int questID) throws IOException {
        return new QuestDir(questsDir, questID);
    }

    public boolean hasQuestInfo(int questID) {
        return new File(questsDir, String.valueOf(questID)).exists();
    }
}
