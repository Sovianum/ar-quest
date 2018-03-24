package com.google.ar.core.examples.java.helloar.storage;

import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;

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
