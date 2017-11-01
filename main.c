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

void makeModel(int bound);
 
/*
 * 
 */
int main(int argc, char** argv) {
    makeModel(2);
    return (EXIT_SUCCESS);
}

void makeModel(int bound) {
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
 
/*
 * 
 */
int main(int argc, char** argv) {
    makeModel(2);
    return (EXIT_SUCCESS);
}

int makeModel(int bound) {
   //Make new Model File
   char nlabel[10] = "N-Gram = ";
   nlabel[9] = bound;
   
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
   FILE *fp = NULL; 
   fp = fopen("model.txt", "ab+");
   fputs(nlabel, fp);
   fclose(fp);
   //Loop up to the bound
   //Make a copy of every trigram
   //Log number for each trigram and for every collection
}