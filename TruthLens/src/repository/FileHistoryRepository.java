package repository;

import model.AnalysisRecord;
import util.FileManager;
import exception.FileStorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * File-backed implementation of IRepository<AnalysisRecord>.
 * Persists records to data/history.txt using a pipe-delimited format.
 *
 * Demonstrates: interface implementation, file I/O, generics, error handling.
 *
 * Record format per line:
 *   id|timestamp|headline|riskScore|classification
 *
 * Pipes within headlines are escaped as {{PIPE}}.
 */
public class FileHistoryRepository implements IRepository<AnalysisRecord> {

    private static final String HISTORY_FILE = "data" + java.io.File.separator + "history.txt";

    /** In-memory cache, loaded on first access. */
    private List<AnalysisRecord> cache;

    public FileHistoryRepository() {
        this.cache = null; // lazy-loaded
    }

    @Override
    public void save(AnalysisRecord record) {
        ensureLoaded();
        cache.add(record);
        persistAll();
    }

    @Override
    public List<AnalysisRecord> getAll() {
        ensureLoaded();
        return new ArrayList<>(cache); // return a defensive copy
    }

    @Override
    public AnalysisRecord findById(String id) {
        ensureLoaded();
        for (AnalysisRecord record : cache) {
            if (record.getId().equalsIgnoreCase(id)) {
                return record;
            }
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        ensureLoaded();
        boolean removed = cache.removeIf(r -> r.getId().equalsIgnoreCase(id));
        if (removed) persistAll();
        return removed;
    }

    @Override
    public void clear() {
        cache = new ArrayList<>();
        persistAll();
    }

    // ---- Private helpers ---------------------------------------------------

    /** Load records from file into cache (only once). */
    private void ensureLoaded() {
        if (cache != null) return;
        cache = new ArrayList<>();
        List<String> lines;
        try {
            lines = FileManager.readLines(HISTORY_FILE);
        } catch (FileStorageException e) {
            // Non-fatal — start with empty history
            System.err.println("[Warning] Could not read history file: " + e.getMessage());
            return;
        }
        int skipped = 0;
        for (String line : lines) {
            AnalysisRecord record = AnalysisRecord.fromStorageString(line);
            if (record != null) {
                cache.add(record);
            } else {
                skipped++;
            }
        }
        if (skipped > 0) {
            System.err.println("[Warning] Skipped " + skipped + " corrupted record(s) in history file.");
        }
    }

    /** Write all cached records back to the file. */
    private void persistAll() {
        List<String> lines = new ArrayList<>();
        for (AnalysisRecord record : cache) {
            lines.add(record.toStorageString());
        }
        try {
            FileManager.writeLines(HISTORY_FILE, lines);
        } catch (FileStorageException e) {
            System.err.println("[Error] Could not save history: " + e.getMessage());
        }
    }
}
