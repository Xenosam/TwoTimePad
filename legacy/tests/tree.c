/*
	Test Code adapted from 
	https://www.cprogramming.com/tutorial/c/lesson18.html
*/
//##INCLUDES##
#include <stdio.h>
#include <stdlib.h>

//##STRUCTS##
struct node {
  int key_value;
  struct node *left;
  struct node *right;
};

//##METHODS##
void destroy_tree(struct node *leaf);
void insert(int key, struct node **leaf);
struct node *search(int key, struct node *leaf);
void examineNode(struct node *newNode);

//##GLOBALS##
struct node *root = 0;

/*
	agrv[1]: search
	argv[2] ... argv[argc - 1]: insert
*/
int main(int argc, char** argv) {
	if(argc < 3) {
		puts("Too Few Number of arguements");
		return(EXIT_SUCCESS);
	}

	for(int i = 2; i < argc; i++) {
		insert(atoi(argv[i]),&root);
	}
	
	printf("SEARCH FOR: %i\n", atoi(argv[1]));
	
	struct node *newNode;
	newNode = search(atoi(argv[1]), root);
	
	if(newNode == 0) {
		puts("NOT FOUND");
	} else {
		puts("#######################");
		printf("KEY:%i\n", newNode -> key_value);
		if(newNode -> left == 0) {
			puts("NO LEFT NODE");
		} else {
			printf("LEFT:%i\n", newNode -> left -> key_value);
		}
		if(newNode -> right == 0) {
			puts("NO RIGHT NODE");
		} else {
			printf("RIGHT:%i\n", newNode -> right -> key_value);
		}
		puts("#######################");
	}
}

void examineNode(struct node *newNode) {
	if(node != 0) {
		puts("#######################");
		printf("HASH:%i\n", newNode -> key_value);
		if(node -> left == 0) {
			puts("NO LEFT NODE");
		} else {
			printf("LEFT:%i\n", newNode -> left -> key_value);
		}
		if(node -> right == 0) {
			puts("NO RIGHT NODE");
		} else {
			printf("RIGHT:%i\n", newNode -> right -> key_value);
		}
		puts("#######################");
	} else {
		puts("\nNODE IS EMPTY\n");
	}
}

void destroy_tree(struct node *leaf) {
  puts("DELETE");
  if( leaf != 0 )
  {
      destroy_tree(leaf->left);
      destroy_tree(leaf->right);
      free( leaf );
  }
}

void insert(int key, struct node **leaf) {
    if( *leaf == 0 )
    {
        puts("INSERT");
		*leaf = (struct node*) malloc( sizeof( struct node ) );
        (*leaf)->key_value = key;
        /* initialize the children to null */
        (*leaf)->left = 0;    
        (*leaf)->right = 0;  
    }
    else if(key < (*leaf)->key_value)
    {
        insert( key, &(*leaf)->left );
    }
    else if(key > (*leaf)->key_value)
    {
        insert( key, &(*leaf)->right );
    }
}

struct node *search(int key, struct node *leaf) {
  puts("SEARCH");
  if( leaf != 0 )
  {
      if(key==leaf->key_value)
      {
          return leaf;
      }
      else if(key<leaf->key_value)
      {
          return search(key, leaf->left);
      }
      else
      {
          return search(key, leaf->right);
      }
  }
  else return 0;
}