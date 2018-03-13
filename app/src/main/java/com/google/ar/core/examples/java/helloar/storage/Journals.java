package com.google.ar.core.examples.java.helloar.storage;

import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.network.Api;

import java.util.HashMap;
import java.util.Map;

public class Journals {
    private Map<Integer, Journal<String>> journals;

    public Journals() {
        journals = new HashMap<>();
    }

    public void addJournal(Integer id, Journal journal) {
        journals.put(id, journal);
    }

    public void addCurrentJournal(Journal journal) {
        journals.put(Api.getCurrentQuestId(), journal);
    }

    public Journal getJournal(Integer id) {
        return journals.get(id);
    }

    public Journal getCurrentJournal() {
        return this.getJournal(Api.getCurrentQuestId());
    }
}
