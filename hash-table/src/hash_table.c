#include <stdlib.h>
#include <string.h>

#include "hash_table.h"

int HT_PRIME_1 = 131;
int HT_PRIME_2 = 151;

static ht_item* ht_new_item(const char* k, const char* v) {
    // heap memeory need to be freed, unlike the stack memeory which get deallocated when function exit
    ht_item* i = malloc(sizeof(ht_item)); // allocate a block of memeory of size bytes on the heap
    i->key = strdup(k);
    i->value = strdup(v);
    return i;
}


ht_hash_table* ht_new() {
    ht_hash_table* ht = malloc(sizeof(ht_hash_table));

    ht->size = 53;
    ht->count = 0;
    ht->items = calloc((size_t)ht->size, 
                        sizeof(ht_item*));
    return ht;
}


static void ht_del_item(ht_item* i) {
    free(i->key);
    free(i->value);
    free(i);
}

void ht_del_hash_table(ht_hash_table* ht) {
    for (int i = 0; i < ht->size; i++) {
        ht_item* item = ht->items[i];
        free(item->key);
        free(item->value);
        free(item);
    }
}



static int ht_hash(const char* s, const int base, const int num_buckets) {

    long hash  = 0;
    const int string_len = strlen(s);
    for (int i =0; i < string_len; i++){
        hash += (long)pow(base, string_len - (i+1)) * s[i];
        hash = hash % num_buckets;

    }

    return (int)hash;
}

static int ht_get_hash(
    const char* s, const int num_buckets, const int attempt
) {
    const int hash_a = ht_hash(s, HT_PRIME_1, num_buckets);
    const int hash_B = ht_hash(s, HT_PRIME_2, num_buckets);
    return (hash_a + (attempt * (hash_a + 1))) % num_buckets;
}


// HashMap Methods

// 1. Inserts

void ht_insert(ht_hash_table* ht, const char* key, const char* value) {

    ht_item* item = ht_new_item(key, value);
    int index = ht_get_hash(item->key, ht->size, 0);

    // EDGE case: if you delete an item in items
    // you might break the chain of collisioon for exmaple CONDIER THE INDEX. [0,1,2,3] -> DEL [2] -> [0,1,2]
    ht_item* cur_item = ht->items[index];
    int i = 1;
     while (cur_item != NULL)  {
        if (cur_item != &HT_DELETED_ITEM) {
            // If item already exist update it
            if (strcmp(cur_item->key, key) == 0) {
                ht_del_item(cur_item);
                ht->items[index] = item;
                return;
            }
        }
        index = ht_get_hash(item->key, ht->size, i);
        cur_item =  ht->items[index];
        i++;
    }
    ht->items[index] = item;
    ht->count++;
}

// 2. Search 

char* ht_search(ht_hash_table* ht, const char* key) {

    int index = ht_get_hash(key, ht->size, 0);
    ht_item* item = ht->items[index];
    int i = 1;
    while (item != NULL) {
        if (item != &HT_DELETED_ITEM) { 
            if (strcmp(item->key, key) == 0) {
                return item->value;
            }
        }
        index = ht_get_hash(key, ht->size, i);
        item = ht->items[index];
        i++;
    }
    return NULL;
}

static ht_item HT_DELETED_ITEM = {NULL, NULL};

// Instead of deleting the item, we simply mark it as deleted.
// We mark an item as deleted by replacing it with a pointer to a global sentinel item 
// which represents that a bucket contains a deleted item.
void ht_delete(ht_hash_table* ht, const char* key) {
    int index = ht_get_hash(key, ht->size, 0);
    ht_item* item = ht->items[index];
    int i = 1;
    while (item != NULL) {
        if (item != &HT_DELETED_ITEM) {
            if (strcmp(item->key, key) == 0) {
                ht_del_item(item);

                // When searching, we ignore and 'jump over' deleted nodes. When inserting, 
                // if we hit a deleted node,
                // we can insert the new node into the deleted slot.
                ht->items[index] = &HT_DELETED_ITEM;
            }
        }
        index = ht_get_hash(key, ht->size, i);
        item = ht->items[index];
        i++;
    } 
    ht->count--;
}



int main() {
    ht_hash_table* ht = ht_new();
    ht_del_hash_table(ht);
}



