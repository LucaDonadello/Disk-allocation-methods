# Disk-allocation-methods

Project name: Disk Allocation Methods

Project folder: The folder contains the following programs

1) Main.java
2) Menu.java
3) Contiguos.java
4) Chained.java
5) Indexed.java
6) DiskDrive.java
7) FileSystem.java
8) FileOperation.java
9) test.txt

How to run the program:

- Download the project folder.
- Check if these two folders are correctly stored inside the folder downloaded:

    1)bin
    2)src

- Open the IDE and change the directory to src
- Once you are inside the src directory, run this code: javac -d ../bin *.java
- The programs should not have generated any warnings or Errors. You should notice the creation of .class files in the bin directory
- After running the previous code, run this one: java -cp ../bin Main <method>
- Replace <method> with one of these keyword:

    1) contiguous
    2) chained
    3) indexed

Note:
I decided to keep the pointer in byte conversion so if you try to display a block and it contains a pointer it will be visualized in byte.

- Run example:
javac -d ../bin *.java
java -cp ../bin Main contiguous


1) Display a file
2) Display the file table
3) Display the free space bitmap
4) Display a disk block
5) Copy a file from the simulation to a file on the real system
6) Copy a file from the real system to a file in the simulation
7) Delete a file
8) Exit

Choice: 6
Copy from: test.txt
Copy to: test
File test.txt copied.

1) Display a file
2) Display the file table
3) Display the free space bitmap
4) Display a disk block
5) Copy a file from the simulation to a file on the real system
6) Copy a file from the real system to a file in the simulation
7) Delete a file
8) Exit

Choice: 3
11110000000000000000000000000000
00000000000000000000000000000000
00000000000000000000000000000000
00000000000000000000000000000000
00000000000000000000000000000000
00000000000000000000000000000000
00000000000000000000000000000000
00000000000000000000000000000000

1) Display a file
2) Display the file table
3) Display the free space bitmap
4) Display a disk block
5) Copy a file from the simulation to a file on the real system
6) Copy a file from the real system to a file in the simulation
7) Delete a file
8) Exit

Choice: 8
Exiting simulation...
