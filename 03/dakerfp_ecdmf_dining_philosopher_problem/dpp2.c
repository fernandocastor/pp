/*
 * dining_philosopher_01.c
 * Copyright (C) 2015 Daker Fernandes, Emiliano Firmino
 *
 * Distributed under terms of the MIT license.
 */

#include <stdio.h>
#include <stdlib.h>
#include "thread.h"

#define N 5

typedef struct context {
    int id;
    MUTEX_T* left_chopstick;
    MUTEX_T* right_chopstick;
} context_t;

THREAD_FUNC(print_hello, param) {
    context_t* ctx = param;

    for (;;) {
        SLEEP(rand() % 5);
        printf("%d is hungry!\n", ctx->id);

        if (MUTEX_TRYLOCK(*ctx->right_chopstick)) {
            printf("%d got right chopstick\n", ctx->id);
        } else {
            printf("%d couldn't get right chopstick, will try later\n", ctx->id);
            continue;
        }

        if (MUTEX_TRYLOCK(*ctx->left_chopstick)) {
            printf("%d got left chopstick\n", ctx->id);
        } else {
            printf("%d couldn't get left chopstick, will try later\n", ctx->id);
            MUTEX_UNLOCK(*ctx->right_chopstick);
            printf("%d release right chopstick\n", ctx->id);
            continue;
        }


        printf("%d start eating\n", ctx->id);
        SLEEP(1);
        printf("%d finish eating\n", ctx->id);

        MUTEX_UNLOCK(*ctx->right_chopstick);
        printf("%d release right chopstick\n", ctx->id);
        MUTEX_UNLOCK(*ctx->left_chopstick);
        printf("%d release left chopstick\n", ctx->id);
    }

    free(ctx);
    return 0;
}

int main() {
    srand(time(NULL));
    MUTEX_T  chopsticks[N];
    THREAD_T philosophers[N];

    for (int i = 0; i < N; i++) {
        MUTEX_INIT(chopsticks[i]);
    }

    for (int i = 0; i < N; i++) {
        context_t* ctx = (context_t*) malloc(sizeof(context_t));
        ctx->id = i;
        ctx->left_chopstick = &chopsticks[i];
        ctx->right_chopstick = &chopsticks[(i+1) % N];

        THREAD_INIT(philosophers[i], print_hello, ctx);
    }

    for (int i = 0; i < N; i++) {
        THREAD_JOIN(philosophers[i]);
        THREAD_DESTROY(philosophers[i]);
    }

    for (int i = 0; i < N; i++) {
        MUTEX_DESTROY(chopsticks[i]);
    }

    return 0;
}
