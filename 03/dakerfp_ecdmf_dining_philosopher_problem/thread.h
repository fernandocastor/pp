/*
 * thread.h
 * Copyright (C) 2015 Daker Fernandes, Emiliano Firmino
 *
 * Distributed under terms of the MIT license.
 */

#ifndef __THREAD_H__
#define __THREAD_H__

#if defined(__unix__) || defined(__APPLE__)
    #include <semaphore.h>
    #include <stdlib.h>
    #include <string.h>
    #include <unistd.h>
    #include <pthread.h>

    #define THREAD_T             pthread_t
    #define THREAD_FUNC(N, P)    void* N (void* P)
    #define THREAD_INIT(T, F, P) pthread_create(&T, NULL, F, P)
    #define THREAD_JOIN(T)       pthread_join(T, NULL)
    #define THREAD_DESTROY(T)

    #define MUTEX_T          pthread_mutex_t
    #define MUTEX_INIT(M)    pthread_mutex_init(&M, NULL)
    #define MUTEX_LOCK(M)    pthread_mutex_lock(&M)
    #define MUTEX_TRYLOCK(M) pthread_mutex_trylock(&M) == 0
    #define MUTEX_UNLOCK(M)  pthread_mutex_unlock(&M)
    #define MUTEX_DESTROY(M) pthread_mutex_destroy(&M)

    #define SEM_T          sem_t
    #define SEM_INIT(S)    sem_init(&S, 0, 0)
    #define SEM_UP(S)      sem_post(&S)
    #define SEM_DOWN(S)    sem_wait(&S)
    #define SEM_DESTROY(S) sem_close(&S)

    #define SLEEP(T)       sleep(T)
#elif _WIN32
    #include <limits.h>
    #include <windows.h>

    #define THREAD_T             HANDLE
    #define THREAD_FUNC(N, P)    DWORD WINAPI N (LPVOID P)
    #define THREAD_INIT(T, F, P) T = CreateThread(NULL, 0, F, P, 0, NULL)
    #define THREAD_JOIN(T)       WaitForSingleObject(T, INFINITE)
    #define THREAD_DESTROY(T)    CloseHandle(T)

    #define MUTEX_T          HANDLE
    #define MUTEX_INIT(M)    M = CreateMutex(NULL, FALSE, NULL)
    #define MUTEX_LOCK(M)    WaitForSingleObject(M, INFINITE)
    #define MUTEX_TRYLOCK(M) WaitForSingleObject(M, 0) == WAIT_OBJECT_0
    #define MUTEX_UNLOCK(M)  ReleaseMutex(M)
    #define MUTEX_DESTROY(M) CloseHandle(M)

    #define SEM_T          HANDLE
    #define SEM_INIT(S)    S = CreateSemaphore(NULL, 0, INT_MAX, NULL)
    #define SEM_UP(S)      ReleaseSemaphore(S, 1, NULL)
    #define SEM_DOWN(S)    WaitForSingleObject(S, INFINITE)
    #define SEM_DESTROY(S) CloseHandle(S)

    #define SLEEP(T)       Sleep(T * 1000)
#endif

#endif /* !__THREAD_H__ */

