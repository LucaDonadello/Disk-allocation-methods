/**
 * This interface represents a set of file operations.
 */
public interface FileOperation {
  /**
   * Displays the contents of the specified file.
   * 
   * @param fileName the name of the file to be displayed
   */
  void displayFile(String fileName);

  /**
   * Simulates the process of copying a file.
   * 
   * @param fileName the name of the file to be copied
   */
  void copyFileSim(String fileName);

  /**
   * Copies the specified file to a new location.
   * 
   * @param fileName the name of the file to be copied
   */
  void copyFileReal(String fileName);

  /**
   * Deletes the specified file.
   * 
   * @param fileName the name of the file to be deleted
   */
  void deleteFile(String fileName);
}
