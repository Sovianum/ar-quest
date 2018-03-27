package technopark.diploma.arquest.storage;

import technopark.diploma.arquest.core.game.journal.Journal;

import java.util.HashMap;
import java.util.Map;

public class Journals {
    private Map<Integer, Journal<String>> journals;

    public Journals() {
        journals = new HashMap<>();
    }

    public void addJournal(Integer id, Journal<String> journal) {
        journals.put(id, journal);
    }

    public Journal<String> getJournal(Integer id) {
        return journals.get(id);
    }
}
