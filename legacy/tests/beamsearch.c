//##INCLUDES##
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

//##STRUCTS##
//##METHODS##
void beamsearch();
void printarr();
void randfill();
void topfive();
void topfiveforsmbol(char input);

//##GLOBALS##
int array[94][94];

int main() {
	randfill();
	printarr();
	topfive();
	topfiveforsmbol('a');
}

void beamsearch() {
	
}

/*
	32 - First Unicode Useful symbol
	126 - End of standard character set
*/
void printarr() {
	for (int i = 32; i < 126; i++) {
		for (int j = 32; j < 126; j++) {
			printf("(%i,%i)(%c,%c): %i\n", i, j, i, j, array[i-32][j-32]);
		}
	}
}

void topfive() {
	//arr[x] = Index, arr[x][0] = char 1, arr[x][1] = char 2, arr[x][2] = number
	int arr[5][3];
	//for swapping
	int lowest = 0;
	
	for (int i = 0; i < 94; i++) {
		for (int j = 0; j < 94; j++) {
			if(j < 5) {
				arr[j][0] = i;
				arr[j][1] = j;
				arr[j][2] = array[i][j];
			} else {
				for (int k = 0; k < 5; k++) {
					
				}
			}
		}
	}
}

void topfiveforsmbol(char input) {
	
}

void randfill() {
	time_t t;
	srand((unsigned) time(&t));
   
	for (int i = 32; i<126; i++) {
		for (int j = 32; j<126; j++) {
			array[i-32][j-32] = (rand() % 94) + 32;
		}
	}
}