#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>
#include <errno.h>

#define MAX_FILENAME 255

struct structModel makeModelSingle(char fp[]);
FILE makeModelDefault(); 

int main() {
	DIR *dp;
	struct dirent *ep;
	char add[] = "./corpus/";
	char file[MAX_FILENAME];
	dp = opendir(add);
	FILE *f;
	char c;
	
	if(dp != NULL) {
		while(ep = readdir(dp)) {
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				printf("PASS\n");
				continue;
			}else {
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
					printf("FAILED PASS\n");
				}	
				fclose(f);
			}
		}
		(void)closedir(dp);
		return 1;
	} else {
		perror("bwaaa bwaaaaaaah");
		return 0;
	}
}

/*

	if(dp != NULL) {
		while(ep = readdir(dp)) {
			if(strcmp(".",ep->d_name) == 0 || strcmp("..",ep->d_name) == 0){
				printf("PASS");
			} else {	
				file = fopen(ep->d_name, "r");
				while ((c = getc(file)) != EOF){
					if(bool != 0) {
						Model.model[old][c]++;
					} else {
						bool = 1;
					}
					putchar(c);
					Model.count = Model.count + 1;
					old = c;
				}
				bool = 0;
			}
		}	
		(void)closedir(dp);
		return 1;
	} else {
		perror("ERROR");
		return 0;
	}
*/

FILE makeModelDefault() {
	FILE *model;
	model = fopen("C:/cygwin64/home/Andrew/LangModel/model.txt", "ab+");
	fprintf(model, "%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c \n%c", 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48 , 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148 , 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248 , 249, 250, 251, 252, 253, 254, 255);
	fclose(model);
	return *model;
}
struct structModel makeModelSingle(char fp[]) {
	FILE *file;
	//FILE *model;
	//model = fopen("C:/cygwin64/home/Andrew/LangModel/model.txt", "w");
	struct structModel Model;
	file = fopen(fp, "r");
	int c;
	int old;
	if (file) {
		while ((c = getc(file)) != EOF){
			if(Model.count != 0) {
				Model.model[old][c]++;
			}
			putchar(c);
			Model.count = Model.count + 1;
			old = c;
		}	
		fclose(file);
	}
	//fclose(model);
	printf("%i", Model.count);
    return Model;
}