//TEMPO DE EXECUCAO
// real	0m1.470s

//
//  sequencialproblema_paralelo_C.c
//  
//
//  Created by Fernando on 4/22/15.
//
//

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/mman.h>
#include <string.h>
#if defined(__APPLE__)
#  define COMMON_DIGEST_FOR_OPENSSL
#  include <CommonCrypto/CommonDigest.h>
#  define SHA1 CC_SHA1
#else
#  include <openssl/md5.h>
#endif
#define ACCESS_ONCE(x) (*(volatile typeof(x) *)&(x))

char* array_de_hashes[500] = {0};

void waitall(void){
    int pid;
    int status;
    
    for (; ;) {
        pid = wait(&status);
        if (pid == -1) {
            if (errno == ECHILD)
                break;
            perror("wait");
            exit(-1);
        }
    }
}


//include <openssl/evp.h>

/**
 * Função que retorna o hash MD5 de uma string
 *
 * @param input String cujo MD5 queremos calcular
 * @param output String onde será escrito o MD5 (deve estar previamente alocada)
 */
char * md5FromString( char *str, int length )
{
    int n;
    MD5_CTX c;
    unsigned char digest[16];
    char *out = (char*)malloc(33);

    MD5_Init(&c);

    while (length > 0) {
        if (length > 512) {
            MD5_Update(&c, str, 512);
        } else {
            MD5_Update(&c, str, length);
        }
        length -= 512;
        str += 512;
    }

    MD5_Final(digest, &c);

    for (n = 0; n < 16; ++n) {
        snprintf(&(out[n*2]), 16*2, "%02x", (unsigned int)digest[n]);
    }

    return out;
}

int main(int argc, char* argv[])
{
    int pid =mmap(NULL, sizeof(int), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANON, -1, 0);
    int xsys =mmap(NULL, sizeof(int), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANON, -1, 0);
    for (xsys = 0; xsys < 500; xsys++ ) {
        pid = fork();
    
    if (pid == 0) { /* child */
        //printf("%d\n", pid);
        
        array_de_hashes[xsys] = md5FromString("SENHA_X",strlen("SENHA_X"));
        //printf("Child process prints %s\n", array_de_hashes[xsys]);
            exit(0);
            
        }
        if (pid < 0) {
            perror("fork");
            exit(-1);
        }
     
    }
    waitall();
    //printf("Parent process prints %d\n", xsys);
    munmap(xsys, sizeof(int));
    munmap(pid, sizeof(int));
}

