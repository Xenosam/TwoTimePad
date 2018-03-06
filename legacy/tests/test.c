//##INCLUDES##
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>
#include <math.h>
#include <errno.h>

//##DEFINES##
#define MAX_SIZE 256

//##STRUCTS##
struct bigram {
	int model[MAX_SIZE][MAX_SIZE];
	int count;
};

//##METHODS##
int test1();
int test2();
int test3();
int test4();

//##GLOBALS##

/*
	Main Method for calling the tests:
		0 = Success
		1 = Failure
		
	Tree Tests have been moved out into Tree.c
	Smoothing Tests have been moved out into Smoothing.c
*/
int main() {
	int state;
	
	state = test1();
	printf("Test1:%i\n", state);
	state = test2();
	printf("Test2:%i\n", state);
	state = test3();
	printf("Test3:%i\n", state);
	state = test4();
	printf("Test4:%i\n", state);
}

/*
	READ FROM FILE
*/
int test1() {
	FILE *f;
	char *file = "../storage/A Tale of Two Cities - Charles Dickens.txt";
	char c;
	f = fopen(file, "r");
	if(f) {
		while ((c = getc(f)) != EOF){
			putchar(c);
		}
	} else {
		perror("FILE ERROR");
		return 1;
	}	
	fclose(f);
	return 0;
}

/*
	READ FROM EACH FILE IN A DIRECTORY
*/
int test2() {
	DIR *dp;
	struct dirent *ep;
	char add[] = "../storage/";
	char file[MAX_SIZE];
	dp = opendir(add);
	FILE *f;
	char c;
	
	if(dp != NULL) {
		while(ep = readdir(dp)) {
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				continue;
			} else {
				//puts(ep->d_name);
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					while ((c = getc(f)) != EOF){
						putchar(c);
					}
				} else {
					perror("FILE ERROR");
					return 1;
				}	
				fclose(f);
			}
		}
		(void)closedir(dp);
	} else {
		perror("DIRECTORY ERROR");
		return 1;
	}
	printf("\n");
	return 0;
}

/*
	Shuffle characters around in the n-gram
	Drop the least significant and add the next
*/
int test3() {
	int n = 10;
	char ngram[] = {'n','e','e','d','i','n','g','/','g','e'};
	for(int i = 0; i < n - 1; i++) {
		ngram[i] = ngram[i + 1];
		printf("%s\n", ngram);
	}
	char c = 't';
	ngram[n-1] = c;
	ngram[n] = '\0';
	printf("%s\n", ngram);
	if(strcmp("eeding/get", ngram) == 0) {
		return 0;
	} else {
		return 1;
	}
}

/*
	Writing into a model structure
*/
int test4() {
	struct bigram Model;
	DIR *dp;
	struct dirent *ep;
	char add[] = "../storage/";
	char file[MAX_SIZE];
	dp = opendir(add);
	FILE *f;
	int bool = 0;
	char c, old;
	
	if(dp != NULL) {
		while(ep = readdir(dp)) {
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				continue;
			} else {
				strcpy(file, add);
				strcat(file, ep->d_name);
				f = fopen(file, "r");
				puts(file);
				if(f) {
					while ((c = getc(f)) != EOF){
						if(c > 31){
							if(bool != 0) {
								Model.model[old][c]++;
							} else {
								bool = 1;
							}
							Model.count = Model.count + 1;
							old = c;
						}
					}
					bool = 0;
				} else {
					perror("FILE ERROR");
					return 1;
				}	
				fclose(f);
			}
		}
		(void)closedir(dp);
	} else {
		perror("DIRECTORY ERROR");
		return 1;
	}
	printf("\n");
	return 0;
}
/*
	Tests 5 and 6 were built into the smoothing.c file and extended as a proof of concept program
*/
/*
	Tests 7 - 10 were built into the tree.c file and extended as a proof of concept program
*/