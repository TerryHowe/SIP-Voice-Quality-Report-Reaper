#! /bin/sh
cd /opt/reaper
/opt/reaper/bin/bpf ${*} >/dev/null 2>/opt/reaper/log/bpf.err