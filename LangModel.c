/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* Imports */
#import <stdio.h>

/* Define */
#define MAX_STRING_LEN 80

/* Function Declaration */
void makeModel(char *filename[]);
void peel();

/* Globals */

int main()
{
    
}

void peel() {
    char tString1[][][];
    char tString2[][][];
    double tProbability[][];
    
    int *tSp1 = tString1[0][0][0];
    int *tSp2 = tString2[0][0][0];
    int *tPp = tProbability[0][0];
    
    char fString1[][][];
    char fString2[][][];
    double fProbability[][];
    
    int *fSp1 = fString1[0][0][0];
    int *fSp2 = fString2[0][0][0];
    int *fPp = fProbability[0][0];
}

void makeModel(char *filename[]) {
    FILE *fopen(filename, 'r');
    
}