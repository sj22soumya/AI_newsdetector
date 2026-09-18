package service;

import model.AnalysisRecord;
import repository.FileHistoryRepository;
import repository.IRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for managing analysis history.
 * Wraps the FileHistoryRepository and adds search/filter logic.
 *
 * Demonstrates: service layer, repository pattern, generics, collections.
 */
public class HistoryService {

    private final IRepository<AnalysisRecord> repository;

    public HistoryService() {
        this.repository = new FileHistoryRepository();
    }

    /** Save a new analysis record to history. */
    public void saveRecord(AnalysisRecord record) {
        repository.save(record);
    }

    /** Get all records in chronological order. */
    public List<AnalysisRecord> getAllRecords() {
        return repository.getAll();
    }

    /** Find a record by its unique ID. Returns null if not found. */
    public AnalysisRecord findById(String id) {
        return repository.findById(id);
    }

    /**
     * Search records by headline keyword (case-insensitive).
     * Returns all records where the headline contains the keyword.
     */
    public List<AnalysisRecord> searchByHeadline(String keyword) {
        List<AnalysisRecord> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (AnalysisRecord record : repository.getAll()) {
            if (record.getHeadline().toLowerCase().contains(lowerKeyword)) {
                results.add(record);
            }
        }
        return results;
    }

    /** Delete a specific record by ID. Returns true if deleted, false if not found. */
    public boolean deleteRecord(String id) {
        return repository.delete(id);
    }

    /** Clear all history records. */
    public void clearHistory() {
        repository.clear();
    }

    /** Return the total number of stored records. */
    public int getRecordCount() {
        return repository.getAll().size();
    }
}
