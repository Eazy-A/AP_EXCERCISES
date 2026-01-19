package e3;

import java.util.*;
import java.util.stream.Collectors;

class FileNameExistsException extends Exception {
    public FileNameExistsException(String fileName, String folderName) {
        super("There is already a file named " + fileName + " in the folder " + folderName);
    }
}

interface IFile {
    String getFileName();

    long getFileSize();

    String getFileInfo(int indent);

    void sortBySize();

    long findLargestFile();

}
class File implements IFile {
    private String name;
    private long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(int indent) {
        return String.format("File name: %10s File size: %10d\n", name, getFileSize());
    }

    @Override
    public void sortBySize() {
    }

    @Override
    public long findLargestFile() {
        return this.size;
    }
}

class Folder implements IFile {
    private String name;
    private Map<String, IFile> files = new LinkedHashMap<>();

    public Folder(String name) {
        this.name = name;
    }

    public void addFile(IFile file) throws FileNameExistsException {
        if (files.containsKey(file.getFileName())) throw new FileNameExistsException(file.getFileName(), name);
        files.put(file.getFileName(), file);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return files.values().stream()
                .mapToLong(IFile::getFileSize)
                .sum();
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Folder name: %10s Folder size: %10d\n", name, getFileSize()));
        for (IFile file : files.values()){
            String space = "    ".repeat(indent);
            sb.append(space).append(file.getFileInfo(indent + 1));
        }
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        for (IFile file : files.values()){
            file.sortBySize();
        }
      files = files.entrySet().stream()
              .sorted(Map.Entry.comparingByValue(Comparator.comparingLong(IFile::getFileSize)))
              .collect(Collectors.toMap(
                      Map.Entry::getKey,
                      Map.Entry::getValue,
                      (oldValue, newValue) -> oldValue,
                      LinkedHashMap::new
              ));
    }

    @Override
    public long findLargestFile() {
        return files.values().stream()
                .mapToLong(IFile::findLargestFile)
                .max()
                .orElse(0);
    }
}
class FileSystem{
    private Folder rootDirectory;

    public FileSystem() {
        this.rootDirectory = new Folder("root");
    }

    public void addFile(IFile file) throws FileNameExistsException {
        rootDirectory.addFile(file);
    }
    public long findLargestFile(){
        return rootDirectory.findLargestFile();
    }
    public void sortBySize(){
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo(1);
    }
}
public class FileSystemTest {

    public static Folder readFolder(Scanner sc) {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < totalFiles; i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String[] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args) {

        //file reading from input

        Scanner sc = new Scanner(System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());


    }
}