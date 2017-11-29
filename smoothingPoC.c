#include <stdio.h>
#include <stdlib.h>
#define SIZE 224

struct bigram make_model();
void examine(struct bigram Model);
struct bigram lagrange(struct bigram Model);
void examineToFile(struct bigram Model, char *filename);

struct bigram {
	int count;
	char model[SIZE][SIZE];
};

int main(int argc, char** argv) {
	//MAKE MODEL
	struct bigram Model = make_model();
	//EXAMINE
	examine(Model);
	examineToFile(Model, "./nosmooth.txt");
	//SMOOTHING
	Model = lagrange(Model);
	//EXAMINE
	examine(Model);
	examineToFile(Model, "./smooth.txt");
	//RETURN
	return 1;
}

struct bigram make_model() {
	puts("MAKE MODEL");
	FILE *file;
	char *fp = "C:/cygwin64/home/Andrew/LangModel/corpus/A Tale of Two Cities - Charles Dickens.txt";
	struct bigram Model;
	file = fopen(fp, "r");
	int c;
	int old;
	int num = 256 - SIZE;
	if (file) {
		while ((c = getc(file)) != EOF){
			if(c >= num) {					
				if(Model.count != 0) {
					Model.model[old - num][c - num]++;
				}
				putchar(c);
				Model.count = Model.count + 1;
				old = c;
			}
		}	
		fclose(file);
	}
	//fclose(model);
	printf("%i", Model.count);
    return Model;
}

struct bigram lagrange(struct bigram Model) {
	puts("LAGRANGE\n");
	for(int i = 0; i < SIZE; i++){
		for(int j = 0; j < SIZE; j++) {
			Model.model[j][i]++;
		}
	}
	return Model;
}

void examine(struct bigram Model) {
	puts("EXAMINE\n");
	int num = 256 - SIZE;
	for(int i = 0; i < SIZE; i++){
		for(int j = 0; j < SIZE; j++) {
			printf("'(%c,%c)' OR '(%i,%i)' %i\n", (j + num), (i + num), (j + num), (i + num), Model.model[j][i]);
		}
	}
}

void examineToFile(struct bigram Model, char *filename) {
	puts("EXAMINE\n");
	FILE *f;
	f = fopen(filename,"a");
	int num = 256 - SIZE;
	for(int i = 0; i < SIZE; i++){
		for(int j = 0; j < SIZE; j++) {
			fprintf(f,"'(%c,%c)' OR '(%i,%i)' %i\n", (j + num), (i + num), (j + num), (i + num), Model.model[j][i]);
		}
	}
	fclose(f);
}