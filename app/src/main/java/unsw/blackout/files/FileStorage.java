package unsw.blackout.files;

import java.util.*;
import unsw.blackout.algorithms.KnapsackSolver;

public class FileStorage {
    private int storageCapacity;
    private int fileCapacity;
    private Map<String, File> files = new HashMap<>();
    private int incomingFiles = 0;
    private int outgoingFiles = 0;

    public FileStorage() {
        this.storageCapacity = Integer.MAX_VALUE;
        this.fileCapacity = Integer.MAX_VALUE;
    }

    public FileStorage(int storageCapacity) {
        this.storageCapacity = storageCapacity;
        this.fileCapacity = Integer.MAX_VALUE;
    }

    public FileStorage(int storageCapacity, int fileCapacity) {
        this.storageCapacity = storageCapacity;
        this.fileCapacity = fileCapacity;
    }

    public File getFile(String filename) {
        return files.get(filename);
    }

    public List<File> listFiles() {
        return new ArrayList<>(files.values());
    }

    public void addFile(String filename, String content, Boolean isComplete) {
        files.put(filename, new File(filename, content, isComplete));
    }

    public void removeFile(String filename) {
        files.remove(filename);
    }

    public int getFileSize(String filename) {
        return files.get(filename).getSize();
    }

    public String getFileContent(String filename) {
        return files.get(filename).getContent();
    }

    public int getAvailableStorage() {
        int storageUsed = files.values().stream().filter(file -> !file.isTransient()).mapToInt(File::getSize).sum();
        return storageCapacity - storageUsed;
    }

    public boolean maxStorageReached(int size) {
        if (listTransientFiles().size() > 0) {
            optimiseTransientStorage(size);
        }
        int storageUsed = files.values().stream().mapToInt(File::getSize).sum();
        return storageUsed + size > storageCapacity;
    }

    private List<File> listTransientFiles() {
        return files.values().stream().filter(file -> file.isTransient()).toList();
    }

    private void optimiseTransientStorage(int size) {
        int availableStorage = getAvailableStorage() - size;
        List<File> transientFiles = listTransientFiles();
        List<File> filesToKeep = KnapsackSolver.solveKnapsack(transientFiles, availableStorage);
        transientFiles.stream().filter(file -> !filesToKeep.contains(file))
                .forEach(file -> removeFile(file.getFileName()));
    }

    public boolean maxFilesReached() {
        return files.size() >= fileCapacity;
    }

    public int getNumIncomingFiles() {
        return incomingFiles;
    }

    public int getNumOutgoingFiles() {
        return outgoingFiles;
    }

    public void setComplete(String filename) {
        files.get(filename).setComplete();
    }

    public void setTransient(String filename, boolean isTransient) {
        files.get(filename).setTransient(isTransient);
    }

    public boolean isTransient(String filename) {
        return files.get(filename).isTransient();
    }

    public void removeRemainingTBytes(String filename, int progress) {
        files.get(filename).removeRemainingTBytes(progress);
    }

    public void removeTBytes(String filename) {
        files.get(filename).removeTBytes();
    }

    public void updateFileData(String filename, int progress) {
        files.get(filename).updateData(progress);
    }

    public void incrementIncomingFiles() {
        incomingFiles++;
    }

    public void incrementOutgoingFiles() {
        outgoingFiles++;
    }

    public void decrementIncomingFiles() {
        incomingFiles--;
    }

    public void decrementOutgoingFiles() {
        outgoingFiles--;
    }

}
