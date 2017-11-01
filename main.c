/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.c
 * Author: Andrew
 *
 * Created on 27 October 2017, 00:26
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>
#include <errno.h>

int makeModel(int bound);
int makeModelSingle(char fp[]);
 
int main(int argc, char** argv) {
    char filename[] =  "C:/Users/Andrew/Documents/NetBeansProjects/CppApplication_1/corpus/A Tale of Two Cities - Charles Dickens.txt";
    printf("%s", filename);
	//makeModelSingle(filename);
    makeModel(2);
    return (EXIT_SUCCESS);
}

FILE makeModelDefault() {
	FILE *model;
	model = fopen("C:/cygwin64/home/Andrew/LangModel/model.txt", "ab+");
	fprintf(model, "\n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48 , 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148 , 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248 , 249, 250, 251, 252, 253, 254, 255);
	fclose(model);
	return *model;
}

int makeModelSingle(char fp[]) {
    FILE *file;
	FILE *model;
	model = fopen("C:/cygwin64/home/Andrew/LangModel/model.txt", "ab+");
	file = fopen(fp, "r");
	int c;
	if (file) {
		while ((c = getc(file)) != EOF)
			putchar(c);
		fclose(file);
	}
	fclose(model);
    return 0;
}

int makeModel(int bound) {
   DIR* FD;
   struct dirent* in_file;
   FILE *common_file;
   FILE *entry_file;
   char buffer[BUFSIZ];
   
   //Writing in Common File
   common_file = fopen("/home/Andrew/LangModel/Corpus/", "w");
   if(common_file == NULL) {
	   fprintf(stderr, "Error : Failed to open common_file - %s\n", strerror(errno));
	   return 1;
   }
   //Scanning in directory
   if(NULL == (FD = opendir ("/home/Andrew/LangModel/Corpus/"))) {
	   fprintf(stderr, "Error : Failed to open input directory - %s\n", strerror(errno));
	   fclose(common_file);
	   return 1;
   }
    while ((in_file = readdir(FD))) 
    {
        if (!strcmp (in_file->d_name, "."))
            continue;
        if (!strcmp (in_file->d_name, ".."))    
            continue;
        /* Open directory entry file for common operation */
        /* TODO : change permissions to meet your need! */
        entry_file = fopen(in_file->d_name, "rw");
        if (entry_file == NULL)
        {
            fprintf(stderr, "Error : Failed to open entry file - %s\n", strerror(errno));
            fclose(common_file);

            return 1;
        }

        /* Doing some struf with entry_file : */
        /* For example use fgets */
        while (fgets(buffer, BUFSIZ, entry_file) != NULL)
        {
            /* Use fprintf or fwrite to write some stuff into common_file*/
        }

        //Close Entry
        fclose(entry_file);
    }

    //Close Common
    fclose(common_file);

    return 0;
}