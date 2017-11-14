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

//##GLOBALS##
	//Root for n model
struct node *root = 0;
	//Root for n-1 model 
struct node *root2 = 0;

//##STRUCT DECLARATIONS##
struct HashItem {
	//Struct to keep key(ngram) and data(occurance) together
	unsigned char *key;
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
int hash(unsigned char *str); 
void insertTree(unsigned char *str, int hashval, struct node **leaf);
void insertArray(unsigned char *str, struct HashItem list[]);
struct node *searchTree(int searchval, struct node *leaf);
struct HashItem *searchArray(unsigned char *str, struct HashItem list[]);
int save(struct node root, struct node root2, unsigned char *filename);
int load(unsigned char *filename);
 
//##METHODS##
/*
	MAIN: Runs makemodel() for the n model and then the n-1 model
*/
int main(int argc, char** argv) {
	//LOAD?
	//N=3 FOR TESTING, REMOVE LATER: n = argv[1], if argc == 1 -> quit
	int n = 3;
	printf("%i", makeModel(n));
	printf("%i", makeModel(n-1));
	//SAVE?
    return (EXIT_SUCCESS);
}

/*
	TO-DO: Binary search item, if it exists data++
	if not insert and add data = 1
*/
void insertArray(unsigned char *str, struct HashItem list[]) {
	//Search Array for ngram
	struct HashItem *newItem; 
	newItem = searchArray(str, list);
	//If ngram doesnt exists
	if(newItem == 0) {
		//Add key in correct place data++
	} else {
		//data++
		if(newItem -> data > 0) {
			newItem -> data++;
		} else {
			newItem -> data = 1;
		}
	}
}

/*
	IMPLEMENT BINARY SEARCH
*/
struct HashItem *searchArray(unsigned char *str, struct HashItem list[]) {
	int size;/* = sizeof(list) / sizeof(list[0]);*/
	int bottom= 0;
    int mid;
    int top = size - 1;
	
    while(bottom <= top){
        mid = (bottom + top)/2;
        if (strcmp(list[mid].key, str) == 0){
            //FOUND;
            return 0;
        } else if (strcmp(list[mid].key, str) > 0){
            top = mid - 1;
        } else if (strcmp(list[mid].key, str) < 0){
            bottom = mid + 1;
        } else {
			return 0;
		}
    }
}

/*
	Inserts a node into the architecture and 
	initializes it.
*/
void insertTree(unsigned char *str, int hashval, struct node **leaf) {
	if( *leaf == 0) {
		//ALLOCATE
		*leaf = (struct node*) malloc(sizeof(struct node));
		//FILL NEW HASH VAL
		(*leaf) -> key_value = hashval;
		//FILL NEW NODES
		(*leaf) -> left = 0;
		(*leaf) -> right = 0;
		//FILL NEW ARRAY
		insertArray(str, (*leaf) -> list[0]);
	} else if(hashval < (*leaf)->key_value) {
		insertTree(str, hashval, &(*leaf)->left);
	} else if(hashval > (*leaf)->key_value) {
		insertTree(str, hashval, &(*leaf)->right);
	} else {
		//ERROR
	}
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
	Useful for using the input string to generate a hash value
	using the djb2 hash system
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
	MakeModel is for examining the files in the corpus folder
	and building the language model
	TO-DO: NEEDS TO BE UPDATED FOR HASH TREE
*/
int makeModel(int n) {
	if(n < 3) {
		perror("invalid value of n");
		return 1;
	}
	//VARIABLES
	int gate = 0;
	int c, old, i;
	char ngram[n];
	//TREE MANAGEMENT
	struct node *newNode = (struct node*) malloc(sizeof(struct node));
	struct HashItem *newItem = (struct HashItem*) malloc(sizeof(struct HashItem));
	int hashval;
	//FILE MANAGEMENT
	DIR *dp;
	struct dirent *ep;
	char *add = "./corpus/";
	char file[MAX_SIZE];
	dp = opendir(add);
	FILE *f;
	
	//Check if directory exists
	if(dp != NULL) {
		//While loops for each file in a directory
		while(ep = readdir(dp)) {
			//Skips "." and ".." in the loop
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				continue;
			}else {
				//opens file
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					//while loops for each character until end of file(EOF)
					while ((c = getc(f)) != EOF){
						//##EDIT INSIDE THIS WHILE LOOP##
						if(gate > n - 2) {
							if(gate != n - 1) {
								gate++;
								continue;
							} else {
								//ngramw[0] = ngram[1] ... ngram[n-2] = ngram[n-1] & ngram[n-1] = c
								for(i = 0; i < n - 1; i++) {
									ngram[i] = ngram[i + 1];
								}
								ngram[n-1] = c;
								//OUTPUT: printf("%s", ngram);
							}
							//Search Tree for hash node of ngram
							hashval = hash(ngram);
							newNode = searchTree(hashval, root);
							
							//If it doesnt exist create new hash node, Add key at 0, data++
							if (newNode == 0) {
								//void insertTree(unsigned char *str, int hashval, struct node **leaf);
								insertTree(ngram, hashval, &root);
							} else {
								//Search Array for ngram
								//struct HashItem *searchArray(int key, struct HashItem list[]);
								newItem = searchArray(ngram, *newNode->list);
							}
						} else {
							//FILL NGRAM UNTIL FULL
							ngram[n] = c;
							gate++;
						}
					// OUTPUT: putchar(c);
					}
				} else {
					perror("FAILED PASS");
					return 1;
				}	
				//Close File
				fclose(f);
			}
		}
		//Close Directory
		(void)closedir(dp);
	} else {
		perror("Cannot find directory!");
		return 1;
	}
	return 0;
}

/*
	Save current model to file:
	
	Root: Root node for n model
	Root2: Root node for n-1 model
	filename: Filename to save as
	
	TO-DO: Iterate through root in order and save to file
*/
int save(struct node root, struct node root2, unsigned char *filename) {
	return 0;
}

/*
	Load a previous model from file
	TO-DO: read in file in order and build tree structure
*/
int load(unsigned char *filename) {
	return 0;
}