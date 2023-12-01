//lxd210013 - Luca Donadello Project 3 - CS4348.001

public class Main {

  /**
   * The main method is the entry point of the program.
   * It creates a DiskDrive object, initializes a FileSystem object with the given mode,
   * and displays the menu based on the selected mode.
   * If the mode is not valid, an error message is displayed and the program exits.
   *
   * @param args The command line arguments passed to the program.
   */
  public static void main(String[] args) {
    DiskDrive disk = new DiskDrive();
    String mode = args[0];
    FileSystem fileSystem = new FileSystem(disk, mode);
    Menu menu = new Menu();

    if (
      !mode.equals("contiguous") &&
      !mode.equals("chained") &&
      !mode.equals("indexed")
    ) {
      System.out.println("Error: mode not found");
      System.exit(0);
    }
    menu.display(fileSystem, mode);
  }
}
