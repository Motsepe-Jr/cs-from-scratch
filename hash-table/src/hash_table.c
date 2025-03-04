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

static int ht_get_has(
    const char* s, const int num_buckets, const int attempt
) {
    const int hash_a = ht_hash(s, HT_PRIME_1, num_buckets);
    const int hash_B = ht_hash(s, HT_PRIME_2, num_buckets);
    return (hash_a + (attempt * (hash_a + 1))) % num_buckets;
}


int main() {
    ht_hash_table* ht = ht_new();
    ht_del_hash_table(ht);
}


