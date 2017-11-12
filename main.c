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
#include <stdbool.h>

#define MAX_SIZE 256

//##STRUCT DECLARATIONS##
struct structModel{
	//MODEL FOR TESTING BIGRAMS, ACTUAL IMPLEMENTATION WITH HASH TREE
	//REMOVE LATER, NOW DEFUNCT
	int model[256][256];
	int count;
};

struct HashItem {
	//Struct to keep key(ngram) and data(occurance) together
	int key;
	int data;
};

struct node {
	//Node for tree, key_value = hash(key)
	//maintains a sorted array of HashItems
	int key_value;
	struct node *left;
	struct node *right;
	struct HashItem *list[MAX_SIZE];
};

int makeModel();
int examine(struct structModel Model); 
int hash(int key); 
void insert(int key, int data);
 
 
int main(int argc, char** argv) {
	printf("%i", makeModel());
    return (EXIT_SUCCESS);
}

void insert(int key, int data) {
	int searchval = hash(key);
	
}

int hash(int key) {
	//GOOD HASHING ALGORITHM
	return key;
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
	int gate = 0;
	int c, old;
	struct structModel Model;
	DIR *dp;
	struct dirent *ep;
	char add[] = "./corpus/";
	char file[MAX_SIZE];
	dp = opendir(add);
	FILE *f;
	
	if(dp != NULL) {
		while(ep = readdir(dp)) {
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				continue;
			}else {
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					while ((c = getc(f)) != EOF){
						//##EDIT INSIDE THIS WHILE LOOP##
						if(gate != 0) {
							Model.model[old][c]++;
						} else {
							gate = 1;
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

