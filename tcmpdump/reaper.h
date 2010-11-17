void reaper_rtp_writer(const struct ip *ip, int sport, int dport, const u_char *hdr, const u_char *ep);
void reaper_sip_writer(const u_char *data, int len);
void reaper_set_source_mac(const char* value);
void reaper_set_destination_mac(const char* value);
