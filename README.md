# PTC-Interpreter
Interpreter/Emulator for Petit Computer.
Written in Java using Swing.

## Note: This version is discontinued. Please see https://github.com/Minxrod/PTC-EmkII for the new version.

# How to use
Compile and run the program.

Upon running the program, you can load a .PTC file; the deafult location is in programs folder.
Samples 1-7 are included, as well as some testing programs I created. Support for each program varies, since most of the functions are still a work in progress. As of now, samples 1-5 should work correctly, with the exception of 3 because sounds have not been implemented yet. Sample 6 is somewhat interactable, and sample 7 should work fully, but has speed issues.

# Controls
Note: For text input, just type as you would on a normal keyboard. Not all keys are working (no shift) but alphanumeric keys should all work.

Emulated-->DSi

 - W = Up
 - A = Left
 - S = Down
 - D = Right
 - Up = X
 - Down = B
 - Left = Y
 - Right = A
 - Q = L
 - NUMPAD1 = R
 - Space = Start
 - Escape = Select

Extra:

 - Pg-Up = Set double zoom
 - Pg-Down = Set original zoom

# Credits

- GS.Games - Tales From The Labyrinth and tileset (used during testing)
- SmileBoom - SAMPLE1.PTC through SAMPLE7.PTC; default graphical files; original software
- Minxrod - Java based interpreter, most of the code.
- https://petitcomputer.fandom.com/wiki/PTC_File_Formats - Reference for most file related stuff

Special thanks to the large number of Swing-based questions on StackOverflow that saved time troubleshooting BufferedImages.
