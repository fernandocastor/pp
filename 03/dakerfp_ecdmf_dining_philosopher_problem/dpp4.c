/*
 * dpp4.c
 * Copyright (C) 2015 Daker Fernandes, Emiliano Firmino
 *
 * Same solution than dpp3.c, but now support external
 * input about the number of philosophers.
 *
 * Distributed under terms of the MIT license.
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "thread.h"

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

        if (ctx->id % 2) {
            // Pick Up Right Before Left
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
        } else {
            // Pick Up Left Before Right
            if (MUTEX_TRYLOCK(*ctx->left_chopstick)) {
                printf("%d got left chopstick\n", ctx->id);
            } else {
                printf("%d couldn't get left chopstick, will try later\n", ctx->id);
                continue;
            }

            if (MUTEX_TRYLOCK(*ctx->right_chopstick)) {
                printf("%d got right chopstick\n", ctx->id);
            } else {
                printf("%d couldn't get right chopstick, will try later\n", ctx->id);
                MUTEX_UNLOCK(*ctx->left_chopstick);
                printf("%d release left chopstick\n", ctx->id);
                continue;
            }

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

int main(int argc, char ** argv) {
    if (argc != 2) {
        printf("usage: %s <number_of_philosopher>\n", argv[0]);
        return 0;
    }

    srand(time(NULL));
    int n = (int) strtol(argv[1], (char **)NULL, 10);

    MUTEX_T*  chopsticks = (MUTEX_T*) malloc(n * sizeof(MUTEX_T));
    THREAD_T* philosophers = (THREAD_T*) malloc(n * sizeof(THREAD_T));

    for (int i = 0; i < n; i++) {
        MUTEX_INIT(chopsticks[i]);
    }

    for (int i = 0; i < n; i++) {
        context_t* ctx = (context_t*) malloc(sizeof(context_t));
        ctx->id = i;
        ctx->left_chopstick = &chopsticks[i];
        ctx->right_chopstick = &chopsticks[(i+1) % n];

        THREAD_INIT(philosophers[i], print_hello, ctx);
    }

    for (int i = 0; i < n; i++) {
        THREAD_JOIN(philosophers[i]);
        THREAD_DESTROY(philosophers[i]);
    }

    for (int i = 0; i < n; i++) {
        MUTEX_DESTROY(chopsticks[i]);
    }

    return 0;
}
