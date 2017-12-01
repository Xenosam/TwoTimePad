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
#include <math.h>
//##DEFINES##
#define MAX_SIZE 256

//##STRUCT DECLARATIONS##
struct HashItem {
	//Struct to keep key(ngram) and data(occurance) together
	char *key;
	int data;
};

struct node {
	//leaf for tree, key_value = hash(searchval)
	//maintains a sorted array of HashItems
	int key_value;
	struct node *left;
	struct node *right;
	struct HashItem *list[MAX_SIZE];
	int list_size;
};

//##METHOD DECLERATIONS##
int makeModel(int n);
int hash(char *str, int n); 
void insertTree(char *str, int hashval, struct node **leaf);
void insertArray(char *str, struct node *leaf);
struct node *searchTree(int searchval, struct node *leaf);
struct HashItem *searchArray(char *str, struct node *leaf);
int save(struct node *root, char *filename);
int load(struct node *root, char *filename);

//##GLOBALS##
struct node *root = 0; 
struct node *root2 = 0;

//##METHODS##
/*
	MAIN: Runs makemodel() for the n model and then the n-1 model
*/
int main(int argc, char** argv) {
	if(argc != 2) {
		puts("Wrong Number of arguements");
		return(EXIT_SUCCESS);
	}
	int n = atoi(argv[1]);
	if(n < 2) {
		perror("invalid value of n");
		return 1;
	} else {
		//##MAIN CALLS##
		printf("%i", makeModel(n));
		save(root, "./output/main/modeln.txt");
		
		printf("%i", makeModel(n-1));
		save(root2, "./output/main/modeln2.txt");
	}
	return (EXIT_SUCCESS);
}

/*
	TO-DO: Binary search item, if it exists data++
	if not insert and add data = 1
*/
void insertArray(char *str, struct node *leaf) {
	//Search Array for ngram
	int size = leaf -> list_size;
	//struct HashItem *list;
	//list = *leaf -> list;
	struct HashItem *newItem; 
	newItem = searchArray(str, leaf);
	//If ngram doesnt exists
	if(newItem == 0) {
		newItem = (struct HashItem*) malloc(sizeof(struct HashItem));
		newItem -> key = str;
		newItem -> data = 1;
		
		if(leaf -> list_size > 1) {
			int i = 0;
			int j;
			while(strcmp(leaf -> list[i] -> key,str) < 0) {
				i++;
			}
			j = size;
			while(j > i) {
				leaf -> list[j] = leaf -> list[j - 1];
				j--;
			}
			leaf -> list[i] = newItem;
		} else {
			leaf -> list[size] = newItem;
		}
		leaf -> list_size++;
		
	} else {
		newItem -> data++;
	}
}

/*
	Binary searches an array using strcmp() outputting a pointer to
	the HashItem
*/
struct HashItem *searchArray(char *str, struct node *leaf) {
	int size = leaf -> list_size;
	int bottom= 0;
    int mid;
    int top = size - 1;
	struct HashItem *out;
	struct HashItem *list;
	list = *leaf -> list;
	
    while(bottom <= top){
        mid = (bottom + top)/2;
		out = leaf -> list[mid];
        if (strcmp(out->key, str) == 0){
            return out;
        } else if (strcmp(out->key, str) > 0){
            top = mid - 1;
        } else if (strcmp(out->key, str) < 0){
            bottom = mid + 1;
        } else {
			return 0;
		}
    }
}

/*
	Inserts a node into the architecture and 
	initializes it.
	Code taken from second year work which adapted code from:
	https://www.cprogramming.com/tutorial/c/lesson18.html
*/
void insertTree(char *str, int hashval, struct node **leaf) {
	if( *leaf == 0) {
		//ALLOCATE
		*leaf = (struct node*) malloc(sizeof(struct node));
		//FILL NEW HASH VAL
		(*leaf) -> key_value = hashval;
		//FILL NEW NODES
		(*leaf) -> left = 0;
		(*leaf) -> right = 0;
		//ADD SIZE 0
		(*leaf) -> list_size = 0;
		//FILL NEW ARRAY
		insertArray(str, (*leaf));
		
	} else if(hashval < (*leaf) -> key_value) {
		insertTree(str, hashval, &(*leaf) ->left);
	} else if(hashval > (*leaf) -> key_value) {
		insertTree(str, hashval, &(*leaf) -> right);
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
		if(searchval == leaf -> key_value) {
			return leaf;
		} else if(searchval < leaf -> key_value) {
			return searchTree(searchval, leaf -> left);
		} else {
			return searchTree(searchval, leaf -> right);
		}
	}
	else return 0;
}

/*
	Useful for using the input string to generate a hash value
	using the djb2 hash system
	djb2 Hash: http://www.cse.yorku.ca/~oz/hash.html
*/
int hash(char *str, int n)
{
	unsigned long hash = 5381;
	int out;
	int c;

    while (c = *str++)
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
	
	//MAX_SIZE = amount of buckets
	//out = hash % (int)pow((int)MAX_SIZE,(int)n);
	out = hash % MAX_SIZE;
	
	//TEST: printf("%i\n", out);
	
    return out;
}

/*
	MakeModel is for examining the files in the corpus folder
	and building the language model
	TO-DO: NEEDS TO BE UPDATED FOR HASH TREE
*/
int makeModel(int n) {
	//VARIABLES
	int gate = -3;
	int count = 0;
	int c, old;
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
			} else {
				//opens file
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					//while loops for each character until end of file(EOF)
					while ((c = getc(f)) != EOF){
						if(c > 31 ) {
							if(gate >= n - 1) {
								if(gate == n - 1) {
									ngram[gate] = c;
									gate++;
								} else {
									//ngramw[0] = ngram[1] ... ngram[n-2] = ngram[n-1] & ngram[n-1] = c
									for(int i = 0; i < n - 1; i++) {
										ngram[i] = ngram[i + 1];
									}	
									ngram[n-1] = c;
									ngram[n] = '\0';
								}
								//Search Tree for hash node of ngram
								hashval = hash(ngram, n);
								newNode = searchTree(hashval, root);
								//If it doesnt exist create new hash node, Add key at 0, data++
								if (newNode == 0) {
									insertTree(ngram, hashval, &root);
								} else {
									newItem = searchArray(ngram, newNode);
								}
							} else {
								//FILL NGRAM UNTIL FULL
								//printf("PASS: %i", gate);
								ngram[gate] = c;
								gate++;
							}							
						// OUTPUT: 
						count++;	
						//printf("(%c/%i)\n", c, count);
						//puts(ngram);
						}
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
*/
int save(struct node *root, char *filename) {
	//FILE IO
	FILE *f;
	f = fopen(filename, "a");
	
	//VALIDATION
	if(f == NULL) {
		puts("File not found");
		return 1;
	}
	
	if(root != 0) {	
		struct node *leaf;
		leaf = root;
		
		printf("\nHASH: %i\n", (int)leaf -> key_value);
		fprintf(f, "%i\n", (int)leaf -> key_value);
		for(int i = 0; i < leaf -> list_size; i++) {
			//PRINT TO FILE
			fprintf(f, "%s\n", leaf -> list[i] -> key);
			fprintf(f, "%i\n", leaf -> list[i] -> data);
			//PRINT TO CONSOLE
			printf("KEY: %s\n", leaf -> list[i] -> key);
			printf("DATA: %i\n", leaf -> list[i] -> data);
		}
		//FOR EACH ITEM IN LIST, OUTPUT KEY AND DATA
		fclose(f);
		
		save(root -> left, filename);
		save(root -> right, filename);
	} else{
		//ESCAPE
	}
	return 0;
}

/*
	Load a previous model from file
*/
int load(struct node *root, char *filename) {
	//FILE IO
	FILE *f;
	char *o = NULL;
	size_t length = 0;
	ssize_t read, read2;
	f = fopen(filename, "r");
	int hash;
	char *key;
	int data;
	
	//VALIDATION
	if(f == NULL) {
		puts("File not found");
		return 1;
	}
	
	while((read = getline(&o, &length, f)) != -1) {
		//READ IN HASH VALUE
		hash = (int)atoi(o);
		//READ UNTIL BLANK LINE
		while((read2 = getline(&o, &length, f)) != 0){
			//READ IN KEY/DATA PAIRS
			key = o;
			read2 = getline(&o, &length, f);
			data = atoi(o);
			//RECREATE NODE
			for(int i = 0; i > data; i++) {
				insertTree(key, hash, &root);
			}
		}
		
	}
	fclose(f);
	return 0;
}