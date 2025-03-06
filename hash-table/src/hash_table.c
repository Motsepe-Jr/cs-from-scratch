#include <stdlib.h>
#include <string.h>

#include "hash_table.h"

int HT_PRIME_1 = 131;
int HT_PRIME_2 = 151;
int HT_INITIAL_BASE_SIZE = 100;

static ht_item* ht_new_item(const char* k, const char* v) {
    // heap memeory need to be freed, unlike the stack memeory which get deallocated when function exit
    ht_item* i = malloc(sizeof(ht_item)); // allocate a block of memeory of size bytes on the heap
    i->key = strdup(k);
    i->value = strdup(v);
    return i;
}


ht_hash_table* ht_new() {
    ht_hash_table* ht = malloc(sizeof(ht_hash_table)); // NULL if memoery fails

    ht->size = 53;
    ht->count = 0;
    ht->items = calloc((size_t)ht->size, 
                        sizeof(ht_item*));
    return ht;
}

// Malloc: Allocates a block of memory of the specified size but does not initialize it. 
// The contents of the allocated memory are undefined.

// Calloc: Allocates a block of memory for an array of elements and initializes the memory to zero.

static ht_hash_table* ht_new_sized(const int base_size) {
    ht_hash_table* ht = xmalloc(sizeof(ht_hash_table)); // Never return Null, terminates the program
    ht->base_size = base_size;
    

    // why the size of ht is suppose tobe a prime  number?
    // a prime number helps distribute the keys more uniformaly acfros the buekcts
    // h(x) = x % ht_size
    // lets say out size is 100 (non prime) and we insert keys that are multiple of 10 (10, 20, 30, ..)
    // key % 100 -> repeating cycles for exmaple 10 mod 100 -> 10; 110 mod 100 -> 10
    // prime number like 10 mod 101 = 10, 110 mod 101 -> 9
    ht->size = next_prime(ht->base_size); // the base3_size has been multipleied by 2; and we find the next pirme number

    ht->count = 0;
    ht->items = xcalloc((size_t)ht->size, sizeof(ht_item*)); // (num_of_elemnt, their size)

    return ht;
}


static void ht_resize(ht_hash_table* ht, const int base_size) {
    if (base_size < HT_INITIAL_BASE_SIZE) {
        return;
    }
    ht_hash_table* new_ht = ht_new_sized(base_size);
    for (int i = 0; i < ht->size; i++) {
        ht_item* item = ht->items[i];
        if (item != NULL && item != &HT_DELETED_ITEM) {
            ht_insert(new_ht, item->key, item->value);
        }
    }

    ht->base_size = new_ht->base_size;
    ht->count = new_ht->count;

    // To delete new_ht, we give it ht's size and items 
    const int tmp_size = ht->size;
    ht->size = new_ht->size;
    new_ht->size = tmp_size;

    ht_item** tmp_items = ht->items;
    ht->items = new_ht->items;
    new_ht->items = tmp_items;

    ht_del_hash_table(new_ht);
}


static void ht_resize_up(ht_hash_table* ht) {
    const int new_size = ht->base_size * 2;
    ht_resize(ht, new_size);
}

static void ht_resize_down(ht_hash_table* ht) {
    const int new_size = ht->base_size / 2;
    ht_resize(ht, new_size);
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

    const int load = ht->count * 100 / ht->size;

    if (load > 70) {
        ht_resize_up(ht);
    }


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

    const int load = ht->count * 100 / ht->size;
    if (load < 10) {
        ht_resize_down(ht);
    }

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



