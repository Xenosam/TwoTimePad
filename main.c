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

 //##INCLUDES##
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>
#include <errno.h>
#include <stdbool.h>
//##DEFINES##
#define MAX_SIZE 256

//##STRUCT DECLARATIONS##
struct structModel{
	//MODEL FOR TESTING BIGRAMS, ACTUAL IMPLEMENTATION WITH HASH TREE
	//REMOVE LATER, NOW DEFUNCT
	int model[256][256];
	int count;
};

struct HashItem {
	//Struct to keep searchval(ngram) and data(occurance) together
	unsigned char *searchval;
	int data;
};

struct node {
	//Node for tree, key_value = hash(searchval)
	//maintains a sorted array of HashItems
	int key_value;
	struct node *left;
	struct node *right;
	struct HashItem *list[MAX_SIZE];
};

//##METHOD DECLERATIONS##
int makeModel(int n);
int examine(struct structModel Model); 
int hash(unsigned char *str); 
void insertTree(unsigned char *str, int hashval, struct node **leaf);
void insertArray(unsigned char *str, struct HashItem list[]);
struct node *searchTree(int searchval, struct node *leaf);
struct HashItem *searchArray(int key, struct HashItem list[]);
 
//##GLOBALS##
struct node *root = 0; 
 
//##METHODS##
/*
	MAIN: Runs makemodel() then exits
*/
int main(int argc, char** argv) {
	printf("%i", makeModel());
    return (EXIT_SUCCESS);
}

/*
	Inserts a node into the architecture and 
	initializes it.
*/
void insertTree(unsigned char *str, int hashval, struct node **leaf) {
	if( *leaf == 0) {
		*leaf = (struct node*) malloc(sizeof(struct node));
		(*leaf) -> key_value = hashval;
		//(*leaf) -> list[0] = newItem;
		(*leaf) -> left = 0;
		(*leaf) -> right = 0;
	} else if(hashval < (*leaf)->key_value) {
		insertTree(str, hashval, &(*leaf)->left);
	} else if(hashval > (*leaf)->key_value) {
		insertTree(str, hashval, &(*leaf)->right);
	} else {
		//ERROR
	}
}

/*
	Binary search item, if it exists data++
	if not insert and add data = 1
*/
void insertArray(unsigned char *str, struct HashItem list[]) {
	
}

/*
	Searches the tree for a given hash value
*/
struct node *searchTree(int searchval, struct node *leaf)
{
	if( leaf != 0 )
	{
		if(searchval==leaf->key_value)
		{
			// Search leaf -> list;
			return leaf;
		}
		else if(searchval<leaf->key_value)
		{
			return searchTree(searchval, leaf->left);
		}
		else
		{
			return searchTree(searchval, leaf->right);
		}
	}
	else return 0;
}

/*
	IMPLEMENT BINARY SEARCH
*/
struct HashItem *searchArray(int key, struct HashItem list[]) {
	return NULL;
}

/*
	djb2 Hash: http://www.cse.yorku.ca/~oz/hash.html
*/
int hash(unsigned char *str)
{
	unsigned long hash = 5381;
	int out;
	int c;

    while (c = *str++)
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
	
	//MAX_SIZE = amount of buckets
	out = hash % MAX_SIZE;
	
    return out;
}

/*
	x > 32 are command characters that arent worth evaluating
	DEFUNT: Implementation of Hash Tree makes this redundant
*/
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

/*
	TO-DO: NEEDS TO BE UPDATED FOR HASH TREE
	MakeModel is for examining the files in the corpus folder
	and building the language model
*/
int makeModel(int n) {
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