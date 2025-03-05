#include <math.h>
#include "prime.h"

// return 0 (not_prime), 1 (prime) or -1 (undefined)

int is_prime(const int x) {

    if (x < 2) { return -1; }
    if (x < 4) { return 1; }
    if ((x%2) == 0) { return 0;}

    for (int i = 3; i <= floor(sqrt((double) x)); i+=2 ) { // add 2 to include only odd number
        if ((x % i) == 0) {
            return 0;
        }
    }
    return 1;
}


int next_prime(int x) {
    while (is_prime(x) != 1) {
        x++;
    }

    return x;
}