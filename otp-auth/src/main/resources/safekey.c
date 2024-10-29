#include <stdio.h> /* printf, sprintf */
#include <string.h>
#include <stdbool.h>
#include <stdlib.h> /* exit */
#include <unistd.h> /* read, write, close */
#include <string.h> /* memcpy, memset */
#include <sys/socket.h> /* socket, connect */
#include <netinet/in.h> /* struct sockaddr_in, struct sockaddr */
#include <netdb.h> /* struct hostent, gethostbyname */
#include <cjson/cJSON.h>

#define SENSOR_ID ""
#define DISK_KEY_LEN 47
#define SAFE_KEY_LEN 214
#define KEY_LEN 33
#define PATH_LEN 50
#define SIZE 1000
#define OTP_FILE "otp.dat"
#define KEYS_FILE "keys.dat"

/***** SafeKey v1.0.0 *******/
/***** Correlation Systems - Janine Son ****/
/***** 2024-10-29 **************************/
/******************************************************************************************
SafeKey sends the device UUID to i46 server and get back a disk key
SafeKey mounts the USB disk
SafeKey retrieves the key from the disk and send it to i46 server in order to open the keys files
SafeKey will receive three keys:
Key 1 - to open the storage
Key 2 - to keep on the disk for the next time
Key 3 - to encrypt the storage
SafeKey extract the top key from the storage
SafeKey encrypt the storage file (without the key that had been used) using key3
SafeKey provide the top key via the API

cJSON-1.7.18
sudo ldconfig /usr/local/lib
Run
gcc safekey.c -o safekey -lcjson
******************************************************************************************/

/* localhost 8080 /otp_auth_war/diskKey?uuid= */

int top = -1;
char inp_array[SIZE][KEY_LEN];
int read_line(FILE *in, char *buffer, size_t max);
char* httpRequest(bool isGet, char *host, int portno, char resource[], char key[]);
char* replace_char(char *orig, char *rep, char *with);
char* getString(int pos, int len, int i, char string[]);
int pop(char responseKey[]);
void error(const char *msg) { perror(msg); exit(0); }

int main()
{
    char *host = "localhost";
    int portno = 8080;
    char resource[PATH_LEN] = "/target-x/diskKey?uuid=";

    char *response = httpRequest(1, host, portno, resource, NULL);

    printf("Response:\n%s\n",response);

    char *e;
    int index;

    e = strchr(response, '{');
    index = (int)(e - response);
    char substr[50];
    strcpy (substr,getString(index+1, DISK_KEY_LEN, 0, response));


    cJSON *root = cJSON_Parse(substr);
    cJSON *command = cJSON_GetObjectItem(root, "diskKey");

    char diskKey[KEY_LEN];
    strcpy (diskKey, command->valuestring);
    printf("%s\n", diskKey);

    /* Mount disk using diskKey */
    /*TODO */

    cJSON_Delete(root);

    /* Retrieve one time key */
    FILE *fp = fopen(OTP_FILE, "ab+");
    char currentKey[KEY_LEN];
    fgets(currentKey, KEY_LEN, fp);

    printf("Current key: %s\n\n", currentKey);

    /* Send to i46 server to verify if key matches
    ** Get 3 keys in the response */
    char resourceValidate[PATH_LEN] = "/target-x/key/validate";

    char *responseValidate = httpRequest(0, host, portno, resourceValidate, currentKey);
    printf("Response:\n%s\n",responseValidate);

    char *eValidate;
    int indexValidate;

    eValidate = strchr(responseValidate, '{');
    indexValidate = (int)(eValidate - responseValidate);
    char substrValidate[214];
    strcpy (substrValidate,getString(indexValidate+1, SAFE_KEY_LEN, 0, responseValidate));

    cJSON *rootValidate = cJSON_Parse(substrValidate);
    cJSON *commandFail = cJSON_GetObjectItem(rootValidate, "fail");

    if (commandFail == NULL){
       cJSON *commandValidate = cJSON_GetObjectItem(rootValidate, "responseKey");

      /* Decrypt storage using storageKey */
      /*TODO */

      char responseKey[KEY_LEN];

      strcpy (responseKey, commandValidate->valuestring);
      cJSON_Delete(rootValidate);

      /* Put responseKey inside otp.dat*/
      freopen(NULL,"w+",fp);
      fprintf(fp, "%s\n", responseKey);
      fclose(fp);

      /* Pop topmost key */
      /* responseKey should match the topmost key */
      int i = pop(responseKey);

      if (i > 0){
        char new_array[i][KEY_LEN];
        for (int j = 0; j < i; j++) {
          strncpy(new_array[j], inp_array[j], KEY_LEN);
          /*printf("%s\n", new_array[j]);*/
        }
        printf("New count %d\n", i);
        /* Reset key list without the topmost key*/
        FILE *f = fopen(KEYS_FILE, "wb");
        fwrite(new_array, sizeof(char), sizeof(new_array), f);
        fclose(f);
      }


      /* Encrypt storage using encryptionKey */
      /*TODO */

      /* Unmount the disk */
      /*TODO */

    }else{
       printf("Invalid key. Exiting program...");
       return -1;
    }

    return 0;
}

int read_line(FILE *in, char *buffer, size_t max)
{
  return fgets(buffer, max, in) == buffer;
}

int pop(char responseKey[])
{
    FILE *in;
    int index = 0, i = 0;
    if((in = fopen(KEYS_FILE, "rt")) != NULL)
    {
      char line[SIZE];

      while(read_line(in, line, sizeof line)){
        if (index == 0){
          if (strcmp(responseKey,line)==0){
            printf("Something is wrong. Popped element and response key does not match. Exiting program...");
            exit(-1);
          }else
            printf("Popped element: %s\n", line);
        } else{
          strncpy(inp_array[i], line, SIZE);
          i++;
        }

        index++;

      }

      fclose(in);

    }
    return i;

}

char *httpRequest(bool isGet, char *host, int portno, char resource[], char key[])
{
    char message_fmt[1024];

    if (isGet){
      strcpy (message_fmt,"GET ");
      strcat(resource, SENSOR_ID);
      strcat(message_fmt, resource);
      strcat(message_fmt," HTTP/1.0\r\n\r\n");
    }else{
      strcpy (message_fmt,"POST ");
      strcat(message_fmt, resource);
      strcat(message_fmt," HTTP/1.0\r\n");
      char postString[100]= "uuid=";
      strcat(postString, SENSOR_ID);
      strcat(postString, "&key=");
      strcat(postString, key);

      int len = strlen(postString);
      char lenStr[3];
      sprintf(lenStr, "%d", len);
      strcat(message_fmt, "Content-Type: application/x-www-form-urlencoded\r\n");
      strcat(message_fmt, "Content-Length: ");
      strcat(message_fmt, lenStr);
      strcat(message_fmt, "\r\n\r\n");
      strcat(message_fmt, postString);
    }

    struct hostent *server;
    struct sockaddr_in serv_addr;
    int sockfd, bytes, sent, received, total;
    char message[1024],response[4096];
    char *ret;

    /* fill in the parameters */
    sprintf(message,message_fmt,host,portno);
    printf("Request:\n%s\n",message);

    /* create the socket */
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) error("ERROR opening socket");

    /* lookup the ip address */
    server = gethostbyname(host);
    if (server == NULL) error("ERROR, no such host");

    /* fill in the structure */
    memset(&serv_addr,0,sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(portno);
    memcpy(&serv_addr.sin_addr.s_addr,server->h_addr,server->h_length);

    /* connect the socket */
    if (connect(sockfd,(struct sockaddr *)&serv_addr,sizeof(serv_addr)) < 0)
        error("ERROR connecting");

    /* send the request */
    total = strlen(message);
    sent = 0;
    do {
        bytes = write(sockfd,message+sent,total-sent);
        if (bytes < 0)
            error("ERROR writing message to socket");
        if (bytes == 0)
            break;
        sent+=bytes;
    } while (sent < total);

    /* receive the response */
    memset(response,0,sizeof(response));
    total = sizeof(response)-1;
    received = 0;
    do {
        bytes = read(sockfd,response+received,total-received);
        if (bytes < 0)
            error("ERROR reading response from socket");
        if (bytes == 0)
            break;
        received+=bytes;
    } while (received < total);

    if (received == total)
        error("ERROR storing complete response from socket");

    /* close the socket */
    close(sockfd);

    ret = response;
   return ret;
}


// Function to get substr in C
char *getString(int pos, int len, int i, char string[])
{

    char substring[1000];
    char *ret;
    while (i < len) {
        substring[i] = string[pos + i - 1];
        i++;
    }

    substring[i] = '\0';
    ret = substring;
    return ret;
}

// You must free the result if result is non-NULL.
char *replace_char(char *orig, char *rep, char *with) {
    char *result; // the return string
    char *ins;    // the next insert point
    char *tmp;    // varies
    int len_rep;  // length of rep (the string to remove)
    int len_with; // length of with (the string to replace rep with)
    int len_front; // distance between rep and end of last rep
    int count;    // number of replacements

    // sanity checks and initialization
    if (!orig || !rep)
        return NULL;
    len_rep = strlen(rep);
    if (len_rep == 0)
        return NULL; // empty rep causes infinite loop during count
    if (!with)
        with = "";
    len_with = strlen(with);

    // count the number of replacements needed
    ins = orig;
    for (count = 0; (tmp = strstr(ins, rep)); ++count) {
        ins = tmp + len_rep;
    }

    tmp = result = malloc(strlen(orig) + (len_with - len_rep) * count + 1);

    if (!result)
        return NULL;

    // first time through the loop, all the variable are set correctly
    // from here on,
    //    tmp points to the end of the result string
    //    ins points to the next occurrence of rep in orig
    //    orig points to the remainder of orig after "end of rep"
    while (count--) {
        ins = strstr(orig, rep);
        len_front = ins - orig;
        tmp = strncpy(tmp, orig, len_front) + len_front;
        tmp = strcpy(tmp, with) + len_with;
        orig += len_front + len_rep; // move to next "end of rep"
    }
    strcpy(tmp, orig);
    return result;
}


