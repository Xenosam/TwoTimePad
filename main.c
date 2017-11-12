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

#define MAX_FILENAME 255

struct structModel{
	int model[256][256];
	int count;
};

int makeModel();
int examine(struct structModel Model); 
 
int main(int argc, char** argv) {
	printf("%i", makeModel());
    return (EXIT_SUCCESS);
}

int examine(struct structModel Model) {
	for(int x = 32; x<256; x++) {
		for(int y = 32; y<256; y++) {
			char cx = x;
			char cy = y;
			if(Model.model[x][y]>=1 && Model.model[x][y]<Model.count)
				printf("%c + %c: %i\n", cx, cy, Model.model[x][y]);
		}
	}
}

int makeModel() {
	int bool = 0;
	int c, old;
	struct structModel Model;
	DIR *dp;
	struct dirent *ep;
	char add[] = "./corpus/";
	char file[MAX_FILENAME];
	dp = opendir(add);
	FILE *f;
	
	if(dp != NULL) {
		while(ep = readdir(dp)) {
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				printf("PASS\n");
				continue;
			}else {
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					while ((c = getc(f)) != EOF){
						//##EDIT INSIDE THIS WHILE LOOP##
						if(bool != 0) {
							Model.model[old][c]++;
						} else {
							bool = 1;
						}
						putchar(c);
						Model.count = Model.count + 1;
						old = c;
					}
				} else {
					printf("FAILED PASS\n");
				}	
				fclose(f);
			}
		}
		(void)closedir(dp);
	} else {
		perror("bwaaa bwaaaaaaah");
		return 1;
	}
	
	examine(Model);
    
	return 0;
}