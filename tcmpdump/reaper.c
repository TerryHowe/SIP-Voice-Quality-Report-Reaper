#include <tcpdump-stdinc.h>
#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <strings.h>
#include "ip.h"
#include "addrtoname.h"
#include "reaper.h"

#define FORMAT "%s;%s;%s;%s;%s;%s;"
#define REAPER_SIP_PORT 5050
#define REAPER_BUFFER_SIZE (10*1024)
#define REAPER_BUFFER_MAX (8*1024)

static int reaper_initialized = 0;
static int reaper_sock = -1;
static struct sockaddr_in reaper_server;
static int reaper_server_length;
static char reaper_packet_buffer[REAPER_BUFFER_SIZE];
static char *reaper_packet_pointer = NULL;
static char reaper_buffer[REAPER_BUFFER_SIZE];
static char reaper_source_mac[64];
static char reaper_destination_mac[64];
static int reaper_base_sec;

static void reaper_init()
{
   if (reaper_initialized)
      return;
   reaper_set_source_mac("00:00:00:00:00:00");
   reaper_set_destination_mac("00:00:00:00:00:00");
   reaper_initialized = 1;
   reaper_sock= socket(AF_INET, SOCK_STREAM, 0);
   if (reaper_sock < 0) {
      fprintf(stderr, "Error creating socket %d\n", errno);
      exit(1);
   }

   struct hostent *hp = gethostbyname("127.0.0.1");
   if (hp == 0) {
      fprintf(stderr, "Error getting host ent %d\n", errno);
      exit(1);
   }

   reaper_server.sin_family = AF_INET;
   reaper_server.sin_port = htons(REAPER_SIP_PORT);
   bcopy((char *)hp->h_addr, (char *)&reaper_server.sin_addr, hp->h_length);
   reaper_server_length = sizeof(struct sockaddr_in);
   if (connect(reaper_sock, (struct sockaddr *)&reaper_server, sizeof(reaper_server)) < 0) {
       fprintf(stderr, "Error connecting to server %d\n", errno);
       exit(1);
   }
   struct timeval val;
   gettimeofday(&val, NULL);
   reaper_base_sec = val.tv_sec;

   return;
}

static int reaper_time_stamp()
{
    struct timeval val;
    gettimeofday(&val, NULL);
    return ((val.tv_sec - reaper_base_sec) * 1000) + (val.tv_usec / 1000);
}

static void reaper_writer(const u_char *data, int len)
{
   int n;
   n = write(reaper_sock, data, len);
   if (n < 0) {
       fprintf(stderr, "Error writing to server %d\n", errno);
       exit(1);
   }
}

static int
reaper_rtp_format(const struct ip *ip, int sport, int dport, const char* data, int len)
{
    if (reaper_packet_pointer == NULL)
        reaper_packet_pointer = reaper_packet_buffer;
   char *buffer = reaper_packet_pointer;
#ifdef INET6
   if (IP_V(ip) == 6) {
      const struct ip6_hdr *ip6;
      ip6 = (const struct ip6_hdr *)ip;
      buffer += sprintf(buffer, FORMAT,
        reaper_source_mac,
        ip6addr_string(&ip6->ip6_src),
        udpport_string(sport),
        reaper_destination_mac,
        ip6addr_string(&ip6->ip6_dst),
        udpport_string(dport));
   }
   else {
#endif /*INET6*/
      buffer += sprintf(buffer, FORMAT,
         reaper_source_mac,
         ipaddr_string(&ip->ip_src),
         udpport_string(sport),
         reaper_destination_mac,
         ipaddr_string(&ip->ip_dst),
         udpport_string(dport));
#ifdef INET6
   }
#endif /*INET6*/
   short sequenceNumber = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
   int timeStamp = ((data[4] & 0xFF) << 24) | ((data[5] & 0xFF) << 16) |
       ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);
   buffer += sprintf(buffer, "%d;%d;%d", sequenceNumber, timeStamp, reaper_time_stamp());
   *(buffer++) = '\n';
   *(buffer) = '\0';
   reaper_packet_pointer = buffer;
   return (buffer - reaper_packet_buffer);
}

static void
reaper_rtp_flush(int force)
{
    static const char *notify = "NOTIFY sip:rtp.example.com SIP/2.0\r\nVia: SIP/2.0/UDP 192.168.1.2;branch=z9hG4bKnp149505178-438c528b192.168.1.2;rport\r\nFrom: <sip:tcpdump@example.com>;tag=4442\r\nTo: <sip:rtp@example.com>;tag=78923\r\nCall-Id: rtp@example.com\r\nCSeq: 20 NOTIFY\r\nContact: <sip:tcpdump@example.com>\r\nMax-Forwards: 10\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s";
   if (reaper_packet_pointer == reaper_packet_buffer)
       return;
   int contentLength = (reaper_packet_pointer - reaper_packet_buffer);
   if (force == FALSE)
   {
      if (contentLength < REAPER_BUFFER_MAX)
        return;
   }
   contentLength = sprintf(reaper_buffer, notify, contentLength, reaper_packet_buffer);
   /* printf("%s\n", reaper_buffer); */
   reaper_writer(reaper_buffer, contentLength);
   reaper_packet_pointer = reaper_packet_buffer;
   *reaper_packet_pointer = '\0';
   return;
}

static int
reaper_rtcp_format(const struct ip *ip, int sport, int dport, const char* data, int len)
{
   char hex[16] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
   char *buffer = reaper_packet_buffer;
#ifdef INET6
   if (IP_V(ip) == 6) {
      const struct ip6_hdr *ip6;
      ip6 = (const struct ip6_hdr *)ip;
      buffer += sprintf(reaper_packet_buffer, FORMAT,
        reaper_source_mac,
        ip6addr_string(&ip6->ip6_src),
        udpport_string(sport),
        reaper_destination_mac,
        ip6addr_string(&ip6->ip6_dst),
        udpport_string(dport));
   }
   else {
#endif /*INET6*/
      buffer += sprintf(reaper_packet_buffer, FORMAT,
         reaper_source_mac,
         ipaddr_string(&ip->ip_src),
         udpport_string(sport),
         reaper_destination_mac,
         ipaddr_string(&ip->ip_dst),
         udpport_string(dport));
#ifdef INET6
   }
#endif /*INET6*/
   fprintf(stderr, "Sending RTCP packet <%s>\n", reaper_packet_buffer);
   char *end_buffer = reaper_packet_buffer + sizeof(reaper_packet_buffer) - 3;
   const char *end_data = data + len;
   while (data < end_data) {
      *(buffer++) = hex[((*data >> 4) & 0xF)];
      *(buffer++) = hex[(*data & 0xF)];
      if (buffer >= end_buffer)
          break;
      ++data;
   }
   *(buffer++) = '\n';
   *(buffer) = '\0';
   return (buffer - reaper_packet_buffer);
}

void reaper_sip_writer(const u_char *data, int len)
{
   reaper_init();
   char *ptr;
   if (((ptr = index(data, '\r')) == NULL) ||
       ((ptr = index(data, '\n')) == NULL)) {
         fprintf(stderr, "Error unexpected packet\n");
         return;
   }
   int lineLen = (ptr - (char*)data);
   reaper_rtp_flush(TRUE);
   fprintf(stderr, "Sending SIP: %.*s\n", lineLen, (char*)data);
   reaper_writer(data, len);
   return;
}

void
reaper_rtp_writer(const struct ip *ip, int sport, int dport, const u_char *hdr, const u_char *ep)
{
   int len = (int)(ep - hdr);
   if (len < 0)
       return;
   if ((dport % 2) != 0)
       return;
   if ((hdr[0] & 0xE0) != 0x80) /* v2 */
       return;
   reaper_init();
   reaper_rtp_format(ip, sport, dport, hdr, len);
   reaper_rtp_flush(FALSE);
}

void
reaper_rtcp_writer(const struct ip *ip, int sport, int dport, const u_char *hdr, const u_char *ep)
{
    static const char *notify = "NOTIFY sip:rtcp.example.com SIP/2.0\r\nVia: SIP/2.0/UDP 192.168.1.2;branch=z9hG4bKnp149505178-438c528b192.168.1.2;rport\r\nFrom: <sip:tcpdump@example.com>;tag=4442\r\nTo: <sip:rtcp@example.com>;tag=78923\r\nCall-Id: rtcp@example.com\r\nCSeq: 20 NOTIFY\r\nContact: <sip:tcpdump@example.com>\r\nMax-Forwards: 10\r\nContent-Type: text/plain\r\nContent-Length: %d\r\n\r\n%s";
   int len = (int)(ep - hdr);
   if (len < 0)
       return;
   if ((dport % 2) != 1)
       return;
   if ((hdr[0] & 0xE0) != 0x80) /* v2 */
       return;
   if ((hdr[1] < 0xc8) || (hdr[1] > 0xcf)) /* SR to XR */
       return;
   reaper_init();
   int contentLength = reaper_rtcp_format(ip, sport, dport, hdr, len);
   contentLength = sprintf(reaper_buffer, notify, contentLength, reaper_packet_buffer);
   printf("%s\n", reaper_buffer);
   reaper_writer(reaper_buffer, contentLength);
}

#include <string.h>

void reaper_set_source_mac(const char* value) {
    strncpy(reaper_source_mac, value, sizeof(reaper_source_mac));
    reaper_source_mac[sizeof(reaper_source_mac)-1] = '\0';
}

void reaper_set_destination_mac(const char* value) {
    strncpy(reaper_destination_mac, value, sizeof(reaper_destination_mac));
    reaper_destination_mac[sizeof(reaper_destination_mac)-1] = '\0';
}
