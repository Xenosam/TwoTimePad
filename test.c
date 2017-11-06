#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <unistd.h>
#include <errno.h>

#define MAX_FILENAME 255

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
				puts(ep->d_name);
				/*f = fopen(ep->d_name, "r");
				while((c = getc(f)) != EOF) {
					putchar(c);
				}
				fclose(f);*/
				//memset(file, '\0', sizeof(file));
				strcpy(file, add);
				strcpy(file, ep->d_name);
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