package technopark.diploma.arquest.storage.fs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import technopark.diploma.arquest.core.game.journal.Journal;
import technopark.diploma.arquest.core.game.slot.Slot;
import technopark.diploma.arquest.model.Quest;

public class QuestDir extends File {
    public static final String ASSETS_DIR = "assets";
    public static final String SCRIPT_NAME = "script.json";
    public static final String SAVE_DIR = "save";
    public static final String JOURNAL_SAVE = "journal.json";
    public static final String INVENTORY_SAVE = "inventory.json";
    public static final String QUEST_SAVE = "quest.json";

    private File saveDir;
    private File assetDir;

    public QuestDir(File parent, int id) throws IOException {
        super(parent, String.valueOf(id));
        if (exists()) {
            assetDir = new File(this, ASSETS_DIR);
            saveDir = new File(this, SAVE_DIR);
            return;
        }
        if (!mkdir()) {
            throw new IOException("failed to create quest directory");
        }

        assetDir = new File(this, ASSETS_DIR);
        if (!assetDir.exists()) {
            if(!assetDir.mkdir()) {
                throw new IOException("failed to create asset directory");
            }
        }

        saveDir = new File(this, SAVE_DIR);
        if (!saveDir.exists()) {
            if (!saveDir.mkdir()) {
                throw new IOException("failed to create save directory");
            }
        }
    }

    public Bitmap loadBitmapAsset(String name) throws FileNotFoundException {
        File assetFile = new File(this, getAssetFullName(name));
        if (!assetFile.exists()) {
            return null;
        }
        return BitmapFactory.decodeStream(new FileInputStream(assetFile));
    }

    public Obj loadObjAsset(String name) throws IOException {
        File assetFile = new File(this, getAssetFullName(name));
        if (!assetFile.exists()) {
            return null;
        }
        return ObjReader.read(new FileInputStream(assetFile));
    }

    public void saveAsset(byte[] data, String name) throws IOException {
        File file = new File(assetDir, name);
        OutputStream out = new FileOutputStream(file);
        out.write(data);
    }

    public Quest loadQuest(Gson gson) throws FileNotFoundException {
        return readFromJson(Quest.class, this, SCRIPT_NAME, gson);
    }

    public void saveQuestSave(Quest quest, Gson gson) throws IOException {
        writeToJson(quest, saveDir, QUEST_SAVE, gson);
    }

    public Quest loadQuestSave(Gson gson) throws FileNotFoundException {
        return readFromJson(Quest.class, saveDir, QUEST_SAVE, gson);
    }

    public void saveInventory(Slot inventory, Gson gson) throws IOException {
        writeToJson(inventory, saveDir, INVENTORY_SAVE, gson);
    }

    public Slot loadInventory(Gson gson) throws FileNotFoundException {
        return readFromJson(Slot.class, saveDir, INVENTORY_SAVE, gson);
    }

    public void saveJournal(Journal<String> journal, Gson gson) throws IOException {
        writeToJson(journal, saveDir, JOURNAL_SAVE, gson);
    }

    public Journal loadJournal(Gson gson) throws FileNotFoundException {
        return readFromJson(Journal.class, saveDir, JOURNAL_SAVE, gson);
    }

    private <T> T readFromJson(Class<T> klass, File parent, String name, Gson gson) throws FileNotFoundException {
        File file = new File(parent, name);
        if (!file.exists()) {
            return null;
        }
        InputStream in = new FileInputStream(file);
        Reader reader = new InputStreamReader(in);
        return gson.fromJson(reader, klass);
    }

    private <T> void writeToJson(T obj, File parent, String name, Gson gson) throws IOException {
        File file = new File(parent, name);
        OutputStream out = new FileOutputStream(file);
        out.write(gson.toJson(obj).getBytes());
    }

    private static String getAssetFullName(String name) {
        return ASSETS_DIR + "/" + name;
    }
}
