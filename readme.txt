The submission directory contains a directory “corpus” and “storage” the relevant C code files and their compiled executables. 
Adding text files into the “corpus” directory from “storage” will allow the “main.exe” and “smoothingPoC.exe” programs to create 
a model based on the files available and create files in the associated "output" directory. These models take some time to build 
and so including less files in the “corpus” directory will allow it to build significantly faster. “main.exe” takes exactly 
1 argument which is the integer value for the length of the n-grams, n must be larger than 2 (as the n-1 model would be worthless!).  