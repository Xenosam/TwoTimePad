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
	int list_size;
};

//##METHOD DECLERATIONS##
int makeModel(int n);
int hash(unsigned char *str, int n); 
void insertTree(unsigned char *str, int hashval, struct node **leaf);
void insertArray(unsigned char *str, struct node *Node);
struct node *searchTree(int searchval, struct node *leaf);
struct HashItem *searchArray(unsigned char *str, struct node *Node);
int save(struct node *root, unsigned char *filename);
int load(unsigned char *filename);

//##METHODS##
/*
	MAIN: Runs makemodel() for the n model and then the n-1 model
*/
int main(int argc, char** argv) {
	//LOAD?
	//N=3 FOR TESTING, REMOVE LATER: n = argv[1], if argc == 1 -> quit
	int n = 3;
	if(n < 2) {
		perror("invalid value of n");
		return 1;
	} else {
		printf("%i", makeModel(n));
		printf("%i", makeModel(n-1));
		//SAVE?
		save(root, "modeln");
		save(root2, "modeln2");
	}
	return (EXIT_SUCCESS);
}

/*
	TO-DO: Binary search item, if it exists data++
	if not insert and add data = 1
*/
void insertArray(unsigned char *str, struct node *Node) {
	puts("INSERT ARRAY");
	//Search Array for ngram
	int size = Node -> list_size;
	//struct HashItem *list;
	//list = *Node -> list;
	struct HashItem *newItem; 
	newItem = searchArray(str, Node);
	//If ngram doesnt exists
	if(newItem == 0) {
		puts("IF: IA");
		newItem = (struct HashItem*) malloc(sizeof(struct HashItem));
		newItem -> key = str;
		newItem -> data = 1;
		
		if(Node -> list_size > 1) {
			//NEED TO ADD SORT HERE
			int i = 0;
			int j;
			while(strcmp(Node -> list[i] -> key,str) < 0) {
				i++;
			}
			j = size;
			while(j > i) {
				Node -> list[j] = Node -> list[j - 1];
				j--;
			}
			Node -> list[i] = newItem;
		} else {
			puts(">1");
			Node -> list[size] = newItem;
		}
		Node -> list_size++;
		
	} else {
		puts("E: IA");
		//data++
		if(newItem -> data > 0) {
			puts("E-IF: IA");
			newItem -> data++;
		} else { 
			//INCASE == NULL
			puts("E-E: IA");
			newItem -> data = 1;
		}
	}
}

/*
	Binary searches an array using strcmp() outputting a pointer to
	the HashItem
*/
struct HashItem *searchArray(unsigned char *str, struct node *Node) {
	puts("SEARCH ARRAY");
	int size = Node -> list_size;
	int bottom= 0;
    int mid;
    int top = size - 1;
	struct HashItem *out;
	struct HashItem *list;
	list = *Node -> list;
	
    while(bottom <= top){
        mid = (bottom + top)/2;
		out = Node -> list[mid];
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
*/
void insertTree(unsigned char *str, int hashval, struct node **leaf) {
	puts("INSERT TREE");
	if( *leaf == 0) {
		puts("IF");
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
		insertArray(str, *leaf);
		
	} else if(hashval < (*leaf)->key_value) {
		puts("ELSE IF <");
		insertTree(str, hashval, &(*leaf)->left);
	} else if(hashval > (*leaf)->key_value) {
		puts("ELSE IF >");
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
	puts("SEARCH TREE");
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
int hash(unsigned char *str, int n)
{
	unsigned long hash = 5381;
	int out;
	int c;

    while (c = *str++)
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
	
	//MAX_SIZE = amount of buckets
	out = hash % (int)pow((double)MAX_SIZE,(double)n);
	
    return out;
}

/*
	MakeModel is for examining the files in the corpus folder
	and building the language model
	TO-DO: NEEDS TO BE UPDATED FOR HASH TREE
*/
int makeModel(int n) {
	//VARIABLES
	int gate = 0;
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
			}else {
				//opens file
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					puts("OPENED FILE");
					//while loops for each character until end of file(EOF)
					while ((c = getc(f)) != EOF){
						if(c > 31 ) {
							//##EDIT INSIDE THIS WHILE LOOP##
							puts("WHILE");
							if(gate >= n - 1) {
								if(gate == n - 1) {
									puts("IF");
									gate++;
								} else {
									puts("ELSE");
									//ngramw[0] = ngram[1] ... ngram[n-2] = ngram[n-1] & ngram[n-1] = c
									for(int i = 0; i < n - 1; i++) {
										puts("FOR");
										ngram[i] = ngram[i + 1];
									}	
									ngram[n-1] = c;
									//OUTPUT: puts(ngram);
								}
								//Search Tree for hash node of ngram
								hashval = hash(ngram, n);
								puts("HASH");
								newNode = searchTree(hashval, root);
								puts("SEARCHED");
								//If it doesnt exist create new hash node, Add key at 0, data++
								if (newNode == 0) {
									puts("IF2");
									//void insertTree(unsigned char *str, int hashval, struct node **leaf);
									insertTree(ngram, hashval, &root);
									puts("INSERTED");
								} else {
									puts("ELSE2");
									//Search Array for ngram
									//struct HashItem *searchArray(int key, struct HashItem list[]);
									newItem = searchArray(ngram, newNode);
									puts("SEARCHED2");
								}
							} else {
								//FILL NGRAM UNTIL FULL
								ngram[n] = c;
								gate++;
							}
						}
					// OUTPUT: 
					putchar(c);
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
int save(struct node *root, unsigned char *filename) {
	//FILE IO
	FILE *f;
	f = fopen(filename, "a");
	struct node *Node;
	Node = root;
	while(Node != 0) {			
		//OUTPUT HASH VALUE
		fprintf(f, "%i\n", Node -> key_value);
		for(int i = 0; i < Node -> list_size; i++) {
			fprintf(f, "%c\n", Node -> list[i] -> key);
			fprintf(f, "%i\n", Node -> list[i] -> data);
		}
		//FOR EACH ITEM IN LIST, OUTPUT KEY AND DATA
	}
	fclose(f);
	save(Node -> left, filename);
	save(Node -> right, filename);
	return 0;
}

/*
	Load a previous model from file
	TO-DO: read in file in order and build tree structure
*/
int load(unsigned char *filename) {
	//READ IN HASH VALUE
	//UNTIL NEXT HASH DUMP INTO LIST A HASH ITEM
	//HASH ITEM = (KEY, DATA)
	return 0;
}