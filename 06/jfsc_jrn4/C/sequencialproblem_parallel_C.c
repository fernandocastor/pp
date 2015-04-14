//
//  sequencialproblema_paralelo_C.c
//  
//
//  Created by Fernando on 4/22/14.
//
//
#TEMPO DE EXECUCAO
#real	0m0.140s
#user	0m0.086s
#sys	0m0.200s

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/mman.h>
#include <string.h>

char* array_de_senhas[500]={0};
char* array_de_hashes[500]={0};
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

void gerarArrayDeSenhas(){
 
    for (int i =0; i<500; i++) {
        array_de_senhas[i] = "SENHA\n";
        //printf("%s",array_de_senhas[i]);
    }
    
}

#include <openssl/evp.h>

/**
 * Função que retorna o hash MD5 de uma string
 *
 * @param input String cujo MD5 queremos calcular
 * @param output String onde será escrito o MD5 (deve estar previamente alocada)
 */
char * md5FromString( char *input, char *output )
{
    EVP_MD_CTX mdctx;
    const EVP_MD *md;
    unsigned int output_len, i;
    unsigned char uOutput[EVP_MAX_MD_SIZE];
    
    /* Initialize digests table */
    OpenSSL_add_all_digests();
    
    /* You can pass the name of another algorithm supported by your version of OpenSSL here */
    /* For instance, MD2, MD4, SHA1, RIPEMD160 etc. Check the OpenSSL documentation for details */
    md = EVP_get_digestbyname( "MD5" );
    
    if ( ! md )
    {
        printf( "Unable to init MD5 digest\n" );
        exit( 1 );
    }
    
    EVP_MD_CTX_init( &mdctx );
    EVP_DigestInit_ex( &mdctx, md, NULL );
    EVP_DigestUpdate( &mdctx, input, strlen( input ) );
    
    EVP_DigestFinal_ex( &mdctx, uOutput, &output_len );
    EVP_MD_CTX_cleanup( &mdctx );
    
    // zera a string antes de começar a concatenação
    strcpy( output, "" );
    for(i = 0; i < output_len; i++)
    {
        sprintf( output, "%s%02x", output, uOutput[i] );
    }
    
    return output;
}

int main(int argc, char* argv[])
{
    gerarArrayDeSenhas();
    int pid =mmap(NULL, sizeof(int), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANON, -1, 0);
    int xsys = mmap(NULL, sizeof(int), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANON, -1, 0);

    for (xsys = 0; xsys < 500; xsys++ ) {
        pid = fork();
    
    if (pid == 0) { /* child */
            char output[33];
        
        array_de_hashes[xsys] = md5FromString(array_de_senhas[xsys],output);
            printf("Child process prints %s\n", array_de_hashes[xsys]);
            exit(0);
        }
        if (pid < 0) {
            perror("fork");
            exit(-1);
        }
    
    }
    waitall();
    printf("Parent process prints %d\n", xsys);
    munmap(xsys, sizeof(int));
    munmap(pid, sizeof(int));
}

